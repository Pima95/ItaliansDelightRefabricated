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
 * Broad, irregular olive crowns built from overlapping flattened lobes.
 * The silhouette is deliberately wider than it is tall, with an uneven
 * outline and occasional gaps rather than a solid cherry-like sphere.
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
        return 3;
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
        // Each trunk attachment gets its own crown lobe. A small horizontal
        // jitter keeps the outline from looking mechanically centred.
        BlockPos center = attachment.pos().above(offset).offset(
            random.nextInt(3) - 1, 0, random.nextInt(3) - 1);

        // The configured radius is 2; expand it to a 3-4 block long axis,
        // while keeping the short axis narrow for an olive-like silhouette.
        int crownSize = Math.max(2, Math.min(3,
            leafRadius + (random.nextBoolean() ? 0 : 1)));
        int longRadius = crownSize + 1;
        int shortRadius = Math.max(1, crownSize - 1);
        boolean longAxisX = random.nextBoolean();

        placeLobe(level, foliageSetter, random, config, center,
            longRadius, shortRadius, longAxisX);

        // Olive crowns grow in uneven clumps. Add one or two smaller lobes
        // on the same randomly chosen side, rather than a symmetric ring.
        int satelliteCount = 1 + random.nextInt(2);
        int side = random.nextBoolean() ? 1 : -1;
        for (int i = 0; i < satelliteCount; i++) {
            int along = side * (Math.max(1, longRadius - 1) + i);
            int across = random.nextInt(3) - 1;
            int vertical = random.nextInt(3) - 1;
            BlockPos satellite = center.offset(
                longAxisX ? along : across,
                vertical,
                longAxisX ? across : along);

            int satelliteLong = random.nextInt(4) == 0 ? 2 : 1;
            placeLobe(level, foliageSetter, random, config, satellite,
                satelliteLong, 1, longAxisX);
        }
    }

    /**
     * Place a flattened, elliptical cluster with a ragged outline.
     * The middle layer is widest; upper and lower layers taper unevenly.
     * Sparse omissions create small openings without making the crown bare.
     */
    private void placeLobe(
        WorldGenLevel level,
        FoliageSetter foliageSetter,
        RandomSource random,
        TreeConfiguration config,
        BlockPos center,
        int longRadius,
        int shortRadius,
        boolean longAxisX
    ) {
        for (int dy = -1; dy <= 1; dy++) {
            int layerLong = dy == 0 ? longRadius : Math.max(1, longRadius - 1);
            int layerShort = dy == 1
                ? Math.max(1, shortRadius - 1) : shortRadius;

            // Small layer-to-layer drift avoids flat, rectangular undersides.
            int driftX = dy == 0 ? 0 : random.nextInt(3) - 1;
            int driftZ = dy == 0 ? 0 : random.nextInt(3) - 1;
            BlockPos layerCenter = center.offset(driftX, 0, driftZ);

            for (int dx = -layerLong; dx <= layerLong; dx++) {
                for (int dz = -layerLong; dz <= layerLong; dz++) {
                    int along = longAxisX ? dx : dz;
                    int across = longAxisX ? dz : dx;
                    if (Math.abs(along) > layerLong || Math.abs(across) > layerShort) {
                        continue;
                    }

                    double ellipse = (double) (along * along)
                        / (layerLong * layerLong + 0.5)
                        + (double) (across * across)
                        / (layerShort * layerShort + 0.5);
                    if (ellipse > 1.18D) {
                        continue;
                    }

                    boolean edge = Math.abs(along) == layerLong
                        || Math.abs(across) == layerShort;
                    float skipChance = edge ? (dy == 0 ? 0.24F : 0.42F) : 0.055F;
                    if (random.nextFloat() < skipChance) {
                        continue;
                    }
                    if (dy == 1 && random.nextFloat() < 0.10F) {
                        continue;
                    }

                    tryPlaceLeaf(level, foliageSetter, random, config,
                        layerCenter.offset(dx, dy, dz));
                }
            }
        }
    }
}
