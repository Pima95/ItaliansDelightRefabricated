package dev.italiansdelight.common.registry;

import dev.italiansdelight.common.fluid.OliveOilFluid;
import dev.italiansdelight.common.fluid.AppleJuiceFluid;
import dev.italiansdelight.common.fluid.ContainedFluid;
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
    public static final ResourceKey<Fluid> APPLE_JUICE_STILL_KEY =
        ResourceKey.create(Registries.FLUID, ModRegistries.id("apple_juice"));
    public static final ResourceKey<Fluid> APPLE_JUICE_FLOWING_KEY =
        ResourceKey.create(Registries.FLUID, ModRegistries.id("flowing_apple_juice"));
    public static final FlowingFluid APPLE_JUICE_STILL =
        Registry.register(BuiltInRegistries.FLUID, APPLE_JUICE_STILL_KEY, new AppleJuiceFluid.Source());
    public static final FlowingFluid APPLE_JUICE_FLOWING =
        Registry.register(BuiltInRegistries.FLUID, APPLE_JUICE_FLOWING_KEY, new AppleJuiceFluid.Flowing());

    // Registered non-world fluids for storage and recipe APIs.
    // Vanilla cow milk remains its historical virtual "minecraft:milk" ID.
    public static final ResourceKey<Fluid> SHEEP_MILK_KEY =
        ResourceKey.create(Registries.FLUID, ModRegistries.id("sheep_milk"));
    public static final Fluid SHEEP_MILK = Registry.register(
        BuiltInRegistries.FLUID, SHEEP_MILK_KEY,
        new ContainedFluid(() -> ModItems.SHEEP_MILK_BUCKET));

    public static final ResourceKey<Fluid> WHEY_KEY =
        ResourceKey.create(Registries.FLUID, ModRegistries.id("whey"));
    public static final Fluid WHEY = Registry.register(
        BuiltInRegistries.FLUID, WHEY_KEY,
        new ContainedFluid(() -> ModItems.WHEY_BUCKET));

    public static final ResourceKey<Fluid> RENNET_KEY =
        ResourceKey.create(Registries.FLUID, ModRegistries.id("rennet"));
    public static final Fluid RENNET = Registry.register(
        BuiltInRegistries.FLUID, RENNET_KEY,
        new ContainedFluid(() -> ModItems.RENNET_BUCKET));

    public static final ResourceKey<Fluid> APPLE_CIDER_VINEGAR_KEY =
        ResourceKey.create(Registries.FLUID, ModRegistries.id("apple_cider_vinegar"));
    public static final Fluid APPLE_CIDER_VINEGAR = Registry.register(
        BuiltInRegistries.FLUID, APPLE_CIDER_VINEGAR_KEY,
        new ContainedFluid(() -> ModItems.APPLE_CIDER_VINEGAR_BUCKET));

    // Golden Apple Juice: a non-placeable Fluid with a storage-only bucket.
    // It has no world block or vanilla water physics/source generation.
    public static final ResourceKey<Fluid> GOLDEN_APPLE_JUICE_KEY =
        ResourceKey.create(Registries.FLUID, ModRegistries.id("golden_apple_juice"));
    public static final Fluid GOLDEN_APPLE_JUICE = Registry.register(
        BuiltInRegistries.FLUID, GOLDEN_APPLE_JUICE_KEY,
        new ContainedFluid(() -> ModItems.GOLDEN_APPLE_JUICE_BUCKET));

    public static void register() {}
}
