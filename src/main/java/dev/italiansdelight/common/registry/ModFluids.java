package dev.italiansdelight.common.registry;

import dev.italiansdelight.common.fluid.OliveOilFluid;
import dev.italiansdelight.common.fluid.AppleJuiceFluid;
import dev.italiansdelight.common.fluid.ContainedFluid;
import net.minecraft.core.Registry;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.fluids.FluidType;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FlowingFluid;

public final class ModFluids {
    private ModFluids() {}

    // Preserve all stable resource IDs: only registry insertion order changes.
    // JEI 'Original' ordering for built-in fluid ingredients follows this order.

    // 1. Animal-derived liquids: milk, whey, rennet and bee honey.
    public static final ResourceKey<Fluid> SHEEP_MILK_KEY =
        ResourceKey.create(Registries.FLUID, ModRegistries.id("sheep_milk"));
    public static Fluid SHEEP_MILK;

    public static final ResourceKey<Fluid> WHEY_KEY =
        ResourceKey.create(Registries.FLUID, ModRegistries.id("whey"));
    public static Fluid WHEY;

    public static final ResourceKey<Fluid> RENNET_KEY =
        ResourceKey.create(Registries.FLUID, ModRegistries.id("rennet"));
    public static Fluid RENNET;

    public static final ResourceKey<Fluid> HONEY_KEY =
        ResourceKey.create(Registries.FLUID, ModRegistries.id("honey"));
    public static Fluid HONEY;


    // 2. Crushing Tub products and non-fermented vegetable liquids.
    public static final ResourceKey<Fluid> OLIVE_OIL_STILL_KEY =
        ResourceKey.create(Registries.FLUID, ModRegistries.id("olive_oil"));
    public static FlowingFluid OLIVE_OIL_STILL;

    public static final ResourceKey<Fluid> OLIVE_OIL_FLOWING_KEY =
        ResourceKey.create(Registries.FLUID, ModRegistries.id("flowing_olive_oil"));
    public static FlowingFluid OLIVE_OIL_FLOWING;

    public static final ResourceKey<Fluid> APPLE_JUICE_STILL_KEY =
        ResourceKey.create(Registries.FLUID, ModRegistries.id("apple_juice"));
    public static FlowingFluid APPLE_JUICE_STILL;

    public static final ResourceKey<Fluid> APPLE_JUICE_FLOWING_KEY =
        ResourceKey.create(Registries.FLUID, ModRegistries.id("flowing_apple_juice"));
    public static FlowingFluid APPLE_JUICE_FLOWING;

    public static final ResourceKey<Fluid> GOLDEN_APPLE_JUICE_KEY =
        ResourceKey.create(Registries.FLUID, ModRegistries.id("golden_apple_juice"));
    public static Fluid GOLDEN_APPLE_JUICE;

    public static final ResourceKey<Fluid> GRAPE_JUICE_KEY =
        ResourceKey.create(Registries.FLUID, ModRegistries.id("grape_juice"));
    public static Fluid GRAPE_JUICE;

    public static final ResourceKey<Fluid> WHITE_GRAPE_JUICE_KEY =
        ResourceKey.create(Registries.FLUID, ModRegistries.id("white_grape_juice"));
    public static Fluid WHITE_GRAPE_JUICE;

    public static final ResourceKey<Fluid> CHERRY_JUICE_KEY =
        ResourceKey.create(Registries.FLUID, ModRegistries.id("cherry_juice"));
    public static Fluid CHERRY_JUICE;


    // 3. Wines, fermented drinks, then wine/cider vinegars.
    public static final ResourceKey<Fluid> WINE_KEY =
        ResourceKey.create(Registries.FLUID, ModRegistries.id("wine"));
    public static Fluid WINE;

    public static final ResourceKey<Fluid> WHITE_WINE_KEY =
        ResourceKey.create(Registries.FLUID, ModRegistries.id("white_wine"));
    public static Fluid WHITE_WINE;

    public static final ResourceKey<Fluid> CHERRY_WINE_KEY =
        ResourceKey.create(Registries.FLUID, ModRegistries.id("cherry_wine"));
    public static Fluid CHERRY_WINE;

    public static final ResourceKey<Fluid> MEAD_KEY =
        ResourceKey.create(Registries.FLUID, ModRegistries.id("mead"));
    public static Fluid MEAD;

