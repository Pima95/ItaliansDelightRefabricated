package dev.italiansdelight.common.block;

import dev.italiansdelight.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

/** Replaces and restores a real FDR rope segment without creating a second rope. */
public final class HangingChiliBlock extends ChiliCropBlock {
    public HangingChiliBlock(Properties props) { super(props); }
    @Override protected boolean shouldRestoreRope() { return true; }
    @Override public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        return below.is(ModBlocks.CHILI_CROP) || below.is(ModBlocks.CHILI_ON_ROPE)
            || CropStakeBlock.hasCrop(below, CropStakeBlock.CHILI);
    }
}
