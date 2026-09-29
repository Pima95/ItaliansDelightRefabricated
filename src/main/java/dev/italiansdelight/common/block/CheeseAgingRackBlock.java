package dev.italiansdelight.common.block;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.MapCodec;

import dev.italiansdelight.common.aging.CheeseShapes;
import dev.italiansdelight.common.aging.RackCheeseState;
import dev.italiansdelight.common.block.entity.CheeseAgingRackBlockEntity;
import dev.italiansdelight.common.item.AgingCheeseItem;
import dev.italiansdelight.common.registry.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * One-block cheese aging rack.
 *
 * The rack has exactly two shelves and one cheese slot per shelf:
 * - lower shelf at the bottom of the block;
 * - upper shelf around half-block height.
 *
 * The BlockState exposes each shelf's cheese appearance. The BlockEntity is
 * authoritative for contents and progress; legacy occupancy flags are retained
 * for saved-world compatibility and synchronized alongside the appearance.
 */
public final class CheeseAgingRackBlock extends BaseEntityBlock {

    public static final BooleanProperty LOWER_OCCUPIED =
        BooleanProperty.create("lower_occupied");

    public static final BooleanProperty UPPER_OCCUPIED =
        BooleanProperty.create("upper_occupied");

    public static final EnumProperty<RackCheeseState> LOWER_CHEESE =
        EnumProperty.create("lower_cheese", RackCheeseState.class);

    public static final EnumProperty<RackCheeseState> UPPER_CHEESE =
        EnumProperty.create("upper_cheese", RackCheeseState.class);

    private static final VoxelShape SHAPE = Shapes.or(
        // Lower and raised shelves.
        Block.box(1, 0, 1, 15, 2, 15),
        Block.box(1, 7, 1, 15, 9, 15),

        // Four vertical supports. Everything remains inside one 1x1x1 block.
        Block.box(0, 0, 0, 2, 16, 2),
        Block.box(14, 0, 0, 16, 16, 2),
        Block.box(0, 0, 14, 2, 16, 16),
        Block.box(14, 0, 14, 16, 16, 16)
    );

    private static final VoxelShape[][] OUTLINES = createOutlines();

    private static VoxelShape[][] createOutlines() {
        RackCheeseState[] states = RackCheeseState.values();
        VoxelShape[][] shapes = new VoxelShape[states.length][states.length];
        for (RackCheeseState lower : states) {
            for (RackCheeseState upper : states) {
                VoxelShape shape = SHAPE;
                if (lower != RackCheeseState.EMPTY) {
                    shape = Shapes.or(shape, CheeseShapes.placed(lower.cheeseType()).move(0, 2.0 / 16, 0));
                }
                if (upper != RackCheeseState.EMPTY) {
                    shape = Shapes.or(shape, CheeseShapes.placed(upper.cheeseType()).move(0, 9.0 / 16, 0));
                }
                shapes[lower.ordinal()][upper.ordinal()] = shape.optimize();
            }
        }
        return shapes;
    }

