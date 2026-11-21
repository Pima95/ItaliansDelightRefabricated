package dev.italiansdelight.common.block.entity;

import dev.italiansdelight.common.registry.ModBlockEntities;
import dev.italiansdelight.common.registry.ModDataComponents;
import dev.italiansdelight.common.registry.ModItems;
import dev.italiansdelight.common.block.entity.container.BrewingBarrelMenu;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Items;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Rustic-inspired closed fermenter with persistent input/output tanks.
 * The existing liquid Barrel's capacity, save format and fluid logic remain untouched.
 * Quality belongs to each single wine batch and is written to individual wine bottles.
 */
public final class BrewingBarrelBlockEntity extends BlockEntity implements Container, MenuProvider {
    // Original Rustic six-slot order: three containers above the tanks, three returns below.
    public static final int INPUT_IN = 0, OUTPUT_IN = 1, CULTURE_IN = 2;
    public static final int INPUT_OUT = 3, OUTPUT_OUT = 4, CULTURE_OUT = 5;
    public static final int SIZE = 6;
    public static final int JUICE_PURPLE = 0, JUICE_WHITE = 1;
    private NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
    private int juiceKind = JUICE_PURPLE;
    private final ContainerData menuData = new ContainerData() {
        @Override public int get(int index) {
            return switch (index) {
                case 0 -> juiceAmount;
                case 1 -> wineAmount;
                case 2 -> cultureAmount;
                case 3 -> progress;
                case 4 -> juiceKind;
                case 5 -> wineQuality;
                case 6 -> cultureQuality;
                default -> 0;
            };
        }
        @Override public void set(int index, int value) {
            switch (index) {
                case 0 -> juiceAmount = Math.clamp(value, 0, CAPACITY);
                case 1 -> wineAmount = Math.clamp(value, 0, CAPACITY);
                case 2 -> cultureAmount = Math.clamp(value, 0, CULTURE_CAPACITY);
                case 3 -> progress = Math.clamp(value, 0, BREW_TIME);
                case 4 -> juiceKind = Math.clamp(value, JUICE_PURPLE, JUICE_WHITE);
                case 5 -> wineQuality = Math.clamp(value, 0, 100);
                case 6 -> cultureQuality = Math.clamp(value, -1, 100);
                default -> {}
            }
        }
        @Override public int getCount() { return 7; }
    };

    public static final int CAPACITY = 8000;
    public static final int BREW_TIME = 12000;
    // Rustic has an independent 1,000 mB auxiliary/booze culture tank.
    // The previous GUI-only version had 250 mB and stored only a quality.
    public static final int CULTURE_CAPACITY = 1000;
    public static final int BOTTLE_AMOUNT = 250;
    public static final int BUCKET_AMOUNT = 1000;
    public static final int QUALITY_CHANGE_MIN = -1;
    public static final int QUALITY_CHANGE_MAX = 4;

    private int juiceAmount;
    private int wineAmount;
    private int wineQuality;
    private int cultureQuality = -1;
    private int cultureAmount;
    private int progress;

