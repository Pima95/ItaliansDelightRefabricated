package dev.italiansdelight.common.block.entity;

import dev.italiansdelight.common.block.PizzaOvenBlock;
import dev.italiansdelight.common.block.entity.container.PizzaOvenMenu;
import dev.italiansdelight.common.pizza.PizzaBaking;
import dev.italiansdelight.common.pizza.PizzaItemData;
import dev.italiansdelight.common.registry.ModBlockEntities;
import dev.italiansdelight.common.registry.ModDataComponents;
import dev.italiansdelight.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Single-pizza, server-authoritative oven.
 *
 * <p>Uses furnace's three physical slots and four GUI data values. It owns
 * fuel/progress independently of vanilla cooking recipes so no data component
 * can be lost. A full output blocks ALL new input, including hopper attempts.
 *
 * <p>Burn energy is frozen while idle or when the output is occupied. The
 * player can remove partly baked raw pizza directly from the input slot and
 * reinsert it without resetting progress (which is stored on that ItemStack).
 */
public final class PizzaOvenBlockEntity extends BaseContainerBlockEntity
        implements WorldlyContainer {
    public static final int INPUT = 0;
    public static final int FUEL = 1;
    public static final int RESULT = 2;
    public static final int SIZE = 3;

    private static final int[] FROM_TOP = {INPUT};
    private static final int[] FROM_SIDES = {FUEL};
    private static final int[] FROM_BOTTOM = {RESULT};

    private NonNullList<ItemStack> items =
            NonNullList.withSize(SIZE, ItemStack.EMPTY);
    private int energyRemaining;
    private int fuelTotal;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> energyRemaining;
                case 1 -> fuelTotal;
                case 2 -> cookingProgress();
                case 3 -> PizzaBaking.MAX_PROGRESS;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            // Client receives its own vanilla FurnaceMenu and syncs four
            // values via the usual slot/data packets. Server is authoritative.
            if (index == 0) energyRemaining = Math.max(0, value);
            if (index == 1) fuelTotal = Math.max(0, value);
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public PizzaOvenBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PIZZA_OVEN, pos, state);
    }

    private int cookingProgress() {
        PizzaItemData pizza = items.get(INPUT).get(ModDataComponents.PIZZA_CONTENTS);
        return pizza == null ? 0 : pizza.bakeProgress();
    }

    /**
     * Called only by the server ticker while this chunk is ticking.
     * The completed pizza and all bowl remainders are committed in one tick,
     * never by the client or by a menu preview.
     */
    public static void serverTick(
            ServerLevel level, BlockPos pos, BlockState state,
            PizzaOvenBlockEntity oven
    ) {
        ItemStack input = oven.items.get(INPUT);
        PizzaItemData data = input.get(ModDataComponents.PIZZA_CONTENTS);
        boolean canCook = input.is(ModItems.RAW_PIZZA)
                && input.getCount() == 1
                && data != null
                && !PizzaBaking.isReady(data)
                && oven.items.get(RESULT).isEmpty();

        if (!canCook) {
            oven.setVisualBurning(level, pos, state, false);
            return; // Do not spend existing energy; no idle burn or fuel use.
        }

        if (oven.energyRemaining <= 0) {
            ItemStack fuel = oven.items.get(FUEL);
            int duration = level.fuelValues().burnDuration(fuel);
            if (duration <= 0) {
                oven.setVisualBurning(level, pos, state, false);
                return; // Preserve partially completed progress.
            }

            // Fuel charging is coupled to a real tick of work. The empty
            // lava bucket stays in the fuel slot as with vanilla furnaces.
            oven.energyRemaining = duration;
            oven.fuelTotal = duration;
            if (fuel.is(Items.LAVA_BUCKET)) {
                oven.items.set(FUEL, new ItemStack(Items.BUCKET));
            } else {
                fuel.shrink(1);
                if (fuel.isEmpty()) {
                    oven.items.set(FUEL, ItemStack.EMPTY);
                }
            }
        }

        // One server tick of cooking always spends exactly one fuel tick.
        oven.energyRemaining--;
        PizzaItemData advanced = PizzaBaking.advance(data, PizzaBaking.Oven.PIZZA_OVEN, 1);
        input.set(ModDataComponents.PIZZA_CONTENTS, advanced);

        if (PizzaBaking.isReady(advanced)) {
            PizzaBaking.BakingResult baked = PizzaBaking.finish(input)
                    .orElseThrow(() -> new IllegalStateException(
                            "Fully baked pizza could not be converted"));

            // One input -> one result. No residual original input may survive.
            oven.items.set(RESULT, baked.cookedPizza());
            oven.items.set(INPUT, ItemStack.EMPTY);
            // There is no fourth inventory slot: bowls are released as
            // ordinary drops once, at successful final conversion. Their
            // number is per original application, never per physical quarter.
            for (ItemStack bowl : baked.containers()) {
                Containers.dropItemStack(
                        level, pos.getX() + 0.5, pos.getY() + 1.0,
                        pos.getZ() + 0.5, bowl);
            }
        }

        oven.setChanged();
        oven.setVisualBurning(level, pos, state,
                !oven.items.get(INPUT).isEmpty() && oven.energyRemaining > 0);
    }

    private void setVisualBurning(
            ServerLevel level, BlockPos pos, BlockState oldState, boolean burning
    ) {
        if (oldState.getValue(PizzaOvenBlock.LIT) != burning) {
            level.setBlock(pos, oldState.setValue(PizzaOvenBlock.LIT, burning),
                    Block.UPDATE_ALL);
        }
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("block.italiansdelight.pizza_oven");
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inv) {
        // The server menu advertises MenuType.FURNACE. Client builds the
        // unmodified FurnaceMenu + FurnaceScreen with vanilla GUI sprites.
        return new PizzaOvenMenu(id, inv, this, data);
    }

    @Override
    public int getContainerSize() {
        // Must match the input, fuel and output slots used by FurnaceMenu.
        return SIZE;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> value) {
        items = value;
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return switch (slot) {
            case INPUT -> stack.is(ModItems.RAW_PIZZA)
                    && stack.get(ModDataComponents.PIZZA_CONTENTS) != null
                    && !PizzaBaking.isReady(stack.get(ModDataComponents.PIZZA_CONTENTS))
                    && items.get(RESULT).isEmpty();
            case FUEL -> level != null && level.fuelValues().isFuel(stack);
            default -> false;
        };
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        super.setItem(slot, stack);
        // Raw progress is stored on input, not on the oven itself.
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.UP) return FROM_TOP;
        if (side == Direction.DOWN) return FROM_BOTTOM;
        return FROM_SIDES;
    }

    @Override
    public boolean canPlaceItemThroughFace(
            int slot, ItemStack stack, Direction side
    ) {
        return (side == Direction.UP && slot == INPUT
                    || side != Direction.UP && side != Direction.DOWN && slot == FUEL)
                && canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(
            int slot, ItemStack stack, Direction side
    ) {
        return side == Direction.DOWN && slot == RESULT
                && stack.is(ModItems.COOKED_PIZZA);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, items);
        energyRemaining = Math.max(0, input.getIntOr("EnergyRemaining", 0));
        fuelTotal = Math.max(0, input.getIntOr("FuelTotal", 0));
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        ContainerHelper.saveAllItems(output, items);
        output.putInt("EnergyRemaining", energyRemaining);
        output.putInt("FuelTotal", fuelTotal);
        super.saveAdditional(output);
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        // Called when the actual block is replaced, not when LIT toggles.
        // Vanilla-style inventory drops; no extra result or bowl synthesis.
        if (level != null && !level.isClientSide()) {
            Containers.dropContents(level, pos, this);
        }
        super.preRemoveSideEffects(pos, state);
    }
}
