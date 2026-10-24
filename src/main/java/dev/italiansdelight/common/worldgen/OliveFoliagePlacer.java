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
 * Small, flattened and irregular leaf clusters around every branch tip.
 * Olive leaves are already silver-green, so no new textures are needed.
 * The trunk guarantees elevated attachment points (not ground level).
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
        // Keep the outlines irregular rather than filling a perfect square.
        int dist = dx * dx + dz * dz;
        if (dist > currentRadius * currentRadius + 1) {
            return true;
        }
        return dist >= currentRadius * currentRadius && random.nextFloat() < 0.55F;
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
        BlockPos center = attachment.pos().above(offset).offset(
            random.nextInt(3) - 1, 0, random.nextInt(3) - 1);
        int crownRadius = Math.max(1, leafRadius - (random.nextInt(3) == 0 ? 1 : 0));
        // Compact, flattened lobes at the tips of two distinct rising limbs.
        // Narrow upper/lower layers keep the silhouette open, not cherry-like.
        for (int dy = -1; dy <= 1; dy++) {
            // Taper both faces of each crown; avoid one continuous flat
            // underside connecting the two separately rising primary arms.
            int radius = dy == 0 ? crownRadius : Math.max(1, crownRadius - 1);
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (shouldSkipLocation(random, dx, dy, dz, radius, false)
                        || (dy != 0 && random.nextFloat() < 0.12F)) {
                        continue;
                    }
                    if (dy == -1 && Math.abs(dx) + Math.abs(dz) >= radius + 1
                        && random.nextFloat() < 0.4F) {
                        continue;
                    }
                    tryPlaceLeaf(level, foliageSetter, random, config,
                        center.offset(dx, dy, dz));
                }
            }
        }
    }
}
