package dev.italiansdelight.common.block;

import dev.italiansdelight.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Reusable 4px wooden crop stake. The 1px rope loop is rendered by the post,
 * not by each span; therefore opposite ropes cannot draw overlapping knots.
 * Planted chili and tomatoes retain this same support and rope connection.
 */
public final class CropStakeBlock extends IronBarsBlock {
    public static final BooleanProperty ROPE_TIED = BooleanProperty.create("rope_tied");
    private static final VoxelShape POST = Block.box(6, 0, 6, 10, 16, 10);

    public CropStakeBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(ROPE_TIED, false));
    }

    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ROPE_TIED);
    }

    /** A placed stake and its planted crop both act as real trellis posts. */
    public static boolean isPost(BlockState state) {
        return state.is(ModBlocks.CROP_STAKE) || state.is(ModBlocks.CHILI_CROP)
            || state.is(ModBlocks.STAKED_TOMATOES);
    }

    /** Check only ropes running towards this post, not perpendicular spans. */
    public static boolean isRopeTied(LevelReader level, BlockPos pos) {
        for (Direction direction : new Direction[] {
                Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST}) {
            BlockState neighbor = level.getBlockState(pos.relative(direction));
            if (neighbor.is(ModBlocks.HORIZONTAL_ROPE)
                    && neighbor.getValue(HorizontalRopeBlock.AXIS) == direction.getAxis()) {
                return true;
            }
            if (neighbor.is(ModBlocks.GRAPE_LEAVES)
                    && neighbor.getValue(GrapeLeavesBlock.AXIS) == direction.getAxis()) {
                return true;
            }
        }
        return false;
    }

    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context)
            .setValue(ROPE_TIED, isRopeTied(context.getLevel(), context.getClickedPos()));
    }

    @Override protected BlockState updateShape(BlockState state, LevelReader level,
            ScheduledTickAccess ticks, BlockPos pos, Direction changedSide,
            BlockPos neighborPos, BlockState neighbor, RandomSource random) {
        return super.updateShape(state, level, ticks, pos, changedSide, neighborPos, neighbor, random)
            .setValue(ROPE_TIED, isRopeTied(level, pos));
    }

    @Override public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos,
            CollisionContext context) { return POST; }
    @Override public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos,
            CollisionContext context) { return Shapes.empty(); }

    @Override protected InteractionResult useItemOn(ItemStack held, BlockState state,
            Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return StakeCropPlanting.tryPlant(player, level, hand, pos);
    }
}
