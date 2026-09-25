package com.piergiuseppe.italiansdelight.common.block.entity.container;

import com.piergiuseppe.italiansdelight.common.block.entity.CheeseVatBlockEntity;
import com.piergiuseppe.italiansdelight.common.registry.ModMenuTypes;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class CheeseVatMenu extends AbstractContainerMenu {

    private static final int DATA_COUNT = 3;

    // Three inputs, the container, the output and the preview are visible.
    private static final int MACHINE_SLOT_COUNT =
        CheeseVatBlockEntity.PREVIEW_SLOT + 1;

    private static final int PLAYER_INVENTORY_START =
        MACHINE_SLOT_COUNT;

    private static final int PLAYER_INVENTORY_END =
        PLAYER_INVENTORY_START
            + Inventory.INVENTORY_SIZE;

    private final Container container;
    private final ContainerData data;

    // Client
    public CheeseVatMenu(
        int containerId,
        Inventory inventory
    ) {
        this(
            containerId,
            inventory,
            new SimpleContainer(
                CheeseVatBlockEntity.CONTAINER_SIZE
            ),
            new SimpleContainerData(
                DATA_COUNT
            )
        );
    }

    // Server
    public CheeseVatMenu(
        int containerId,
        Inventory inventory,
        Container container,
        ContainerData data
    ) {
        super(
            ModMenuTypes.CHEESE_VAT,
            containerId
        );

        checkContainerSize(
            container,
            CheeseVatBlockEntity.CONTAINER_SIZE
        );

        checkContainerDataCount(
            data,
            DATA_COUNT
        );

        this.container = container;
        this.data = data;

        container.startOpen(
            inventory.player
        );

        // Ingredienti
        for (
            int i = 0;
            i < CheeseVatBlockEntity.INPUT_SLOT_COUNT;
            i++
        ) {
            this.addSlot(
                new Slot(
                    container,
                    i,
                    30 + i * 18,
                    26
                )
            );
        }

        // Contenitore
        this.addSlot(
            new Slot(
                container,
                CheeseVatBlockEntity.CONTAINER_SLOT,
                92,
                55
            )
        );

        // Risultato reale: contiene soltanto oggetti già confezionati.
        this.addSlot(
            new Slot(
                container,
                CheeseVatBlockEntity.OUTPUT_SLOT,
                124,
                55
            ) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }
            }
        );

        // Anteprima del prodotto cotto, a destra della freccia.
        // Non è mai prelevabile né sostituibile.
        this.addSlot(
            new Slot(
                container,
                CheeseVatBlockEntity.PREVIEW_SLOT,
                124,
                26
            ) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }

                @Override
                public boolean mayPickup(Player player) {
                    return false;
                }

                @Override
                public ItemStack remove(int amount) {
                    return ItemStack.EMPTY;
                }
            }
        );

        // Inventario giocatore
        this.addStandardInventorySlots(
            inventory,
            8,
            84
        );

        this.addDataSlots(
            data
        );
    }

    public int getCookProgressionScaled() {

        int cookTime =
            data.get(0);

        int cookTimeTotal =
            data.get(1);

        if (
            cookTimeTotal == 0
            || cookTime == 0
        ) {
            return 0;
        }

        int scaled =
            cookTime
                * 24
                / cookTimeTotal;

        return Math.min(
            24,
            Math.max(
                1,
                scaled
            )
        );
    }

    public boolean isHeated() {
        return data.get(2) != 0;
    }

    @Override
    public ItemStack quickMoveStack(
        Player player,
        int slotIndex
    ) {
        Slot slot =
            this.slots.get(slotIndex);

        if (!slot.hasItem() || !slot.mayPickup(player)) {
            return ItemStack.EMPTY;
        }

        ItemStack stack =
            slot.getItem();

        ItemStack copy =
            stack.copy();

        if (
            slotIndex
                < MACHINE_SLOT_COUNT
        ) {

            if (
                !moveItemStackTo(
                    stack,
                    PLAYER_INVENTORY_START,
                    PLAYER_INVENTORY_END,
                    true
                )
            ) {
                return ItemStack.EMPTY;
            }

        } else {

            // Shift-click dal giocatore:
            // prova solamente i 3 slot ingredienti.
            if (
                !moveItemStackTo(
                    stack,
                    0,
                    CheeseVatBlockEntity.INPUT_SLOT_COUNT,
                    false
                )
            ) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(
                ItemStack.EMPTY
            );
        } else {
            slot.setChanged();
        }

        return copy;
    }

    @Override
    public boolean stillValid(
        Player player
    ) {
        return container.stillValid(
            player
        );
    }

    @Override
    public void removed(
        Player player
    ) {
        super.removed(player);

        container.stopOpen(
            player
        );
    }
}