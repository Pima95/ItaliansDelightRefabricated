package dev.italiansdelight.common.registry;

import dev.italiansdelight.common.block.entity.container.CheeseVatMenu;
import dev.italiansdelight.common.block.entity.container.BrewingBarrelMenu;
import dev.italiansdelight.common.block.entity.container.PizzaOvenMenu;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

/**
 * Registry for menu types used to synchronize server containers and client screens.
 */

public final class ModMenuTypes {

    private ModMenuTypes() {
    }

    public static MenuType<BrewingBarrelMenu> BREWING_BARREL;

    public static MenuType<CheeseVatMenu> CHEESE_VAT;

    /**
     * Custom menu prevents Minecraft from constructing FurnaceScreen and
     * its unrelated vanilla recipe book, while keeping the same three slots.
     */
    public static MenuType<PizzaOvenMenu> PIZZA_OVEN;

    private static <T extends AbstractContainerMenu> MenuType<T> register(
        String name,
        MenuType.MenuSupplier<T> constructor
    ) {
        return Registry.register(
            BuiltInRegistries.MENU,
            ModRegistries.id(name),
            new MenuType<>(constructor, FeatureFlags.DEFAULT_FLAGS)
        );
    }

    public static void onRegister(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.MENU)) {
            BREWING_BARREL = register("brewing_barrel", BrewingBarrelMenu::new);
            CHEESE_VAT = register("cheese_vat", CheeseVatMenu::new);
            PIZZA_OVEN = register("pizza_oven", PizzaOvenMenu::new);
        }
    }
}