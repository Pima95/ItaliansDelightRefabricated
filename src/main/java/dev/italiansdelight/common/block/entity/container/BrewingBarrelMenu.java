package dev.italiansdelight.common.block.entity.container;

import dev.italiansdelight.common.block.entity.BrewingBarrelBlockEntity;
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
 * Port of Rustic's brewing barrel slot arrangement (176x166 GUI):
 * 0 input (62,7), 1 output-tank vessel (116,7), 2 culture vessel (26,15);
 * 3,4,5 returned containers respectively at (62,63), (116,63), (26,55).
 *
 * Only vessels are handled here: there is intentionally no wine recipe.
 */
public final class BrewingBarrelMenu extends AbstractContainerMenu {
    private static final int MACHINE_END = BrewingBarrelBlockEntity.SIZE;
    private static final int PLAYER_START = MACHINE_END;
    private static final int PLAYER_END = PLAYER_START + Inventory.INVENTORY_SIZE;
    private final Container container;
    private final ContainerData data;

    public BrewingBarrelMenu(int id, Inventory inventory) {
        this(id, inventory, new SimpleContainer(MACHINE_END), new SimpleContainerData(5));
    }

    public BrewingBarrelMenu(int id, Inventory inventory, Container container, ContainerData data) {
        super(ModMenuTypes.BREWING_BARREL, id);
        checkContainerSize(container, MACHINE_END);
        checkContainerDataCount(data, 5);
        this.container = container;
        this.data = data;
        container.startOpen(inventory.player);

        addInputSlot(BrewingBarrelBlockEntity.INPUT_IN, 62, 7);
        addInputSlot(BrewingBarrelBlockEntity.OUTPUT_IN, 116, 7);
        addInputSlot(BrewingBarrelBlockEntity.CULTURE_IN, 26, 15);
        addReturnSlot(BrewingBarrelBlockEntity.INPUT_OUT, 62, 63);
        addReturnSlot(BrewingBarrelBlockEntity.OUTPUT_OUT, 116, 63);
        addReturnSlot(BrewingBarrelBlockEntity.CULTURE_OUT, 26, 55);
        addStandardInventorySlots(inventory, 8, 84);
        addDataSlots(data);
    }

    private void addInputSlot(int index, int x, int y) {
        addSlot(new Slot(container, index, x, y) {
            @Override public boolean mayPlace(ItemStack stack) {
                return container.canPlaceItem(index, stack);
            }
        });
    }

    private void addReturnSlot(int index, int x, int y) {
        addSlot(new Slot(container, index, x, y) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
        });
    }

    public int getInputAmount() { return data.get(0); }
    public int getOutputAmount() { return data.get(1); }
    public int getCultureAmount() { return data.get(2); }
    public int getProgress() { return data.get(3); }
    public boolean hasWhiteGrapeJuice() { return data.get(4) == BrewingBarrelBlockEntity.JUICE_WHITE; }

    @Override public boolean stillValid(Player player) {
        return container.stillValid(player);
    }

    @Override public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= slots.size()) return ItemStack.EMPTY;
        Slot source = slots.get(index);
        if (!source.hasItem() || !source.mayPickup(player)) return ItemStack.EMPTY;
        ItemStack stack = source.getItem();
        ItemStack original = stack.copy();
        if (index < MACHINE_END) {
            if (!moveItemStackTo(stack, PLAYER_START, PLAYER_END, true)) return ItemStack.EMPTY;
        } else {
            boolean placed = false;
            if (stack.is(Items.GLASS_BOTTLE)) {
                // Reuse the output tank first, then source and culture tanks.
                for (int target : new int[] {BrewingBarrelBlockEntity.OUTPUT_IN,
                        BrewingBarrelBlockEntity.INPUT_IN, BrewingBarrelBlockEntity.CULTURE_IN}) {
                    if (container.canPlaceItem(target, stack)
                            && moveItemStackTo(stack, target, target + 1, false)) {
                        placed = true;
                        break;
                    }
                }
            } else {
                int target = container.canPlaceItem(BrewingBarrelBlockEntity.INPUT_IN, stack)
                    ? BrewingBarrelBlockEntity.INPUT_IN
                    : container.canPlaceItem(BrewingBarrelBlockEntity.CULTURE_IN, stack)
                        ? BrewingBarrelBlockEntity.CULTURE_IN : -1;
                placed = target >= 0 && moveItemStackTo(stack, target, target + 1, false);
            }
            if (!placed) {
                // Normal inventory/hotbar exchange still works for unrelated items.
                if (index < PLAYER_START + 27) {
                    if (!moveItemStackTo(stack, PLAYER_START + 27, PLAYER_END, false)) return ItemStack.EMPTY;
                } else if (!moveItemStackTo(stack, PLAYER_START, PLAYER_START + 27, false)) {
                    return ItemStack.EMPTY;
                }
            }
        }
        if (stack.isEmpty()) source.setByPlayer(ItemStack.EMPTY);
        else source.setChanged();
        source.onTake(player, original.copyWithCount(original.getCount() - stack.getCount()));
        return original;
    }

    @Override public void removed(Player player) {
        super.removed(player);
        container.stopOpen(player);
    }
}
