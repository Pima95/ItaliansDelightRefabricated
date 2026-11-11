package dev.italiansdelight.common.block.entity;

import dev.italiansdelight.common.fluid.CompatibleFluidTank;
import dev.italiansdelight.common.registry.ModBlockEntities;
import dev.italiansdelight.common.block.BarrelBlock;
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
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;

/** 16 buckets: registered Fabric fluids retain components; legacy milk/whey retain IDs. */
public final class BarrelBlockEntity extends BlockEntity {
    public static final int CAPACITY = 16_000;
    public static final int BUCKET_AMOUNT = 1_000;
    public static final int BOTTLE_AMOUNT = 250;

    private final CompatibleFluidTank tank = new CompatibleFluidTank(CAPACITY, false,
        () -> !getBlockState().getValue(BarrelBlock.WATERLOGGED),
        this::changedAndSync);

    public BarrelBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BARREL, pos, state);
    }
    public CompatibleFluidTank getFluidStorage() { return tank; }
    public FluidVariant getFluidVariant() { return tank.currentVariant(); }
    public int getAmount() { return tank.amountMb(); }
    public String getFluidId() { return tank.id(); }
    public boolean isEmpty() { return tank.isEmptyTank(); }
    public boolean insert(String id, int volume) { return tank.insertMb(id, volume); }
    public boolean extract(int volume) { return tank.extractMb(volume); }

    public void preserveContents(ItemStack stack, HolderLookup.Provider registries) {
        if (tank.isEmptyTank()) return;
        TagValueOutput data = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, registries);
        tank.save(data);
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
        tank.save(output);
    }
    @Override protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        tank.load(input, true);
    }
}
