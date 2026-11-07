package dev.italiansdelight.common.block.entity;

import dev.italiansdelight.common.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/** One kind of liquid at a time, 16 buckets total, persistent when moved. */
public final class BarrelBlockEntity extends BlockEntity {
    public static final int CAPACITY = 16_000;
    public static final int BUCKET_AMOUNT = 1_000;
    public static final int BOTTLE_AMOUNT = 250;
    private String fluidId = "";
    private int amount;

    public BarrelBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BARREL, pos, state);
    }
    public int getAmount() { return amount; }
    public String getFluidId() { return fluidId; }
    public boolean isEmpty() { return amount == 0; }

    public boolean insert(String id, int volume) {
        if (id == null || id.isBlank() || volume <= 0 || volume > CAPACITY - amount ||
            (amount > 0 && !fluidId.equals(id))) return false;
        fluidId = id;
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
    public void preserveContents(ItemStack stack, HolderLookup.Provider registries) {
        if (amount == 0) return;
        TagValueOutput data = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, registries);
        data.putString("FluidId", fluidId);
        data.putInt("Amount", amount);
        BlockItem.setBlockEntityData(stack, getType(), data);
    }
    private void changedAndSync() {
        setChanged();
        if (level != null && !level.isClientSide()) {
            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_CLIENTS);
        }
    }

    @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putString("FluidId", fluidId);
        output.putInt("Amount", amount);
    }
    @Override protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        // Migrate old olive-only barrels and keep their liquids.
        int oliveLegacy = input.getIntOr("OliveOilAmount", 0);
        amount = Math.max(0, Math.min(CAPACITY, input.getIntOr("Amount", oliveLegacy)));
        fluidId = input.getStringOr("FluidId", oliveLegacy > 0 ? "italiansdelight:olive_oil" : "");
        if (amount == 0 || fluidId.isBlank()) { amount = 0; fluidId = ""; }
    }
}
