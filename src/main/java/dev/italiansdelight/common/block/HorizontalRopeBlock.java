package dev.italiansdelight.common.block;

import dev.italiansdelight.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Horizontal span from an ORIGINAL Farmer's Delight rope item.
 * Tracks posts on either end for optional visible rope-to-wood knots.
 * No duplicate rope item is registered, and breaking a span drops FDR rope.
 */
public final class HorizontalRopeBlock extends Block {
    public static final EnumProperty<Direction.Axis> AXIS =
        EnumProperty.create("axis", Direction.Axis.class,
            Direction.Axis.X, Direction.Axis.Z);
    public static final BooleanProperty TIED_NEGATIVE = BooleanProperty.create("tied_negative");
    public static final BooleanProperty TIED_POSITIVE = BooleanProperty.create("tied_positive");
    private static final VoxelShape X = Block.box(0, 7, 7, 16, 9, 9);
    private static final VoxelShape Z = Block.box(7, 7, 0, 9, 9, 16);

    public HorizontalRopeBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(AXIS, Direction.Axis.X)
            .setValue(TIED_NEGATIVE, false).setValue(TIED_POSITIVE, false));
    }

    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS, TIED_NEGATIVE, TIED_POSITIVE);
    }

    /** Call before placing a span and when restoring one from harvested vines. */
    public static BlockState withConnections(BlockState state, LevelReader level, BlockPos pos) {
        Direction.Axis axis = state.getValue(AXIS);
        return state.setValue(TIED_NEGATIVE, level.getBlockState(
                    axis == Direction.Axis.X ? pos.west() : pos.north()).is(ModBlocks.CROP_STAKE))
            .setValue(TIED_POSITIVE, level.getBlockState(
                    axis == Direction.Axis.X ? pos.east() : pos.south()).is(ModBlocks.CROP_STAKE));
    }

    @Override protected BlockState updateShape(BlockState state, LevelReader level,
            ScheduledTickAccess ticks, BlockPos pos, Direction direction, BlockPos neighborPos,
            BlockState neighbor, RandomSource random) {
        Direction.Axis axis = state.getValue(AXIS);
        if ((axis == Direction.Axis.X && direction == Direction.WEST)
                || (axis == Direction.Axis.Z && direction == Direction.NORTH))
            return state.setValue(TIED_NEGATIVE, neighbor.is(ModBlocks.CROP_STAKE));
        if ((axis == Direction.Axis.X && direction == Direction.EAST)
                || (axis == Direction.Axis.Z && direction == Direction.SOUTH))
            return state.setValue(TIED_POSITIVE, neighbor.is(ModBlocks.CROP_STAKE));
        return state;
    }

    @Override public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos,
            CollisionContext context) {
        return state.getValue(AXIS) == Direction.Axis.X ? X : Z;
    }

    @Override public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos,
            CollisionContext context) {
        return Shapes.empty();
    }
}
