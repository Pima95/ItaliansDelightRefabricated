package dev.italiansdelight.common.block;

import dev.italiansdelight.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import vectorwing.farmersdelight.common.block.TomatoBlock;
import vectorwing.farmersdelight.refabricated.block.ReplaceOnRemoval;

/**
 * The actual Farmer's Delight tomato crop on a Rustic-style stake.
 * Its fruit, regrowth and upper-rope climbing use FD's original methods.
 * Breaking the crop removes the plant but leaves the reusable wooden stake.
 */
public final class StakedTomatoBlock extends TomatoBlock implements ReplaceOnRemoval {
    public StakedTomatoBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
            .setValue(VINE_AGE, 0).setValue(CropStakeBlock.ROPE_TIED, false));
    }

    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CropStakeBlock.ROPE_TIED);
    }

    @Override public VoxelShape getCollisionShape(BlockState state, BlockGetter level,
            BlockPos pos, CollisionContext context) {
        return CropStakeBlock.POST;
    }

    // FDR's TomatoBlock already handles maturity, harvesting and upper
    // rope-bearing segments. Add just the wooden-stake type as a further
    // permissible upward support; each newly grown section begins at age 0.
    @Override public boolean canClimbBlock(BlockState stateAbove) {
        return stateAbove.is(ModBlocks.CROP_STAKE) || super.canClimbBlock(stateAbove);
    }

    @Override public BlockState getClimbingState(BlockState stateAbove) {
        if (stateAbove.is(ModBlocks.CROP_STAKE)) {
            return defaultBlockState().setValue(VINE_AGE, 0)
                .setValue(CropStakeBlock.ROPE_TIED,
                    stateAbove.getValue(CropStakeBlock.ROPE_TIED));
        }
        return super.getClimbingState(stateAbove);
    }

    // Do not call the FDR tick that destroys the entire block when farmland
    // disappears. Recover the reusable wooden post, dropping only the crop.
    @Override public void tick(BlockState state, ServerLevel level, BlockPos pos,
            RandomSource random) {
        if (!state.canSurvive(level, pos)) {
            Block.dropResources(state, level, pos);
            level.setBlock(pos, ModBlocks.CROP_STAKE.defaultBlockState()
                .setValue(CropStakeBlock.ROPE_TIED,
                    CropStakeBlock.isRopeTied(level, pos)), Block.UPDATE_ALL);
        }
    }

    @Override public BlockState updateShape(BlockState state, LevelReader level,
            ScheduledTickAccess ticks, BlockPos pos, Direction side,
            BlockPos neighborPos, BlockState neighbor, RandomSource random) {
        if (!state.canSurvive(level, pos)) {
            ticks.scheduleTick(pos, this, 1);
            return state;
        }
        return super.updateShape(state, level, ticks, pos, side, neighborPos, neighbor, random)
            .setValue(CropStakeBlock.ROPE_TIED, CropStakeBlock.isRopeTied(level, pos));
    }

    @Override public boolean onRemove(BlockState state, BlockState newState,
            LevelAccessor level, BlockPos pos, int flags) {
        if (newState.isAir() && !state.is(newState.getBlock())) {
            return level.setBlock(pos, ModBlocks.CROP_STAKE.defaultBlockState()
                .setValue(CropStakeBlock.ROPE_TIED, CropStakeBlock.isRopeTied(level,pos)), 3);
        }
        return false;
    }
}