    public static final ResourceKey<Fluid> CIDER_KEY =
        ResourceKey.create(Registries.FLUID, ModRegistries.id("cider"));
    public static Fluid CIDER;

    public static final ResourceKey<Fluid> APPLE_CIDER_VINEGAR_KEY =
        ResourceKey.create(Registries.FLUID, ModRegistries.id("apple_cider_vinegar"));
    public static Fluid APPLE_CIDER_VINEGAR;

    public static final ResourceKey<Fluid> RED_WINE_VINEGAR_KEY =
        ResourceKey.create(Registries.FLUID, ModRegistries.id("red_wine_vinegar"));
    public static Fluid RED_WINE_VINEGAR;

    private static final Map<String, FluidType> TYPES = new LinkedHashMap<>();
    static {
        TYPES.put("sheep_milk", new FluidType(FluidType.Properties.create()
            .descriptionId("fluid.italiansdelight.sheep_milk").canConvertToSource(false)));
        TYPES.put("whey", new FluidType(FluidType.Properties.create()
            .descriptionId("fluid.italiansdelight.whey").canConvertToSource(false)));
        TYPES.put("rennet", new FluidType(FluidType.Properties.create()
            .descriptionId("fluid.italiansdelight.rennet").canConvertToSource(false)));
        TYPES.put("honey", new FluidType(FluidType.Properties.create()
            .descriptionId("fluid.italiansdelight.honey").canConvertToSource(false)));
        TYPES.put("olive_oil", new FluidType(FluidType.Properties.create()
            .descriptionId("fluid.italiansdelight.olive_oil").canConvertToSource(false)));
        TYPES.put("apple_juice", new FluidType(FluidType.Properties.create()
            .descriptionId("fluid.italiansdelight.apple_juice").canConvertToSource(false)));
        TYPES.put("golden_apple_juice", new FluidType(FluidType.Properties.create()
            .descriptionId("fluid.italiansdelight.golden_apple_juice").canConvertToSource(false)));
        TYPES.put("grape_juice", new FluidType(FluidType.Properties.create()
            .descriptionId("fluid.italiansdelight.grape_juice").canConvertToSource(false)));
        TYPES.put("white_grape_juice", new FluidType(FluidType.Properties.create()
            .descriptionId("fluid.italiansdelight.white_grape_juice").canConvertToSource(false)));
        TYPES.put("cherry_juice", new FluidType(FluidType.Properties.create()
            .descriptionId("fluid.italiansdelight.cherry_juice").canConvertToSource(false)));
        TYPES.put("wine", new FluidType(FluidType.Properties.create()
            .descriptionId("fluid.italiansdelight.wine").canConvertToSource(false)));
        TYPES.put("white_wine", new FluidType(FluidType.Properties.create()
            .descriptionId("fluid.italiansdelight.white_wine").canConvertToSource(false)));
        TYPES.put("cherry_wine", new FluidType(FluidType.Properties.create()
            .descriptionId("fluid.italiansdelight.cherry_wine").canConvertToSource(false)));
        TYPES.put("mead", new FluidType(FluidType.Properties.create()
            .descriptionId("fluid.italiansdelight.mead").canConvertToSource(false)));
        TYPES.put("cider", new FluidType(FluidType.Properties.create()
            .descriptionId("fluid.italiansdelight.cider").canConvertToSource(false)));
        TYPES.put("apple_cider_vinegar", new FluidType(FluidType.Properties.create()
            .descriptionId("fluid.italiansdelight.apple_cider_vinegar").canConvertToSource(false)));
        TYPES.put("red_wine_vinegar", new FluidType(FluidType.Properties.create()
            .descriptionId("fluid.italiansdelight.red_wine_vinegar").canConvertToSource(false)));
    }

    public static FluidType type(String id) { return TYPES.get(id); }

