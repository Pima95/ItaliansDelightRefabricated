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
 * Original 2x2 ancient olive: thick, slightly displaced core and four low,
 * ascending limbs. No vanilla dark-oak or prebuilt structure geometry.
 *
 * The whole wood and canopy envelope is checked BEFORE changing the ground
 * or placing logs. A failed orientation leaves all four saplings untouched
 * (the vanilla TreeGrower restores them when TreeFeature returns false).
 */
public final class AncientOliveTrunkPlacer extends TrunkPlacer {
    public static final MapCodec<AncientOliveTrunkPlacer> CODEC =
        RecordCodecBuilder.mapCodec(instance ->
            trunkPlacerParts(instance).apply(instance, AncientOliveTrunkPlacer::new));

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
        int stemHeight = Math.max(5, treeHeight - 4);
        Direction initialLean = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        boolean clockwise = random.nextBoolean();
        boolean[] outerBends = new boolean[4];
        int[] branchRise = new int[4];
        for (int i = 0; i < 4; i++) {
            outerBends[i] = random.nextBoolean();
            branchRise[i] = random.nextInt(2);
        }

        // Retain the same tree silhouette when rotating to avoid an obstacle.
        for (int rotation = 0; rotation < 4; rotation++) {
            Direction lean = initialLean;
            for (int i = 0; i < rotation; i++) {
                lean = lean.getClockWise();
            }
            AncientPlan plan = planTree(origin, stemHeight, lean, clockwise,
                outerBends, branchRise);
            if (!hasClearSpace(level, origin, plan)) {
                continue;
            }

            // All placement checks have passed. Preserve the four-block base.
            for (int dx = 0; dx < 2; dx++) {
                for (int dz = 0; dz < 2; dz++) {
                    placeBelowTrunkBlock(level, trunkSetter, random,
                        origin.offset(dx, -1, dz), config);
                }
            }
            for (Map.Entry<BlockPos, Boolean> entry : plan.logs().entrySet()) {
                boolean placed;
                if (entry.getValue()) {
                    placed = placeLog(level, trunkSetter, random, entry.getKey(), config,
                        state -> ModBlocks.OLIVE_WOOD.defaultBlockState());
                } else {
                    placed = placeLog(level, trunkSetter, random, entry.getKey(), config);
                }
                if (!placed) {
                    // All wood cells were prechecked; there should be no
                    // obstruction at this point in synchronous tree generation.
                    return List.of();
                }
            }

            List<FoliagePlacer.FoliageAttachment> crowns = new ArrayList<>();
            for (BlockPos tip : plan.tips()) {
                crowns.add(new FoliagePlacer.FoliageAttachment(tip.above(), 0, false));
            }
            return crowns;
        }

        // Reject cramped growth rather than overwriting terrain or building
        // a truncated monumental olive.
        return List.of();
    }

    private static AncientPlan planTree(
        BlockPos origin, int stemHeight, Direction lean, boolean clockwise,
        boolean[] outerBends, int[] branchRise
    ) {
        Map<BlockPos, Boolean> logs = new LinkedHashMap<>();
        Set<BlockPos> tips = new LinkedHashSet<>();
        int bendY = Math.max(3, stemHeight - 2);

        // A continuous 2x2 base and central bole, with a one-block-wide
        // offset of the ENTIRE 2x2 cross-section near its top. The overlapping
        // layers remain connected by faces and resemble a twisting old trunk.
        for (int y = 0; y < stemHeight; y++) {
            BlockPos layer = y >= bendY ? origin.relative(lean) : origin;
            for (int dx = 0; dx < 2; dx++) {
                for (int dz = 0; dz < 2; dz++) {
                    logs.put(layer.offset(dx, y, dz), y >= 2);
                }
            }
        }

        Direction start = lean;
        for (int i = 0; i < 4; i++) {
            Direction outward = start;
            for (int turn = 0; turn < i; turn++) {
                outward = clockwise ? outward.getClockWise() : outward.getCounterClockWise();
            }

            int y = stemHeight - 3 + branchRise[i];
            BlockPos core = y >= bendY ? origin.relative(lean) : origin;
            BlockPos root;
            if (outward == Direction.NORTH) {
                root = core.offset(i % 2, y, 0);
            } else if (outward == Direction.SOUTH) {
                root = core.offset(i % 2, y, 1);
            } else if (outward == Direction.EAST) {
                root = core.offset(1, y, i % 2);
            } else {
                root = core.offset(0, y, i % 2);
            }

            // Every horizontal step is followed by an upward one, so all
            // branch segments touch. The optional third step replaces a
            // straight move with a sideways kink, producing uneven lobes.
            BlockPos tip = root.relative(outward);
            logs.put(tip, true);
            tip = tip.above();
            logs.put(tip, true);
            tip = tip.relative(outward);
            logs.put(tip, true);
            tip = tip.above();
            logs.put(tip, true);
            if (outerBends[i]) {
                Direction side = clockwise
                    ? outward.getClockWise() : outward.getCounterClockWise();
                tip = tip.relative(side);
                logs.put(tip, true);
                tip = tip.above();
                logs.put(tip, true);
            }
            tips.add(tip);
        }

        // A fifth overlapping crown bridges the four spreading limbs.
        BlockPos summit = origin.relative(lean).offset(1, stemHeight - 1, 1);
        tips.add(summit);
        return new AncientPlan(logs, tips);
    }

    private static boolean hasClearSpace(WorldGenLevel level, BlockPos origin, AncientPlan plan) {
        for (BlockPos position : plan.logs().keySet()) {
            if (!canReplace(level, position)) {
                return false;
            }
        }

        // Reserve each rounded lobe before touching the four saplings. This
        // is intentionally conservative near buildings and other trees.
        for (BlockPos tip : plan.tips()) {
            for (int dy = -2; dy <= 2; dy++) {
                BlockPos slice = tip.above(dy);
                // Keep two full blocks clear above ground.
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
