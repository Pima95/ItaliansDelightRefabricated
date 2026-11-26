package dev.italiansdelight.common.fluid;

import java.util.function.BooleanSupplier;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

/** Native NeoForge tank; amounts are millibuckets. Unknown legacy fluids
 * remain isolated in the virtual ID store and never enter capabilities.
 */
public final class CompatibleFluidTank extends FluidTank {
    private final Runnable onChanged;
    private final BooleanSupplier enabled;
    private final boolean quarterSteps;
    private String virtualId = "";
    private int virtualAmount;

    public CompatibleFluidTank(int capacityMb, boolean quarterSteps,
            BooleanSupplier enabled, Runnable onChanged) {
        super(capacityMb);
        this.quarterSteps = quarterSteps;
        this.enabled = enabled;
        this.onChanged = onChanged;
    }

    private int allowed(int requested) {
        int n = Math.max(0, requested);
        return quarterSteps ? n / 250 * 250 : n;
    }

    @Override public boolean isFluidValid(FluidStack resource) {
        return !hasVirtualFluid() && enabled.getAsBoolean()
            && !resource.isEmpty() && resource.getFluid() != Fluids.LAVA
            && resource.getFluid() != Fluids.FLOWING_LAVA;
    }

    @Override public int fill(FluidStack resource, FluidAction action) {
        if (!isFluidValid(resource)) return 0;
        int n = allowed(Math.min(resource.getAmount(), capacity - getFluidAmount()));
        return n > 0 ? super.fill(resource.copyWithAmount(n), action) : 0;
    }

    @Override public FluidStack drain(int requested, FluidAction action) {
        if (!enabled.getAsBoolean() || hasVirtualFluid()) return FluidStack.EMPTY;
        int n = allowed(Math.min(requested, getFluidAmount()));
        return n > 0 ? super.drain(n, action) : FluidStack.EMPTY;
    }

    @Override public FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || !FluidStack.isSameFluidSameComponents(resource, fluid))
            return FluidStack.EMPTY;
        return drain(resource.getAmount(), action);
    }

    @Override protected void onContentsChanged() { onChanged.run(); }

    public FluidStack currentVariant() { return fluid.copy(); }
    public boolean hasVirtualFluid() { return virtualAmount > 0; }
    public int amountMb() { return hasVirtualFluid() ? virtualAmount : getFluidAmount(); }
    public boolean isEmptyTank() { return amountMb() == 0; }

    public String id() {
        if (hasVirtualFluid()) return virtualId;
        if (fluid.isEmpty()) return "";
        return BuiltInRegistries.FLUID.getKey(fluid.getFluid()).toString();
    }

    private static FluidStack known(String fluidId, int amount) {
        if (fluidId == null || fluidId.isBlank() || amount <= 0) return FluidStack.EMPTY;
        ResourceLocation id = ResourceLocation.tryParse(fluidId);
        if (id == null || !BuiltInRegistries.FLUID.containsKey(id)) return FluidStack.EMPTY;
        Fluid value = BuiltInRegistries.FLUID.get(id);
        return value == null || value == Fluids.EMPTY ? FluidStack.EMPTY : new FluidStack(value, amount);
    }

    public boolean insertMb(String id, int volume) {
        if (!enabled.getAsBoolean() || id == null || id.isBlank()
            || volume <= 0 || volume > capacity - amountMb() || allowed(volume) != volume)
            return false;
        FluidStack incoming = known(id, volume);
        if (!incoming.isEmpty()) {
            if (fill(incoming, FluidAction.SIMULATE) != volume) return false;
            return fill(incoming, FluidAction.EXECUTE) == volume;
        }
        if (!fluid.isEmpty() || (hasVirtualFluid() && !virtualId.equals(id))) return false;
        virtualId = id;
        virtualAmount += volume;
        onChanged.run();
        return true;
    }

    public boolean extractMb(int volume) {
        if (!enabled.getAsBoolean() || volume <= 0 || volume > amountMb() || allowed(volume) != volume)
            return false;
        if (hasVirtualFluid()) {
            virtualAmount -= volume;
            if (virtualAmount == 0) virtualId = "";
            onChanged.run();
            return true;
        }
        if (drain(volume, FluidAction.SIMULATE).getAmount() != volume) return false;
        return drain(volume, FluidAction.EXECUTE).getAmount() == volume;
    }

    public void save(CompoundTag output, HolderLookup.Provider registries) {
        output.putString("FluidId", id());
        output.putInt("Amount", amountMb());
        if (!fluid.isEmpty()) output.put("Fluid", fluid.save(registries));
    }

    public void load(CompoundTag input, HolderLookup.Provider registries, boolean oliveLegacy) {
        // Legacy amounts were droplets (81 per mB); 1.21.1 NeoForge stores mB.
        int original = input.contains("Amount", Tag.TAG_ANY_NUMERIC)
            ? input.getInt("Amount") : oliveLegacy ? input.getInt("OliveOilAmount") : 0;
        long requested = input.contains("AmountDroplets", Tag.TAG_ANY_NUMERIC)
            ? input.getLong("AmountDroplets") / 81L : original;
        int volume = (int) Math.clamp(requested, 0L, (long) capacity);
        String id = input.contains("FluidId", Tag.TAG_STRING) ? input.getString("FluidId")
            : oliveLegacy && original > 0 ? "italiansdelight:olive_oil" : "";
        if ("italiansdelight:cow_milk".equals(id)) id = "minecraft:milk";
        FluidStack loaded = input.contains("Fluid")
            ? FluidStack.parse(registries, input.get("Fluid")).orElse(FluidStack.EMPTY)
            : FluidStack.EMPTY;
        if (!loaded.isEmpty()) {
            volume = Math.min(capacity, loaded.getAmount());
        } else {
            // Recover known legacy IDs. Downgrading 26.3 worlds is not supported.
            loaded = known(id, volume);
        }
        setFluid(loaded.isEmpty() ? FluidStack.EMPTY : loaded.copyWithAmount(volume));
        virtualAmount = loaded.isEmpty() && !id.isBlank() ? volume : 0;
        virtualId = virtualAmount > 0 ? id : "";
    }
}
