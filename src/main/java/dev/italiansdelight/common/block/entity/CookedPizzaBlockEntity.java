package dev.italiansdelight.common.block.entity;

import dev.italiansdelight.common.pizza.PizzaItemData;
import dev.italiansdelight.common.pizza.PizzaSliceData;
import dev.italiansdelight.common.pizza.PizzaComposition;
import dev.italiansdelight.common.pizza.PizzaToppingApplication;
import dev.italiansdelight.common.block.CookedPizzaBlock;
import java.util.ArrayList;
import java.util.List;
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
    // Permanent even after all four quarters are reinserted. Persisted on disk.
    private boolean servedEver;
    // Slices transferred here from this or another pizza, indexed by their
    // NEW physical slot. The original four quarters remain in pizza().
    private List<PizzaSliceData> insertedSlices = List.of();

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
        servedEver = false;
        insertedSlices = List.of();
        updateClients();
    }

    /** Whether any quarter has EVER been removed or an outside slice inserted. */
    public boolean servedEver() {
        return servedEver;
    }

    /** A reinserted slice wins over the historical topping data for this slot. */
    public PizzaSliceData sliceAt(int quarter) {
        if (quarter < 0 || quarter >= 4) {
            throw new IllegalArgumentException("Pizza quarter must be 0..3");
        }
        for (PizzaSliceData inserted : insertedSlices) {
            if (inserted.quarter() == quarter) {
                return inserted;
            }
        }
        return PizzaSliceData.fromPizza(pizza, quarter);
    }

    public void markServed(int quarter) {
        if (level != null && level.isClientSide()) {
            throw new IllegalStateException("Cooked pizza is server-authoritative");
        }
        // A served replacement MUST be removed from the data map: otherwise
        // inserting a different slice in the same emptied slot would fail.
        List<PizzaSliceData> updated = new ArrayList<>();
        for (PizzaSliceData inserted : insertedSlices) {
            if (inserted.quarter() != quarter) {
                updated.add(inserted);
            }
        }
        insertedSlices = List.copyOf(updated);
        servedEver = true;
        updateClients();
    }

    /** Commit an already validated slice after setting its blockstate bit. */
    public void insertSlice(int quarter, PizzaSliceData source) {
        if (level != null && level.isClientSide()) {
            throw new IllegalStateException("Cannot insert a pizza slice on the client");
        }
        if (quarter < 0 || quarter >= 4) {
            throw new IllegalArgumentException("Invalid destination quarter");
        }
        for (PizzaSliceData inserted : insertedSlices) {
            if (inserted.quarter() == quarter) {
                throw new IllegalStateException("Destination quarter already inserted");
            }
        }
        List<PizzaSliceData> updated = new ArrayList<>(insertedSlices);
        // Quarter position is local to this pizza; saved ingredient provenance
        // remains untouched and can never be refunded as uncooked toppings.
        updated.add(new PizzaSliceData(quarter, source.toppings(), source.legacyMozzarella()));
        insertedSlices = List.copyOf(updated);
        servedEver = true;
        updateClients();
    }

    /** Rebuild the visible toppings from each actual slice for the renderer. */
    public PizzaComposition renderComposition() {
        int remaining = getBlockState().getValue(CookedPizzaBlock.REMAINING);
        String facing = getBlockState().getValue(CookedPizzaBlock.FACING).getName();
        List<PizzaToppingApplication> applications = new ArrayList<>();
        long sequence = 1L;
        for (int quarter = 0; quarter < 4; quarter++) {
            if ((remaining & (1 << quarter)) == 0) {
                continue;
            }
            for (PizzaSliceData.Topping topping : sliceAt(quarter).toppings()) {
                int bit = 1 << quarter;
                applications.add(new PizzaToppingApplication(
                        sequence++, topping.canonicalId(), topping.originalStack(), bit, bit));
            }
        }
        return new PizzaComposition(
                PizzaComposition.FORMAT_VERSION, 0, facing, sequence,
                pizza.composition().renderSeed(), applications);
    }

    /** Legacy mozzarella has quarter-specific coverage after slice transfers. */
    public int renderLegacyMask() {
        int remaining = getBlockState().getValue(CookedPizzaBlock.REMAINING);
        int mask = 0;
        for (int quarter = 0; quarter < 4; quarter++) {
            if ((remaining & (1 << quarter)) != 0 && sliceAt(quarter).legacyMozzarella()) {
                mask |= 1 << quarter;
            }
        }
        return mask;
    }

    private void updateClients() {
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
        servedEver = input.getBooleanOr("ServedEver", false);
        insertedSlices = List.copyOf(input.read("InsertedSlices", PizzaSliceData.CODEC.listOf())
                .orElse(List.of()));
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store("CookedPizza", PizzaItemData.CODEC, pizza);
        output.putBoolean("ServedEver", servedEver);
        output.store("InsertedSlices", PizzaSliceData.CODEC.listOf(), insertedSlices);
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
