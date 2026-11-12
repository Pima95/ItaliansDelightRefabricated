package dev.italiansdelight.common.block;

import dev.italiansdelight.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
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

    @Override public BlockState updateShape(BlockState state, LevelReader level,
            ScheduledTickAccess ticks, BlockPos pos, Direction side,
            BlockPos neighborPos, BlockState neighbor, RandomSource random) {
        if (!state.canSurvive(level, pos)) {
            ticks.scheduleTick(pos, this, 1);
            return state;
        }
        return super.updateShape(state, level, ticks, pos, side, neighborPos, neighbor, random);
    }

    @Override public boolean onRemove(BlockState state, BlockState newState,
            LevelAccessor level, BlockPos pos, int flags) {
        if (newState.isAir() && !state.is(newState.getBlock())) {
            return level.setBlock(pos, ModBlocks.CROP_STAKE.defaultBlockState(), 3);
        }
        return false;
    }
}
