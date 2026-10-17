package dev.italiansdelight.common.block.entity;

import dev.italiansdelight.common.block.PizzaOvenBlock;
import dev.italiansdelight.common.block.entity.container.PizzaOvenMenu;
import dev.italiansdelight.common.pizza.PizzaBaking;
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
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Pizza-only furnace with vanilla furnace cooking and fuel behavior.
 *
 * <p>As with a vanilla furnace, cooking progress belongs to THIS machine,
 * never the raw pizza ItemStack. Removing/replacing the input resets it.
 * The machine saves its in-flight progress across chunk/world saves, but
 * the pizza carried in a player's inventory has no baking progress.
 *
 * <p>After a fuel is ignited its remaining burn time always decreases,
 * even when idle/blocked. The GUI's fire gauge therefore diminishes just
 * like the vanilla furnace. The oven never starts a NEW fuel while idle.
 *
 * <p>Completion consumes one raw pizza and creates one cooked pizza. Bowl
 * remainders are counted per ORIGINAL sauce/cream application. If a hopper
 * sits directly below the oven, bowls are inserted into it first; otherwise
 * or when full, they are dropped like a Cooking Pot remainder. The hopper
 * also extracts the cooked pizza via the standard output slot.
 */
public final class PizzaOvenBlockEntity extends BaseContainerBlockEntity
        implements WorldlyContainer {
    public static final int INPUT = 0;
    public static final int FUEL = 1;
    public static final int RESULT = 2;
    public static final int SIZE = 3;

    private static final int[] FROM_TOP = {INPUT};
    private static final int[] FROM_SIDES = {FUEL};
    // Vanilla furnace can also extract the empty lava bucket from underneath.
    private static final int[] FROM_BOTTOM = {RESULT, FUEL};

    private NonNullList<ItemStack> items =
            NonNullList.withSize(SIZE, ItemStack.EMPTY);
    private int energyRemaining;
    private int fuelTotal;
    private int cookingProgress;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> energyRemaining;
                case 1 -> fuelTotal;
                case 2 -> cookingProgress;
                case 3 -> PizzaBaking.MAX_PROGRESS;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) energyRemaining = Math.max(0, value);
            if (index == 1) fuelTotal = Math.max(0, value);
            if (index == 2) cookingProgress = Math.clamp(
                    value, 0, PizzaBaking.MAX_PROGRESS);
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public PizzaOvenBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PIZZA_OVEN, pos, state);
    }

    /**
     * Tick server-side only. Fuel burns continuously after ignition, exactly
     * as in a normal furnace. The raw input is NEVER mutated for progress.
     */
    public static void serverTick(
            ServerLevel level, BlockPos pos, BlockState state,
            PizzaOvenBlockEntity oven
    ) {
        boolean wasBurning = oven.energyRemaining > 0;
        if (oven.energyRemaining > 0) {
            oven.energyRemaining--; // Fire gauge decays EVEN without a pizza.
            oven.setChanged();
        }

        ItemStack input = oven.items.get(INPUT);
        boolean canCook = input.is(ModItems.RAW_PIZZA)
                && input.getCount() == 1
                && input.get(ModDataComponents.PIZZA_CONTENTS) != null
                && oven.items.get(RESULT).isEmpty();

        if (canCook && oven.energyRemaining == 0) {
            ItemStack fuel = oven.items.get(FUEL);
            int duration = level.fuelValues().burnDuration(fuel);
            if (duration > 0) {
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
                oven.setChanged();
            }
        }

        if (canCook && oven.energyRemaining > 0) {
            oven.cookingProgress = PizzaBaking.advance(
                    oven.cookingProgress, PizzaBaking.Oven.PIZZA_OVEN, 1);
            oven.setChanged();

            if (oven.cookingProgress >= PizzaBaking.MAX_PROGRESS) {
                PizzaBaking.BakingResult result =
                        PizzaBaking.finish(input, oven.cookingProgress)
                                .orElseThrow(() -> new IllegalStateException(
                                        "Unable to finish a completed raw pizza"));

                // Commit exactly once. The raw input is removed before any
                // remainders are delivered, and no progress is on the result.
                oven.items.set(RESULT, result.cookedPizza());
                oven.items.set(INPUT, ItemStack.EMPTY);
                oven.cookingProgress = 0;
                oven.setChanged();

                for (ItemStack bowls : result.containers()) {
                    deliverRemainder(level, pos, bowls);
                }
            }
        } else if (!canCook) {
            // Vanilla resets when input changes or output blocks a recipe.
            if (oven.cookingProgress != 0) {
                oven.cookingProgress = 0;
                oven.setChanged();
            }
        } else if (oven.cookingProgress > 0) {
            // Vanilla furnaces gradually lose cooking progress while unlit.
            oven.cookingProgress = Math.max(0, oven.cookingProgress - 2);
            oven.setChanged();
        }

        boolean burning = oven.energyRemaining > 0;
        if (wasBurning != burning || state.getValue(PizzaOvenBlock.LIT) != burning) {
            oven.setVisualBurning(level, pos, state, burning);
        }
    }

    /**
     * Move bowl remainders to the BELOW hopper, not into the oven's output
     * slot (which must contain only cooked pizzas). If absent/full, eject
     * remaining bowls to the world, with no loss or extra copies.
     */
    private static void deliverRemainder(
            ServerLevel level, BlockPos ovenPos, ItemStack remainder
    ) {
        ItemStack left = remainder.copy();
        if (level.getBlockEntity(ovenPos.below()) instanceof HopperBlockEntity hopper) {
            for (int i = 0; i < hopper.getContainerSize() && !left.isEmpty(); i++) {
                ItemStack existing = hopper.getItem(i);
                if (!existing.isEmpty() && !existing.is(Items.BOWL)) {
                    continue;
                }
                int max = existing.isEmpty()
                        ? left.getMaxStackSize()
                        : existing.getMaxStackSize();
                int free = max - existing.getCount();
                int moved = Math.min(left.getCount(), free);
                if (moved <= 0) {
                    continue;
                }
                if (existing.isEmpty()) {
                    hopper.setItem(i, left.copyWithCount(moved));
                } else {
                    existing.grow(moved);
                    hopper.setChanged();
                }
                left.shrink(moved);
            }
        }
        if (!left.isEmpty()) {
            Containers.dropItemStack(
                    level,
                    ovenPos.getX() + 0.5,
                    ovenPos.getY() + 1.0,
                    ovenPos.getZ() + 0.5,
                    left);
        }
    }

    private void setVisualBurning(
            ServerLevel level, BlockPos pos, BlockState current, boolean burning
    ) {
        if (current.getValue(PizzaOvenBlock.LIT) != burning) {
            level.setBlock(pos, current.setValue(PizzaOvenBlock.LIT, burning),
                    Block.UPDATE_ALL);
        }
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("block.italiansdelight.pizza_oven");
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inv) {
        return new PizzaOvenMenu(id, inv, this, data);
    }

    @Override
    public int getContainerSize() {
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

    /**
     * Direct peel insertion is an alternative to the GUI. One pizza total is
     * allowed in INPUT + RESULT, and only a raw pizza can be baked.
     */
    public boolean insertFromPeel(ItemStack rawPizza) {
        if (rawPizza.isEmpty() || rawPizza.getCount() != 1
                || !rawPizza.is(ModItems.RAW_PIZZA)
                || rawPizza.get(ModDataComponents.PIZZA_CONTENTS) == null
                || !items.get(INPUT).isEmpty()
                || !items.get(RESULT).isEmpty()) {
            return false;
        }
        setItem(INPUT, rawPizza.copy());
        setChanged();
        return true;
    }

    /**
     * Direct peel pickup prefers the finished pizza. Partially cooked input
     * becomes raw again: progress is RESET on removal, like a vanilla furnace.
     * Burning fuel is left untouched.
     */
    public ItemStack takeForPeel() {
        int slot;
        if (items.get(RESULT).is(ModItems.COOKED_PIZZA)) {
            slot = RESULT;
        } else if (items.get(INPUT).is(ModItems.RAW_PIZZA)) {
            slot = INPUT;
        } else {
            return ItemStack.EMPTY;
        }
        if (items.get(slot).getCount() != 1
                || items.get(slot).get(ModDataComponents.PIZZA_CONTENTS) == null) {
            return ItemStack.EMPTY;
        }
        ItemStack taken = removeItem(slot, 1);
        if (!taken.isEmpty()) {
            setChanged();
        }
        return taken;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return switch (slot) {
            case INPUT -> stack.is(ModItems.RAW_PIZZA)
                    && stack.get(ModDataComponents.PIZZA_CONTENTS) != null
                    && items.get(RESULT).isEmpty();
            case FUEL -> level != null && level.fuelValues().isFuel(stack);
            default -> false;
        };
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot == INPUT) {
            // Every manual/hopper replacement starts an entirely NEW cooking
            // cycle. Source ItemStack never retains the previous progress.
            cookingProgress = 0;
        }
        super.setItem(slot, stack);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack taken = super.removeItem(slot, amount);
        if (slot == INPUT && !taken.isEmpty()) {
            cookingProgress = 0;
            setChanged();
        }
        return taken;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack taken = super.removeItemNoUpdate(slot);
        if (slot == INPUT && !taken.isEmpty()) {
            cookingProgress = 0;
            setChanged();
        }
        return taken;
    }

    @Override
    public void clearContent() {
        super.clearContent();
        cookingProgress = 0;
        setChanged();
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
        return side == Direction.DOWN
                && (slot == RESULT && stack.is(ModItems.COOKED_PIZZA)
                    || slot == FUEL && stack.is(Items.BUCKET));
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, items);
        energyRemaining = Math.max(0, input.getIntOr("EnergyRemaining", 0));
        fuelTotal = Math.max(0, input.getIntOr("FuelTotal", 0));
        cookingProgress = Math.clamp(
                input.getIntOr("CookTime", 0), 0, PizzaBaking.MAX_PROGRESS);
        // Older oven saves placed progress on the raw pizza, not in the oven:
        // their missing CookTime correctly defaults to 0, just as a new input.
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        ContainerHelper.saveAllItems(output, items);
        output.putInt("EnergyRemaining", energyRemaining);
        output.putInt("FuelTotal", fuelTotal);
        output.putInt("CookTime", cookingProgress);
        super.saveAdditional(output);
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (level != null && !level.isClientSide()) {
            Containers.dropContents(level, pos, this);
        }
        super.preRemoveSideEffects(pos, state);
    }
}
