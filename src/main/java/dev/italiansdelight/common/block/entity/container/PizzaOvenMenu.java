package dev.italiansdelight.common.block.entity.container;

import dev.italiansdelight.common.block.entity.PizzaOvenBlockEntity;
import dev.italiansdelight.common.registry.ModDataComponents;
import dev.italiansdelight.common.registry.ModItems;
import dev.italiansdelight.common.registry.ModMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Pizza-only menu, registered under ModMenuTypes.PIZZA_OVEN.
 *
 * <p>Uses the EXACT vanilla furnace slot positions and four data fields,
 * but NOT vanilla MenuType.FURNACE: that type automatically opens a
 * FurnaceScreen with its unrelated vanilla recipe book.
 * The paired PizzaOvenScreen uses the original vanilla GUI resources with
 * NO recipe-book panel or button.
 *
 * <p>Server validates every input and shift-click: no smelting recipe lookup
 * can lose the pizza's composition data.
 */
public final class PizzaOvenMenu extends AbstractContainerMenu {
    private static final int PLAYER_START = PizzaOvenBlockEntity.SIZE;
    private static final int PLAYER_END = PLAYER_START + Inventory.INVENTORY_SIZE;
    private final Container container;
    private final ContainerData data;

    /** Client-side constructor: vanilla menu synchronization fills slots/data. */
    public PizzaOvenMenu(int id, Inventory inventory) {
        this(id, inventory, new SimpleContainer(PizzaOvenBlockEntity.SIZE),
                new SimpleContainerData(4));
    }

    public PizzaOvenMenu(
            int id, Inventory inventory, Container container, ContainerData data
    ) {
        super(ModMenuTypes.PIZZA_OVEN, id);
        checkContainerSize(container, PizzaOvenBlockEntity.SIZE);
        checkContainerDataCount(data, 4);
        this.container = container;
        this.data = data;
        container.startOpen(inventory.player);

        addSlot(new Slot(container, PizzaOvenBlockEntity.INPUT, 56, 17) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return container.canPlaceItem(PizzaOvenBlockEntity.INPUT, stack);
            }
            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });
        addSlot(new Slot(container, PizzaOvenBlockEntity.FUEL, 56, 53) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return container.canPlaceItem(PizzaOvenBlockEntity.FUEL, stack);
            }
        });
        addSlot(new Slot(container, PizzaOvenBlockEntity.RESULT, 116, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        addStandardInventorySlots(inventory, 8, 84);
        addDataSlots(data);
    }

    /** Exact vanilla-furnace 24-pixel progress arrow. */
    public int getCookProgressionScaled() {
        int progress = data.get(2);
        int total = data.get(3);
        return progress > 0 && total > 0
                ? Math.min(24, progress * 24 / total)
                : 0;
    }

    /** Exact vanilla-furnace 14-pixel lit indicator. */
    public int getBurnProgressionScaled() {
        int burning = data.get(0);
        int maximum = data.get(1);
        if (burning <= 0) {
            return 0;
        }
        int height = burning * 13 / (maximum > 0 ? maximum : 200);
        return Math.min(13, Math.max(1, height));
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= slots.size()) return ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (!slot.hasItem() || !slot.mayPickup(player)) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        if (index < PLAYER_START) {
            if (!moveItemStackTo(stack, PLAYER_START, PLAYER_END, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (stack.is(ModItems.RAW_PIZZA)
                    && stack.get(ModDataComponents.PIZZA_CONTENTS) != null
                    && container.canPlaceItem(PizzaOvenBlockEntity.INPUT, stack)) {
                if (!moveItemStackTo(stack, PizzaOvenBlockEntity.INPUT,
                        PizzaOvenBlockEntity.INPUT + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (container.canPlaceItem(PizzaOvenBlockEntity.FUEL, stack)) {
                if (!moveItemStackTo(stack, PizzaOvenBlockEntity.FUEL,
                        PizzaOvenBlockEntity.FUEL + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        slot.onTake(player, original.copyWithCount(original.getCount() - stack.getCount()));
        return original;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        container.stopOpen(player);
    }
}
