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
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

/**
 * Rounded fruit-cherry canopy on the shared Olive/Almond crooked trunk,
 * significantly broader than Almond's narrow crown while avoiding tall leaf
 * spikes. Leafy shoulders cover real boughs, and only blocks within six
 * face-steps of Fruit Cherry wood survive.
 * No fruit appears until the shared fruiting phase.
 */
public final class FruitCherryFoliagePlacer extends FoliagePlacer {
    public static final MapCodec<FruitCherryFoliagePlacer> CODEC =
        RecordCodecBuilder.mapCodec(instance ->
            foliagePlacerParts(instance).apply(instance, FruitCherryFoliagePlacer::new));

    public FruitCherryFoliagePlacer(IntProvider radius, IntProvider offset) {
        super(radius, offset);
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return ModTreePlacers.FRUIT_CHERRY_FOLIAGE;
    }

    @Override
    public int foliageHeight(RandomSource random, int treeHeight, TreeFeature config) {
        return 6;
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
        TreeFeature config,
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
        // Ignore existing FruitCherry wood and foliage while finding terrain.
        int groundY = anchor.getY() - 1;
        while (groundY > level.getMinY()) {
            BlockPos check = new BlockPos(anchor.getX(), groundY, anchor.getZ());
            BlockState state = level.getBlockState(check);
            if (!state.isAir() && !state.canBeReplaced()
                && !state.is(ModBlocks.FRUIT_CHERRY_LEAVES) && !isFruitCherryWood(state)) {
                break;
            }
            groundY--;
        }
        int trunkHeightAboveGround = anchor.getY() - groundY;
        int minDy = trunkHeightAboveGround >= 5 ? -2 : -1;

        // Keep the approved trunk, vertical profile and walking clearance.
        // Grow a broader rounded crown: the middle layer reaches radius 4.10
        // versus Almond's 2.45, with shoulder layers tapered around it.
        // The scan MUST include the new outer ring; a fixed 7x7 grid
        // previously clipped radius 3.00+ and concealed visual changes.
        double[] radii = {2.55D, 2.70D, 3.65D, 4.10D, 3.50D, 2.55D};
        final int canopyExtent = 5; // Covers even a mildly scaled 4.1-radius ring.
        for (int dy = minDy; dy <= 3; dy++) {
            if (anchor.getY() + dy - groundY < 3) {
                continue; // Retain the olive's two-block walking clearance.
            }
            double r = radii[dy + 2] * scale;
            double jitterX = (random.nextDouble() - 0.5D) * 0.30D;
            double jitterZ = (random.nextDouble() - 0.5D) * 0.30D;

            for (int dx = -canopyExtent; dx <= canopyExtent; dx++) {
                for (int dz = -canopyExtent; dz <= canopyExtent; dz++) {
                    double distance = Math.hypot(dx - jitterX, dz - jitterZ);
                    if (distance > r) {
                        continue;
                    }
                    // Open gaps only on the outer rim, not inside foliage.
                    if (distance > r - 0.26D && random.nextFloat() < 0.18F) {
                        continue;
                    }
                    BlockPos pos = anchor.offset(dx, dy, dz);
                    if (clearBelow(level, pos)) {
                        planned.add(pos);
                    }
                }
            }
        }

        // Rounded canopy: no Almond-style upright leaf shoots or vertical
        // antennae. The natural shoulders and secondary bough shape come
        // from layered rings centered on the actual trunk attachments.

        // The shared crooked trunk bends twice before its main attachment.
        // Keep the wooden elbows naturally covered by a subtle leaf collar.
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
                    if (!isFruitCherryWood(level.getBlockState(wood))) {
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
                if (isFruitCherryWood(neighbor)) {
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

    private static boolean isFruitCherryWood(BlockState state) {
        return state.is(ModBlocks.FRUIT_CHERRY_LOG) || state.is(ModBlocks.FRUIT_CHERRY_WOOD);
    }

    private static boolean clearBelow(WorldGenLevel level, BlockPos pos) {
        // Keep two free spaces under hanging foliage near sloped terrain.
        for (int n = 1; n <= 2; n++) {
            BlockState below = level.getBlockState(pos.below(n));
            if (!below.isAir() && !below.canBeReplaced()
                && !below.is(ModBlocks.FRUIT_CHERRY_LEAVES) && !isFruitCherryWood(below)) {
                return false;
            }
        }
        return true;
    }
}
