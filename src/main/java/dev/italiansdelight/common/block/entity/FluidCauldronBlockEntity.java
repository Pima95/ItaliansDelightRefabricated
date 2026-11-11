package dev.italiansdelight.common.block.entity;

import dev.italiansdelight.common.fluid.CompatibleFluidTank;
import dev.italiansdelight.common.registry.ModBlockEntities;
import dev.italiansdelight.common.block.FluidCauldronBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;

/** Four 250mB levels in the original vanilla-iron shell. */
public final class FluidCauldronBlockEntity extends BlockEntity {
    public static final int CAPACITY = 1_000;
    public static final int BOTTLE_AMOUNT = 250;
    private final CompatibleFluidTank tank = new CompatibleFluidTank(CAPACITY, true,
        () -> true, this::changedAndSync);

    public FluidCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FLUID_CAULDRON, pos, state);
    }

    public CompatibleFluidTank getFluidStorage() { return tank; }
    public FluidVariant getFluidVariant() { return tank.currentVariant(); }
    public int getAmount() { return tank.amountMb(); }
    public String getFluidId() { return tank.id(); }
    public boolean insert(String fluid, int volume) { return tank.insertMb(fluid, volume); }
    public boolean extract(int volume) { return tank.extractMb(volume); }

    private void changedAndSync() {
        setChanged();
        if (level != null && !level.isClientSide()) {
            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_CLIENTS);
            if (state.getBlock() instanceof FluidCauldronBlock) {
                int levels = getAmount() / BOTTLE_AMOUNT;
                if (levels == 0)
                    level.setBlockAndUpdate(worldPosition, Blocks.CAULDRON.defaultBlockState());
                else if (levels != state.getValue(FluidCauldronBlock.LEVEL))
                    level.setBlockAndUpdate(worldPosition,
                        state.setValue(FluidCauldronBlock.LEVEL, levels));
            }
        }
    }
    @Override protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        tank.save(output);
    }
    @Override protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        tank.load(input, false);
    }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup) {
        return saveWithoutMetadata(lookup);
    }
    @Override public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
