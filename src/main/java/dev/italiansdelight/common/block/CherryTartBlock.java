package dev.italiansdelight.common.block;

import dev.italiansdelight.common.registry.ModBlocks;
import dev.italiansdelight.common.registry.ModItems;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A four-portion placeable Cherry Tart, matching the four serving semantics
 * and tabletop silhouette of Farmer's Delight's Apple Pie.
 *
 * An empty-hand right click feeds one portion if the player is hungry.
 * A tagged knife instead cuts and drops ONE edible slice, consuming no food.
 * Every operation updates BITES server-side first; no fifth slice is possible.
 *
 * A broken whole cake drops its original BlockItem; a partial cake drops
 * only its remaining slice items. The Cutting Board can cut a whole cake
 * BlockItem directly into exactly four slices as well.
 */
public final class CherryTartBlock extends Block {
public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty BITES = IntegerProperty.create("bites", 0, 3);
    private static final TagKey<Item> KNIVES =
            TagKey.create(Registries.ITEM, Identifier.parse("c:tools/knife"));
    private static final VoxelShape[][] SHAPES = buildShapes();

    public CherryTartBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(BITES, 0));
    }
@Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, BITES);
    }

    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    /** Each model uses the same 12x12x4 pie geometry as Farmer's Delight. */
    @Override protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos,
                                            CollisionContext context) {
        return SHAPES[state.getValue(BITES)][state.getValue(FACING).get2DDataValue()];
    }

    private static VoxelShape[][] buildShapes() {
        VoxelShape[][] shapes = new VoxelShape[4][4];
        for (int bites = 0; bites < 4; bites++) {
            for (Direction facing : new Direction[] {
                    Direction.SOUTH, Direction.WEST, Direction.NORTH, Direction.EAST}) {
                VoxelShape result = Shapes.empty();
                // FD order: full -> three quarters -> upper half -> last quarter.
                boolean[] quarters = switch (bites) {
                    case 0 -> new boolean[] {true, true, true, true};
                    case 1 -> new boolean[] {true, true, true, false};
                    case 2 -> new boolean[] {true, true, false, false};
                    default -> new boolean[] {false, true, false, false};
                };
                for (int q = 0; q < 4; q++) {
                    if (!quarters[q]) continue;
                    int x1 = q % 2 == 0 ? 2 : 8;
                    int z1 = q / 2 == 0 ? 2 : 8;
                    int x2 = x1 + 6, z2 = z1 + 6;
                    VoxelShape part = switch (facing) {
                        case EAST -> Block.box(16 - z2, 0, x1, 16 - z1, 4, x2);
                        case SOUTH -> Block.box(16 - x2, 0, 16 - z2, 16 - x1, 4, 16 - z1);
                        case WEST -> Block.box(z1, 0, 16 - x2, z2, 4, 16 - x1);
                        default -> Block.box(x1, 0, z1, x2, 4, z2);
                    };
                    result = Shapes.or(result, part);
                }
                shapes[bites][facing.get2DDataValue()] = result.optimize();
            }
        }
        return shapes;
    }

    @Override protected InteractionResult useItemOn(
            ItemStack held, BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {
        if (!held.is(KNIVES)) return InteractionResult.PASS;
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        // Remove before spawning the item: one quarter is consumed atomically.
        int bites = state.getValue(BITES);
        boolean success = bites == 3
                ? level.removeBlock(pos, false)
                : level.setBlock(pos, state.setValue(BITES, bites + 1), Block.UPDATE_ALL);
        if (!success) return InteractionResult.FAIL;
        Block.popResource(level, pos.above(), new ItemStack(ModItems.CHERRY_TART_SLICE));
        level.playSound(null, pos, SoundEvents.WOOL_BREAK, SoundSource.BLOCKS, 0.75f, 1.05f);
        return InteractionResult.SUCCESS;
    }

    @Override protected InteractionResult useWithoutItem(
            BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!player.getMainHandItem().isEmpty() || !player.getFoodData().needsFood())
            return InteractionResult.PASS;
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        int bites = state.getValue(BITES);
        boolean success = bites == 3
                ? level.removeBlock(pos, false)
                : level.setBlock(pos, state.setValue(BITES, bites + 1), Block.UPDATE_ALL);
        if (!success) return InteractionResult.FAIL;
        // Same nutrition/saturation as the portable Cherry Tart Slice item.
        player.getFoodData().eat(4, 0.5f);
        level.playSound(null, pos, SoundEvents.GENERIC_EAT.value(), SoundSource.PLAYERS, 0.8f, 1f);
        return InteractionResult.SUCCESS;
    }

    @Override protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP);
    }

    @Override protected BlockState updateShape(
            BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos,
            Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (direction == Direction.DOWN && !canSurvive(state, level, pos)) {
            // Scheduled breaking invokes getDrops: no lost or doubled servings.
            ticks.scheduleTick(pos, this, 1);
        }
        return super.updateShape(state, level, ticks, pos, direction, neighborPos, neighborState, random);
    }

    @Override protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!canSurvive(state, level, pos)) level.destroyBlock(pos, true);
    }

    @Override protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        int remaining = 4 - state.getValue(BITES);
        // Never return a WHOLE cake after any part has been eaten/cut.
        if (remaining == 4) return List.of(new ItemStack(ModBlocks.CHERRY_TART));
        return List.of(new ItemStack(ModItems.CHERRY_TART_SLICE, remaining));
    }
}
