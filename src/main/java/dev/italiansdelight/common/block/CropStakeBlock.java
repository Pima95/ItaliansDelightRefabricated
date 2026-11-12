package dev.italiansdelight.common.block;

import dev.italiansdelight.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Decorative wooden pole. No plants, farmland requirement, or seed interaction. */
public final class CropStakeBlock extends RotatedPillarBlock {
    public static final BooleanProperty ROPE_TIED = BooleanProperty.create("rope_tied");
    public static final VoxelShape POST = Block.box(6, 0, 6, 10, 16, 10);
    private static final VoxelShape X_SHAPE = Block.box(0, 6, 6, 16, 10, 10);
    private static final VoxelShape Z_SHAPE = Block.box(6, 6, 0, 10, 10, 16);

    public CropStakeBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(ROPE_TIED, false));
    }

    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ROPE_TIED);
    }

    /** Every decorative wood variant remains a rope anchor. */
    public static boolean isPost(BlockState state) {
        return state.getBlock() instanceof CropStakeBlock;
    }

    /** Unchanged rope-connection detection; there is no crop logic here. */
    public static boolean isRopeTied(LevelReader level, BlockPos pos) {
        for (Direction direction : new Direction[] {
                Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST}) {
            BlockState neighbor = level.getBlockState(pos.relative(direction));
            if (neighbor.is(ModBlocks.HORIZONTAL_ROPE)
                    && neighbor.getValue(HorizontalRopeBlock.AXIS) == direction.getAxis())
                return true;
            if (neighbor.is(ModBlocks.GRAPE_LEAVES)
                    && neighbor.getValue(GrapeLeavesBlock.AXIS) == direction.getAxis())
                return true;
        }
        return false;
    }

    @Override public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState state = super.getStateForPlacement(ctx);
        return state == null ? null : state.setValue(ROPE_TIED,
            isRopeTied(ctx.getLevel(), ctx.getClickedPos()));
    }

    @Override protected BlockState updateShape(BlockState state, LevelReader level,
            ScheduledTickAccess ticks, BlockPos pos, Direction side,
            BlockPos neighborPos, BlockState neighbor, RandomSource random) {
        return super.updateShape(state, level, ticks, pos, side, neighborPos, random)
            .setValue(ROPE_TIED, isRopeTied(level, pos));
    }

    private static VoxelShape shape(BlockState state) {
        return switch (state.getValue(AXIS)) {
            case X -> X_SHAPE;
            case Y -> POST;
            case Z -> Z_SHAPE;
        };
    }

    @Override public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos,
            CollisionContext context) { return shape(state); }

    @Override public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos,
            CollisionContext context) { return shape(state); }
}
