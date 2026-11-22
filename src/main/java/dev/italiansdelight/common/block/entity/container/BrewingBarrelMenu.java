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
 * Rustic six-slot arrangement extended to a 176x198 GUI with a 20px header:
 * 0 input (62,27), 1 output-tank vessel (116,27), 2 culture vessel (26,35);
 * 3,4,5 returned containers at (62,83), (116,83), (26,75).
 *
 * Transfers and brewing follow Rustic: six vessel slots and three synced tanks.
 */
public final class BrewingBarrelMenu extends AbstractContainerMenu {
    public static final int MACHINE_Y_OFFSET = 20;
    public static final int INVENTORY_Y_OFFSET = 32;
    private static final int MACHINE_END = BrewingBarrelBlockEntity.SIZE;
    private static final int PLAYER_START = MACHINE_END;
    private static final int PLAYER_END = PLAYER_START + Inventory.INVENTORY_SIZE;
    private final Container container;
    private final ContainerData data;

    public BrewingBarrelMenu(int id, Inventory inventory) {
        this(id, inventory, new SimpleContainer(MACHINE_END), new SimpleContainerData(9));
    }

    public BrewingBarrelMenu(int id, Inventory inventory, Container container, ContainerData data) {
        super(ModMenuTypes.BREWING_BARREL, id);
        checkContainerSize(container, MACHINE_END);
        checkContainerDataCount(data, 9);
        this.container = container;
        this.data = data;
        container.startOpen(inventory.player);

        addInputSlot(BrewingBarrelBlockEntity.INPUT_IN, 62, 7 + MACHINE_Y_OFFSET);
        addInputSlot(BrewingBarrelBlockEntity.OUTPUT_IN, 116, 7 + MACHINE_Y_OFFSET);
        addInputSlot(BrewingBarrelBlockEntity.CULTURE_IN, 26, 15 + MACHINE_Y_OFFSET);
        addReturnSlot(BrewingBarrelBlockEntity.INPUT_OUT, 62, 63 + MACHINE_Y_OFFSET);
        addReturnSlot(BrewingBarrelBlockEntity.OUTPUT_OUT, 116, 63 + MACHINE_Y_OFFSET);
        addReturnSlot(BrewingBarrelBlockEntity.CULTURE_OUT, 26, 55 + MACHINE_Y_OFFSET);
        addStandardInventorySlots(inventory, 8, 84 + INVENTORY_Y_OFFSET);
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
    public int getMaxBrewTime() { return BrewingBarrelBlockEntity.BREW_TIME; }
    public int getOutputQuality() { return data.get(5); }
    public int getCultureQuality() { return data.get(6); }
    // New field appended at index 8, keeping 0..7 unchanged for existing menus.
    public int getCultureWineKind() { return data.get(8); }
    public int getOutputKind() { return data.get(7); }
    public int getInputKind() { return data.get(4); }
    public boolean isAppleJuice() { return getInputKind() == BrewingBarrelBlockEntity.JUICE_APPLE; }
    public boolean hasVinegarOutput() {
        return getOutputKind() == BrewingBarrelBlockEntity.OUTPUT_VINEGAR
            || getOutputKind() == BrewingBarrelBlockEntity.OUTPUT_WINE_VINEGAR;
    }
    public boolean isBrewing() { return getProgress() > 0; }
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
            if (stack.is(Items.GLASS_BOTTLE) || stack.is(Items.BUCKET)) {
                // Empty bottles/buckets: prefer the finished product first,
                // then the input and culture tanks. This also allows the new
                // cider-vinegar bucket to be collected with shift-click.
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
