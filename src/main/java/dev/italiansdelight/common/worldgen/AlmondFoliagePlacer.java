package dev.italiansdelight.common.worldgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.italiansdelight.common.registry.ModBlocks;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

/**
 * Almond crown: olive-like crooked trunk below, but foliage is much more
 * upright than the OLIVE dome. The multi-layer canopy is fairly narrow at the
 * shoulders; three short upward leaf sprays merge into the crown shoulders.
 * Every planned leaf must connect to real almond wood through <=6 face
 * steps, respecting vanilla leaves decay/distance mechanics.
 */
public final class AlmondFoliagePlacer extends FoliagePlacer {
    public static final MapCodec<AlmondFoliagePlacer> CODEC =
        RecordCodecBuilder.mapCodec(instance ->
            foliagePlacerParts(instance).apply(instance, AlmondFoliagePlacer::new));

    public AlmondFoliagePlacer(IntProvider radius, IntProvider offset) {
        super(radius, offset);
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return ModTreePlacers.ALMOND_FOLIAGE;
    }

    @Override
    public int foliageHeight(RandomSource random, int treeHeight, TreeConfiguration config) {
        return 7;
    }

    @Override
    protected boolean shouldSkipLocation(
        RandomSource random, int dx, int y, int dz, int radius, boolean doubleTrunk
    ) {
        return dx * dx + dz * dz > radius * radius + 1;
    }

