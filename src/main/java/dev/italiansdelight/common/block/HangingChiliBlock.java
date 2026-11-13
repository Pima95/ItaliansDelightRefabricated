package dev.italiansdelight.common.block;

import dev.italiansdelight.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import vectorwing.farmersdelight.common.Configuration;
import vectorwing.farmersdelight.common.block.HangingTomatoBlock;
import vectorwing.farmersdelight.refabricated.block.ReplaceOnRemoval;

/**
 * FDR HangingTomatoBlock equivalent. No duplicate rope: a chili replaces
 * one real FDR rope while growing, then optionally restores the same rope
 * when broken, following FDR's own permanence setting.
 */
public final class HangingChiliBlock extends ChiliCropBlock implements ReplaceOnRemoval {
    public HangingChiliBlock(Properties properties) {
        super(properties, true);
        registerDefaultState(stateDefinition.any().setValue(CHILI_AGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        // Like HangingTomatoBlock: age ONLY. No ghost root/stake flags.
        builder.add(CHILI_AGE);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        return (below.is(ModBlocks.CHILI_CROP) || below.is(ModBlocks.CHILI_ON_ROPE))
            && hasGoodCropConditions(level, pos);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // FDR HangingTomatoBlock does not read ROPELOGGED (not a property of
        // hanging blocks). Its version would throw on an unsupported state.
        if (!state.canSurvive(level, pos)) level.destroyBlock(pos, true);
    }

    @Override
    public boolean onRemove(BlockState state, BlockState next,
            LevelAccessor level, BlockPos pos, int flags) {
        if (Configuration.ENABLE_TOMATO_ROPE_PERMANENCE.get()
                && (flags ^ Block.UPDATE_MOVE_BY_PISTON) != 0
                && !state.is(next.getBlock()) && next.isAir()) {
            return HangingTomatoBlock.placeRope(level, pos);
        }
        return true;
    }
}
