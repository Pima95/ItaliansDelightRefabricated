package dev.italiansdelight.common.block.entity;

import dev.italiansdelight.common.registry.ModBlockEntities;
import dev.italiansdelight.common.registry.ModDataComponents;
import dev.italiansdelight.common.registry.ModItems;
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
public final class BrewingBarrelBlockEntity extends BlockEntity {
    public static final int CAPACITY = 8000;
    public static final int BREW_TIME = 12000;
    public static final int CULTURE_CAPACITY = 250;

    private int juiceAmount;
    private int wineAmount;
    private int wineQuality;
    private int cultureQuality = -1;
    private int progress;

    public BrewingBarrelBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BREWING_BARREL, pos, state);
    }

    public int getJuiceAmount() { return juiceAmount; }
    public int getWineAmount() { return wineAmount; }
    public int getProgress() { return progress; }
    public int getWineQuality() { return wineQuality; }
    public int getCultureQuality() { return cultureQuality; }

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

    /** A single inoculation bottle seeds future batches without being consumed. */
    public boolean insertCulture(int quality) {
        if (cultureQuality >= 0 || quality < 0 || quality > 100) return false;
        cultureQuality = quality;
        changedAndSync();
        return true;
    }

    public int takeCulture() {
        if (cultureQuality < 0) return -1;
        int quality = cultureQuality;
        cultureQuality = -1;
        changedAndSync();
        return quality;
    }

    public static ItemStack wineBottle(int quality) {
        ItemStack result = new ItemStack(ModItems.WINE_BOTTLE);
        result.set(ModDataComponents.WINE_QUALITY, Math.clamp(quality, 0, 100));
        return result;
    }

    public static void serverTick(ServerLevel level, BlockPos pos, BlockState state,
                                  BrewingBarrelBlockEntity barrel) {
        if (barrel.juiceAmount < 250 || barrel.wineAmount >= CAPACITY) {
            if (barrel.progress != 0) {
                barrel.progress = 0;
                barrel.changedAndSync();
            }
            return;
        }
        if (++barrel.progress >= BREW_TIME) {
            int amount = Math.min(barrel.juiceAmount, CAPACITY - barrel.wineAmount);
            // One fermentation converts the available batch, not one bottle per tick.
            if (barrel.wineAmount == 0) {
                barrel.wineQuality = barrel.cultureQuality >= 0
                        ? Math.clamp(barrel.cultureQuality
                                + level.random.nextInt(6) - 1, 0, 100)
                        : 5 + level.random.nextInt(71);
            }
            barrel.juiceAmount -= amount;
            barrel.wineAmount += amount;
            barrel.progress = 0;
            barrel.changedAndSync();
        } else {
            // Persist timer across server saves; sync UI status periodically.
            barrel.setChanged();
            if (barrel.progress % 200 == 0) barrel.changedAndSync();
        }
    }

    public void preserveContents(ItemStack stack, HolderLookup.Provider registries) {
        if (juiceAmount == 0 && wineAmount == 0 && cultureQuality < 0 && progress == 0) return;
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
        output.putInt("JuiceAmount", juiceAmount);
        output.putInt("WineAmount", wineAmount);
        output.putInt("WineQuality", wineQuality);
        output.putInt("CultureQuality", cultureQuality);
        output.putInt("BrewTime", progress);
    }

    @Override protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        juiceAmount = Math.clamp(input.getIntOr("JuiceAmount", 0), 0, CAPACITY);
        wineAmount = Math.clamp(input.getIntOr("WineAmount", 0), 0, CAPACITY);
        wineQuality = Math.clamp(input.getIntOr("WineQuality", 0), 0, 100);
        cultureQuality = Math.clamp(input.getIntOr("CultureQuality", -1), -1, 100);
        progress = Math.clamp(input.getIntOr("BrewTime", 0), 0, BREW_TIME - 1);
        if (wineAmount == 0) wineQuality = 0;
        if (juiceAmount == 0 || wineAmount >= CAPACITY) progress = 0;
    }
}
