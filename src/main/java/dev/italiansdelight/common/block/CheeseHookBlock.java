package dev.italiansdelight.common.block;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.MapCodec;

import dev.italiansdelight.common.aging.HangingCheeseType;
import dev.italiansdelight.common.block.entity.CheeseHookBlockEntity;
import dev.italiansdelight.common.registry.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Ceiling-mounted cheese hook.
 *
 * The curved side faces the player when placed. One Scamorza or Provolone can
 * hang from it at a time. Fresh cheeses process while attached; mature cheeses
 * can be re-attached for decoration.
 */
public final class CheeseHookBlock extends BaseEntityBlock {

    public static final EnumProperty<Direction> FACING =
        BlockStateProperties.HORIZONTAL_FACING;

    public static final EnumProperty<HangingCheeseType> CHEESE =
        EnumProperty.create(
            "cheese",
            HangingCheeseType.class
        );

    public static final MapCodec<CheeseHookBlock> CODEC =
        simpleCodec(CheeseHookBlock::new);

    private static final VoxelShape HOOK_SHAPE =
        Shapes.or(
            // Ceiling plate.
            Block.box(5, 14, 5, 11, 16, 11),

            // Compact selection volume around the stepped hook. The visual
            // model is more detailed, but its lowest point remains centered
            // at X/Z 8.
            Block.box(5, 5, 4, 11, 14, 12)
        );

    private static final VoxelShape HANGING_CHEESE_SHAPE =
        // Hanging models extend below the hook block into the space under it.
        Block.box(
            4,
            -8,
            4,
            12,
            5,
            12
        );

    public CheeseHookBlock(Properties properties) {
        super(properties);

        registerDefaultState(
            stateDefinition.any()
                .setValue(
                    FACING,
                    Direction.NORTH
                )
                .setValue(
                    CHEESE,
                    HangingCheeseType.EMPTY
                )
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
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

        builder.add(
            FACING,
            CHEESE
        );
    }

    @Override
    protected BlockState rotate(
        BlockState state,
        Rotation rotation
    ) {
        return state.setValue(
            FACING,
            rotation.rotate(
                state.getValue(FACING)
            )
        );
    }

    @Override
    protected BlockState mirror(
        BlockState state,
        Mirror mirror
    ) {
        return rotate(
            state,
            mirror.getRotation(
                state.getValue(FACING)
            )
        );
    }

    @Override
    public BlockEntity newBlockEntity(
        BlockPos pos,
        BlockState state
    ) {
        return new CheeseHookBlockEntity(
            pos,
            state
        );
    }

    @Override
    protected VoxelShape getShape(
        BlockState state,
        BlockGetter level,
        BlockPos pos,
        CollisionContext context
    ) {
        return state.getValue(CHEESE)
                .isEmpty()
            ? HOOK_SHAPE
            : Shapes.or(
                HOOK_SHAPE,
                HANGING_CHEESE_SHAPE
            );
    }

    @Override
    protected InteractionResult useItemOn(
        ItemStack stack,
        BlockState state,
        Level level,
        BlockPos pos,
        Player player,
        InteractionHand hand,
        BlockHitResult hit
    ) {
        HangingCheeseType current =
            state.getValue(CHEESE);

        if (!current.isEmpty()) {
            if (level.isClientSide()) {
                return InteractionResult.SUCCESS;
            }

            if (
                level instanceof ServerLevel serverLevel
                && level.getBlockEntity(pos)
                    instanceof CheeseHookBlockEntity hook
            ) {
                giveRemovedCheeseToInventory(
                    hook.remove(serverLevel),
                    player
                );
            }

            return InteractionResult.SUCCESS;
        }

        HangingCheeseType requested =
            HangingCheeseType.fromStack(
                stack
            );

        if (requested.isEmpty()) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (
            level instanceof ServerLevel serverLevel
            && level.getBlockEntity(pos)
                instanceof CheeseHookBlockEntity hook
            && hook.insert(
                serverLevel,
                requested
            )
            && !player.getAbilities().instabuild
        ) {
            stack.shrink(1);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useWithoutItem(
        BlockState state,
        Level level,
        BlockPos pos,
        Player player,
        BlockHitResult hit
    ) {
        if (
            state.getValue(CHEESE)
                .isEmpty()
        ) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (
            level instanceof ServerLevel serverLevel
            && level.getBlockEntity(pos)
                instanceof CheeseHookBlockEntity hook
        ) {
            giveRemovedCheese(
                hook.remove(serverLevel),
                player
            );
        }

        return InteractionResult.SUCCESS;
    }

    private static void giveRemovedCheese(
        ItemStack stack,
        Player player
    ) {
        if (
            !stack.isEmpty()
            && !player.addItem(stack)
        ) {
            player.drop(
                stack,
                false
            );
        }
    }

    private static void giveRemovedCheeseToInventory(
        ItemStack stack,
        Player player
    ) {
        if (stack.isEmpty()) {
            return;
        }

        // Vanilla Inventory first looks for a compatible non-full stack, then
        // the first empty slot and finally drops the item if the inventory is
        // full. This is exactly the desired removal priority.
        player.getInventory()
            .placeItemBackInInventory(
                stack
            );
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
            && !canSurvive(
                state,
                level,
                pos
            )
        ) {
            return Blocks.AIR
                .defaultBlockState();
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

    @Override
    protected List<ItemStack> getDrops(
        BlockState state,
        LootParams.Builder params
    ) {
        List<ItemStack> drops =
            new ArrayList<>(
                super.getDrops(
                    state,
                    params
                )
            );

        if (
            params.getOptionalParameter(
                LootContextParams.BLOCK_ENTITY
            ) instanceof CheeseHookBlockEntity hook
        ) {
            ItemStack cheese =
                hook.getCheeseDrop(
                    state
                );

            if (!cheese.isEmpty()) {
                drops.add(cheese);
            }
        }

        return drops;
    }

    @Override
    public <T extends BlockEntity>
    BlockEntityTicker<T> getTicker(
        Level level,
        BlockState state,
        BlockEntityType<T> type
    ) {
        if (level instanceof ServerLevel serverLevel) {
            return createTickerHelper(
                type,
                ModBlockEntities.CHEESE_HOOK,
                (
                    commonLevel,
                    pos,
                    blockState,
                    hook
                ) ->
                    CheeseHookBlockEntity.serverTick(
                        serverLevel,
                        pos,
                        blockState,
                        hook
                    )
            );
        }

        return null;
    }
}
