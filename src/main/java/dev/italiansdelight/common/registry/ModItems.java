package dev.italiansdelight.common.registry;

import java.util.function.Function;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.core.Registry;

import dev.italiansdelight.ItaliansDelight;

import vectorwing.farmersdelight.common.FoodValues;
import vectorwing.farmersdelight.common.item.ConsumableItem;

/**
 * Central registry for Italian's Delight items.
 *
 * Items are registered in the mod namespace through ResourceKey,
 * as required by the Minecraft 26.2 APIs.
 */
public final class ModItems {

    // Every item uses a stable ResourceKey: the same identifier is reused by
    // the registry, models, and datapacks.

        private ModItems() {
        }

        // Pasta with tomato sauce
        public static final ResourceKey<Item> PASTA_WITH_TOMATO_SAUCE_KEY = ResourceKey.create(
                        BuiltInRegistries.ITEM.key(),
                        ModRegistries.id("pasta_with_tomato_sauce"));

        public static final Item PASTA_WITH_TOMATO_SAUCE = register(
                        PASTA_WITH_TOMATO_SAUCE_KEY,
                        new Item.Properties().food(
                                        new FoodProperties.Builder()
                                                        .nutrition(8)
                                                        .saturationModifier(0.8f)
                                                        .build())
                                        .craftRemainder(net.minecraft.world.item.Items.BOWL) // Returns the bowl after
                                                                                             // consumption
                                        .stacksTo(16));

        // Risotto with tomato sauce
        public static final ResourceKey<Item> RISOTTO_WITH_TOMATO_SAUCE_KEY = ResourceKey.create(
                        BuiltInRegistries.ITEM.key(),
                        ModRegistries.id("risotto_with_tomato_sauce"));

        public static final Item RISOTTO_WITH_TOMATO_SAUCE = register(
                        RISOTTO_WITH_TOMATO_SAUCE_KEY,
                        new Item.Properties().food(
                                        new FoodProperties.Builder()
                                                        .nutrition(8)
                                                        .saturationModifier(0.8f)
                                                        .build())
                                        .craftRemainder(net.minecraft.world.item.Items.BOWL)
                                        .stacksTo(16));

        public static final ResourceKey<Item> MOZZARELLA_KEY = ResourceKey.create(
                        BuiltInRegistries.ITEM.key(),
                        ModRegistries.id("mozzarella"));

        // A whole mozzarella has the same nutrition and saturation as four slices.
        public static final Item MOZZARELLA = register(
                        MOZZARELLA_KEY,
                        new Item.Properties().food(
                                        new FoodProperties.Builder()
                                                        .nutrition(4)
                                                        .saturationModifier(0.2f)
                                                        .build()));

        private static Item register(ResourceKey<Item> itemKey, Item.Properties properties) {
                return register(itemKey, Item::new, properties);
        }

        private static Item register(
                ResourceKey<Item> itemKey,
                Function<Item.Properties, Item> factory,
                Item.Properties properties
        ) {
                Item item = factory.apply(properties.setId(itemKey));
                Registry.register(BuiltInRegistries.ITEM, itemKey, item);
                return item;
        }

        // Mozzarella slices
        public static final ResourceKey<Item> MOZZARELLA_SLICE_KEY = ResourceKey.create(
                        BuiltInRegistries.ITEM.key(),
                        ModRegistries.id("mozzarella_slice"));

        public static final Item MOZZARELLA_SLICE = register(
                        MOZZARELLA_SLICE_KEY,
                        new Item.Properties().food(
                                        new FoodProperties.Builder()
                                                        .nutrition(1)
                                                        .saturationModifier(0.2f)
                                                        .build()));

        // Tomato slices
        public static final ResourceKey<Item> TOMATO_SLICE_KEY = ResourceKey.create(
                        BuiltInRegistries.ITEM.key(),
                        ModRegistries.id("tomato_slice"));
        
        public static final Item TOMATO_SLICE = register(
                        TOMATO_SLICE_KEY,
                        new Item.Properties().food(
                                        new FoodProperties.Builder()
                                                        .nutrition(1)
                                                        .saturationModifier(0.2f)
                                                        .build()));
        