    public CheeseAgingRackBlock(Properties properties) {
        super(properties);

        registerDefaultState(
            stateDefinition.any()
                .setValue(LOWER_OCCUPIED, false)
                .setValue(UPPER_OCCUPIED, false)
                .setValue(LOWER_CHEESE, RackCheeseState.EMPTY)
                .setValue(UPPER_CHEESE, RackCheeseState.EMPTY)
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(CheeseAgingRackBlock::new);
    }

    @Override
    public BlockEntity newBlockEntity(
        BlockPos pos,
        BlockState state
    ) {
        return new CheeseAgingRackBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(
        StateDefinition.Builder<Block, BlockState> builder
    ) {
        super.createBlockStateDefinition(builder);
        builder.add(LOWER_OCCUPIED, UPPER_OCCUPIED, LOWER_CHEESE, UPPER_CHEESE);
    }

    @Override
    public VoxelShape getShape(
        BlockState state,
        BlockGetter level,
        BlockPos pos,
        CollisionContext context
    ) {
        return OUTLINES[state.getValue(LOWER_CHEESE).ordinal()][state.getValue(UPPER_CHEESE).ordinal()];
    }

    @Override
    public VoxelShape getCollisionShape(
        BlockState state,
        BlockGetter level,
        BlockPos pos,
        CollisionContext context
    ) {
        return SHAPE;
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
        int shelf =
            shelfFromHit(pos, hit);

        boolean occupied =
            shelf == CheeseAgingRackBlockEntity.LOWER_SLOT
                ? state.getValue(LOWER_OCCUPIED)
                : state.getValue(UPPER_OCCUPIED);

        // An occupied shelf always has interaction priority. Any held item can
        // be used to take the cheese out without consuming or replacing it.
        if (occupied) {
            if (level.isClientSide()) {
                return InteractionResult.SUCCESS;
            }

            if (
                level instanceof ServerLevel serverLevel
                && level.getBlockEntity(pos)
                    instanceof CheeseAgingRackBlockEntity rack
            ) {
                giveRemovedCheeseToInventory(
                    rack.remove(
                        serverLevel,
                        shelf
                    ),
                    player
                );
            }

            return InteractionResult.SUCCESS;
        }

        // Empty shelf: only the three wheel families, fresh or mature, fit.
        if (!(stack.getItem() instanceof AgingCheeseItem agingCheeseItem)) {
            return InteractionResult.PASS;
        }

        if (!agingCheeseItem.cheeseType().fitsInRack()) {
            return InteractionResult.FAIL;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (
            level instanceof ServerLevel serverLevel
            && level.getBlockEntity(pos)
                instanceof CheeseAgingRackBlockEntity rack
            && rack.insert(
                serverLevel,
                shelf,
                agingCheeseItem.cheeseType(),
                agingCheeseItem.isMature()
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
        int shelf =
            shelfFromHit(pos, hit);

        boolean occupied =
            shelf == CheeseAgingRackBlockEntity.LOWER_SLOT
                ? state.getValue(LOWER_OCCUPIED)
                : state.getValue(UPPER_OCCUPIED);

        if (!occupied) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (
            level instanceof ServerLevel serverLevel
            && level.getBlockEntity(pos)
                instanceof CheeseAgingRackBlockEntity rack
        ) {
            giveRemovedCheese(
                rack.remove(
                    serverLevel,
                    shelf
                ),
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

        // Prefer an existing compatible stack, then the first empty slot;
        // Vanilla drops the item automatically if no inventory space remains.
        player.getInventory()
            .placeItemBackInInventory(
                stack
            );
    }

    private static int shelfFromHit(
        BlockPos pos,
        BlockHitResult hit
    ) {
        double localY =
            hit.getLocation().y
                - pos.getY();

        return localY >= (7.0D / 16.0D)
            ? CheeseAgingRackBlockEntity.UPPER_SLOT
            : CheeseAgingRackBlockEntity.LOWER_SLOT;
    }

    @Override
    protected List<ItemStack> getDrops(
        BlockState state,
        LootParams.Builder params
    ) {
        List<ItemStack> drops =
            new ArrayList<>(
                super.getDrops(state, params)
            );

        if (
            params.getOptionalParameter(
                LootContextParams.BLOCK_ENTITY
            ) instanceof CheeseAgingRackBlockEntity rack
        ) {
            drops.addAll(
                rack.getCheeseDrops(
                    params.getLevel()
                )
            );
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
                ModBlockEntities.CHEESE_AGING_RACK,
                (
                    commonLevel,
                    pos,
                    blockState,
                    rack
                ) ->
                    CheeseAgingRackBlockEntity.serverTick(
                        serverLevel,
                        pos,
                        blockState,
                        rack
                    )
            );
        }

        return null;
    }
}
