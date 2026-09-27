package dev.italiansdelight.common.block.entity.container;

import dev.italiansdelight.common.block.entity.CheeseVatBlockEntity;
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
import net.minecraft.world.item.Items;

/**
 * Synchronized Cheese Vat menu. Defines machine slots, player inventory, GUI data, and shift-click behavior.
 */

public class CheeseVatMenu extends AbstractContainerMenu {

    // -------------------- Menu indices and sizes --------------------
    private static final int DATA_COUNT = 5;

    public static final int TOGGLE_WHEY_FLOW_BUTTON = 0;

    // Five real slots plus the virtual preview slot.
    private static final int MACHINE_SLOT_COUNT =
        CheeseVatBlockEntity.CONTAINER_SIZE + 1;

    private static final int PLAYER_INVENTORY_START =
        MACHINE_SLOT_COUNT;

    private static final int PLAYER_INVENTORY_END =
        PLAYER_INVENTORY_START
            + Inventory.INVENTORY_SIZE;

    private final Container container;
    private final ContainerData data;

    // Client constructor: uses placeholder containers synchronized with
    // data received from the server.
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

    // Server constructor: links the menu to the actual BlockEntity.
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

        // Ingredients
        for (
            int i = 0;
            i < CheeseVatBlockEntity.INPUT_SLOT_COUNT;
            i++
        ) {
            this.addSlot(
                new Slot(
                    container,
                    i,
                    17 + i * 18,
                    26
                )
            );
        }

        // Container
        this.addSlot(
            new Slot(
                container,
                CheeseVatBlockEntity.CONTAINER_SLOT,
                79,
                55
            )
        );

        // Real output: contains only items that have already been packaged.
        this.addSlot(
            new Slot(
                container,
                CheeseVatBlockEntity.OUTPUT_SLOT,
                111,
                55
            ) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }

                @Override
                public void onTake(Player player, ItemStack stack) {
                    super.onTake(player, stack);
                    if (CheeseVatMenu.this.container instanceof CheeseVatBlockEntity vat) {
                        vat.awardExperience();
                    }
                }
            }
        );

        // Preview of the cooked product, to the right of the arrow.
        // It can never be picked up or replaced.
        this.addSlot(
            new Slot(
                new SimpleContainer(1),
                0,
                111,
                26
            ) {
                @Override
                public ItemStack getItem() {
                    if (CheeseVatMenu.this.container instanceof CheeseVatBlockEntity vat) {
                        return vat.getPendingPreview();
                    }
                    return super.getItem();
                }

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

        // Player inventory
        this.addStandardInventorySlots(
            inventory,
            8,
            84
        );

        this.addDataSlots(
            data
        );
    }

    /**
     * Converts the actual recipe progress into the 24 pixels used by the
     * GUI progress arrow.
     */
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

    public boolean isWheyFlowEnabled() {
        return data.get(3) != 0;
    }

    public int getWheyAmount() {
        return data.get(4);
    }

    public int getWheyCapacity() {
        return CheeseVatBlockEntity.WHEY_TANK_CAPACITY;
    }

    public int getWheyLevelScaled(int height) {
        int amount = getWheyAmount();

        if (amount <= 0 || height <= 0) {
            return 0;
        }

        int scaled =
            amount
                * height
                / CheeseVatBlockEntity.WHEY_TANK_CAPACITY;

        return Math.min(
            height,
            Math.max(
                1,
                scaled
            )
        );
    }

    @Override
    public boolean clickMenuButton(
        Player player,
        int id
    ) {
        if (
            id == TOGGLE_WHEY_FLOW_BUTTON
            && container instanceof CheeseVatBlockEntity vat
        ) {
            vat.toggleWheyFlow();
            return true;
        }

        return false;
    }

    /**
     * Handles shift-click: from the machine to the player's inventory,
     * or from the inventory to the container/ingredient slots.
     */
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

            // Route the containers used by the built-in recipes to their slot.
            // Other datapack containers can still be inserted manually.
            boolean isContainer = stack.is(Items.BOWL) || stack.is(Items.GLASS_BOTTLE);
            int start = isContainer ? CheeseVatBlockEntity.CONTAINER_SLOT : 0;
            int end = isContainer ? CheeseVatBlockEntity.CONTAINER_SLOT + 1
                : CheeseVatBlockEntity.INPUT_SLOT_COUNT;
            if (
                !moveItemStackTo(
                    stack,
                    start,
                    end,
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

        slot.onTake(player, copy.copyWithCount(copy.getCount() - stack.getCount()));
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
