package dev.italiansdelight.common.registry;

import dev.italiansdelight.common.fluid.OliveOilFluid;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FlowingFluid;

public final class ModFluids {
    private ModFluids() {}
    public static final ResourceKey<Fluid> OLIVE_OIL_STILL_KEY =
        ResourceKey.create(Registries.FLUID, ModRegistries.id("olive_oil"));
    public static final ResourceKey<Fluid> OLIVE_OIL_FLOWING_KEY =
        ResourceKey.create(Registries.FLUID, ModRegistries.id("flowing_olive_oil"));
    public static final FlowingFluid OLIVE_OIL_STILL =
        Registry.register(BuiltInRegistries.FLUID, OLIVE_OIL_STILL_KEY, new OliveOilFluid.Source());
    public static final FlowingFluid OLIVE_OIL_FLOWING =
        Registry.register(BuiltInRegistries.FLUID, OLIVE_OIL_FLOWING_KEY, new OliveOilFluid.Flowing());
    public static void register() {}
}
