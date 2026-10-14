package dev.italiansdelight.common.registry;

import dev.italiansdelight.common.pizza.PizzaItemData;
import net.minecraft.core.Registry;
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

    public static final DataComponentType<PizzaItemData> PIZZA_CONTENTS =
            Registry.register(
                    BuiltInRegistries.DATA_COMPONENT_TYPE,
                    ModRegistries.id("pizza_contents"),
                    DataComponentType.<PizzaItemData>builder()
                            .persistent(PizzaItemData.CODEC)
                            .build()
            );

    public static void register() {
        // Trigger static registration before items and block interactions.
    }
}
