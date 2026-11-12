package dev.italiansdelight.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import vectorwing.farmersdelight.common.block.TomatoBlock;

/** Legacy save ID only: convert old stake/tomato blocks to ordinary FDR tomatoes. */
public final class StakedTomatoBlock extends TomatoBlock {
    public StakedTomatoBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(VINE_AGE, 0)
            .setValue(CropStakeBlock.ROPE_TIED, false));
    }

    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CropStakeBlock.ROPE_TIED);
    }

    @Override public VoxelShape getCollisionShape(BlockState state, BlockGetter level,
            BlockPos pos, CollisionContext context) { return Shapes.empty(); }

    @Override public void randomTick(BlockState state, ServerLevel level,
            BlockPos pos, RandomSource random) {
        level.setBlock(pos, vectorwing.farmersdelight.common.registry.ModBlocks.TOMATO_CROP.get()
            .defaultBlockState().setValue(VINE_AGE, state.getValue(VINE_AGE)), Block.UPDATE_ALL);
    }

    @Override public void tick(BlockState state, ServerLevel level,
            BlockPos pos, RandomSource random) {
        if (!state.canSurvive(level, pos)) level.destroyBlock(pos, true);
        else randomTick(state, level, pos, random);
    }
}
