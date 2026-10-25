package dev.italiansdelight.common.worldgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.italiansdelight.common.registry.ModBlocks;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

/**
 * An ancient Mediterranean olive, not a tall baobab or a dark oak.
 *
 * The 2x2 bole is only three layers tall: the upper layer shifts sideways
 * and immediately splits into low, climbing limbs. Irregular buttress roots
 * and bark-clad wooden knuckles give the base its centuries-old silhouette.
 * Broad overlapping silver crowns conceal the upper branches.
 *
 * Build and preflight the COMPLETE shape before placing any wood. A cramped
 * tree does not destroy buildings, spawn disconnected limbs or lose saplings.
 */
public final class AncientOliveTrunkPlacer extends TrunkPlacer {
    public static final MapCodec<AncientOliveTrunkPlacer> CODEC =
        RecordCodecBuilder.mapCodec(instance ->
            trunkPlacerParts(instance).apply(instance, AncientOliveTrunkPlacer::new));

    private static final int BOLE_HEIGHT = 3;
    private static final int MAIN_LIMBS = 4;

    public AncientOliveTrunkPlacer(int baseHeight, int heightRandA, int heightRandB) {
        super(baseHeight, heightRandA, heightRandB);
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return ModTreePlacers.ANCIENT_OLIVE_TRUNK;
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(
        WorldGenLevel level,
        BiConsumer<BlockPos, BlockState> trunkSetter,
        RandomSource random,
        int treeHeight,
        BlockPos origin,
        TreeConfiguration config
    ) {
        Direction initialLean = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        boolean clockwise = random.nextBoolean();
        boolean[] sidewaysKink = new boolean[MAIN_LIMBS];
        boolean[] longLimb = new boolean[MAIN_LIMBS];
        int[] attachmentHeight = new int[MAIN_LIMBS];
        int lowFirst = random.nextInt(2);

        // Generate one immutable silhouette; obstacle handling may rotate it,
        // but must never change it into a tall vertical fallback tree.
        for (int i = 0; i < MAIN_LIMBS; i++) {
            sidewaysKink[i] = random.nextBoolean();
            longLimb[i] = random.nextInt(4) == 0;
            attachmentHeight[i] = 1 + ((i + lowFirst) % 2);
        }

        for (int rotation = 0; rotation < 4; rotation++) {
            Direction lean = initialLean;
            for (int i = 0; i < rotation; i++) {
                lean = lean.getClockWise();
            }
            AncientPlan plan = planTree(origin, lean, clockwise,
                sidewaysKink, longLimb, attachmentHeight);
            if (!hasClearSpace(level, origin, plan)) {
                continue;
            }

            for (int dx = 0; dx < 2; dx++) {
                for (int dz = 0; dz < 2; dz++) {
                    placeBelowTrunkBlock(level, trunkSetter, random,
                        origin.offset(dx, -1, dz), config);
                }
            }
            for (Map.Entry<BlockPos, Boolean> entry : plan.logs().entrySet()) {
                boolean placed = entry.getValue()
                    ? placeLog(level, trunkSetter, random, entry.getKey(), config,
                        state -> ModBlocks.OLIVE_WOOD.defaultBlockState())
                    : placeLog(level, trunkSetter, random, entry.getKey(), config);
                if (!placed) {
                    // All planned blocks were already checked; no partial
                    // fallback crown is deliberately produced.
                    return List.of();
                }
            }

            List<FoliagePlacer.FoliageAttachment> crowns = new ArrayList<>(5);
            for (BlockPos tip : plan.tips()) {
                crowns.add(new FoliagePlacer.FoliageAttachment(tip.above(), 0, false));
            }
            return crowns;
        }

        return List.of();
    }

    private static AncientPlan planTree(
        BlockPos origin, Direction lean, boolean clockwise,
        boolean[] sidewaysKink, boolean[] longLimb, int[] attachmentHeight
    ) {
        Map<BlockPos, Boolean> logs = new LinkedHashMap<>();
        Set<BlockPos> tips = new LinkedHashSet<>();

        // The bole is 2x2 at the bottom, but bends sharply from the THIRD
        // layer: only two consecutive layers share the same footprint.
        // The staggered 2x2 overlap remains connected face-to-face.
        BlockPos upper = origin.relative(lean);
        for (int y = 0; y < BOLE_HEIGHT; y++) {
            BlockPos layer = y == BOLE_HEIGHT - 1 ? upper : origin;
            for (int dx = 0; dx < 2; dx++) {
                for (int dz = 0; dz < 2; dz++) {
                    logs.put(layer.offset(dx, y, dz), y != 0);
                }
            }
        }

        // Buttress knots begin ONLY on the SECOND trunk level (y=1).
        // Projecting blocks at y=0 looked like branches growing directly
        // from the first block; keep the planted footprint strictly 2x2.
        for (int i = 0; i < MAIN_LIMBS; i++) {
            Direction side = rotated(lean, clockwise, i);
            if (i == 3) {
                continue; // An asymmetric missing buttress.
            }
            BlockPos edge = edgeOf(origin, side, i % 2);
            // y=0 contains the four base logs and nothing else.
            // These irregular buttresses now grow outward at y=1.
            BlockPos root = edge.relative(side).above();
            logs.put(root, true);
            if (i == 0 || i == 2) {
                logs.put(root.above(), true);
            }
        }

        // A pair of offset wooden knuckles forms an open, crooked fork.
        // There is intentionally NO solid 2x2 column above y=2.
        BlockPos knotA = upper.offset(0, BOLE_HEIGHT, 1);
        BlockPos knotB = upper.offset(1, BOLE_HEIGHT, 1);
        logs.put(knotA, true);
        logs.put(knotB, true);
        BlockPos centralTip = knotB.above();
        logs.put(centralTip, true);

        // Four major limbs begin at the second/third trunk block (y=1/2).
        // Every lateral segment climbs a block before the next lateral move.
        // This creates visible gnarled Y-forks instead of horizontal beams.
        for (int i = 0; i < MAIN_LIMBS; i++) {
            Direction outward = rotated(lean, clockwise, i);
            int rootY = attachmentHeight[i];
            BlockPos section = rootY == BOLE_HEIGHT - 1 ? upper : origin;
            BlockPos tip = edgeOf(section, outward, (i + 1) % 2)
                .above(rootY);

            tip = tip.relative(outward);
            logs.putIfAbsent(tip, true);
            tip = tip.above();
            logs.putIfAbsent(tip, true);

            Direction second = sidewaysKink[i]
                ? (clockwise ? outward.getClockWise() : outward.getCounterClockWise())
                : outward;
            tip = tip.relative(second);
            logs.putIfAbsent(tip, true);
            tip = tip.above();
            logs.putIfAbsent(tip, true);

            if (longLimb[i]) {
                tip = tip.relative(outward);
                logs.putIfAbsent(tip, true);
                tip = tip.above();
                logs.putIfAbsent(tip, true);
            }
            tips.add(tip);
        }

        tips.add(centralTip);
        return new AncientPlan(logs, tips);
    }

    private static Direction rotated(Direction initial, boolean clockwise, int rotations) {
        Direction result = initial;
        for (int i = 0; i < rotations; i++) {
            result = clockwise ? result.getClockWise() : result.getCounterClockWise();
        }
        return result;
    }

    /** One of the two outermost blocks of a 2x2 section, on the selected side. */
    private static BlockPos edgeOf(BlockPos section, Direction side, int position) {
        if (side == Direction.NORTH) {
            return section.offset(position, 0, 0);
        }
        if (side == Direction.SOUTH) {
            return section.offset(position, 0, 1);
        }
        if (side == Direction.EAST) {
            return section.offset(1, 0, position);
        }
        return section.offset(0, 0, position);
    }

    private static boolean hasClearSpace(WorldGenLevel level, BlockPos origin, AncientPlan plan) {
        for (BlockPos position : plan.logs().keySet()) {
            if (!canReplace(level, position)) {
                return false;
            }
        }

        // Reserve foliage volumes before moving even one trunk block. Small
        // clearances prevent overwriting walls, paths or neighboring trees.
        // Unlike the former baobab shape, no tall central-column reservation.
        for (BlockPos tip : plan.tips()) {
            for (int dy = -2; dy <= 2; dy++) {
                BlockPos slice = tip.above(dy);
                if (slice.getY() - (origin.getY() - 1) < 3) {
                    continue;
                }
                for (int dx = -5; dx <= 5; dx++) {
                    for (int dz = -5; dz <= 5; dz++) {
                        if (dx * dx + dz * dz > 25) {
                            continue;
                        }
                        BlockPos position = slice.offset(dx, 0, dz);
                        if (!plan.logs().containsKey(position)
                            && !canReplace(level, position)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private static boolean canReplace(WorldGenLevel level, BlockPos position) {
        BlockState state = level.getBlockState(position);
        return state.isAir() || state.canBeReplaced();
    }

    private record AncientPlan(Map<BlockPos, Boolean> logs, Set<BlockPos> tips) {}
}
