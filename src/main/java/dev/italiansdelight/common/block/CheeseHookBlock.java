package dev.italiansdelight.common.block;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Ceiling-mounted cheese hook.
 *
 * The curved side faces the player when the block is placed.
 */
public final class CheeseHookBlock extends HorizontalDirectionalBlock {

    public static final MapCodec<CheeseHookBlock> CODEC =
        simpleCodec(CheeseHookBlock::new);

    private static final VoxelShape NORTH_SHAPE =
        Shapes.or(
            Block.box(5, 14, 5, 11, 16, 11),
            Block.box(7, 8, 7, 9, 14, 9),
            Block.box(7, 6, 4, 9, 8, 9),
            Block.box(7, 7, 4, 9, 11, 6)
        );

    private static final VoxelShape EAST_SHAPE =
        Shapes.or(
            Block.box(5, 14, 5, 11, 16, 11),
            Block.box(7, 8, 7, 9, 14, 9),
            Block.box(7, 6, 7, 12, 8, 9),
            Block.box(10, 7, 7, 12, 11, 9)
        );

    private static final VoxelShape SOUTH_SHAPE =
        Shapes.or(
            Block.box(5, 14, 5, 11, 16, 11),
            Block.box(7, 8, 7, 9, 14, 9),
            Block.box(7, 6, 7, 9, 8, 12),
            Block.box(7, 7, 10, 9, 11, 12)
        );

    private static final VoxelShape WEST_SHAPE =
        Shapes.or(
            Block.box(5, 14, 5, 11, 16, 11),
            Block.box(7, 8, 7, 9, 14, 9),
            Block.box(4, 6, 7, 9, 8, 9),
            Block.box(4, 7, 7, 6, 11, 9)
        );

    public CheeseHookBlock(Properties properties) {
        super(properties);

        registerDefaultState(
            stateDefinition.any()
                .setValue(
                    FACING,
                    Direction.NORTH
                )
        );
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockState getStateForPlacement(
        BlockPlaceContext context
    ) {
        return defaultBlockState()
            .setValue(
                FACING,
                context.getHorizontalDirection()
                    .getOpposite()
            );
    }

    @Override
    protected void createBlockStateDefinition(
        StateDefinition.Builder<Block, BlockState> builder
    ) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Override
    protected VoxelShape getShape(
        BlockState state,
        BlockGetter level,
        BlockPos pos,
        CollisionContext context
    ) {
        return switch (state.getValue(FACING)) {
            case EAST -> EAST_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            default -> NORTH_SHAPE;
        };
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
