package dev.italiansdelight.common.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import dev.italiansdelight.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;

/**
 * Mirrors the two-half placement of FDR WildRiceFeature, on land.
 * Vanilla simple_block does not invoke DoublePlantBlock.setPlacedBy.
 */
public final class WildGrapeFeature extends Feature<NoneFeatureConfiguration> {
    private final boolean white;

    public WildGrapeFeature(Codec<NoneFeatureConfiguration> codec, boolean white) {
        super(codec);
        this.white = white;
    }

    @Override public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos pos = context.origin();
        if (pos.getY() >= level.getMaxY() - 1
                || !level.getBlockState(pos).isAir()
                || !level.getBlockState(pos.above()).isAir()) return false;

        Block plant = white ? ModBlocks.WILD_WHITE_GRAPE_VINE : ModBlocks.WILD_GRAPE_VINE;
        BlockState lower = plant.defaultBlockState()
            .setValue(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER);
        if (!lower.canSurvive(level, pos)) return false;

        DoublePlantBlock.placeAt(level, lower, pos, 2);
        return true;
    }
}
