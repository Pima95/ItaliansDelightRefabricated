package dev.italiansdelight.common.registry;

import dev.italiansdelight.common.pizza.PizzaItemData;
import dev.italiansdelight.common.pizza.PizzaSliceData;
import dev.italiansdelight.common.pizza.PizzaPeelCargo;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.RegisterEvent;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;

/**
 * Persisted and automatically network-synchronized item components.
 *
 * <p>Minecraft 26.2 derives a network codec from the persistent codec when
 * none is provided explicitly. The same value survives world saves,
 * inventory drops/pickups and server-client item synchronization.
 */
public final class ModDataComponents {
    private ModDataComponents() {
    }

    public static DataComponentType<PizzaItemData> PIZZA_CONTENTS;

    /** A cooked slice holds ONLY its selected quarter's ingredients. */
    public static DataComponentType<PizzaSliceData> PIZZA_SLICE_CONTENTS;

    /** One complete pizza on a peel; absent component means empty peel. */
    public static DataComponentType<PizzaPeelCargo> PEEL_CARGO;

    /** Percentage 0..100 stored on individual wine bottles; never global. */
    public static DataComponentType<Integer> WINE_QUALITY;

    public static void onRegister(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.DATA_COMPONENT_TYPE)) {
            PIZZA_CONTENTS = Registry.register(
                                BuiltInRegistries.DATA_COMPONENT_TYPE,
                                ModRegistries.id("pizza_contents"),
                                DataComponentType.<PizzaItemData>builder()
                                        .persistent(PizzaItemData.CODEC)
                                        .build()
                        );
            PIZZA_SLICE_CONTENTS = Registry.register(
                                BuiltInRegistries.DATA_COMPONENT_TYPE,
                                ModRegistries.id("pizza_slice_contents"),
                                DataComponentType.<PizzaSliceData>builder()
                                        .persistent(PizzaSliceData.CODEC)
                                        .build()
                        );
            PEEL_CARGO = Registry.register(
                                BuiltInRegistries.DATA_COMPONENT_TYPE,
                                ModRegistries.id("peel_cargo"),
                                DataComponentType.<PizzaPeelCargo>builder()
                                        .persistent(PizzaPeelCargo.CODEC)
                                        .build()
                        );
            WINE_QUALITY = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,
                                ModRegistries.id("wine_quality"),
                                DataComponentType.<Integer>builder()
                                        .persistent(Codec.intRange(0, 100))
                                        .build());
        }
    }
}
