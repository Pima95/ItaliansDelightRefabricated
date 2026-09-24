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

    // Pasta cruda - da togliere
    public static final ResourceKey<Item> RAW_PASTA_DOUGH_KEY =
            ResourceKey.create(BuiltInRegistries.ITEM.key(), ModRegistries.id("raw_pasta_dough"));

    public static final Item RAW_PASTA_DOUGH = register(
            RAW_PASTA_DOUGH_KEY,
            new Item.Properties()
    );

    // Cacio e pepe - da togliere
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

    //Pasta al sugo
    public static final ResourceKey<Item> PASTA_WITH_TOMATO_SAUCE_KEY =
            ResourceKey.create(
                BuiltInRegistries.ITEM.key(), 
                ModRegistries.id("pasta_with_tomato_sauce")
            );

    public static final Item PASTA_WITH_TOMATO_SAUCE = register(
            PASTA_WITH_TOMATO_SAUCE_KEY,
            new Item.Properties().food(
                    new FoodProperties.Builder()
                            .nutrition(8)
                            .saturationModifier(0.8f)
                            .build()
            )
            .craftRemainder(net.minecraft.world.item.Items.BOWL) // Resto della ciotola dopo il consumo
            .stacksTo(16)
    );

    public static final ResourceKey<Item> RISOTTO_WITH_TOMATO_SAUCE_KEY =
            ResourceKey.create(
                BuiltInRegistries.ITEM.key(), 
                ModRegistries.id("risotto_with_tomato_sauce")
            );

    public static final Item RISOTTO_WITH_TOMATO_SAUCE = register(
            RISOTTO_WITH_TOMATO_SAUCE_KEY,
            new Item.Properties().food(
                    new FoodProperties.Builder()
                            .nutrition(8)
                            .saturationModifier(0.8f)
                            .build()
            )
            .craftRemainder(net.minecraft.world.item.Items.BOWL)
            .stacksTo(16)
    );

    public static final ResourceKey<Item> MOZZARELLA_KEY =
            ResourceKey.create(
                BuiltInRegistries.ITEM.key(), 
                ModRegistries.id("mozzarella")
            );

    public static final Item MOZZARELLA = register(
            MOZZARELLA_KEY,
            new Item.Properties().food(
                    new FoodProperties.Builder()
                            .nutrition(2)
                            .saturationModifier(0.3f)
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
