package dev.italiansdelight.common.worldgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.italiansdelight.common.registry.ModBlocks;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashSet;
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
 * Broad ancient olive foliage: five individually rounded lobes around the
 * branches, with soft taper, irregular edges and occasional small openings.
 * Its underside has cherry-like, shallow scallops: some rim sections recede
 * and a few leaf tufts hang one block lower. The crown now reaches at most
 * two blocks sideways from each limb, rather than three; its vertical layers
 * and the existing normal-olive canopy remain unchanged.
 */
public final class AncientOliveFoliagePlacer extends FoliagePlacer {
    // Maximum horizontal reach of each foliage lobe, in block coordinates.
    private static final int MAX_HORIZONTAL_REACH = 2;

    public static final MapCodec<AncientOliveFoliagePlacer> CODEC =
        RecordCodecBuilder.mapCodec(instance ->
            foliagePlacerParts(instance).apply(instance, AncientOliveFoliagePlacer::new));

    public AncientOliveFoliagePlacer(IntProvider radius, IntProvider offset) {
        super(radius, offset);
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return ModTreePlacers.ANCIENT_OLIVE_FOLIAGE;
    }

    @Override
    public int foliageHeight(RandomSource random, int treeHeight, TreeConfiguration config) {
        return 5;
    }

    @Override
    protected boolean shouldSkipLocation(
        RandomSource random, int dx, int y, int dz, int currentRadius, boolean doubleTrunk
    ) {
        return dx * dx + dz * dz > currentRadius * currentRadius;
    }

