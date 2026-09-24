package com.piergiuseppe.italiansdelight.registry;

import com.piergiuseppe.italiansdelight.menu.CheeseVatMenu;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public final class ModMenuTypes {

    private ModMenuTypes() {
    }

    public static final MenuType<CheeseVatMenu> CHEESE_VAT =
        register("cheese_vat", CheeseVatMenu::new);

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