        // Rennet
        public static final ResourceKey<Item> RENNET_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("rennet"));

        public static final Item RENNET = register(
                RENNET_KEY,
                new Item.Properties()
                        .craftRemainder(net.minecraft.world.item.Items.GLASS_BOTTLE)
                        .stacksTo(16));

        // Curd
        public static final ResourceKey<Item> CURD_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("curd"));

        public static final Item CURD = register(
                CURD_KEY,
                new Item.Properties()
                        .food(
                                new FoodProperties.Builder()
                                        .nutrition(1)
                                        .saturationModifier(0.1f)
                                        .build()
                        )
                        .craftRemainder(net.minecraft.world.item.Items.BOWL)
                        .usingConvertsTo(net.minecraft.world.item.Items.BOWL)
                        .stacksTo(16));

        // Cream Bowl
        // PLACEHOLDER BALANCE: values will be reviewed with the full dairy line.
        public static final ResourceKey<Item> CREAM_BOWL_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("cream_bowl"));

        public static final Item CREAM_BOWL = register(
                CREAM_BOWL_KEY,
                new Item.Properties()
                        .food(
                                new FoodProperties.Builder()
                                        .nutrition(2)
                                        .saturationModifier(0.2f)
                                        .build()
                        )
                        .craftRemainder(net.minecraft.world.item.Items.BOWL)
                        .usingConvertsTo(net.minecraft.world.item.Items.BOWL)
                        .stacksTo(16));

        // Blue Mold Culture
        public static final ResourceKey<Item> BLUE_MOLD_CULTURE_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("blue_mold_culture"));

        public static final Item BLUE_MOLD_CULTURE = register(
                BLUE_MOLD_CULTURE_KEY,
                new Item.Properties()
                        .stacksTo(64));

        // Ricotta
        // PLACEHOLDER BALANCE: nutrition/saturation/stack size will be reviewed
        // together with the other dairy products at the end of development.
        public static final ResourceKey<Item> RICOTTA_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("ricotta"));

        public static final Item RICOTTA = register(
                RICOTTA_KEY,
                new Item.Properties()
                        .food(
                                new FoodProperties.Builder()
                                        .nutrition(4)
                                        .saturationModifier(0.3f)
                                        .build()
                        )
                        .craftRemainder(net.minecraft.world.item.Items.BOWL)
                        .usingConvertsTo(net.minecraft.world.item.Items.BOWL)
                        .stacksTo(16));

        // Sheep Milk Bottle
        // Mirrors Farmer's Delight Refabricated Milk Bottle behavior:
        // drinking removes one compatible random status effect.
        public static final ResourceKey<Item> SHEEP_MILK_BOTTLE_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("sheep_milk_bottle"));

        public static final Item SHEEP_MILK_BOTTLE = register(
                SHEEP_MILK_BOTTLE_KEY,
                properties -> new ConsumableItem(properties, false, true),
                new Item.Properties()
                        .craftRemainder(Items.GLASS_BOTTLE)
                        .component(
                                DataComponents.CONSUMABLE,
                                FoodValues.ConsumableValues.MILK_BOTTLE
                        )
                        .stacksTo(16));

        // Sheep Milk Bucket
        // Uses the same consumable component as vanilla milk, clearing all
        // active status effects and converting back to an empty bucket.
        public static final ResourceKey<Item> SHEEP_MILK_BUCKET_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("sheep_milk_bucket"));

        public static final Item SHEEP_MILK_BUCKET = register(
                SHEEP_MILK_BUCKET_KEY,
                new Item.Properties()
                        .craftRemainder(Items.BUCKET)
                        .component(
                                DataComponents.CONSUMABLE,
                                Consumables.MILK_BUCKET
                        )
                        .usingConvertsTo(Items.BUCKET)
                        .stacksTo(1));

        // Whey bottle: 250 mB of whey. It is a processing resource, not a drink.
        public static final ResourceKey<Item> WHEY_BOTTLE_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("whey_bottle"));

        public static final Item WHEY_BOTTLE = register(
                WHEY_BOTTLE_KEY,
                new Item.Properties()
                        .craftRemainder(net.minecraft.world.item.Items.GLASS_BOTTLE)
                        .stacksTo(16));

        // Whey bucket: 1000 mB of whey. It is not placeable as a world fluid;
        // cauldron interaction will be handled explicitly when that system is added.
        public static final ResourceKey<Item> WHEY_BUCKET_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("whey_bucket"));

        public static final Item WHEY_BUCKET = register(
                WHEY_BUCKET_KEY,
                new Item.Properties()
                        .craftRemainder(net.minecraft.world.item.Items.BUCKET)
                        .stacksTo(1));

        // Salt
        public static final ResourceKey<Item> SALT_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("salt"));

        public static final Item SALT = register(
                SALT_KEY,
                new Item.Properties()
                        .stacksTo(64));

        public static void register() {
        }
}
