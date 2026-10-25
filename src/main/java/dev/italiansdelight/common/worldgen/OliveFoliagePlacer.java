package dev.italiansdelight.common.worldgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.italiansdelight.common.registry.ModBlocks;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
 * Natural Mediterranean olive crown generator.
 *
 * Real olive trees possess a light, airy umbrella- or bowl-like canopy
 * formed by soft cushions (cuscini) of silvery foliage. The canopy maintains
 * at least 2 clear blocks of clearance above ground level. If the trunk is tall
 * (6 blocks high), foliage can envelop down to the 3rd-last trunk block; on shorter
 * trees (4-5 blocks high), it envelops down at most to the 2nd-last block.
 */
public final class OliveFoliagePlacer extends FoliagePlacer {
    public static final MapCodec<OliveFoliagePlacer> CODEC =
        RecordCodecBuilder.mapCodec(instance ->
            foliagePlacerParts(instance).apply(instance, OliveFoliagePlacer::new));

    public OliveFoliagePlacer(IntProvider radius, IntProvider offset) {
        super(radius, offset);
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return ModTreePlacers.OLIVE_FOLIAGE;
    }

    @Override
    public int foliageHeight(RandomSource random, int treeHeight, TreeConfiguration config) {
        return 4;
    }

    @Override
    protected boolean shouldSkipLocation(
        RandomSource random, int dx, int y, int dz, int currentRadius, boolean doubleTrunk
    ) {
        int distance = dx * dx + dz * dz;
        if (distance > currentRadius * currentRadius + 1) {
            return true;
        }
        return distance >= currentRadius * currentRadius && random.nextFloat() < 0.55F;
    }