    @Override
    protected void createFoliage(
        WorldGenLevel level, FoliageSetter foliageSetter, RandomSource random,
        TreeConfiguration config, int treeHeight, FoliageAttachment attachment,
        int foliageHeight, int leafRadius, int offset
    ) {
        BlockPos anchor = attachment.pos().above(offset).below();
        double scale = Math.max(1.0D, leafRadius / 2.0D);
        double phase = random.nextDouble() * Math.PI * 2.0D;
        int frequency = 3 + random.nextInt(2);
        double wave = 0.11D + 0.06D * random.nextDouble();
        Set<BlockPos> planned = new LinkedHashSet<>();

        for (int dy = -2; dy <= 2; dy++) {
            // Keep the same five vertical levels and overlapping olive lobes,
            // while reducing their sideways thickness by roughly one block.
            double baseRadius;
            if (dy == -2) {
                baseRadius = 1.90D;
            } else if (dy == -1) {
                baseRadius = 2.20D;
            } else if (dy == 0) {
                baseRadius = 2.60D;
            } else if (dy == 1) {
                baseRadius = 2.20D;
            } else {
                baseRadius = 1.50D;
            }
            baseRadius = baseRadius * scale + random.nextDouble() * 0.14D - 0.07D;
            double jitterX = random.nextDouble() * 0.30D - 0.15D;
            double jitterZ = random.nextDouble() * 0.30D - 0.15D;

            // Hard limit: never exceed two blocks from the attachment along
            // either horizontal axis, even with the organic radial waves.
            for (int dx = -MAX_HORIZONTAL_REACH; dx <= MAX_HORIZONTAL_REACH; dx++) {
                for (int dz = -MAX_HORIZONTAL_REACH; dz <= MAX_HORIZONTAL_REACH; dz++) {
                    double x = dx - jitterX;
                    double z = dz - jitterZ;
                    double distance = Math.sqrt(x * x + z * z);
                    if (distance > baseRadius + 0.5D) {
                        continue;
                    }
                    double angle = distance > 0.0D ? Math.atan2(z, x) : 0.0D;
                    double localRadius = baseRadius
                        * (1.0D + wave * Math.cos(frequency * angle + phase));
                    if (distance > localRadius) {
                        continue;
                    }

                    // Only on the UNDERSIDE, carve shallow scallops along
                    // the outer rim. The crown's middle and top are unchanged.
                    // Alternating angular sectors leave some gaps and some
                    // lower foliage, inspired by vanilla cherry leaf shapes.
                    if (dy == -2 && distance > baseRadius * 0.62D
                        && Math.sin(4.0D * angle + phase) < -0.25D
                        && random.nextFloat() < 0.82F) {
                        continue;
                    }

                    BlockPos leaf = anchor.offset(dx, dy, dz);
                    BlockState existing = level.getBlockState(leaf);
                    if (existing.is(ModBlocks.OLIVE_LOG)
                        || existing.is(ModBlocks.OLIVE_WOOD)
                        || !hasGroundClearance(level, leaf)) {
                        continue;
                    }

                    boolean rim = distance > localRadius - 0.55D;
                    if (rim && random.nextFloat() < (dy == 2 ? 0.28F : 0.15F)) {
                        continue;
                    }
                    planned.add(leaf);
                }
            }
        }

        // A few underside tufts hang exactly one block below an existing
        // lower-rim leaf. Use the opposite angular sectors from the gaps,
        // avoiding a uniform fringe, curtains or a flat lower disk.
        // Collect separately to avoid mutating the set during iteration.
        Set<BlockPos> hanging = new LinkedHashSet<>();
        for (BlockPos leaf : planned) {
            if (leaf.getY() != anchor.getY() - 2) {
                continue;
            }
            int dx = leaf.getX() - anchor.getX();
            int dz = leaf.getZ() - anchor.getZ();
            double radial = Math.sqrt(dx * dx + dz * dz);
            if (radial < 1.45D * scale) {
                continue; // Leave the central wood and clear underside intact.
            }
            double angle = Math.atan2(dz, dx);
            if (Math.sin(4.0D * angle + phase) <= 0.30D
                || random.nextFloat() >= 0.35F) {
                continue;
            }
            BlockPos tip = leaf.below(); // Maximum one extra downward block.
            BlockState state = level.getBlockState(tip);
            if (!hasGroundClearance(level, tip)
                || (!state.isAir() && !state.canBeReplaced())) {
                continue; // Never replace terrain, trunks or obstacles.
            }
            hanging.add(tip);
        }
        planned.addAll(hanging);

        // Breadth-first traversal from real wood, so thin detached islands
        // are never generated as the randomness cuts gaps at crown edges.
        Deque<BlockPos> frontier = new ArrayDeque<>();
        Set<BlockPos> connected = new HashSet<>();
        for (BlockPos leaf : planned) {
            for (Direction direction : Direction.values()) {
                BlockState neighbor = level.getBlockState(leaf.relative(direction));
                if (neighbor.is(ModBlocks.OLIVE_LOG) || neighbor.is(ModBlocks.OLIVE_WOOD)) {
                    if (connected.add(leaf)) {
                        frontier.addLast(leaf);
                    }
                    break;
                }
            }
        }
        while (!frontier.isEmpty()) {
            BlockPos leaf = frontier.removeFirst();
            for (Direction direction : Direction.values()) {
                BlockPos neighbor = leaf.relative(direction);
                if (planned.contains(neighbor) && connected.add(neighbor)) {
                    frontier.addLast(neighbor);
                }
            }
        }
        for (BlockPos leaf : planned) {
            if (connected.contains(leaf)) {
                tryPlaceLeaf(level, foliageSetter, random, config, leaf);
            }
        }
    }

    private static boolean hasGroundClearance(WorldGenLevel level, BlockPos position) {
        for (int down = 1; down <= 2; down++) {
            BlockState below = level.getBlockState(position.below(down));
            if (!below.isAir() && !below.canBeReplaced()
                && !below.is(ModBlocks.OLIVE_LEAVES)
                && !below.is(ModBlocks.OLIVE_LOG)
                && !below.is(ModBlocks.OLIVE_WOOD)) {
                return false;
            }
        }
        return true;
    }
}
