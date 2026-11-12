package dev.italiansdelight.common.block;

import dev.italiansdelight.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import vectorwing.farmersdelight.common.block.TomatoBlock;
import vectorwing.farmersdelight.refabricated.block.ReplaceOnRemoval;

/**
 * The actual Farmer's Delight tomato crop on a Rustic-style stake.
 * Its fruit, regrowth and upper-rope climbing use FD's original methods.
 * Breaking the crop removes the plant but leaves the reusable wooden stake.
 */
public final class StakedTomatoBlock extends TomatoBlock implements ReplaceOnRemoval {
    public StakedTomatoBlock(Properties properties) { super(properties); }

    @Override public boolean onRemove(BlockState state, BlockState newState,
            LevelAccessor level, BlockPos pos, int flags) {
        if (newState.isAir() && !state.is(newState.getBlock())) {
            return level.setBlock(pos, ModBlocks.CROP_STAKE.defaultBlockState(), 3);
        }
        return false;
    }
}