    @Override
    protected void createFoliage(
        WorldGenLevel level,
        FoliageSetter foliageSetter,
        RandomSource random,
        TreeConfiguration config,
        int treeHeight,
        FoliageAttachment attachment,
        int foliageHeight,
        int leafRadius,
        int offset
    ) {
        // Anchor directly on the topmost trunk log (mainTip)
        BlockPos anchor = attachment.pos().above(offset).below();
        double radiusScale = Math.max(0.8D, leafRadius / 2.0D);

        // Determine ground elevation under the tree to check trunk height and air clearance
        int groundY = anchor.getY() - 1;
        while (groundY > level.getMinY()) {
            BlockPos checkPos = new BlockPos(anchor.getX(), groundY, anchor.getZ());
            BlockState state = level.getBlockState(checkPos);
            if (!state.isAir() && !state.canBeReplaced()
                && !state.is(ModBlocks.OLIVE_LEAVES)
                && !state.is(ModBlocks.OLIVE_LOG)
                && !state.is(ModBlocks.OLIVE_WOOD)) {
                break;
            }
            groundY--;
        }
        int trunkHeightAboveGround = anchor.getY() - groundY;
        boolean isTallTrunk = trunkHeightAboveGround >= 5;

        // If trunk is 6 blocks tall, envelop down to terzultimo blocco (dy = -2).
        // Otherwise (4-5 blocks tall), envelop maximum down to secondo blocco (dy = -1).
        int minDy = isTallTrunk ? -2 : -1;

        // Light, airy olive canopy: slightly reduced horizontal width
        List<FoliageCluster> clusters = new ArrayList<>(4);

        double mainRx = (2.4D + (random.nextDouble() * 0.3D - 0.15D)) * radiusScale;
        double mainRz = (2.4D + (random.nextDouble() * 0.3D - 0.15D)) * radiusScale;
        double mainRy = isTallTrunk ? 2.0D : 1.7D;
        double centerY = isTallTrunk ? -0.3D : -0.1D;
        clusters.add(new FoliageCluster(0.0D, centerY, 0.0D, mainRx, mainRy, mainRz));

        // 3 secondary spreading cushions around the perimeter
        int numLobes = 3;
        double baseAngle = random.nextDouble() * 2.0D * Math.PI;
        for (int i = 0; i < numLobes; i++) {
            double angle = baseAngle + (i * 2.0D * Math.PI / numLobes)
                + (random.nextDouble() * 0.6D - 0.3D);
            double dist = (1.5D + random.nextDouble() * 0.4D) * radiusScale;
            double lx = Math.cos(angle) * dist;
            double lz = Math.sin(angle) * dist;
            double ly = centerY + (random.nextDouble() * 0.6D - 0.3D);
            double lr = (1.6D + random.nextDouble() * 0.3D) * radiusScale;
            double lry = 1.3D + random.nextDouble() * 0.3D;
            clusters.add(new FoliageCluster(lx, ly, lz, lr, lry, lr));
        }

        Set<BlockPos> planned = new HashSet<>();

        // Generate canopy volume from minDy to +2, dx/dz from -4 to +4
        for (int dy = minDy; dy <= 2; dy++) {
            // Strictly enforce at least 2 clear air blocks between ground and leaves
            if (anchor.getY() + dy - groundY < 3) {
                continue;
            }

            for (int dx = -4; dx <= 4; dx++) {
                for (int dz = -4; dz <= 4; dz++) {
                    BlockPos leafPos = anchor.offset(dx, dy, dz);
                    if (!hasGroundClearance(level, leafPos)) {
                        continue;
                    }

                    double minDistanceSq = Double.MAX_VALUE;
                    for (FoliageCluster cluster : clusters) {
                        double dSq = cluster.distanceSq(dx, dy, dz);
                        if (dSq < minDistanceSq) {
                            minDistanceSq = dSq;
                        }
                    }

                    if (minDistanceSq <= 0.70D) {
                        // Dense core
                        planned.add(leafPos);
                    } else if (minDistanceSq <= 0.95D) {
                        // Main canopy body with soft natural gaps
                        if (random.nextFloat() < 0.80F) {
                            planned.add(leafPos);
                        }
                    } else if (minDistanceSq <= 1.15D) {
                        // Outer boundary
                        float chance = (dy >= minDy && dy <= 0) ? 0.25F : 0.12F;
                        if (random.nextFloat() < chance) {
                            planned.add(leafPos);
                        }
                    }
                }
            }
        }

        // Place leaves, ensuring no disconnected floating singletons.
        // A leaf is connected if it touches the upper trunk logs or another placed leaf.
        Direction[] directions = Direction.values();
        BlockPos trunk1 = anchor;
        BlockPos trunk2 = anchor.below(1);
        BlockPos trunk3 = anchor.below(2);

        for (BlockPos pos : planned) {
            boolean connected = false;
            for (Direction dir : directions) {
                BlockPos neighbor = pos.relative(dir);
                if (neighbor.equals(trunk1) || neighbor.equals(trunk2)
                    || (isTallTrunk && neighbor.equals(trunk3))
                    || planned.contains(neighbor)) {
                    connected = true;
                    break;
                }
            }
            if (connected) {
                tryPlaceLeaf(level, foliageSetter, random, config, pos);
            }
        }
    }

    /**
     * Enforce at least 2 blocks of clear vertical clearance between the ground and leaves.
     * Both pos.below(1) and pos.below(2) must not be solid terrain/ground.
     */
    private static boolean hasGroundClearance(WorldGenLevel level, BlockPos pos) {
        for (int down = 1; down <= 2; down++) {
            BlockPos check = pos.below(down);
            BlockState state = level.getBlockState(check);
            if (!state.isAir() && !state.canBeReplaced()
                && !state.is(ModBlocks.OLIVE_LEAVES)
                && !state.is(ModBlocks.OLIVE_LOG)
                && !state.is(ModBlocks.OLIVE_WOOD)) {
                return false;
            }
        }
        return true;
    }

    private record FoliageCluster(
        double x,
        double y,
        double z,
        double rx,
        double ry,
        double rz
    ) {
        double distanceSq(double px, double py, double pz) {
            double dx = (px - x) / rx;
            double dy = (py - y) / ry;
            double dz = (pz - z) / rz;
            return dx * dx + dy * dy + dz * dz;
        }
    }
}
