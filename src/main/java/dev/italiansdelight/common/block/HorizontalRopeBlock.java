package dev.italiansdelight.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A visual, horizontal span made using the ORIGINAL Farmer's Delight rope.
 * No additional rope item is registered; breaking a span drops one FDR rope.
 * Axis is saved, so vine canopies can replace and restore exactly that span.
 */
public final class HorizontalRopeBlock extends Block {
    public static final EnumProperty<Direction.Axis> AXIS =
        EnumProperty.create("axis", Direction.Axis.class,
            Direction.Axis.X, Direction.Axis.Z);
    private static final VoxelShape X = Block.box(0, 7, 7, 16, 9, 9);
    private static final VoxelShape Z = Block.box(7, 7, 0, 9, 9, 16);

    public HorizontalRopeBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(AXIS, Direction.Axis.X));
    }

    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
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
