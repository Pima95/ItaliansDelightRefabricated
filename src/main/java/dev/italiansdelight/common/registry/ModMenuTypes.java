package dev.italiansdelight.common.registry;

import dev.italiansdelight.common.block.entity.container.CheeseVatMenu;
import dev.italiansdelight.common.block.entity.container.PizzaOvenMenu;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

/**
 * Registry for menu types used to synchronize server containers and client screens.
 */

public final class ModMenuTypes {

    private ModMenuTypes() {
    }

    public static final MenuType<CheeseVatMenu> CHEESE_VAT =
        register("cheese_vat", CheeseVatMenu::new);

    /**
     * Custom menu prevents Minecraft from constructing FurnaceScreen and
     * its unrelated vanilla recipe book, while keeping the same three slots.
     */
    public static final MenuType<PizzaOvenMenu> PIZZA_OVEN =
        register("pizza_oven", PizzaOvenMenu::new);

    private static <T extends AbstractContainerMenu> MenuType<T> register(
        String name,
        MenuType.MenuSupplier<T> constructor
    ) {
        return Registry.register(
            BuiltInRegistries.MENU,
            ModRegistries.id(name),
            new MenuType<>(constructor, FeatureFlagSet.of())
        );
    }

    public static void register() {
    }
}