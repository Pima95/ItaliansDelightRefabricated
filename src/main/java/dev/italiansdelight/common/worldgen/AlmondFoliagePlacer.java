package dev.italiansdelight.common.worldgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

/**
 * A compact, tiered, asymmetrically rounded Almond crown. Unlike the olive
 * canopy it is slightly taller than it is broad, with a delicate pointed top.
 * Green foliage only; flowering and fruit maturity are later separate phases.
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
        return 5;
    }

    @Override
    protected boolean shouldSkipLocation(
        RandomSource random, int dx, int y, int dz, int currentRadius, boolean doubleTrunk
    ) {
        return dx * dx + dz * dz > currentRadius * currentRadius + 1;
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
        // The trunk attachment is one block above its tip.
        BlockPos center = attachment.pos().below().above(offset);
        double scale = Math.max(0.75D, leafRadius / 2.0D);
        for (int dy = -2; dy <= 2; dy++) {
            double r = switch (dy) {
                case -2 -> 1.45D;
                case -1 -> 2.25D;
                case  0 -> 2.80D;
                case  1 -> 2.30D;
                default -> 1.45D;
            };
            r *= scale;
            // Subtle drift only, to soften block-aligned, square silhouettes.
            double shiftX = (random.nextDouble() - 0.5D) * 0.26D;
            double shiftZ = (random.nextDouble() - 0.5D) * 0.26D;
            for (int dx = -3; dx <= 3; dx++) {
                for (int dz = -3; dz <= 3; dz++) {
                    double distance = Math.hypot(dx - shiftX, dz - shiftZ);
                    if (distance > r) {
                        continue;
                    }
                    if (distance > r - 0.35D && random.nextFloat() < 0.22F) {
                        continue; // Irregular tips, never random holes at core.
                    }
                    tryPlaceLeaf(level, setter, random, config, center.offset(dx, dy, dz));
                }
            }
        }
    }
}
