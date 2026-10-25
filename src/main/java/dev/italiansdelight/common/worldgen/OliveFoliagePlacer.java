package dev.italiansdelight.common.worldgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

/**
 * Natural Mediterranean olive crown generator.
 *
 * Real olive trees possess a broad, open umbrella- or bowl-like canopy
 * formed by overlapping cushions (cuscini) of silvery foliage. The canopy
 * is wider than it is tall, with a gently convex dome on top, lush foliage
 * enclosing the branch tips, and delicate drooping twiglets around the lower outer rim.
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
        BlockPos center = attachment.pos().above(offset);
        double radiusScale = Math.max(0.8D, leafRadius / 2.0D);

        // Define the organic multi-cluster model (cushions of foliage)
        List<FoliageCluster> clusters = new ArrayList<>(5);

        // 1. Central broad dome cluster, anchored over the limb
        double mainRx = (3.1D + (random.nextDouble() * 0.5D - 0.2D)) * radiusScale;
        double mainRz = (3.1D + (random.nextDouble() * 0.5D - 0.2D)) * radiusScale;
        double mainRy = 2.1D;
        clusters.add(new FoliageCluster(0.0D, 0.5D, 0.0D, mainRx, mainRy, mainRz));

        // 2. 3 to 4 secondary spreading cushions around the perimeter
        int numLobes = 3 + random.nextInt(2);
        double baseAngle = random.nextDouble() * 2.0D * Math.PI;
        for (int i = 0; i < numLobes; i++) {
            double angle = baseAngle + (i * 2.0D * Math.PI / numLobes)
                + (random.nextDouble() * 0.6D - 0.3D);
            double dist = (1.8D + random.nextDouble() * 0.6D) * radiusScale;
            double lx = Math.cos(angle) * dist;
            double lz = Math.sin(angle) * dist;
            double ly = random.nextDouble() * 0.8D - 0.2D;
            double lr = (1.9D + random.nextDouble() * 0.4D) * radiusScale;
            double lry = 1.4D + random.nextDouble() * 0.3D;
            clusters.add(new FoliageCluster(lx, ly, lz, lr, lry, lr));
        }

        Set<BlockPos> planned = new HashSet<>();

        // Generate canopy volume from dy = -2 to +3, dx/dz from -5 to +5
        for (int dy = -2; dy <= 3; dy++) {
            for (int dx = -5; dx <= 5; dx++) {
                for (int dz = -5; dz <= 5; dz++) {
                    double minDistanceSq = Double.MAX_VALUE;
                    for (FoliageCluster cluster : clusters) {
                        double dSq = cluster.distanceSq(dx, dy, dz);
                        if (dSq < minDistanceSq) {
                            minDistanceSq = dSq;
                        }
                    }

                    if (minDistanceSq <= 0.85D) {
                        // Dense, lush canopy core
                        planned.add(center.offset(dx, dy, dz));
                    } else if (minDistanceSq <= 1.05D) {
                        // Main canopy body with soft natural gaps
                        if (random.nextFloat() < 0.85F) {
                            planned.add(center.offset(dx, dy, dz));
                        }
                    } else if (minDistanceSq <= 1.25D) {
                        // Outer feathered boundary
                        float chance = (dy == 0 || dy == 1) ? 0.35F : 0.15F;
                        if (random.nextFloat() < chance) {
                            planned.add(center.offset(dx, dy, dz));
                        }
                    }
                }
            }
        }

        // Add delicate drooping branchlets at dy = -2 along the outer rim
        // (quintessential weeping habit of mature olive canopies)
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                BlockPos abovePos = center.offset(dx, -1, dz);
                BlockPos hangPos = center.offset(dx, -2, dz);
                if (planned.contains(abovePos) && !planned.contains(hangPos)) {
                    double distH = Math.sqrt(dx * dx + dz * dz);
                    if (distH >= 2.2D && distH <= 3.8D && random.nextFloat() < 0.30F) {
                        planned.add(hangPos);
                    }
                }
            }
        }

        // Place leaves, ensuring no disconnected floating singletons
        Direction[] directions = Direction.values();
        BlockPos limbTip = center.below();
        for (BlockPos pos : planned) {
            boolean connected = false;
            for (Direction dir : directions) {
                BlockPos neighbor = pos.relative(dir);
                if (neighbor.equals(limbTip) || planned.contains(neighbor)) {
                    connected = true;
                    break;
                }
            }
            if (connected) {
                tryPlaceLeaf(level, foliageSetter, random, config, pos);
            }
        }
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
