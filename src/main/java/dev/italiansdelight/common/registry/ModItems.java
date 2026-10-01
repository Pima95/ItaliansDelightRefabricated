package dev.italiansdelight.common.registry;

import java.util.function.Function;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.core.Registry;

import dev.italiansdelight.ItaliansDelight;
import dev.italiansdelight.common.aging.AgingCheeseType;
import dev.italiansdelight.common.item.AgingCheeseItem;

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

        public static final ResourceKey<Item> CHEESE_GRATER_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("cheese_grater"));

        public static final Item CHEESE_GRATER = register(
                CHEESE_GRATER_KEY,
                new Item.Properties()
                        // Farmer's Delight's iron knife uses this same material durability.
                        .durability(ToolMaterial.IRON.durability())
                        .repairable(ToolMaterial.IRON.repairItems()));

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
                                                        .saturationModifier(0.3f)
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

        /**
         * Compact registration helpers used by content families with many simple items.
         * A stable ResourceKey is still created for every identifier.
         */
        private static Item registerSimpleItem(String id, int maxStackSize) {
                ResourceKey<Item> key = ResourceKey.create(
                        BuiltInRegistries.ITEM.key(),
                        ModRegistries.id(id)
                );

                return register(
                        key,
                        new Item.Properties().stacksTo(maxStackSize)
                );
        }

        private static Item registerFoodItem(
                String id,
                int nutrition,
                float saturation
        ) {
                ResourceKey<Item> key = ResourceKey.create(
                        BuiltInRegistries.ITEM.key(),
                        ModRegistries.id(id)
                );

                return register(
                        key,
                        new Item.Properties()
                                .food(
                                        new FoodProperties.Builder()
                                                .nutrition(nutrition)
                                                .saturationModifier(saturation)
                                                .build()
                                )
                                .stacksTo(64)
                );
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
                                                        .saturationModifier(0.3f)
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
                                        .nutrition(2)
                                        .saturationModifier(0.2f)
                                        .build()
                        )
                        .craftRemainder(net.minecraft.world.item.Items.BOWL)
                        .usingConvertsTo(net.minecraft.world.item.Items.BOWL)
                        .stacksTo(16));

        // Cream Bowl
        public static final ResourceKey<Item> CREAM_BOWL_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("cream_bowl"));

        public static final Item CREAM_BOWL = register(
                CREAM_BOWL_KEY,
                new Item.Properties()
                        .food(
                                new FoodProperties.Builder()
                                        .nutrition(3)
                                        .saturationModifier(0.3f)
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


        // Apple Cider Vinegar Bottle
        // The production recipe belongs to the wine/barrel development branch.
        // As a Cheese Vat ingredient it returns its glass bottle normally.
        public static final ResourceKey<Item> APPLE_CIDER_VINEGAR_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("apple_cider_vinegar"));

        public static final Item APPLE_CIDER_VINEGAR = register(
                APPLE_CIDER_VINEGAR_KEY,
                new Item.Properties()
                        .craftRemainder(Items.GLASS_BOTTLE)
                        .stacksTo(16));

        // Ricotta
        public static final ResourceKey<Item> RICOTTA_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("ricotta"));

        public static final Item RICOTTA = register(
                RICOTTA_KEY,
                new Item.Properties()
                        .food(
                                new FoodProperties.Builder()
                                        .nutrition(5)
                                        .saturationModifier(0.4f)
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


        // -----------------------------------------------------------------
        // Cheese line — balanced values
        // -----------------------------------------------------------------
        // Whole aging forms remain stackable processing items; edible portions
        // carry the nutrition values defined by the final v1 dairy balance.

        // Bocconcino
        public static final ResourceKey<Item> BOCCONCINO_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("bocconcino"));

        public static final Item BOCCONCINO = register(
                BOCCONCINO_KEY,
                new Item.Properties()
                        .food(
                                new FoodProperties.Builder()
                                        .nutrition(2)
                                        .saturationModifier(0.3f)
                                        .build()
                        )
                        .stacksTo(64));

        // Parmigiano Reggiano: fresh wheel -> aged wheel -> wedge / grated.
        public static final ResourceKey<Item> FRESH_PARMIGIANO_REGGIANO_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("fresh_parmigiano_reggiano"));

        public static final Item FRESH_PARMIGIANO_REGGIANO = register(
                FRESH_PARMIGIANO_REGGIANO_KEY,
                properties -> new AgingCheeseItem(
                        AgingCheeseType.PARMIGIANO_REGGIANO,
                        properties
                ),
                new Item.Properties().stacksTo(16));

        public static final ResourceKey<Item> PARMIGIANO_REGGIANO_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("parmigiano_reggiano"));

        public static final Item PARMIGIANO_REGGIANO = register(
                PARMIGIANO_REGGIANO_KEY,
                properties -> new AgingCheeseItem(AgingCheeseType.PARMIGIANO_REGGIANO, true, properties),
                new Item.Properties().stacksTo(16));

        public static final ResourceKey<Item> PARMIGIANO_REGGIANO_WEDGE_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("parmigiano_reggiano_wedge"));

        public static final Item PARMIGIANO_REGGIANO_WEDGE = register(
                PARMIGIANO_REGGIANO_WEDGE_KEY,
                new Item.Properties()
                        .food(
                                new FoodProperties.Builder()
                                        .nutrition(4)
                                        .saturationModifier(0.5f)
                                        .build()
                        )
                        .stacksTo(64));

        public static final ResourceKey<Item> GRATED_PARMIGIANO_REGGIANO_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("grated_parmigiano_reggiano"));

        public static final Item GRATED_PARMIGIANO_REGGIANO = register(
                GRATED_PARMIGIANO_REGGIANO_KEY,
                new Item.Properties().stacksTo(64));

        // Pecorino Romano: fresh wheel -> aged wheel -> wedge / grated.
        public static final ResourceKey<Item> FRESH_PECORINO_ROMANO_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("fresh_pecorino_romano"));

        public static final Item FRESH_PECORINO_ROMANO = register(
                FRESH_PECORINO_ROMANO_KEY,
                properties -> new AgingCheeseItem(
                        AgingCheeseType.PECORINO_ROMANO,
                        properties
                ),
                new Item.Properties().stacksTo(16));

        public static final ResourceKey<Item> PECORINO_ROMANO_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("pecorino_romano"));

        public static final Item PECORINO_ROMANO = register(
                PECORINO_ROMANO_KEY,
                properties -> new AgingCheeseItem(AgingCheeseType.PECORINO_ROMANO, true, properties),
                new Item.Properties().stacksTo(16));

        public static final ResourceKey<Item> PECORINO_ROMANO_WEDGE_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("pecorino_romano_wedge"));

        public static final Item PECORINO_ROMANO_WEDGE = register(
                PECORINO_ROMANO_WEDGE_KEY,
                new Item.Properties()
                        .food(
                                new FoodProperties.Builder()
                                        .nutrition(4)
                                        .saturationModifier(0.5f)
                                        .build()
                        )
                        .stacksTo(64));

        public static final ResourceKey<Item> GRATED_PECORINO_ROMANO_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("grated_pecorino_romano"));

        public static final Item GRATED_PECORINO_ROMANO = register(
                GRATED_PECORINO_ROMANO_KEY,
                new Item.Properties().stacksTo(64));

        // Gorgonzola: fresh wheel -> aged wheel -> wedge.
        public static final ResourceKey<Item> FRESH_GORGONZOLA_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("fresh_gorgonzola"));

        public static final Item FRESH_GORGONZOLA = register(
                FRESH_GORGONZOLA_KEY,
                properties -> new AgingCheeseItem(
                        AgingCheeseType.GORGONZOLA,
                        properties
                ),
                new Item.Properties().stacksTo(16));

        public static final ResourceKey<Item> GORGONZOLA_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("gorgonzola"));

        public static final Item GORGONZOLA = register(
                GORGONZOLA_KEY,
                properties -> new AgingCheeseItem(AgingCheeseType.GORGONZOLA, true, properties),
                new Item.Properties().stacksTo(16));

        public static final ResourceKey<Item> GORGONZOLA_WEDGE_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("gorgonzola_wedge"));

        public static final Item GORGONZOLA_WEDGE = register(
                GORGONZOLA_WEDGE_KEY,
                new Item.Properties()
                        .food(
                                new FoodProperties.Builder()
                                        .nutrition(4)
                                        .saturationModifier(0.4f)
                                        .build()
                        )
                        .stacksTo(64));

        // Provolone: fresh whole cheese -> aged whole cheese -> slices.
        public static final ResourceKey<Item> FRESH_PROVOLONE_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("fresh_provolone"));

        public static final Item FRESH_PROVOLONE = register(
                FRESH_PROVOLONE_KEY,
                properties -> new AgingCheeseItem(
                        AgingCheeseType.PROVOLONE,
                        properties
                ),
                new Item.Properties().stacksTo(16));

        public static final ResourceKey<Item> PROVOLONE_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("provolone"));

        public static final Item PROVOLONE = register(
                PROVOLONE_KEY,
                properties -> new AgingCheeseItem(AgingCheeseType.PROVOLONE, true, properties),
                new Item.Properties().stacksTo(16));

        public static final ResourceKey<Item> PROVOLONE_SLICE_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("provolone_slice"));

        public static final Item PROVOLONE_SLICE = register(
                PROVOLONE_SLICE_KEY,
                new Item.Properties()
                        .food(
                                new FoodProperties.Builder()
                                        .nutrition(3)
                                        .saturationModifier(0.4f)
                                        .build()
                        )
                        .stacksTo(64));

        // Scamorza: fresh intermediate -> dried cheese; smoked variant included.
        public static final ResourceKey<Item> FRESH_SCAMORZA_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("fresh_scamorza"));

        public static final Item FRESH_SCAMORZA = register(
                FRESH_SCAMORZA_KEY,
                properties -> new AgingCheeseItem(AgingCheeseType.SCAMORZA, false, properties),
                new Item.Properties().stacksTo(16));

        public static final ResourceKey<Item> SCAMORZA_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("scamorza"));

        public static final Item SCAMORZA = register(
                SCAMORZA_KEY,
                properties -> new AgingCheeseItem(AgingCheeseType.SCAMORZA, true, properties),
                new Item.Properties()
                        .food(
                                new FoodProperties.Builder()
                                        .nutrition(8)
                                        .saturationModifier(0.3f)
                                        .build()
                        )
                        .stacksTo(16));

        public static final ResourceKey<Item> SMOKED_SCAMORZA_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("smoked_scamorza"));

        public static final Item SMOKED_SCAMORZA = register(
                SMOKED_SCAMORZA_KEY,
                properties -> new AgingCheeseItem(AgingCheeseType.SMOKED_SCAMORZA, true, properties),
                new Item.Properties()
                        .food(
                                new FoodProperties.Builder()
                                        .nutrition(8)
                                        .saturationModifier(0.5f)
                                        .build()
                        )
                        .stacksTo(16));


        // Scamorza slices: four slices preserve the whole cheese's exact
        // nutrition/saturation budget. The tied Lead is returned by the Cutting Board.
        public static final ResourceKey<Item> SCAMORZA_SLICE_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("scamorza_slice"));

        public static final Item SCAMORZA_SLICE = register(
                SCAMORZA_SLICE_KEY,
                new Item.Properties()
                        .food(
                                new FoodProperties.Builder()
                                        .nutrition(2)
                                        .saturationModifier(0.3f)
                                        .build()
                        )
                        .stacksTo(64));

        public static final ResourceKey<Item> SMOKED_SCAMORZA_SLICE_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("smoked_scamorza_slice"));

        public static final Item SMOKED_SCAMORZA_SLICE = register(
                SMOKED_SCAMORZA_SLICE_KEY,
                new Item.Properties()
                        .food(
                                new FoodProperties.Builder()
                                        .nutrition(2)
                                        .saturationModifier(0.5f)
                                        .build()
                        )
                        .stacksTo(64));

        // Burrata
        public static final ResourceKey<Item> BURRATA_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("burrata"));

        public static final Item BURRATA = register(
                BURRATA_KEY,
                new Item.Properties()
                        .food(
                                new FoodProperties.Builder()
                                        .nutrition(7)
                                        .saturationModifier(0.5f)
                                        .build()
                        )
                        .stacksTo(16));

        // Mascarpone is served in a bowl and returns it after consumption.
        public static final ResourceKey<Item> MASCARPONE_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("mascarpone"));

        public static final Item MASCARPONE = register(
                MASCARPONE_KEY,
                new Item.Properties()
                        .food(
                                new FoodProperties.Builder()
                                        .nutrition(5)
                                        .saturationModifier(0.5f)
                                        .build()
                        )
                        .craftRemainder(Items.BOWL)
                        .usingConvertsTo(Items.BOWL)
                        .stacksTo(16));


        // -----------------------------------------------------------------
        // Cured meats — first implementation
        // -----------------------------------------------------------------
        // Intermediate and whole products use placeholder balance values.
        // Finished slices are edible; final balance will be tuned after survival tests.

        public static final Item SALTED_HAM =
                registerSimpleItem("salted_ham", 16);

        public static final Item RAW_SALAME =
                registerSimpleItem("raw_salame", 16);

        public static final Item RAW_MORTADELLA =
                registerSimpleItem("raw_mortadella", 16);

        public static final Item PREPARED_PANCETTA =
                registerSimpleItem("prepared_pancetta", 16);

        public static final Item PREPARED_GUANCIALE =
                registerSimpleItem("prepared_guanciale", 16);

        public static final Item PREPARED_BRESAOLA =
                registerSimpleItem("prepared_bresaola", 16);

        public static final Item PREPARED_COPPA =
                registerSimpleItem("prepared_coppa", 16);

        public static final Item PREPARED_SPECK =
                registerSimpleItem("prepared_speck", 16);

        public static final Item SMOKED_PREPARED_SPECK =
                registerSimpleItem("smoked_prepared_speck", 16);

        public static final Item PROSCIUTTO_CRUDO =
                registerSimpleItem("prosciutto_crudo", 16);

        public static final Item SALAME =
                registerSimpleItem("salame", 16);

        public static final Item MORTADELLA =
                registerSimpleItem("mortadella", 16);

        public static final Item PANCETTA =
                registerSimpleItem("pancetta", 16);

        public static final Item GUANCIALE =
                registerSimpleItem("guanciale", 16);

        public static final Item BRESAOLA =
                registerSimpleItem("bresaola", 16);

        public static final Item COPPA =
                registerSimpleItem("coppa", 16);

        public static final Item SPECK =
                registerSimpleItem("speck", 16);

        public static final Item PROSCIUTTO_CRUDO_SLICE =
                registerFoodItem("prosciutto_crudo_slice", 3, 0.5f);

        public static final Item SALAME_SLICE =
                registerFoodItem("salame_slice", 3, 0.5f);

        public static final Item MORTADELLA_SLICE =
                registerFoodItem("mortadella_slice", 3, 0.4f);

        public static final Item PANCETTA_SLICE =
                registerFoodItem("pancetta_slice", 2, 0.5f);

        public static final Item GUANCIALE_SLICE =
                registerFoodItem("guanciale_slice", 2, 0.5f);

        public static final Item BRESAOLA_SLICE =
                registerFoodItem("bresaola_slice", 3, 0.4f);

        public static final Item COPPA_SLICE =
                registerFoodItem("coppa_slice", 3, 0.5f);

        public static final Item SPECK_SLICE =
                registerFoodItem("speck_slice", 3, 0.5f);

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
