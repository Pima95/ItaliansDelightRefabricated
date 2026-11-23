package dev.italiansdelight.common.worldgen;

import com.mojang.serialization.MapCodec;
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
public final class WildGrapeFeature implements Feature {
    public static final MapCodec<WildGrapeFeature> RED_CODEC =
        MapCodec.unit(new WildGrapeFeature(false));
    public static final MapCodec<WildGrapeFeature> WHITE_CODEC =
        MapCodec.unit(new WildGrapeFeature(true));

    private final boolean white;

    private WildGrapeFeature(boolean white) { this.white = white; }

    @Override public MapCodec<? extends Feature> codec() {
        return white ? WHITE_CODEC : RED_CODEC;
    }

    @Override public boolean place(WorldGenLevel level, ChunkGenerator generator,
            RandomSource random, BlockPos pos) {
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
