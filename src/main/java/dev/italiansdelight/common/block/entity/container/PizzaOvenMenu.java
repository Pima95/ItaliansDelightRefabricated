package dev.italiansdelight.common.block.entity.container;

import dev.italiansdelight.common.block.entity.PizzaOvenBlockEntity;
import dev.italiansdelight.common.registry.ModDataComponents;
import dev.italiansdelight.common.registry.ModItems;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Server menu for the Pizza Oven. Intentionally advertises vanilla
 * MenuType.FURNACE, not a new menu type. On the client Minecraft constructs
 * its usual FurnaceMenu/FurnaceScreen: identical slot positions and 4 data
 * fields, with the standard fire and arrow sprites.
 *
 * <p>This server menu has custom input validation instead of vanilla's
 * smelting recipe lookup, so it never accidentally cooks pizza through the
 * ordinary vanilla recipe system (which would discard its components).
 */
public final class PizzaOvenMenu extends AbstractContainerMenu {
    private static final int PLAYER_START = PizzaOvenBlockEntity.SIZE;
    private static final int PLAYER_END = PLAYER_START + Inventory.INVENTORY_SIZE;
    private final Container container;

    public PizzaOvenMenu(
            int id, Inventory inventory, Container container, ContainerData data
    ) {
        super(MenuType.FURNACE, id);
        checkContainerSize(container, PizzaOvenBlockEntity.SIZE);
        checkContainerDataCount(data, 4);
        this.container = container;
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
