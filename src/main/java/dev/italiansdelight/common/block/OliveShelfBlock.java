package dev.italiansdelight.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Small decorative wall shelf made of olive planks. This is NOT a cheese rack:
 * it has no block entity, inventory, food processing or item display logic.
 * The four shapes align to the wall-facing blockstate/model rotations.
 */
public final class OliveShelfBlock extends Block {
    private static final MapCodec<OliveShelfBlock> CODEC = simpleCodec(OliveShelfBlock::new);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    // Model origin faces north: the backboard is close to the southern wall.
    private static final VoxelShape NORTH = Shapes.or(
        box(1, 9, 3, 15, 11, 16), // Main plank
        box(1, 7, 14, 15, 13, 16), // Backboard
        box(2, 5, 11, 4, 9, 14), // Brackets
        box(12, 5, 11, 14, 9, 14)
    ).optimize();
    private static final VoxelShape SOUTH = Shapes.or(
        box(1, 9, 0, 15, 11, 13),
        box(1, 7, 0, 15, 13, 2),
        box(2, 5, 2, 4, 9, 5),
        box(12, 5, 2, 14, 9, 5)
    ).optimize();
    private static final VoxelShape EAST = Shapes.or(
        box(0, 9, 1, 13, 11, 15),
        box(0, 7, 1, 2, 13, 15),
        box(2, 5, 2, 5, 9, 4),
        box(2, 5, 12, 5, 9, 14)
    ).optimize();
    private static final VoxelShape WEST = Shapes.or(
        box(3, 9, 1, 16, 11, 15),
        box(14, 7, 1, 16, 13, 15),
        box(11, 5, 2, 14, 9, 4),
        box(11, 5, 12, 14, 9, 14)
    ).optimize();

    public OliveShelfBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<OliveShelfBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction clicked = context.getClickedFace();
        Direction facing = clicked.getAxis().isHorizontal()
            ? clicked : context.getHorizontalDirection().getOpposite();
        BlockState candidate = defaultBlockState().setValue(FACING, facing);
        return candidate.canSurvive(context.getLevel(), context.getClickedPos())
            ? candidate : null;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        BlockPos wall = pos.relative(facing.getOpposite());
        return level.getBlockState(wall).isFaceSturdy(level, wall, facing);
    }

    @Override
    protected BlockState updateShape(
        BlockState state, LevelReader level, ScheduledTickAccess ticks,
        BlockPos pos, Direction changedDirection, BlockPos neighborPos,
        BlockState neighborState, RandomSource random
    ) {
        if (changedDirection == state.getValue(FACING).getOpposite()
            && !canSurvive(state, level, pos)) {
            // Defer removal to tick so that vanilla breaking drops the shelf.
            ticks.scheduleTick(pos, this, 1);
        }
        return super.updateShape(
            state, level, ticks, pos, changedDirection, neighborPos, neighborState, random
        );
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!canSurvive(state, level, pos)) {
            level.destroyBlock(pos, true);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return rotate(state, mirror.getRotation(state.getValue(FACING)));
    }

    private static VoxelShape shape(Direction facing) {
        return switch (facing) {
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
            default -> NORTH;
        };
    }

    @Override
    protected VoxelShape getShape(
        BlockState state, BlockGetter level, BlockPos pos, CollisionContext context
    ) {
        return shape(state.getValue(FACING));
    }

    @Override
    protected VoxelShape getCollisionShape(
        BlockState state, BlockGetter level, BlockPos pos, CollisionContext context
    ) {
        return shape(state.getValue(FACING));
    }
}
