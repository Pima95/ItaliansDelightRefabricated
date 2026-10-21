package dev.italiansdelight.common.block;

import com.mojang.serialization.MapCodec;
import dev.italiansdelight.common.block.entity.CookedPizzaBlockEntity;
import dev.italiansdelight.common.pizza.PizzaDoughSupport;
import dev.italiansdelight.common.pizza.PizzaItemData;
import dev.italiansdelight.common.pizza.PizzaDisplayNames;
import dev.italiansdelight.common.pizza.PizzaQuarterSelection;
import dev.italiansdelight.common.pizza.PizzaSliceFactory;
import dev.italiansdelight.common.registry.ModDataComponents;
import dev.italiansdelight.common.registry.ModItems;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * One baked pizza placed on a flat support. Right-click with an empty hand
 * to collect ONE of the four physical slices, with exact ingredient data.
 *
 * <p>Four bits in REMAINING correspond to PizzaQuarterSelection's stable
 * saved orientation. A successful click clears its bit atomically, so even
 * two players clicking the same quarter cannot obtain duplicate slices.
 *
 * <p>Breaking a full pizza drops the whole cooked_pizza item. Breaking a
 * partially served pizza drops ONLY the remaining slice items. Bowls have
 * already been granted when cooking finished: NEVER refund source toppings.
 */
public final class CookedPizzaBlock extends Block implements EntityBlock {
    private static final MapCodec<CookedPizzaBlock> CODEC =
            simpleCodec(CookedPizzaBlock::new);

    public static final IntegerProperty REMAINING = IntegerProperty.create("remaining", 0, 15);
    public static final EnumProperty<Direction> FACING =
            BlockStateProperties.HORIZONTAL_FACING;

    private static final Direction[] DIRECTIONS = {
            Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST
    };
    private static final VoxelShape[][] QUARTER_SHAPES = buildShapes();