    @Override
    protected void createFoliage(
        WorldGenLevel level,
        FoliageSetter setter,
        RandomSource random,
        TreeConfiguration config,
        int treeHeight,
        FoliageAttachment attachment,
        int foliageHeight,
        int leafRadius,
        int offset
    ) {
        // Attachment is one block above the trunk tip (as on olive trees).
        BlockPos anchor = attachment.pos().above(offset).below();
        double scale = Math.max(0.85D, leafRadius / 2.0D);
        Set<BlockPos> planned = new LinkedHashSet<>();

        // Begin the canopy at EXACTLY the same relative lower height as
        // OliveFoliagePlacer: -2 on tall trunks, -1 on shorter trunks.
        // Ignore existing Almond wood and foliage while finding terrain.
        int groundY = anchor.getY() - 1;
        while (groundY > level.getMinY()) {
            BlockPos check = new BlockPos(anchor.getX(), groundY, anchor.getZ());
            BlockState state = level.getBlockState(check);
            if (!state.isAir() && !state.canBeReplaced()
                && !state.is(ModBlocks.ALMOND_LEAVES) && !isAlmondWood(state)) {
                break;
            }
            groundY--;
        }
        int trunkHeightAboveGround = anchor.getY() - groundY;
        int minDy = trunkHeightAboveGround >= 5 ? -2 : -1;

        // ONLY extend the skirt downwards. The previously approved radii
        // from dy=-1 through dy=+4 and the top height (+4) are unchanged.
        double[] radii = {1.95D, 1.70D, 2.35D, 2.45D, 2.00D, 1.45D, 0.85D};
        for (int dy = minDy; dy <= 4; dy++) {
            if (anchor.getY() + dy - groundY < 3) {
                continue; // Retain the olive's two-block walking clearance.
            }
            double r = radii[dy + 2] * scale;
            double jitterX = (random.nextDouble() - 0.5D) * 0.30D;
            double jitterZ = (random.nextDouble() - 0.5D) * 0.30D;

            for (int dx = -3; dx <= 3; dx++) {
                for (int dz = -3; dz <= 3; dz++) {
                    double distance = Math.hypot(dx - jitterX, dz - jitterZ);
                    if (distance > r) {
                        continue;
                    }
                    // Open gaps only on the outer rim, not inside foliage.
                    if (distance > r - 0.30D && random.nextFloat() < 0.24F) {
                        continue;
                    }
                    BlockPos pos = anchor.offset(dx, dy, dz);
                    if (clearBelow(level, pos)) {
                        planned.add(pos);
                    }
                }
            }
        }

        // Keep the approved upright Almond silhouette, but NO tall leaf
        // pillars: the original shoots filled y=1..4 in the same x/z
        // position and made four-block-high artificial columns.
        // The tapered crown above already provides the overall height.
        // Each fringe twig now contains at most TWO vertically adjacent
        // foliage blocks, anchored on the crown's natural shoulders.
        Direction first = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        for (int arm = 0; arm < 3; arm++) {
            Direction d = first;
            for (int turn = 0; turn < arm; turn++) {
                d = d.getClockWise();
            }
            int dx = d.getStepX() * 2;
            int dz = d.getStepZ() * 2;
            for (int dy = 2; dy <= 3; dy++) {
                BlockPos shoot = anchor.offset(dx, dy, dz);
                if (clearBelow(level, shoot)) {
                    planned.add(shoot);
                }
                if (dy == 2 && random.nextBoolean()) {
                    // A small lateral whorl softens the narrow spike.
                    Direction side = d.getClockWise();
                    BlockPos leaf = shoot.relative(side);
                    if (clearBelow(level, leaf)) {
                        planned.add(leaf);
                    }
                }
            }
        }
        // The core crown's last taper is the highest point. Do not add
        // a separate leaf at y+5: it looked like a lone vertical antenna.

        // A curved Almond stem may lean one or two blocks between the
        // lower fork and the final tip. A canopy centered only on that tip
        // leaves naked wooden elbows (visible from below in planted trees).
        // Follow the ACTUAL upper wood rather than guessing which side the
        // randomly rotated trunk bends towards. This adds a small leafy
        // collar to the upper branch segments, not a second low tree crown.
        for (int dy = -3; dy <= -1; dy++) {
            int y = anchor.getY() + dy;
            if (y - groundY < 3) {
                continue; // The olive/almond two-block ground clearance.
            }
            for (int dx = -3; dx <= 3; dx++) {
                for (int dz = -3; dz <= 3; dz++) {
                    BlockPos wood = anchor.offset(dx, dy, dz);
                    if (!isAlmondWood(level.getBlockState(wood))) {
                        continue;
                    }
                    for (Direction side : Direction.Plane.HORIZONTAL) {
                        BlockPos leaf = wood.relative(side);
                        // Prevent a nearby tree or outlying branch from
                        // widening the outline beyond the approved canopy.
                        if (Math.abs(leaf.getX() - anchor.getX()) > 3
                            || Math.abs(leaf.getZ() - anchor.getZ()) > 3) {
                            continue;
                        }
                        if (clearBelow(level, leaf)) {
                            planned.add(leaf); // Face-adjacent to REAL logs.
                        }
                    }
                }
            }
        }

        // A complete connectivity and Minecraft leaves-distance check:
        // no floating wisps, and tips at >6 steps from actual logs decay.
        Map<BlockPos, Integer> distanceFromWood = new HashMap<>();
        Deque<BlockPos> queue = new ArrayDeque<>();
        for (BlockPos pos : planned) {
            for (Direction direction : Direction.values()) {
                BlockState neighbor = level.getBlockState(pos.relative(direction));
                if (isAlmondWood(neighbor)) {
                    distanceFromWood.put(pos, 1);
                    queue.addLast(pos);
                    break;
                }
            }
        }

        while (!queue.isEmpty()) {
            BlockPos pos = queue.removeFirst();
            int nextDistance = distanceFromWood.get(pos) + 1;
            if (nextDistance > 6) {
                continue;
            }
            for (Direction direction : Direction.values()) {
                BlockPos next = pos.relative(direction);
                if (planned.contains(next) && !distanceFromWood.containsKey(next)) {
                    distanceFromWood.put(next, nextDistance);
                    queue.addLast(next);
                }
            }
        }
        for (BlockPos pos : planned) {
            if (distanceFromWood.containsKey(pos)) {
                tryPlaceLeaf(level, setter, random, config, pos);
            }
        }
    }

    private static boolean isAlmondWood(BlockState state) {
        return state.is(ModBlocks.ALMOND_LOG) || state.is(ModBlocks.ALMOND_WOOD);
    }

    private static boolean clearBelow(WorldGenLevel level, BlockPos pos) {
        // Keep two free spaces under hanging foliage near sloped terrain.
        for (int n = 1; n <= 2; n++) {
            BlockState below = level.getBlockState(pos.below(n));
            if (!below.isAir() && !below.canBeReplaced()
                && !below.is(ModBlocks.ALMOND_LEAVES) && !isAlmondWood(below)) {
                return false;
            }
        }
        return true;
    }
}
