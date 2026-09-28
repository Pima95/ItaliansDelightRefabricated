package dev.italiansdelight.common.block;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Ceiling-mounted cheese hook.
 *
 * This first implementation only provides placement, survival, model and
 * collision/selection shape. The hanging-cheese processing logic is added in a
 * later step.
 */
public final class CheeseHookBlock extends Block {

    public static final MapCodec<CheeseHookBlock> CODEC =
        simpleCodec(CheeseHookBlock::new);

    private static final VoxelShape SHAPE =
        Shapes.or(
            // Mounting plate touching the ceiling.
            Block.box(5, 14, 5, 11, 16, 11),

            // Vertical iron stem.
            Block.box(7, 8, 7, 9, 14, 9),

            // Bottom of the J curve.
            Block.box(7, 6, 7, 12, 8, 9),

            // Upturned tip of the hook.
            Block.box(10, 7, 7, 12, 11, 9)
        );

    public CheeseHookBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(
        BlockState state,
        BlockGetter level,
        BlockPos pos,
        CollisionContext context
    ) {
        return SHAPE;
    }

    @Override
    protected boolean canSurvive(
        BlockState state,
        LevelReader level,
        BlockPos pos
    ) {
        BlockPos supportPos =
            pos.above();

        return level.getBlockState(supportPos)
            .isFaceSturdy(
                level,
                supportPos,
                Direction.DOWN
            );
    }

    @Override
    protected BlockState updateShape(
        BlockState state,
        LevelReader level,
        ScheduledTickAccess ticks,
        BlockPos pos,
        Direction directionToNeighbour,
        BlockPos neighbourPos,
        BlockState neighbourState,
        RandomSource random
    ) {
        if (
            directionToNeighbour == Direction.UP
            && !canSurvive(state, level, pos)
        ) {
            return Blocks.AIR.defaultBlockState();
        }

        return super.updateShape(
            state,
            level,
            ticks,
            pos,
            directionToNeighbour,
            neighbourPos,
            neighbourState,
            random
        );
    }
}
