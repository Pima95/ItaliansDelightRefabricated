package dev.italiansdelight.common.fluid;

import java.util.function.BooleanSupplier;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * One component-aware Fabric fluid tank. The historic virtual fields remain
 * only as a recovery path for missing/unregistered fluids and old saved worlds.
 * Registered dairy and condiment liquids now travel as FluidVariant.
 *
 * Registered fluids from ANY Fabric mod use FluidVariant, including component
 * data from custom tanks/buckets. 81 droplets equal 1 mB.
 */
public final class CompatibleFluidTank extends SingleFluidStorage {
    private static final long PER_MB = FluidConstants.BUCKET / 1000L;
    private final long limit;
    private final Runnable onChanged;
    private final BooleanSupplier enabled;
    private final boolean quarterSteps;
    private String virtualId = "";
    private long virtualAmount = 0;

    public CompatibleFluidTank(int capacityMb, boolean quarterSteps,
            BooleanSupplier enabled, Runnable onChanged) {
        this.limit = capacityMb * PER_MB;
        this.quarterSteps = quarterSteps;
        this.enabled = enabled;
        this.onChanged = onChanged;
    }

    @Override protected long getCapacity(FluidVariant resource) { return limit; }
    @Override protected boolean canInsert(FluidVariant resource) {
        return virtualAmount == 0 && enabled.getAsBoolean()
            && resource.getFluid() != Fluids.LAVA;
    }
    @Override protected boolean canExtract(FluidVariant resource) {
        return enabled.getAsBoolean();
    }
    @Override public long insert(FluidVariant variant, long requested, TransactionContext tx) {
        long allowed = quarterSteps ? requested / FluidConstants.BOTTLE * FluidConstants.BOTTLE : requested;
        return allowed <= 0 ? 0 : super.insert(variant, allowed, tx);
    }
    @Override public long extract(FluidVariant variant, long requested, TransactionContext tx) {
        long allowed = quarterSteps ? requested / FluidConstants.BOTTLE * FluidConstants.BOTTLE : requested;
        return allowed <= 0 ? 0 : super.extract(variant, allowed, tx);
    }
    @Override protected void onFinalCommit() { onChanged.run(); }

    public FluidVariant currentVariant() { return getResource(); }
    public boolean hasVirtualFluid() { return virtualAmount > 0; }
    public long amountDroplets() { return hasVirtualFluid() ? virtualAmount : amount; }
    public int amountMb() { return (int) (amountDroplets() / PER_MB); }
    public boolean isEmptyTank() { return amountDroplets() == 0; }
    public String id() {
        if (hasVirtualFluid()) return virtualId;
        if (variant.isBlank()) return "";
        Identifier key = BuiltInRegistries.FLUID.getKey(variant.getFluid());
        return key == null ? "" : key.toString();
    }

    private static FluidVariant known(String fluidId) {
        if (fluidId == null || fluidId.isBlank()) return FluidVariant.blank();
        Identifier id = Identifier.tryParse(fluidId);
        if (id == null || !BuiltInRegistries.FLUID.containsKey(id))
            return FluidVariant.blank();
        Fluid value = BuiltInRegistries.FLUID.getValue(id);
        return value == null || value == Fluids.EMPTY ? FluidVariant.blank() : FluidVariant.of(value);
    }

    /** Used by existing in-game bottle/bucket mechanics (mB, not droplets). */
    public boolean insertMb(String id, int volume) {
        if (!enabled.getAsBoolean() || id == null || id.isBlank() ||
            volume <= 0 || (long) volume * PER_MB > limit - amountDroplets()) return false;
        FluidVariant fluid = known(id);
        if (!fluid.isBlank()) {
            if (hasVirtualFluid()) return false;
            long value = volume * PER_MB;
            try (Transaction tx = Transaction.openOuter()) {
                if (insert(fluid, value, tx) != value) return false;
                tx.commit();
                return true;
            }
        }
        if (amount > 0 || (virtualAmount > 0 && !virtualId.equals(id))) return false;
        virtualId = id;
        virtualAmount += volume * PER_MB;
        onChanged.run();
        return true;
    }

    public boolean extractMb(int volume) {
        long value = (long) volume * PER_MB;
        if (!enabled.getAsBoolean() || value <= 0 || value > amountDroplets()) return false;
        if (hasVirtualFluid()) {
            virtualAmount -= value;
            if (virtualAmount == 0) virtualId = "";
            onChanged.run();
            return true;
        }
        if (variant.isBlank()) return false;
        try (Transaction tx = Transaction.openOuter()) {
            if (extract(variant, value, tx) != value) return false;
            tx.commit();
            return true;
        }
    }

    public void save(ValueOutput output) {
        output.putString("FluidId", id());
        output.putInt("Amount", amountMb()); // legacy compatibility
        output.putLong("AmountDroplets", amountDroplets());
        if (!variant.isBlank() && amount > 0)
            output.store("FluidVariant", FluidVariant.CODEC, variant);
    }

    public void load(ValueInput input, boolean oliveLegacy) {
        // Backwards-compatible reads for all 1.1.0 world and dropped-block NBT.
        int original = input.getIntOr("Amount",
            oliveLegacy ? input.getIntOr("OliveOilAmount", 0) : 0);
        long requested = input.getLongOr("AmountDroplets", Math.max(0, original) * PER_MB);
        requested = Math.clamp(requested, 0L, limit);
        String fallback = oliveLegacy && original > 0 ? "italiansdelight:olive_oil" : "";
        String id = input.getStringOr("FluidId", fallback);
        // Only undo the briefly introduced non-vanilla cow_milk ID from the
        // previous development build. Never rewrite the historical
        // "minecraft:milk" identifier in existing world saves.
        if ("italiansdelight:cow_milk".equals(id)) id = "minecraft:milk";
        FluidVariant found = input.read("FluidVariant", FluidVariant.CODEC)
            .orElse(FluidVariant.blank());
        if (found.isBlank()) found = known(id);
        if (!found.isBlank() && requested > 0) {
            variant = found;
            amount = requested;
            virtualAmount = 0;
            virtualId = "";
        } else {
            variant = FluidVariant.blank();
            amount = 0;
            virtualAmount = id.isBlank() ? 0 : requested;
            virtualId = virtualAmount == 0 ? "" : id;
        }
    }
}
