package dev.italiansdelight.common.block.entity;

import dev.italiansdelight.common.pizza.PizzaItemData;
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
import java.util.Objects;

/**
 * Ingredient provenance for the baked pizza served on a block.
 *
 * <p>Remaining quarters are saved as blockstate (REMAINING, 0..15), rather
 * than here: world shape, visibility, and client/server selection therefore
 * agree without a custom network protocol.
 *
 * <p>Data is set only on the authoritative server when the cooked pizza is
 * placed; individual slices do not consume/return the original raw toppings.
 */
public final class CookedPizzaBlockEntity extends BlockEntity {
    private PizzaItemData pizza = PizzaItemData.empty();

    public CookedPizzaBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COOKED_PIZZA, pos, state);
    }

    public PizzaItemData pizza() {
        return pizza;
    }

    public void setPizza(PizzaItemData data) {
        Objects.requireNonNull(data, "data");
        if (level != null && level.isClientSide()) {
            throw new IllegalStateException("Cooked pizza is server-authoritative");
        }
        pizza = data;
        setChanged();
        if (level != null) {
            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_CLIENTS);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        pizza = input.read("CookedPizza", PizzaItemData.CODEC)
                .orElseGet(PizzaItemData::empty);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store("CookedPizza", PizzaItemData.CODEC, pizza);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