    public CookedPizzaBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(REMAINING, PizzaQuarterSelection.ALL_QUARTERS)
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CookedPizzaBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder
    ) {
        super.createBlockStateDefinition(builder);
        builder.add(REMAINING, FACING);
    }

    /** Match each visually present quarter, not the whole pizza bounding box. */
    @Override
    protected VoxelShape getShape(
            BlockState state, BlockGetter level, BlockPos pos, CollisionContext context
    ) {
        Direction facing = state.getValue(FACING);
        int facingIndex = switch (facing) {
            case NORTH -> 0;
            case EAST -> 1;
            case SOUTH -> 2;
            case WEST -> 3;
            default -> 0;
        };
        return QUARTER_SHAPES[facingIndex][state.getValue(REMAINING)];
    }

    private static VoxelShape[][] buildShapes() {
        VoxelShape[][] shapes = new VoxelShape[DIRECTIONS.length][16];
        for (int facingIndex = 0; facingIndex < DIRECTIONS.length; facingIndex++) {
            Direction forward = DIRECTIONS[facingIndex];
            int rightX = -forward.getStepZ(), rightZ = forward.getStepX();
            int frontX = forward.getStepX(), frontZ = forward.getStepZ();
            for (int mask = 0; mask <= 15; mask++) {
                VoxelShape shape = Shapes.empty();
                for (int q = 0; q < 4; q++) {
                    if ((mask & (1 << q)) == 0) {
                        continue;
                    }
                    double u = q >= 2 ? 3.5 : -3.5;
                    double v = (q & 1) != 0 ? 3.5 : -3.5;
                    int cx = (int) Math.round(8.0 + rightX * u + frontX * v);
                    int cz = (int) Math.round(8.0 + rightZ * u + frontZ * v);
                    shape = Shapes.or(shape, box(cx - 4, 0, cz - 4, cx + 3, 2, cz + 3));
                }
                shapes[facingIndex][mask] = shape.optimize();
            }
        }
        return shapes;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos under = pos.below();
        return PizzaDoughSupport.canSupport(level.getBlockState(under), level, under);
    }

    @Override
    protected BlockState updateShape(
            BlockState state, LevelReader level, ScheduledTickAccess ticks,
            BlockPos pos, Direction direction, BlockPos neighborPos,
            BlockState neighborState, RandomSource random
    ) {
        if (direction == Direction.DOWN && !canSurvive(state, level, pos)) {
            // Avoid immediate AIR replacement, which loses saved quarters
            // without going through this block's composition-aware getDrops.
            ticks.scheduleTick(pos, this, 1);
        }
        return super.updateShape(
                state, level, ticks, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!canSurvive(state, level, pos)) {
            // A full pizza drops as one item; a served pizza drops only its
            // remaining slices. The block entity is still present here.
            level.destroyBlock(pos, true);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state, Level level, BlockPos pos,
            Player player, BlockHitResult hit
    ) {
        if (!player.getMainHandItem().isEmpty()
                || !player.mayUseItemAt(pos, hit.getDirection(), ItemStack.EMPTY)) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (!(level instanceof ServerLevel server)
                || !(server.getBlockEntity(pos) instanceof CookedPizzaBlockEntity entity)) {
            return InteractionResult.FAIL;
        }
        PizzaItemData data = entity.pizza();
        int q = PizzaQuarterSelection.physicalQuarter(
                data.composition().orientation(),
                hit.getLocation().x - pos.getX(),
                hit.getLocation().z - pos.getZ());
        int bit = 1 << q;
        int remaining = state.getValue(REMAINING);
        if ((remaining & bit) == 0) {
            return InteractionResult.PASS; // Already eaten or empty hit region.
        }

        ItemStack slice = PizzaSliceFactory.create(data, q);
        // Established pizza serving rule: an inventory with no capacity
        // must NOT consume the slice and must NOT drop it to the ground.
        // Slots with a compatible non-full stack still count as capacity.
        if (!canReceiveSlice(player, slice)) {
            return InteractionResult.SUCCESS;
        }

        // Commit the removal BEFORE issuing an item. Every physical quarter
        // can be claimed exactly once, even with simultaneous players/clicks.
        boolean changed = (remaining == bit)
                ? server.removeBlock(pos, false)
                : server.setBlock(pos, state.setValue(REMAINING, remaining & ~bit),
                        Block.UPDATE_ALL);
        if (!changed) {
            return InteractionResult.FAIL;
        }

        // Inventory space was validated before changing the block. If an
        // external inventory handler unexpectedly refuses, preserve the
        // committed item as a final failsafe instead of silently losing it.
        if (!player.addItem(slice) && !slice.isEmpty()) {
            player.drop(slice, false);
        }
        server.playSound(null, pos, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.6F, 1.0F);
        return InteractionResult.SUCCESS;
    }

    /** Check before mutating the block; never serve into a full inventory. */
    private static boolean canReceiveSlice(Player player, ItemStack slice) {
        if (player.getInventory().getFreeSlot() >= 0) {
            return true;
        }
        for (int slot = 0; slot < 36; slot++) {
            ItemStack current = player.getInventory().getItem(slot);
            if (!current.isEmpty()
                    && ItemStack.isSameItemSameComponents(current, slice)
                    && current.getCount() < current.getMaxStackSize()) {
                return true;
            }
        }
        return false;
    }

    private static List<ItemStack> remainingDrops(
            BlockState state, CookedPizzaBlockEntity entity
    ) {
        if (entity == null) {
            return List.of(); // Never fabricate or duplicate missing saved data.
        }
        PizzaItemData pizza = entity.pizza();
        int mask = state.getValue(REMAINING);
        if (mask == PizzaQuarterSelection.ALL_QUARTERS) {
            ItemStack whole = new ItemStack(ModItems.COOKED_PIZZA);
            whole.set(ModDataComponents.PIZZA_CONTENTS, pizza);
            PizzaDisplayNames.nameWholeStack(whole, pizza);
            return List.of(whole);
        }

        List<ItemStack> drops = new ArrayList<>();
        for (int q = 0; q < 4; q++) {
            if ((mask & (1 << q)) != 0) {
                drops.add(PizzaSliceFactory.create(pizza, q));
            }
        }
        return List.copyOf(drops);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        return remainingDrops(state,
                params.getOptionalParameter(LootContextParams.BLOCK_ENTITY)
                        instanceof CookedPizzaBlockEntity entity ? entity : null);
    }

    @Override
    protected ItemStack getCloneItemStack(
            LevelReader level, BlockPos pos, BlockState state, boolean includeData
    ) {
        CookedPizzaBlockEntity entity =
                level.getBlockEntity(pos) instanceof CookedPizzaBlockEntity cooked ? cooked : null;
        List<ItemStack> remaining = remainingDrops(state, entity);
        return remaining.isEmpty() ? ItemStack.EMPTY : remaining.get(0);
    }
}
