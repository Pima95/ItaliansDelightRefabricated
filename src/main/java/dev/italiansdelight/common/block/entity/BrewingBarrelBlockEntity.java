package dev.italiansdelight.common.block.entity;

import dev.italiansdelight.common.persistence.PortNbt;

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
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

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
    // Persisted identifiers: keep 0/1/2 and 0/1 stable for existing worlds.
    public static final int JUICE_PURPLE = 0, JUICE_WHITE = 1, JUICE_APPLE = 2;
    public static final int JUICE_CHERRY = 3, JUICE_HONEY = 4, JUICE_RED_WINE = 5, JUICE_CIDER = 6;
    public static final int OUTPUT_WINE = 0, OUTPUT_VINEGAR = 1;
    public static final int OUTPUT_WHITE_WINE = 2, OUTPUT_CHERRY_WINE = 3;
    public static final int OUTPUT_MEAD = 4, OUTPUT_CIDER = 5, OUTPUT_WINE_VINEGAR = 6;
    private NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
    private int juiceKind = JUICE_PURPLE;
    // 0 by default so barrels saved before vinegar support retain their wine.
    private int outputKind = OUTPUT_WINE;
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
                case 7 -> outputKind;
                case 8 -> cultureWineKind;
                default -> 0;
            };
        }
        @Override public void set(int index, int value) {
            switch (index) {
                case 0 -> juiceAmount = Math.clamp(value, 0, CAPACITY);
                case 1 -> wineAmount = Math.clamp(value, 0, CAPACITY);
                case 2 -> cultureAmount = Math.clamp(value, 0, CULTURE_CAPACITY);
                case 3 -> progress = Math.clamp(value, 0, BREW_TIME);
                case 4 -> juiceKind = Math.clamp(value, JUICE_PURPLE, JUICE_CIDER);
                case 5 -> wineQuality = Math.clamp(value, 0, 100);
                case 6 -> cultureQuality = Math.clamp(value, -1, 100);
                case 7 -> outputKind = Math.clamp(value, OUTPUT_WINE, OUTPUT_WINE_VINEGAR);
                case 8 -> cultureWineKind = Math.clamp(value, 0, 2);
                default -> {}
            }
        }
        @Override public int getCount() { return 9; }
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
    // 0 = red, 1 = white, 2 = cherry. Missing key in old saves means red.
    private int cultureWineKind;
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
    public int getCultureWineKind() { return cultureWineKind; }
    public int getJuiceKind() { return juiceKind; }
    public int getOutputKind() { return outputKind; }

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
                || stack.is(ModItems.WHITE_GRAPE_JUICE_BOTTLE) || stack.is(ModItems.WHITE_GRAPE_JUICE_BUCKET)
                || stack.is(ModItems.APPLE_JUICE_BOTTLE) || stack.is(ModItems.APPLE_JUICE_BUCKET)
                || stack.is(ModItems.CHERRY_JUICE_BOTTLE) || stack.is(ModItems.CHERRY_JUICE_BUCKET)
                || stack.is(Items.HONEY_BOTTLE) || stack.is(ModItems.HONEY_BUCKET)
                || stack.is(ModItems.WINE_BOTTLE) || stack.is(ModItems.WINE_BUCKET)
                || stack.is(ModItems.CIDER_BOTTLE) || stack.is(ModItems.CIDER_BUCKET);
            case OUTPUT_IN -> stack.is(Items.GLASS_BOTTLE) || stack.is(Items.BUCKET);
            case CULTURE_IN -> stack.is(Items.GLASS_BOTTLE) || stack.is(Items.BUCKET)
                || cultureWineKind(stack) >= 0;
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
     * Fermentation is recipe-gated: red grapes -> wine, apples -> cider
     * vinegar. White grapes, cherries and honey each produce their own drink.
     * An existing output of another type blocks processing to avoid mixing.
     * Any successful vessel transfer restarts the current brew as in Rustic.
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

        if (!barrel.hasMatchingRecipe()) {
            if (barrel.progress != 0) {
                barrel.progress = 0;
                barrel.changedAndSync();
            }
            return;
        }

        if (++barrel.progress >= BREW_TIME) {
            barrel.finishBrewing(level);
        } else if (barrel.progress % 20 == 0) {
            barrel.changedAndSync();
        }
    }

    /**
     * Input + optional reusable wine culture selects an exact recipe.
     * Apple juice alone still makes the original apple cider vinegar.
     * A seeded apple batch instead makes cider; remove culture to select
     * vinegar again. This preserves legacy behavior without requiring a
     * second GUI selector or an ambiguous item-only recipe.
     */
    public static int recipeOutput(int input, boolean hasCulture) {
        return switch (input) {
            case JUICE_PURPLE -> OUTPUT_WINE;
            case JUICE_WHITE -> OUTPUT_WHITE_WINE;
            case JUICE_APPLE -> hasCulture ? OUTPUT_CIDER : OUTPUT_VINEGAR;
            case JUICE_CHERRY -> OUTPUT_CHERRY_WINE;
            case JUICE_HONEY -> OUTPUT_MEAD;
            case JUICE_RED_WINE -> OUTPUT_WINE_VINEGAR;
            case JUICE_CIDER -> OUTPUT_VINEGAR;
            default -> -1;
        };
    }

    private boolean hasMatchingRecipe() {
        if (juiceAmount <= 0 || wineAmount >= CAPACITY) return false;
        int expected = recipeOutput(juiceKind, cultureAmount > 0);
        return expected >= 0 && (wineAmount == 0 || outputKind == expected);
    }

    private static boolean hasQuality(int output) {
        return output != OUTPUT_VINEGAR && output != OUTPUT_WINE_VINEGAR;
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
        if (!hasMatchingRecipe()) {
            progress = 0;
            changedAndSync();
            return;
        }
        int amount = Math.min(juiceAmount, CAPACITY - wineAmount);
        int resultKind = recipeOutput(juiceKind, cultureAmount > 0);
        if (wineAmount == 0) {
            outputKind = resultKind;
            // Every fermented drink has batch quality; both vinegars have none.
            wineQuality = hasQuality(resultKind)
                ? brewedQuality(cultureAmount, cultureQuality, level.getRandom()) : 0;
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

    private static int incomingKind(ItemStack stack) {
        if (stack.is(ModItems.GRAPE_JUICE_BOTTLE) || stack.is(ModItems.GRAPE_JUICE_BUCKET))
            return JUICE_PURPLE;
        if (stack.is(ModItems.WHITE_GRAPE_JUICE_BOTTLE) || stack.is(ModItems.WHITE_GRAPE_JUICE_BUCKET))
            return JUICE_WHITE;
        if (stack.is(ModItems.APPLE_JUICE_BOTTLE) || stack.is(ModItems.APPLE_JUICE_BUCKET))
            return JUICE_APPLE;
        if (stack.is(ModItems.CHERRY_JUICE_BOTTLE) || stack.is(ModItems.CHERRY_JUICE_BUCKET))
            return JUICE_CHERRY;
        if (stack.is(Items.HONEY_BOTTLE) || stack.is(ModItems.HONEY_BUCKET))
            return JUICE_HONEY;
        if (stack.is(ModItems.WINE_BOTTLE) || stack.is(ModItems.WINE_BUCKET))
            return JUICE_RED_WINE;
        if (stack.is(ModItems.CIDER_BOTTLE) || stack.is(ModItems.CIDER_BUCKET))
            return JUICE_CIDER;
        return -1;
    }

    private static boolean isIncomingBucket(ItemStack stack) {
        return stack.is(ModItems.GRAPE_JUICE_BUCKET)
            || stack.is(ModItems.WHITE_GRAPE_JUICE_BUCKET)
            || stack.is(ModItems.APPLE_JUICE_BUCKET)
            || stack.is(ModItems.CHERRY_JUICE_BUCKET)
            || stack.is(ModItems.HONEY_BUCKET)
            || stack.is(ModItems.WINE_BUCKET)
            || stack.is(ModItems.CIDER_BUCKET);
    }

    private static ItemStack inputContainer(int kind, boolean bucket) {
        return new ItemStack(switch (kind) {
            case JUICE_WHITE -> bucket ? ModItems.WHITE_GRAPE_JUICE_BUCKET : ModItems.WHITE_GRAPE_JUICE_BOTTLE;
            case JUICE_APPLE -> bucket ? ModItems.APPLE_JUICE_BUCKET : ModItems.APPLE_JUICE_BOTTLE;
            case JUICE_CHERRY -> bucket ? ModItems.CHERRY_JUICE_BUCKET : ModItems.CHERRY_JUICE_BOTTLE;
            case JUICE_HONEY -> bucket ? ModItems.HONEY_BUCKET : Items.HONEY_BOTTLE;
            case JUICE_RED_WINE -> bucket ? ModItems.WINE_BUCKET : ModItems.WINE_BOTTLE;
            case JUICE_CIDER -> bucket ? ModItems.CIDER_BUCKET : ModItems.CIDER_BOTTLE;
            default -> bucket ? ModItems.GRAPE_JUICE_BUCKET : ModItems.GRAPE_JUICE_BOTTLE;
        });
    }

    private static ItemStack outputContainer(int kind, boolean bucket, int quality) {
        if (kind == OUTPUT_WINE) return bucket ? wineBucket(quality) : wineBottle(quality);
        ItemStack stack = new ItemStack(switch (kind) {
            case OUTPUT_VINEGAR -> bucket ? ModItems.APPLE_CIDER_VINEGAR_BUCKET : ModItems.APPLE_CIDER_VINEGAR;
            case OUTPUT_WHITE_WINE -> bucket ? ModItems.WHITE_WINE_BUCKET : ModItems.WHITE_WINE_BOTTLE;
            case OUTPUT_CHERRY_WINE -> bucket ? ModItems.CHERRY_WINE_BUCKET : ModItems.CHERRY_WINE_BOTTLE;
            case OUTPUT_MEAD -> bucket ? ModItems.MEAD_BUCKET : ModItems.MEAD_BOTTLE;
            case OUTPUT_CIDER -> bucket ? ModItems.CIDER_BUCKET : ModItems.CIDER_BOTTLE;
            case OUTPUT_WINE_VINEGAR -> bucket ? ModItems.RED_WINE_VINEGAR_BUCKET : ModItems.RED_WINE_VINEGAR_BOTTLE;
            default -> bucket ? ModItems.WINE_BUCKET : ModItems.WINE_BOTTLE;
        });
        if (hasQuality(kind))
            stack.set(ModDataComponents.WINE_QUALITY, Math.clamp(quality, 0, 100));
        return stack;
    }

    private void handleInputContainer() {
        ItemStack vessel = items.get(INPUT_IN);
        if (vessel.isEmpty()) return;
        int kind = incomingKind(vessel);
        if (kind >= 0) {
            int amount = isIncomingBucket(vessel) ? BUCKET_AMOUNT : BOTTLE_AMOUNT;
            ItemStack empty = new ItemStack(amount == BUCKET_AMOUNT ? Items.BUCKET : Items.GLASS_BOTTLE);
            if ((juiceAmount == 0 || juiceKind == kind) && juiceAmount <= CAPACITY - amount
                    && canReturn(items.get(INPUT_OUT), empty)) {
                if (juiceAmount == 0) juiceKind = kind;
                juiceAmount += amount;
                returnContainer(INPUT_IN, INPUT_OUT, empty);
            }
        } else if (vessel.is(Items.BUCKET) || vessel.is(Items.GLASS_BOTTLE)) {
            boolean bucket = vessel.is(Items.BUCKET);
            int amount = bucket ? BUCKET_AMOUNT : BOTTLE_AMOUNT;
            ItemStack filled = inputContainer(juiceKind, bucket);
            if (juiceAmount >= amount && canReturn(items.get(INPUT_OUT), filled)) {
                juiceAmount -= amount;
                returnContainer(INPUT_IN, INPUT_OUT, filled);
            }
        }
    }

    private void handleOutputContainer() {
        ItemStack vessel = items.get(OUTPUT_IN);
        boolean bucket = vessel.is(Items.BUCKET);
        boolean bottle = vessel.is(Items.GLASS_BOTTLE);
        int amount = bucket ? BUCKET_AMOUNT : BOTTLE_AMOUNT;
        if ((!bottle && !bucket) || wineAmount < amount) return;
        ItemStack filled = outputContainer(outputKind, bucket, wineQuality);
        if (canReturn(items.get(OUTPUT_OUT), filled)) {
            wineAmount -= amount;
            if (wineAmount == 0) wineQuality = 0;
            returnContainer(OUTPUT_IN, OUTPUT_OUT, filled);
        }
    }

    private static int cultureWineKind(ItemStack stack) {
        if (stack.is(ModItems.WINE_BOTTLE) || stack.is(ModItems.WINE_BUCKET)) return 0;
        if (stack.is(ModItems.WHITE_WINE_BOTTLE) || stack.is(ModItems.WHITE_WINE_BUCKET)) return 1;
        if (stack.is(ModItems.CHERRY_WINE_BOTTLE) || stack.is(ModItems.CHERRY_WINE_BUCKET)) return 2;
        return -1;
    }

    private static ItemStack cultureWineContainer(int kind, boolean bucket, int quality) {
        ItemStack filled = new ItemStack(switch (kind) {
            case 1 -> bucket ? ModItems.WHITE_WINE_BUCKET : ModItems.WHITE_WINE_BOTTLE;
            case 2 -> bucket ? ModItems.CHERRY_WINE_BUCKET : ModItems.CHERRY_WINE_BOTTLE;
            default -> bucket ? ModItems.WINE_BUCKET : ModItems.WINE_BOTTLE;
        });
        filled.set(ModDataComponents.WINE_QUALITY, Math.clamp(quality, 0, 100));
        return filled;
    }

    private void handleCultureContainer() {
        ItemStack vessel = items.get(CULTURE_IN);
        if (vessel.isEmpty()) return;

        int incomingWineKind = cultureWineKind(vessel);
        boolean wineBucket = vessel.is(ModItems.WINE_BUCKET)
            || vessel.is(ModItems.WHITE_WINE_BUCKET) || vessel.is(ModItems.CHERRY_WINE_BUCKET);
        if (incomingWineKind >= 0) {
            int amount = wineBucket ? BUCKET_AMOUNT : BOTTLE_AMOUNT;
            int quality = Math.clamp(vessel.getOrDefault(ModDataComponents.WINE_QUALITY, 0), 0, 100);
            ItemStack empty = new ItemStack(wineBucket ? Items.BUCKET : Items.GLASS_BOTTLE);
            if (cultureAmount <= CULTURE_CAPACITY - amount
                    && (cultureAmount == 0 || (cultureQuality == quality && cultureWineKind == incomingWineKind))
                    && canReturn(items.get(CULTURE_OUT), empty)) {
                cultureAmount += amount;
                cultureQuality = quality;
                cultureWineKind = incomingWineKind;
                returnContainer(CULTURE_IN, CULTURE_OUT, empty);
            }
            return;
        }

        boolean emptyBottle = vessel.is(Items.GLASS_BOTTLE);
        boolean emptyBucket = vessel.is(Items.BUCKET);
        int amount = emptyBucket ? BUCKET_AMOUNT : BOTTLE_AMOUNT;
        if ((emptyBottle || emptyBucket) && cultureAmount >= amount) {
            ItemStack filled = cultureWineContainer(cultureWineKind, emptyBucket, cultureQuality);
            if (canReturn(items.get(CULTURE_OUT), filled)) {
                cultureAmount -= amount;
                if (cultureAmount == 0) cultureQuality = -1;
                returnContainer(CULTURE_IN, CULTURE_OUT, filled);
            }
        }
    }

    public void preserveContents(ItemStack stack, HolderLookup.Provider registries) {
        if (isEmpty() && juiceAmount == 0 && wineAmount == 0 && cultureAmount == 0 && progress == 0) return;
        CompoundTag output = new CompoundTag();
        saveAdditional(output, registries);
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

    @Override protected void saveAdditional(CompoundTag output, HolderLookup.Provider registries) {
        super.saveAdditional(output, registries);
        ContainerHelper.saveAllItems(output, items, registries);
        output.putInt("JuiceAmount", juiceAmount);
        output.putInt("JuiceKind", juiceKind);
        output.putInt("OutputKind", outputKind);
        output.putInt("WineAmount", wineAmount);
        output.putInt("WineQuality", wineQuality);
        output.putInt("CultureQuality", cultureQuality);
        output.putInt("CultureAmount", cultureAmount);
        output.putInt("CultureWineKind", cultureWineKind);
        output.putInt("BrewTime", progress);
    }

    @Override protected void loadAdditional(CompoundTag input, HolderLookup.Provider registries) {
        super.loadAdditional(input, registries);
        items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, items, registries);
        juiceKind = Math.clamp(PortNbt.intOr(input, "JuiceKind", JUICE_PURPLE), JUICE_PURPLE, JUICE_CIDER);
        outputKind = Math.clamp(PortNbt.intOr(input, "OutputKind", OUTPUT_WINE),
            OUTPUT_WINE, OUTPUT_WINE_VINEGAR);
        juiceAmount = Math.clamp(PortNbt.intOr(input, "JuiceAmount", 0), 0, CAPACITY);
        wineAmount = Math.clamp(PortNbt.intOr(input, "WineAmount", 0), 0, CAPACITY);
        wineQuality = Math.clamp(PortNbt.intOr(input, "WineQuality", 0), 0, 100);
        cultureQuality = Math.clamp(PortNbt.intOr(input, "CultureQuality", -1), -1, 100);
        cultureWineKind = Math.clamp(PortNbt.intOr(input, "CultureWineKind", 0), 0, 2);
        // Older saved barrels stored only the quality of a 250 mB culture
        // bottle. Migrate it without discarding the player's existing wine.
        cultureAmount = Math.clamp(PortNbt.intOr(input, "CultureAmount",
            cultureQuality >= 0 ? BOTTLE_AMOUNT : 0), 0, CULTURE_CAPACITY);
        if (cultureAmount == 0) cultureQuality = -1;
        if (cultureAmount > 0 && cultureQuality < 0) cultureAmount = 0;
        progress = Math.clamp(PortNbt.intOr(input, "BrewTime", 0), 0, BREW_TIME - 1);
        if (wineAmount == 0) wineQuality = 0;
        if (!hasMatchingRecipe()) progress = 0;
    }
}
