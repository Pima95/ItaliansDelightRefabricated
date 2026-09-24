package com.piergiuseppe.italiansdelight.menu;

import com.piergiuseppe.italiansdelight.registry.ModMenuTypes;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class CheeseVatMenu extends AbstractContainerMenu {

    private static final int CONTAINER_SIZE = 3;

    private static final int CONTAINER_START = 0;
    private static final int CONTAINER_END = CONTAINER_START + CONTAINER_SIZE;

    private static final int INVENTORY_START = CONTAINER_END;
    private static final int INVENTORY_END = INVENTORY_START + Inventory.INVENTORY_SIZE;

    private final Container container;

    // Costruttore utilizzato dal client
    public CheeseVatMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(CONTAINER_SIZE));
    }

    // Costruttore utilizzato dal server
    public CheeseVatMenu(
        int containerId,
        Inventory inventory,
        Container container
    ) {
        super(ModMenuTypes.CHEESE_VAT, containerId);

        checkContainerSize(container, CONTAINER_SIZE);

        this.container = container;

        container.startOpen(inventory.player);

        // I 3 slot della Caldaia
        for (int i = 0; i < CONTAINER_SIZE; i++) {
            this.addSlot(
                new Slot(
                    container,
                    i,
                    30 + i * 18,
                    26
                )
            );
        }

        // Inventario del giocatore
        this.addStandardInventorySlots(
            inventory,
            8,
            84
        );
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        Slot slot = this.slots.get(slotIndex);

        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        ItemStack clicked = stack.copy();

        if (slotIndex < CONTAINER_END) {
            if (!this.moveItemStackTo(
                stack,
                INVENTORY_START,
                INVENTORY_END,
                true
            )) {
                return ItemStack.EMPTY;
            }
        } else {
            if (!this.moveItemStackTo(
                stack,
                CONTAINER_START,
                CONTAINER_END,
                false
            )) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return clicked;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }
}