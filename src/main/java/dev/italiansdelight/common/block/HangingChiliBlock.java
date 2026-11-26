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
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import vectorwing.farmersdelight.common.block.RopeBlock;

/**
 * FDR HangingTomatoBlock equivalent. No duplicate rope: a chili replaces
 * one real FDR rope while growing, then optionally restores the same rope
 * when broken, following FDR's own permanence setting.
 */
public final class HangingChiliBlock extends ChiliCropBlock {
    public HangingChiliBlock(Properties properties) {
        super(properties, true);
        registerDefaultState(stateDefinition.any().setValue(CHILI_AGE, 0));
    }


    // Reuse FDR TomatoBlock#getShape for hanging peppers as well.

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
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        playerWillDestroy(level, pos, state, player);
        return restoreRope(level, pos);
    }

    @Override public void onRemove(BlockState state, Level level, BlockPos pos, BlockState next, boolean movedByPiston) {
        super.onRemove(state, level, pos, next, movedByPiston);
        if (Configuration.ENABLE_TOMATO_ROPE_PERMANENCE.get() && !movedByPiston
                && !state.is(next.getBlock()) && next.isAir()) restoreRope(level, pos);
    }

    private boolean restoreRope(Level level, BlockPos pos) {
        Block rope = BuiltInRegistries.BLOCK.getOptional(ResourceLocation.parse(Configuration.DEFAULT_TOMATO_VINE_ROPE.get()))
            .orElse(vectorwing.farmersdelight.common.registry.ModBlocks.ROPE.get());
        BlockState state = rope == vectorwing.farmersdelight.common.registry.ModBlocks.ROPE.get()
            ? RopeBlock.getStateWithConnections(rope.defaultBlockState(), level, pos, Direction.UP)
            : rope.defaultBlockState();
        return level.setBlock(pos, state, level.isClientSide ? 11 : 3);
    }
}
