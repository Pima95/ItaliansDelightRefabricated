package dev.italiansdelight.common.block.entity;

import dev.italiansdelight.common.block.CrushingTubRecipes;
import dev.italiansdelight.common.registry.ModBlockEntities;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Rustic Crushing Tub: one ingredient stack (64), one fluid (8000 mB),
 * one recipe processed per landing. All mutations are server-side.
 */
public final class CrushingTubBlockEntity extends BlockEntity {
    public static final int CAPACITY = 8_000;
    public static final int MAX_ITEMS = 64;
    private ItemStack input = ItemStack.EMPTY;
    private String fluidId = "";
    private int amount;

    public CrushingTubBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CRUSHING_TUB, pos, state);
    }

    public ItemStack getInput() { return input.copy(); }
    public int getAmount() { return amount; }
    public String getFluidId() { return fluidId; }
    public boolean isEmpty() { return amount == 0; }

    /** Insert as many ingredients as will fit, without overwriting a different input. */
    public int insert(ItemStack held) {
        if (held.isEmpty() || CrushingTubRecipes.find(held) == null) return 0;
        if (!input.isEmpty() && !ItemStack.isSameItemSameComponents(input, held)) return 0;
        int n = Math.min(held.getCount(), MAX_ITEMS - input.getCount());
        if (n <= 0) return 0;
        if (input.isEmpty()) { input = held.copy(); input.setCount(n); }
        else input.grow(n);
        changedAndSync();
        return n;
    }

    public ItemStack takeInputs() {
        if (input.isEmpty()) return ItemStack.EMPTY;
        ItemStack result = input;
        input = ItemStack.EMPTY;
        changedAndSync();
        return result;
    }

    /** Atomically process exactly ONE item, respecting fluid identity and capacity. */
    public ItemStack stomp() {
        CrushingTubRecipes.Recipe recipe = CrushingTubRecipes.find(input);
        if (recipe == null || recipe.amount() > CAPACITY - amount ||
            (amount > 0 && !fluidId.equals(recipe.fluidId()))) return null;
        if (amount == 0) fluidId = recipe.fluidId();
        amount += recipe.amount();
        input.shrink(1);
        if (input.isEmpty()) input = ItemStack.EMPTY;
        changedAndSync();
        return recipe.byproduct().copy();
    }

    public boolean canExtract(int volume) { return volume > 0 && volume <= amount; }

    public boolean extract(int volume) {
        if (!canExtract(volume)) return false;
        amount -= volume;
        if (amount == 0) fluidId = "";
        changedAndSync();
        return true;
    }

    public boolean discardFluid() {
        if (amount == 0) return false;
        amount = 0;
        fluidId = "";
        changedAndSync();
        return true;
    }

    private void changedAndSync() {
        setChanged();
        if (level != null && !level.isClientSide()) {
            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_CLIENTS);
        }
    }

    @Override protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (!input.isEmpty()) output.store("Input", ItemStack.CODEC, input);
        output.putString("FluidId", fluidId);
        output.putInt("Amount", amount);
    }
    @Override protected void loadAdditional(ValueInput inputData) {
        super.loadAdditional(inputData);
        input = inputData.read("Input", ItemStack.CODEC).orElse(ItemStack.EMPTY);
        if (input.getCount() > MAX_ITEMS) input.setCount(MAX_ITEMS);
        amount = Math.clamp(inputData.getIntOr("Amount", 0), 0, CAPACITY);
        fluidId = inputData.getStringOr("FluidId", "");
        if (amount == 0 || fluidId.isBlank()) { amount = 0; fluidId = ""; }
    }

    @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }
    @Override public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
