package dev.italiansdelight.common.block;

import dev.italiansdelight.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
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


    /**
     * A hanging chili consists of both a climbable rope visual and an
     * age-dependent crop. Preserve a narrow full-height selection for the
     * rope while expanding only the plant's selection as it grows.
     *
     * Cache shapes once to avoid rebuilding voxel unions every frame.
     */
    private static final VoxelShape ROPE_OUTLINE =
        Block.box(7, 0, 7, 9, 16, 9);
    private static final VoxelShape[] HANGING_SHAPES = createHangingShapes();

    private static VoxelShape[] createHangingShapes() {
        VoxelShape[] shapes = new VoxelShape[8];
        for (int age = 0; age < shapes.length; age++) {
            shapes[age] = Shapes.or(ROPE_OUTLINE, shapeForAge(age));
        }
        return shapes;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos,
            CollisionContext context) {
        return HANGING_SHAPES[state.getValue(CHILI_AGE)];
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
