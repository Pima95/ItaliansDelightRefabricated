package dev.italiansdelight.common.block.entity;

import dev.italiansdelight.common.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Small reusable tank for the filled-vanilla-cauldron transition.
 * No vanilla block registry is altered. Exactly one liquid, maximum 1000 mB.
 */
public final class FluidCauldronBlockEntity extends BlockEntity {
    public static final int CAPACITY = 1000;
    public static final int BOTTLE_AMOUNT = 250;
    private String fluidId = "";
    private int amount;

    public FluidCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FLUID_CAULDRON, pos, state);
    }

    public int getAmount() { return amount; }
    public String getFluidId() { return fluidId; }

    public boolean insert(String fluid, int volume) {
        if (fluid == null || fluid.isBlank() || volume <= 0 ||
            volume > CAPACITY - amount ||
            (amount > 0 && !fluidId.equals(fluid))) return false;
        fluidId = fluid;
        amount += volume;
        changedAndSync();
        return true;
    }

    public boolean extract(int volume) {
        if (volume <= 0 || volume > amount) return false;
        amount -= volume;
        if (amount == 0) fluidId = "";
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
        output.putInt("Amount", amount);
        output.putString("FluidId", fluidId);
    }
    @Override protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        amount = Math.max(0, Math.min(CAPACITY, input.getIntOr("Amount", 0)));
        fluidId = input.getStringOr("FluidId", "");
        if (fluidId.isBlank() || amount == 0) { fluidId = ""; amount = 0; }
    }

    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup) {
        return saveWithoutMetadata(lookup);
    }
    @Override public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