    public static void onRegister(RegisterEvent event) {
        if (event.getRegistryKey().equals(NeoForgeRegistries.Keys.FLUID_TYPES)) {
            TYPES.forEach((id, type) -> event.register(NeoForgeRegistries.Keys.FLUID_TYPES,
                    ModRegistries.id(id), () -> type));
        }
        if (event.getRegistryKey().equals(Registries.FLUID)) {
            SHEEP_MILK = Registry.register(
                    BuiltInRegistries.FLUID, SHEEP_MILK_KEY.location(),
                    new ContainedFluid(() -> type("sheep_milk"), () -> ModItems.SHEEP_MILK_BUCKET));
            WHEY = Registry.register(
                    BuiltInRegistries.FLUID, WHEY_KEY.location(),
                    new ContainedFluid(() -> type("whey"), () -> ModItems.WHEY_BUCKET));
            RENNET = Registry.register(
                    BuiltInRegistries.FLUID, RENNET_KEY.location(),
                    new ContainedFluid(() -> type("rennet"), () -> ModItems.RENNET_BUCKET));
            HONEY = Registry.register(
                    BuiltInRegistries.FLUID, HONEY_KEY.location(),
                    new ContainedFluid(() -> type("honey"), () -> ModItems.HONEY_BUCKET));
            OLIVE_OIL_STILL = Registry.register(BuiltInRegistries.FLUID, OLIVE_OIL_STILL_KEY.location(), new OliveOilFluid.Source());
            OLIVE_OIL_FLOWING = Registry.register(BuiltInRegistries.FLUID, OLIVE_OIL_FLOWING_KEY.location(), new OliveOilFluid.Flowing());
            APPLE_JUICE_STILL = Registry.register(BuiltInRegistries.FLUID, APPLE_JUICE_STILL_KEY.location(), new AppleJuiceFluid.Source());
            APPLE_JUICE_FLOWING = Registry.register(BuiltInRegistries.FLUID, APPLE_JUICE_FLOWING_KEY.location(), new AppleJuiceFluid.Flowing());
            GOLDEN_APPLE_JUICE = Registry.register(
                    BuiltInRegistries.FLUID, GOLDEN_APPLE_JUICE_KEY.location(),
                    new ContainedFluid(() -> type("golden_apple_juice"), () -> ModItems.GOLDEN_APPLE_JUICE_BUCKET));
            GRAPE_JUICE = Registry.register(
                    BuiltInRegistries.FLUID, GRAPE_JUICE_KEY.location(),
                    new ContainedFluid(() -> type("grape_juice"), () -> ModItems.GRAPE_JUICE_BUCKET));
            WHITE_GRAPE_JUICE = Registry.register(
                    BuiltInRegistries.FLUID, WHITE_GRAPE_JUICE_KEY.location(),
                    new ContainedFluid(() -> type("white_grape_juice"), () -> ModItems.WHITE_GRAPE_JUICE_BUCKET));
            CHERRY_JUICE = Registry.register(
                    BuiltInRegistries.FLUID, CHERRY_JUICE_KEY.location(),
                    new ContainedFluid(() -> type("cherry_juice"), () -> ModItems.CHERRY_JUICE_BUCKET));
            WINE = Registry.register(
                    BuiltInRegistries.FLUID, WINE_KEY.location(),
                    new ContainedFluid(() -> type("wine"), () -> ModItems.WINE_BUCKET));
            WHITE_WINE = Registry.register(
                    BuiltInRegistries.FLUID, WHITE_WINE_KEY.location(),
                    new ContainedFluid(() -> type("white_wine"), () -> ModItems.WHITE_WINE_BUCKET));
            CHERRY_WINE = Registry.register(
                    BuiltInRegistries.FLUID, CHERRY_WINE_KEY.location(),
                    new ContainedFluid(() -> type("cherry_wine"), () -> ModItems.CHERRY_WINE_BUCKET));
            MEAD = Registry.register(
                    BuiltInRegistries.FLUID, MEAD_KEY.location(),
                    new ContainedFluid(() -> type("mead"), () -> ModItems.MEAD_BUCKET));
            CIDER = Registry.register(
                    BuiltInRegistries.FLUID, CIDER_KEY.location(),
                    new ContainedFluid(() -> type("cider"), () -> ModItems.CIDER_BUCKET));
            APPLE_CIDER_VINEGAR = Registry.register(
                    BuiltInRegistries.FLUID, APPLE_CIDER_VINEGAR_KEY.location(),
                    new ContainedFluid(() -> type("apple_cider_vinegar"), () -> ModItems.APPLE_CIDER_VINEGAR_BUCKET));
            RED_WINE_VINEGAR = Registry.register(
                    BuiltInRegistries.FLUID, RED_WINE_VINEGAR_KEY.location(),
                    new ContainedFluid(() -> type("red_wine_vinegar"), () -> ModItems.RED_WINE_VINEGAR_BUCKET));
        }
    }
}