    public BrewingBarrelBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BREWING_BARREL, pos, state);
    }

    public int getJuiceAmount() { return juiceAmount; }
    public int getWineAmount() { return wineAmount; }
    public int getProgress() { return progress; }
    public int getWineQuality() { return wineQuality; }
    public int getCultureQuality() { return cultureQuality; }
    public int getCultureAmount() { return cultureAmount; }
    public int getJuiceKind() { return juiceKind; }

    @Override public Component getDisplayName() {
        return Component.translatable(getBlockState().getBlock().getDescriptionId());
    }
    @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new BrewingBarrelMenu(id, inventory, this, menuData);
    }
    @Override public int getContainerSize() { return SIZE; }
    @Override public boolean isEmpty() {
        for (ItemStack item : items) if (!item.isEmpty()) return false;
        return true;
    }
    @Override public ItemStack getItem(int slot) {
        return slot >= 0 && slot < SIZE ? items.get(slot) : ItemStack.EMPTY;
    }
    @Override public ItemStack removeItem(int slot, int count) {
        if (slot < 0 || slot >= SIZE) return ItemStack.EMPTY;
        ItemStack taken = ContainerHelper.removeItem(items, slot, count);
        if (!taken.isEmpty()) changedAndSync();
        return taken;
    }
    @Override public ItemStack removeItemNoUpdate(int slot) {
        return slot >= 0 && slot < SIZE ? ContainerHelper.takeItem(items, slot) : ItemStack.EMPTY;
    }
    @Override public void setItem(int slot, ItemStack stack) {
        if (slot < 0 || slot >= SIZE) return;
        items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) stack.setCount(getMaxStackSize());
        changedAndSync();
    }
    @Override public void clearContent() {
        items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        changedAndSync();
    }
    @Override public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) {
        return switch (slot) {
            case INPUT_IN -> stack.is(Items.GLASS_BOTTLE) || stack.is(Items.BUCKET)
                || stack.is(ModItems.GRAPE_JUICE_BOTTLE) || stack.is(ModItems.GRAPE_JUICE_BUCKET)
                || stack.is(ModItems.WHITE_GRAPE_JUICE_BOTTLE) || stack.is(ModItems.WHITE_GRAPE_JUICE_BUCKET);
            case OUTPUT_IN -> stack.is(Items.GLASS_BOTTLE);
            case CULTURE_IN -> stack.is(Items.GLASS_BOTTLE) || stack.is(ModItems.WINE_BOTTLE)
                || stack.is(Items.BUCKET) || stack.is(ModItems.WINE_BUCKET);
            default -> false; // Result slots are extraction-only.
        };
    }


    public boolean insertJuice(int amount) {
        if (amount <= 0 || amount % 250 != 0 || juiceAmount > CAPACITY - amount)
            return false;
        juiceAmount += amount;
        changedAndSync();
        return true;
    }

    /** Return -1 if output insufficient; otherwise returns the output batch's quality. */
    public int extractWine(int amount) {
        if (amount <= 0 || amount % 250 != 0 || wineAmount < amount)
            return -1;
        int quality = wineQuality;
        wineAmount -= amount;
        if (wineAmount == 0) wineQuality = 0;
        changedAndSync();
        return quality;
    }

    /** Optional inoculation: a culture can be stored, extracted and reused. */
    public boolean insertCulture(int quality) {
        if (cultureAmount > CULTURE_CAPACITY - BOTTLE_AMOUNT || quality < 0 || quality > 100
                || (cultureAmount != 0 && cultureQuality != quality)) return false;
        cultureQuality = quality;
        cultureAmount += BOTTLE_AMOUNT;
        changedAndSync();
        return true;
    }

    /** Drain one 250 mB bottle of culture, retaining the remaining liquid. */
    public int takeCulture() {
        if (cultureAmount < BOTTLE_AMOUNT) return -1;
        int quality = cultureQuality;
        cultureAmount -= BOTTLE_AMOUNT;
        if (cultureAmount == 0) cultureQuality = -1;
        changedAndSync();
        return quality;
    }

    public static ItemStack wineBottle(int quality) {
        ItemStack result = new ItemStack(ModItems.WINE_BOTTLE);
        result.set(ModDataComponents.WINE_QUALITY, Math.clamp(quality, 0, 100));
        return result;
    }

    public static ItemStack wineBucket(int quality) {
        ItemStack result = new ItemStack(ModItems.WINE_BUCKET);
        result.set(ModDataComponents.WINE_QUALITY, Math.clamp(quality, 0, 100));
        return result;
    }

    /**
     * Rustic 1.12.2 fermentation cycle ported to native Fabric 26.3:
     *  - transfers run first; any successful transfer resets the timer;
     *  - fermentation needs juice and output capacity, NOT a culture;
     *  - after 12,000 ticks, transfer as much as fits, preserving output
     *    quality if there is already wine in that tank;
     *  - culture is optional, affects only quality, and is never consumed.
     * Both purple and white must use the registered wine result. The two
     * juice variants cannot be mixed inside a single input tank.
     */
    public static void serverTick(ServerLevel level, BlockPos pos, BlockState state,
                                  BrewingBarrelBlockEntity barrel) {
        int originalInput = barrel.juiceAmount;
        int originalOutput = barrel.wineAmount;
        int originalCulture = barrel.cultureAmount;
        barrel.handleInputContainer();
        barrel.handleOutputContainer();
        barrel.handleCultureContainer();

        if (originalInput != barrel.juiceAmount
                || originalOutput != barrel.wineAmount
                || originalCulture != barrel.cultureAmount) {
            barrel.progress = 0;
            barrel.changedAndSync();
        }

        if (barrel.juiceAmount <= 0 || barrel.wineAmount >= CAPACITY) {
            if (barrel.progress != 0) {
                barrel.progress = 0;
                barrel.changedAndSync();
            }
            return;
        }

        if (++barrel.progress >= BREW_TIME) {
            barrel.finishBrewing(level);
        } else if (barrel.progress % 20 == 0) {
            // Persist progress and sync tank/quality data at Rustic's 20-tick
            // recipe-check cadence, not via an expensive block update/tick.
            barrel.changedAndSync();
        }
    }

    /**
     * Rustic BrewingBarrelRecipe: no culture yields 5..75 quality (inclusive);
     * a matching wine culture yields cultureQuality-1..cultureQuality+4.
     * The generated value is a percentage, stored in the stable component
     * used by preexisting Italian's Delight wine bottles and buckets.
     */
    public static int brewedQuality(int auxiliaryAmount, int auxiliaryQuality,
                                    net.minecraft.util.RandomSource random) {
        if (auxiliaryAmount <= 0 || auxiliaryQuality < 0) {
            return 5 + random.nextInt(71);
        }
        return Math.clamp(auxiliaryQuality
            + QUALITY_CHANGE_MIN
            + random.nextInt(QUALITY_CHANGE_MAX - QUALITY_CHANGE_MIN + 1), 0, 100);
    }

    private void finishBrewing(ServerLevel level) {
        int amount = Math.min(juiceAmount, CAPACITY - wineAmount);
        if (amount <= 0) {
            progress = 0;
            changedAndSync();
            return;
        }
        // Original Rustic keeps the existing output fluid's quality, rather
        // than blending two batches. Generate once for an empty output tank.
        if (wineAmount == 0) {
            wineQuality = brewedQuality(cultureAmount, cultureQuality, level.getRandom());
        }
        juiceAmount -= amount;
        wineAmount += amount;
        progress = 0;
        changedAndSync();
    }

    private static boolean canReturn(ItemStack slot, ItemStack result) {
        return slot.isEmpty() || (ItemStack.isSameItemSameComponents(slot, result)
                && slot.getCount() < slot.getMaxStackSize());
    }

    /** Atomically move one processed vessel to its lower return slot. */
    private void returnContainer(int source, int target, ItemStack result) {
        ItemStack remainder = items.get(target);
        if (remainder.isEmpty()) items.set(target, result.copy());
        else remainder.grow(1);
        items.get(source).shrink(1);
        changedAndSync();
    }

    private void handleInputContainer() {
        ItemStack vessel = items.get(INPUT_IN);
        if (vessel.isEmpty()) return;
        boolean purple = vessel.is(ModItems.GRAPE_JUICE_BOTTLE)
                || vessel.is(ModItems.GRAPE_JUICE_BUCKET);
        boolean white = vessel.is(ModItems.WHITE_GRAPE_JUICE_BOTTLE)
                || vessel.is(ModItems.WHITE_GRAPE_JUICE_BUCKET);
        if (purple || white) {
            int kind = white ? JUICE_WHITE : JUICE_PURPLE;
            int amount = vessel.is(ModItems.GRAPE_JUICE_BUCKET)
                    || vessel.is(ModItems.WHITE_GRAPE_JUICE_BUCKET) ? 1000 : 250;
            ItemStack empty = new ItemStack(amount == 1000 ? Items.BUCKET : Items.GLASS_BOTTLE);
            if ((juiceAmount == 0 || juiceKind == kind) && juiceAmount <= CAPACITY - amount
                    && canReturn(items.get(INPUT_OUT), empty)) {
                if (juiceAmount == 0) juiceKind = kind;
                juiceAmount += amount;
                returnContainer(INPUT_IN, INPUT_OUT, empty);
            }
        } else {
            int amount = vessel.is(Items.BUCKET) ? 1000 : 250;
            ItemStack filled = new ItemStack(juiceKind == JUICE_WHITE
                ? (amount == 1000 ? ModItems.WHITE_GRAPE_JUICE_BUCKET : ModItems.WHITE_GRAPE_JUICE_BOTTLE)
                : (amount == 1000 ? ModItems.GRAPE_JUICE_BUCKET : ModItems.GRAPE_JUICE_BOTTLE));
            if (juiceAmount >= amount && canReturn(items.get(INPUT_OUT), filled)) {
                juiceAmount -= amount;
                returnContainer(INPUT_IN, INPUT_OUT, filled);
            }
        }
    }

    private void handleOutputContainer() {
        ItemStack vessel = items.get(OUTPUT_IN);
        if (!vessel.is(Items.GLASS_BOTTLE) || wineAmount < 250) return;
        ItemStack filled = wineBottle(wineQuality);
        if (canReturn(items.get(OUTPUT_OUT), filled)) {
            wineAmount -= 250;
            if (wineAmount == 0) wineQuality = 0;
            returnContainer(OUTPUT_IN, OUTPUT_OUT, filled);
        }
    }

    private void handleCultureContainer() {
        ItemStack vessel = items.get(CULTURE_IN);
        if (vessel.isEmpty()) return;

        boolean wineBottle = vessel.is(ModItems.WINE_BOTTLE);
        boolean wineBucket = vessel.is(ModItems.WINE_BUCKET);
        if (wineBottle || wineBucket) {
            int amount = wineBucket ? BUCKET_AMOUNT : BOTTLE_AMOUNT;
            int quality = Math.clamp(vessel.getOrDefault(ModDataComponents.WINE_QUALITY, 0), 0, 100);
            ItemStack empty = new ItemStack(wineBucket ? Items.BUCKET : Items.GLASS_BOTTLE);
            if (cultureAmount <= CULTURE_CAPACITY - amount
                    && (cultureAmount == 0 || cultureQuality == quality)
                    && canReturn(items.get(CULTURE_OUT), empty)) {
                cultureAmount += amount;
                cultureQuality = quality;
                returnContainer(CULTURE_IN, CULTURE_OUT, empty);
            }
            return;
        }

        boolean emptyBottle = vessel.is(Items.GLASS_BOTTLE);
        boolean emptyBucket = vessel.is(Items.BUCKET);
        int amount = emptyBucket ? BUCKET_AMOUNT : BOTTLE_AMOUNT;
        if ((emptyBottle || emptyBucket) && cultureAmount >= amount) {
            ItemStack filled = emptyBucket ? wineBucket(cultureQuality) : wineBottle(cultureQuality);
            if (canReturn(items.get(CULTURE_OUT), filled)) {
                cultureAmount -= amount;
                if (cultureAmount == 0) cultureQuality = -1;
                returnContainer(CULTURE_IN, CULTURE_OUT, filled);
            }
        }
    }

    public void preserveContents(ItemStack stack, HolderLookup.Provider registries) {
        if (isEmpty() && juiceAmount == 0 && wineAmount == 0 && cultureAmount == 0 && progress == 0) return;
        TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, registries);
        saveAdditional(output);
        BlockItem.setBlockEntityData(stack, getType(), output);
    }

    private void changedAndSync() {
        setChanged();
        if (level != null && !level.isClientSide()) {
            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_CLIENTS);
        }
    }

    @Override public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return saveWithoutMetadata(provider);
    }

    @Override public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, items);
        output.putInt("JuiceAmount", juiceAmount);
        output.putInt("JuiceKind", juiceKind);
        output.putInt("WineAmount", wineAmount);
        output.putInt("WineQuality", wineQuality);
        output.putInt("CultureQuality", cultureQuality);
        output.putInt("CultureAmount", cultureAmount);
        output.putInt("BrewTime", progress);
    }

    @Override protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, items);
        juiceKind = Math.clamp(input.getIntOr("JuiceKind", JUICE_PURPLE), JUICE_PURPLE, JUICE_WHITE);
        juiceAmount = Math.clamp(input.getIntOr("JuiceAmount", 0), 0, CAPACITY);
        wineAmount = Math.clamp(input.getIntOr("WineAmount", 0), 0, CAPACITY);
        wineQuality = Math.clamp(input.getIntOr("WineQuality", 0), 0, 100);
        cultureQuality = Math.clamp(input.getIntOr("CultureQuality", -1), -1, 100);
        // Older saved barrels stored only the quality of a 250 mB culture
        // bottle. Migrate it without discarding the player's existing wine.
        cultureAmount = Math.clamp(input.getIntOr("CultureAmount",
            cultureQuality >= 0 ? BOTTLE_AMOUNT : 0), 0, CULTURE_CAPACITY);
        if (cultureAmount == 0) cultureQuality = -1;
        if (cultureAmount > 0 && cultureQuality < 0) cultureAmount = 0;
        progress = Math.clamp(input.getIntOr("BrewTime", 0), 0, BREW_TIME - 1);
        if (wineAmount == 0) wineQuality = 0;
        if (juiceAmount == 0 || wineAmount >= CAPACITY) progress = 0;
    }
}
