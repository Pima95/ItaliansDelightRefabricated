package com.piergiuseppe.italiansdelight.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.core.Registry;

import com.piergiuseppe.italiansdelight.ItaliansDelight;

/**
 * Registro centrale degli item di Italian's Delight.
 *
 * Gli item vengono registrati nel namespace della mod tramite ResourceKey,
 * come richiesto dalle API di Minecraft 26.2.
 */
public final class ModItems {

    private ModItems() {
    }

    public static final ResourceKey<Item> RAW_PASTA_DOUGH_KEY =
            ResourceKey.create(BuiltInRegistries.ITEM.key(), ModRegistries.id("raw_pasta_dough"));

    public static final Item RAW_PASTA_DOUGH = register(
            RAW_PASTA_DOUGH_KEY,
            new Item.Properties()
    );

    public static final ResourceKey<Item> CACIO_E_PEPE_KEY =
            ResourceKey.create(BuiltInRegistries.ITEM.key(), ModRegistries.id("cacio_e_pepe"));

    public static final Item CACIO_E_PEPE = register(
            CACIO_E_PEPE_KEY,
            new Item.Properties().food(
                    new FoodProperties.Builder()
                            .nutrition(8)
                            .saturationModifier(0.8f)
                            .build()
            )
    );

    private static Item register(ResourceKey<Item> itemKey, Item.Properties properties) {
        Item item = new Item(properties.setId(itemKey));
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);
        return item;
    }

    public static void register() {
    }
}
