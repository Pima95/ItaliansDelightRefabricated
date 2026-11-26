package dev.italiansdelight.common.registry;

import java.util.function.Function;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.MilkBucketItem;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.RegisterEvent;
import vectorwing.farmersdelight.common.item.DrinkableItem;
import vectorwing.farmersdelight.common.item.MilkBottleItem;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.core.Registry;

import dev.italiansdelight.ItaliansDelight;
import dev.italiansdelight.common.aging.AgingCheeseType;
import dev.italiansdelight.common.pizza.PizzaItemData;
import dev.italiansdelight.common.pizza.PizzaSliceData;
import dev.italiansdelight.common.item.AgingCheeseItem;
import dev.italiansdelight.common.item.PizzaNamedItem;
import dev.italiansdelight.common.item.PizzaPeelItem;
import dev.italiansdelight.common.item.WineBottleItem;

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

        public static Item CHEESE_GRATER;

        // Formed raw pizza item: retains only the full ingredient composition.
        // Baking progress is held by the oven and never by this ItemStack.
        public static final ResourceKey<Item> RAW_PIZZA_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("raw_pizza"));

        public static Item RAW_PIZZA;

        // Baked pizza stays non-edible AS A WHOLE. Place it in the world
        // to retrieve four independent edible pizza_slice items.
        // Approved 16x16 cooked item models are chosen dynamically from the
        // four quarters by PizzaDisplayNames. Mixed pizzas deliberately keep
        // the original Margherita icon, without changing the saved ingredients.
        public static final ResourceKey<Item> COOKED_PIZZA_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("cooked_pizza"));

        public static Item COOKED_PIZZA;

        /**
         * Edible quarter of a baked pizza. Food values are replaced on each
         * created stack by PizzaSliceFactory using that quarter's categories.
         * The original ingredient data is persisted for localized names/tooltips.
         */
        public static final ResourceKey<Item> PIZZA_SLICE_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("pizza_slice"));

        public static Item PIZZA_SLICE;

        // Uses the owner's existing 16x16 pizza_peel texture.
        // A loaded peel stores one whole pizza in ModDataComponents.PEEL_CARGO.
        // Crafting costs are not yet approved: test via the creative tab.
        public static final ResourceKey<Item> PIZZA_PEEL_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(), ModRegistries.id("pizza_peel"));

        public static Item PIZZA_PEEL;

        // Minecraft 26.2 binds vanilla item default components AFTER
        // mod registration. Reading Items.WOODEN_SHOVEL.getDefaultInstance()
        // here crashed during ModItems.<clinit> ("Components not bound yet").
        // Instead use exactly the vanilla WOODEN_SHOVEL combat baselines,
        // calculated without instantiating any vanilla ItemStack.
        // The mattarello remains an ordinary Item, NOT a shovel: only combat
        // attributes are copied, so mining and pizza interactions stay intact.
        public static final ResourceKey<Item> ROLLING_PIN_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("rolling_pin"));

        private static final ItemAttributeModifiers ROLLING_PIN_COMBAT_ATTRIBUTES =
                ItemAttributeModifiers.builder()
                        .add(Attributes.ATTACK_DAMAGE,
                                new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID,
                                        1.5F + Tiers.WOOD.getAttackDamageBonus(),
                                        AttributeModifier.Operation.ADD_VALUE),
                                EquipmentSlotGroup.MAINHAND)
                        .add(Attributes.ATTACK_SPEED,
                                new AttributeModifier(Item.BASE_ATTACK_SPEED_ID,
                                        -3.0F,
                                        AttributeModifier.Operation.ADD_VALUE),
                                EquipmentSlotGroup.MAINHAND)
                        .build();

        public static Item ROLLING_PIN;

        // Pasta with tomato sauce
        public static final ResourceKey<Item> PASTA_WITH_TOMATO_SAUCE_KEY = ResourceKey.create(
                        BuiltInRegistries.ITEM.key(),
                        ModRegistries.id("pasta_with_tomato_sauce"));

        public static Item PASTA_WITH_TOMATO_SAUCE;

        // Risotto with tomato sauce
        public static final ResourceKey<Item> RISOTTO_WITH_TOMATO_SAUCE_KEY = ResourceKey.create(
                        BuiltInRegistries.ITEM.key(),
                        ModRegistries.id("risotto_with_tomato_sauce"));

        public static Item RISOTTO_WITH_TOMATO_SAUCE;

        // -----------------------------------------------------------------
        // Prepared dishes — cheese + cured-meat integration
        // -----------------------------------------------------------------
        // Balance follows Farmer's Delight Refabricated 26.2 tiers:
        // simple meals -> Short, complete meals -> Medium, rich meals -> Long.

        public static Item PASTA_ALLA_CARBONARA;

        public static Item PASTA_ALLA_GRICIA;

        public static Item PASTA_ALL_AMATRICIANA;

        public static Item PASTA_WITH_SPECK_AND_GORGONZOLA;

        public static Item RISOTTO_WITH_SPECK_AND_GORGONZOLA;

        public static Item RISOTTO_WITH_PARMIGIANO;

        public static Item MOZZARELLA_SALAD;

        public static Item RISOTTO_WITH_MUSHROOM;

        public static final ResourceKey<Item> MOZZARELLA_KEY = ResourceKey.create(
                        BuiltInRegistries.ITEM.key(),
                        ModRegistries.id("mozzarella"));

        // A whole mozzarella has the same nutrition and saturation as four slices.
        public static Item MOZZARELLA;

        private static Item register(ResourceKey<Item> itemKey, Item.Properties properties) {
                return register(itemKey, Item::new, properties);
        }

        private static Item register(
                ResourceKey<Item> itemKey,
                Function<Item.Properties, Item> factory,
                Item.Properties properties
        ) {
                Item item = factory.apply(properties);
                Registry.register(BuiltInRegistries.ITEM, itemKey.location(), item);
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

        /**
         * Farmer's Delight-style drinkable bottle. ConsumableItem returns
         * one empty bottle per use even from stacks; Fabric fluid transfer
         * providers still handle filling/emptying via machines.
         */
        private static Item.Properties drinkBottleProperties() {
                return new Item.Properties().craftRemainder(Items.GLASS_BOTTLE).stacksTo(16);
        }

        private static FoodProperties meal(int nutrition, float saturation, int duration) {
                FoodProperties.Builder builder = new FoodProperties.Builder()
                        .nutrition(nutrition).saturationModifier(saturation);
                if (duration > 0) builder.effect(() -> FoodValues.nourishment(duration), 1.0F);
                return builder.build();
        }

        private static Item registerBowlFoodItem(
                String id,
                int nutrition,
                float saturation,
                int duration
        ) {
                ResourceKey<Item> key = ResourceKey.create(
                        BuiltInRegistries.ITEM.key(),
                        ModRegistries.id(id)
                );

                return register(
                        key,
                        properties -> new ConsumableItem(properties, true),
                        new Item.Properties()
                                .food(meal(nutrition, saturation, duration))
                                .craftRemainder(Items.BOWL)
                                .stacksTo(16)
                );
        }

        // Mozzarella slices
        public static final ResourceKey<Item> MOZZARELLA_SLICE_KEY = ResourceKey.create(
                        BuiltInRegistries.ITEM.key(),
                        ModRegistries.id("mozzarella_slice"));

        public static Item MOZZARELLA_SLICE;

        // Tomato slices
        public static final ResourceKey<Item> TOMATO_SLICE_KEY = ResourceKey.create(
                        BuiltInRegistries.ITEM.key(),
                        ModRegistries.id("tomato_slice"));
        
        public static Item TOMATO_SLICE;
        
        // Pizza 1.0.0: prepared vegetable slices made on Farmer's Delight\'s
        // Cutting Board (one whole vegetable -> four slices with a knife).
        // These are the only approved carrot/potato pizza forms. Whole
        // minecraft:carrot and minecraft:potato remain explicitly excluded.
        public static final ResourceKey<Item> CARROT_SLICE_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("carrot_slice"));

        public static Item CARROT_SLICE;

        public static final ResourceKey<Item> POTATO_SLICE_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("potato_slice"));

        public static Item POTATO_SLICE;

        // Rennet
        public static final ResourceKey<Item> RENNET_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("rennet"));

        public static Item RENNET;

        // Four 250 mB rennet bottles combine into one 1000 mB bucket.
        public static final ResourceKey<Item> RENNET_BUCKET_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(), ModRegistries.id("rennet_bucket"));
        public static Item RENNET_BUCKET;

        // Curd
        public static final ResourceKey<Item> CURD_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("curd"));

        public static Item CURD;

        // Cream Bowl
        public static final ResourceKey<Item> CREAM_BOWL_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("cream_bowl"));

        public static Item CREAM_BOWL;

        // Blue Mold Culture
        public static final ResourceKey<Item> BLUE_MOLD_CULTURE_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("blue_mold_culture"));

        public static Item BLUE_MOLD_CULTURE;


        // Apple Cider Vinegar Bottle
        // The production recipe belongs to the wine/barrel development branch.
        // As a Cheese Vat ingredient it returns its glass bottle normally.
        public static final ResourceKey<Item> APPLE_CIDER_VINEGAR_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("apple_cider_vinegar"));

        public static Item APPLE_CIDER_VINEGAR;

        // Four 250 mB apple_cider_vinegar bottles combine into one 1000 mB bucket.
        public static final ResourceKey<Item> APPLE_CIDER_VINEGAR_BUCKET_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(), ModRegistries.id("apple_cider_vinegar_bucket"));
        public static Item APPLE_CIDER_VINEGAR_BUCKET;

        // Ricotta
        public static final ResourceKey<Item> RICOTTA_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("ricotta"));

        public static Item RICOTTA;

        // Sheep Milk Bottle
        // Mirrors Farmer's Delight Refabricated Milk Bottle behavior:
        // drinking removes one compatible random status effect.
        public static final ResourceKey<Item> SHEEP_MILK_BOTTLE_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("sheep_milk_bottle"));

        public static Item SHEEP_MILK_BOTTLE;

        // Sheep Milk Bucket
        // Uses the same consumable component as vanilla milk, clearing all
        // active status effects and converting back to an empty bucket.
        public static final ResourceKey<Item> SHEEP_MILK_BUCKET_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("sheep_milk_bucket"));

        public static Item SHEEP_MILK_BUCKET;

        // Whey bottle: 250 mB of whey. It is a processing resource, not a drink.
        public static final ResourceKey<Item> WHEY_BOTTLE_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("whey_bottle"));

        public static Item WHEY_BOTTLE;

        // Whey bucket: 1000 mB of whey. It is not placeable as a world fluid;
        // cauldron interaction will be handled explicitly when that system is added.
        public static final ResourceKey<Item> WHEY_BUCKET_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("whey_bucket"));

        public static Item WHEY_BUCKET;


        // -----------------------------------------------------------------
        // Cheese line — balanced values
        // -----------------------------------------------------------------
        // Whole aging forms remain stackable processing items; edible portions
        // carry the nutrition values defined by the final v1 dairy balance.

        // Bocconcino
        public static final ResourceKey<Item> BOCCONCINO_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("bocconcino"));

        public static Item BOCCONCINO;

        // Parmigiano Reggiano: fresh wheel -> aged wheel -> wedge / grated.
        public static final ResourceKey<Item> FRESH_PARMIGIANO_REGGIANO_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("fresh_parmigiano_reggiano"));

        public static Item FRESH_PARMIGIANO_REGGIANO;

        public static final ResourceKey<Item> PARMIGIANO_REGGIANO_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("parmigiano_reggiano"));

        public static Item PARMIGIANO_REGGIANO;

        public static final ResourceKey<Item> PARMIGIANO_REGGIANO_WEDGE_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("parmigiano_reggiano_wedge"));

        public static Item PARMIGIANO_REGGIANO_WEDGE;

        public static final ResourceKey<Item> GRATED_PARMIGIANO_REGGIANO_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("grated_parmigiano_reggiano"));

        public static Item GRATED_PARMIGIANO_REGGIANO;

        // Pecorino Romano: fresh wheel -> aged wheel -> wedge / grated.
        public static final ResourceKey<Item> FRESH_PECORINO_ROMANO_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("fresh_pecorino_romano"));

        public static Item FRESH_PECORINO_ROMANO;

        public static final ResourceKey<Item> PECORINO_ROMANO_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("pecorino_romano"));

        public static Item PECORINO_ROMANO;

        public static final ResourceKey<Item> PECORINO_ROMANO_WEDGE_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("pecorino_romano_wedge"));

        public static Item PECORINO_ROMANO_WEDGE;

        public static final ResourceKey<Item> GRATED_PECORINO_ROMANO_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("grated_pecorino_romano"));

        public static Item GRATED_PECORINO_ROMANO;

        // Gorgonzola: fresh wheel -> aged wheel -> wedge.
        public static final ResourceKey<Item> FRESH_GORGONZOLA_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("fresh_gorgonzola"));

        public static Item FRESH_GORGONZOLA;

        public static final ResourceKey<Item> GORGONZOLA_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("gorgonzola"));

        public static Item GORGONZOLA;

        public static final ResourceKey<Item> GORGONZOLA_WEDGE_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("gorgonzola_wedge"));

        public static Item GORGONZOLA_WEDGE;

        // Provolone: fresh whole cheese -> aged whole cheese -> slices.
        public static final ResourceKey<Item> FRESH_PROVOLONE_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("fresh_provolone"));

        public static Item FRESH_PROVOLONE;

        public static final ResourceKey<Item> PROVOLONE_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("provolone"));

        public static Item PROVOLONE;

        public static final ResourceKey<Item> PROVOLONE_SLICE_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("provolone_slice"));

        public static Item PROVOLONE_SLICE;

        // Scamorza: fresh intermediate -> dried cheese; smoked variant included.
        public static final ResourceKey<Item> FRESH_SCAMORZA_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("fresh_scamorza"));

        public static Item FRESH_SCAMORZA;

        public static final ResourceKey<Item> SCAMORZA_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("scamorza"));

        public static Item SCAMORZA;

        public static final ResourceKey<Item> SMOKED_SCAMORZA_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("smoked_scamorza"));

        public static Item SMOKED_SCAMORZA;


        // Scamorza slices: four slices preserve the whole cheese's exact
        // nutrition/saturation budget. The tied Lead is returned by the Cutting Board.
        public static final ResourceKey<Item> SCAMORZA_SLICE_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("scamorza_slice"));

        public static Item SCAMORZA_SLICE;

        public static final ResourceKey<Item> SMOKED_SCAMORZA_SLICE_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("smoked_scamorza_slice"));

        public static Item SMOKED_SCAMORZA_SLICE;

        // Burrata
        public static final ResourceKey<Item> BURRATA_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("burrata"));

        public static Item BURRATA;

        // Mascarpone is served in a bowl and returns it after consumption.
        public static final ResourceKey<Item> MASCARPONE_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("mascarpone"));

        public static Item MASCARPONE;


        // -----------------------------------------------------------------
        // Cured meats — balance v1
        // -----------------------------------------------------------------
        // Intermediate and whole products stack to 16; finished edible slices
        // stack to 64 through registerFoodItem().

        public static Item SALTED_HAM;

        public static Item RAW_SALAME;

        public static Item RAW_MORTADELLA;

        public static Item PREPARED_PANCETTA;

        public static Item PREPARED_GUANCIALE;

        public static Item PREPARED_BRESAOLA;

        public static Item PREPARED_COPPA;

        public static Item PREPARED_SPECK;

        public static Item SMOKED_PREPARED_SPECK;

        public static Item PROSCIUTTO_CRUDO;

        public static Item SALAME;

        public static Item MORTADELLA;

        public static Item PANCETTA;

        public static Item GUANCIALE;

        public static Item BRESAOLA;

        public static Item COPPA;

        public static Item SPECK;

        public static Item PROSCIUTTO_CRUDO_SLICE;

        public static Item SALAME_SLICE;

        public static Item MORTADELLA_SLICE;

        public static Item PANCETTA_SLICE;

        public static Item GUANCIALE_SLICE;

        public static Item BRESAOLA_SLICE;

        public static Item COPPA_SLICE;

        public static Item SPECK_SLICE;

        // Orchard fruits: Creative-only acquisition until harvesting is added.
        // Only reversible storage crafting is provided; no foods or tree drops.
        // New vineyard/chili crop produce; seed BlockItems live in ModBlocks.
        public static Item GRAPES;
        public static Item WHITE_GRAPES;
        public static Item CHILI_PEPPER;
        public static Item GHOST_PEPPER;

        // Rustic byproduct: apple pips. Tree planting will be added with the apple orchard.
        public static Item APPLE_SEEDS;

        // Olive pitting: one edible flesh and one pit per whole Olive.
        // The existing olive_seed texture is a non-plantable byproduct.
        public static Item OLIVE_SEED;
        public static Item OLIVE_PITTED;
        // A bowl of pate returns the bowl when spread on the bread.
        public static Item OLIVE_PATE;
        public static Item BRUSCHETTA;
        public static Item OLIVE_BRUSCHETTA;
        public static Item OLIVES;
        public static Item ALMONDS;
        // Orchard 1.1.0 almond preparations. Uncut almonds cannot go on pizzas.
        public static Item CHOPPED_ALMONDS;
        public static Item ALMOND_PESTO;
        public static Item BASIL_PESTO;
        // The almond_cake ITEM ID stays exactly the same, now registered as
        // a placeable BlockItem by ModBlocks to serve 4 portions in-world.
        // Registering an Item here as well would duplicate that registry ID.
        public static Item ALMOND_CAKE_SLICE;
        // Historical pasta_with_pesto art/ID = BASIL pesto, not almond.
        public static Item PASTA_WITH_PESTO;
        public static Item PASTA_WITH_ALMOND_PESTO;
        // 1.2.0 finished dishes. All required ingredients already exist.
        public static Item SPICY_OLIVE_PASTA;
        public static Item PASTA_WITH_RICOTTA_AND_PANCETTA;
        public static Item RISOTTO_WITH_SCAMORZA_AND_OLIVES;
        public static Item PASTA_WITH_ALMOND_PESTO_AND_SPECK;
        public static Item GRAPE_CHERRY_FRUIT_SALAD;
        public static Item ALMOND_BISCUITS;

        public static Item CHERRIES;

        // The Cooking Pot serves this jam into a glass bottle; crafting with
        // it leaves an empty bottle.
        public static Item CHERRY_JAM_BOTTLE;
        public static Item CHERRY_JAM_TOAST;
        // Cherry Tart itself is registered as a placeable BlockItem by ModBlocks.
        public static Item CHERRY_TART_SLICE;

        public static final ResourceKey<Item> WINE_BUCKET_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(), ModRegistries.id("wine_bucket"));
        public static Item WINE_BUCKET;

        public static final ResourceKey<Item> WINE_BOTTLE_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(), ModRegistries.id("wine_bottle"));
        public static Item WINE_BOTTLE;

        // Grape juice: a real Fabric storage fluid, intentionally not world-placeable.
        // The buckets and bottles participate in the existing Barrel/Cauldron API.
        public static final ResourceKey<Item> GRAPE_JUICE_BUCKET_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(), ModRegistries.id("grape_juice_bucket"));
        public static Item GRAPE_JUICE_BUCKET;
        public static final ResourceKey<Item> GRAPE_JUICE_BOTTLE_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(), ModRegistries.id("grape_juice_bottle"));
        public static Item GRAPE_JUICE_BOTTLE;

        // White grape juice uses a distinct, non-placeable fluid ID while
        // its bottle remains interchangeable with the fluid transfer API.
        public static final ResourceKey<Item> WHITE_GRAPE_JUICE_BUCKET_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(), ModRegistries.id("white_grape_juice_bucket"));
        public static Item WHITE_GRAPE_JUICE_BUCKET;
        public static final ResourceKey<Item> WHITE_GRAPE_JUICE_BOTTLE_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(), ModRegistries.id("white_grape_juice_bottle"));
        public static Item WHITE_GRAPE_JUICE_BOTTLE;

        // Apple juice: a placeable fluid bucket and a drinkable bottle.
        public static final ResourceKey<Item> APPLE_JUICE_BUCKET_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(), ModRegistries.id("apple_juice_bucket"));
        public static Item APPLE_JUICE_BUCKET;

        public static final ResourceKey<Item> APPLE_JUICE_BOTTLE_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(), ModRegistries.id("apple_juice_bottle"));
        public static Item APPLE_JUICE_BOTTLE;

        // Golden apple juice is non-placeable; drinking grants the NORMAL
        // golden apple effects, not the enchanted golden apple effects.
        public static final ResourceKey<Item> GOLDEN_APPLE_JUICE_BOTTLE_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(), ModRegistries.id("golden_apple_juice_bottle"));
        public static Item GOLDEN_APPLE_JUICE_BOTTLE;

        // These are NON-PLACEABLE container items: no BucketItem.placeLiquid.
        // They still exchange 1000 mB in the Crushing Tub, Barrel and Cauldron.
        public static final ResourceKey<Item> GOLDEN_APPLE_JUICE_BUCKET_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(), ModRegistries.id("golden_apple_juice_bucket"));
        public static Item GOLDEN_APPLE_JUICE_BUCKET;

        // Existing virtual honey uses minecraft:honey, NOT a new registered
        // world fluid. Its bucket is handled by the legacy liquid containers.
        public static final ResourceKey<Item> HONEY_BUCKET_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(), ModRegistries.id("honey_bucket"));
        public static Item HONEY_BUCKET;

        public static final ResourceKey<Item> OLIVE_OIL_BUCKET_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(), ModRegistries.id("olive_oil_bucket"));
        public static Item OLIVE_OIL_BUCKET;
        public static final ResourceKey<Item> OLIVE_OIL_BOTTLE_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(), ModRegistries.id("olive_oil_bottle"));
        public static Item OLIVE_OIL_BOTTLE;

        // Salt
        public static final ResourceKey<Item> SALT_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("salt"));

        public static Item SALT;


        // Rustic-style wine, cider, mead and cherry juice families.
        public static Item CHERRY_JUICE_BOTTLE;
        public static Item CHERRY_JUICE_BUCKET;
        public static Item WHITE_WINE_BOTTLE;
        public static Item WHITE_WINE_BUCKET;
        public static Item CHERRY_WINE_BOTTLE;
        public static Item CHERRY_WINE_BUCKET;
        public static Item MEAD_BOTTLE;
        public static Item MEAD_BUCKET;
        public static Item CIDER_BOTTLE;
        public static Item CIDER_BUCKET;
        public static Item RED_WINE_VINEGAR_BOTTLE;
        public static Item RED_WINE_VINEGAR_BUCKET;

        public static void onRegister(RegisterEvent event) {
                if (!event.getRegistryKey().equals(Registries.ITEM)) return;
                CHEESE_GRATER = register(
                CHEESE_GRATER_KEY,
                properties -> new Item(properties) {
                    @Override public boolean isValidRepairItem(net.minecraft.world.item.ItemStack stack,
                            net.minecraft.world.item.ItemStack repair) {
                        return Tiers.IRON.getRepairIngredient().test(repair);
                    }
                },
                new Item.Properties()
                        // Farmer's Delight's iron knife uses this same material durability.
                        .durability(Tiers.IRON.getUses())
                        );
                RAW_PIZZA = register(
                RAW_PIZZA_KEY,
                properties -> new PizzaNamedItem(properties, false),
                new Item.Properties()
                        .stacksTo(1)
                        .component(ModDataComponents.PIZZA_CONTENTS, PizzaItemData.empty()));
                COOKED_PIZZA = register(
                COOKED_PIZZA_KEY,
                properties -> new PizzaNamedItem(properties, false),
                new Item.Properties()
                        .stacksTo(1)
                        .component(ModDataComponents.PIZZA_CONTENTS, PizzaItemData.empty()));
                PIZZA_SLICE = register(
                PIZZA_SLICE_KEY,
                properties -> new PizzaNamedItem(properties, true),
                new Item.Properties()
                        .stacksTo(16)
                        .food(new FoodProperties.Builder()
                                .nutrition(1).saturationModifier(0.5f).build())
                        .component(ModDataComponents.PIZZA_SLICE_CONTENTS, PizzaSliceData.empty()));
                PIZZA_PEEL = register(
                PIZZA_PEEL_KEY, PizzaPeelItem::new,
                // Same damage, speed and knockback as the FDR 26.2 skillet,
                // but only when the peel is EMPTY. A loaded stack overrides
                // ATTRIBUTE_MODIFIERS with EMPTY; PizzaPeelItem restores them.
                // No durability: the peel remains endlessly reusable.
                new Item.Properties().stacksTo(1)
                        .attributes(PizzaPeelItem.EMPTY_PEEL_WEAPON_ATTRIBUTES));
                ROLLING_PIN = register(
                ROLLING_PIN_KEY,
                new Item.Properties()
                        .durability(64)
                        .attributes(ROLLING_PIN_COMBAT_ATTRIBUTES));
                PASTA_WITH_TOMATO_SAUCE = register(
                        PASTA_WITH_TOMATO_SAUCE_KEY,
                        properties -> new ConsumableItem(properties, true),
                        new Item.Properties()
                                        .food(
                                                new FoodProperties.Builder()
                                                        .nutrition(8)
                                                        .saturationModifier(0.7f).effect(() -> FoodValues.nourishment(FoodValues.SHORT_DURATION), 1.0F)
                                                        .build()
                                        )
                                        
                                        .craftRemainder(Items.BOWL)
                                        .stacksTo(16));
                RISOTTO_WITH_TOMATO_SAUCE = register(
                        RISOTTO_WITH_TOMATO_SAUCE_KEY,
                        properties -> new ConsumableItem(properties, true),
                        new Item.Properties()
                                        .food(
                                                new FoodProperties.Builder()
                                                        .nutrition(8)
                                                        .saturationModifier(0.7f).effect(() -> FoodValues.nourishment(FoodValues.SHORT_DURATION), 1.0F)
                                                        .build()
                                        )
                                        
                                        .craftRemainder(Items.BOWL)
                                        .stacksTo(16));
                PASTA_ALLA_CARBONARA = registerBowlFoodItem("pasta_alla_carbonara", 14, 0.75f, FoodValues.LONG_DURATION);
                PASTA_ALLA_GRICIA = registerBowlFoodItem("pasta_alla_gricia", 12, 0.8f, FoodValues.MEDIUM_DURATION);
                PASTA_ALL_AMATRICIANA = registerBowlFoodItem("pasta_all_amatriciana", 14, 0.75f, FoodValues.LONG_DURATION);
                PASTA_WITH_SPECK_AND_GORGONZOLA = registerBowlFoodItem("pasta_with_speck_and_gorgonzola", 14, 0.75f, FoodValues.LONG_DURATION);
                RISOTTO_WITH_SPECK_AND_GORGONZOLA = registerBowlFoodItem("risotto_with_speck_and_gorgonzola", 14, 0.75f, FoodValues.LONG_DURATION);
                RISOTTO_WITH_PARMIGIANO = registerBowlFoodItem("risotto_with_parmigiano", 10, 0.7f, FoodValues.SHORT_DURATION);
                MOZZARELLA_SALAD = registerBowlFoodItem("mozzarella_salad", 8, 0.6f, FoodValues.SHORT_DURATION);
                RISOTTO_WITH_MUSHROOM = registerBowlFoodItem("risotto_with_mushroom", 12, 0.8f, FoodValues.MEDIUM_DURATION);
                MOZZARELLA = register(
                        MOZZARELLA_KEY,
                        new Item.Properties().food(
                                        new FoodProperties.Builder()
                                                        .nutrition(4)
                                                        .saturationModifier(0.3f)
                                                        .build()));
                MOZZARELLA_SLICE = register(
                        MOZZARELLA_SLICE_KEY,
                        new Item.Properties().food(
                                        new FoodProperties.Builder()
                                                        .nutrition(1)
                                                        .saturationModifier(0.3f)
                                                        .build()));
                TOMATO_SLICE = register(
                        TOMATO_SLICE_KEY,
                        new Item.Properties().food(
                                        new FoodProperties.Builder()
                                                        .nutrition(1)
                                                        .saturationModifier(0.2f)
                                                        .build()));
                CARROT_SLICE = register(
                CARROT_SLICE_KEY,
                new Item.Properties().food(
                        new FoodProperties.Builder()
                                .nutrition(1)
                                .saturationModifier(0.2f)
                                .build()));
                POTATO_SLICE = register(
                POTATO_SLICE_KEY,
                new Item.Properties().food(
                        new FoodProperties.Builder()
                                .nutrition(1)
                                .saturationModifier(0.2f)
                                .build()));
                RENNET = register(
                RENNET_KEY,
                new Item.Properties()
                        .craftRemainder(net.minecraft.world.item.Items.GLASS_BOTTLE)
                        .stacksTo(16));
                RENNET_BUCKET = register(
                RENNET_BUCKET_KEY,
                new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1));
                CURD = register(
                CURD_KEY, ConsumableItem::new, new Item.Properties()
                        .food(
                                new FoodProperties.Builder()
                                        .nutrition(2)
                                        .saturationModifier(0.2f)
                                        .build()
                        )
                        .craftRemainder(net.minecraft.world.item.Items.BOWL)
                        
                        .stacksTo(16));
                CREAM_BOWL = register(
                CREAM_BOWL_KEY, ConsumableItem::new, new Item.Properties()
                        .food(
                                new FoodProperties.Builder()
                                        .nutrition(3)
                                        .saturationModifier(0.3f)
                                        .build()
                        )
                        .craftRemainder(net.minecraft.world.item.Items.BOWL)
                        
                        .stacksTo(16));
                BLUE_MOLD_CULTURE = register(
                BLUE_MOLD_CULTURE_KEY,
                new Item.Properties()
                        .stacksTo(64));
                APPLE_CIDER_VINEGAR = register(
                APPLE_CIDER_VINEGAR_KEY,
                new Item.Properties()
                        .craftRemainder(Items.GLASS_BOTTLE)
                        .stacksTo(16));
                APPLE_CIDER_VINEGAR_BUCKET = register(
                APPLE_CIDER_VINEGAR_BUCKET_KEY,
                new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1));
                RICOTTA = register(
                RICOTTA_KEY, ConsumableItem::new, new Item.Properties()
                        .food(
                                new FoodProperties.Builder()
                                        .nutrition(5)
                                        .saturationModifier(0.4f)
                                        .build()
                        )
                        .craftRemainder(net.minecraft.world.item.Items.BOWL)
                        
                        .stacksTo(16));
                SHEEP_MILK_BOTTLE = register(
                SHEEP_MILK_BOTTLE_KEY,
                MilkBottleItem::new,
                new Item.Properties()
                        .craftRemainder(Items.GLASS_BOTTLE)
                        
                        .stacksTo(16));
                SHEEP_MILK_BUCKET = register(
                SHEEP_MILK_BUCKET_KEY,
                MilkBucketItem::new, new Item.Properties()
                        .craftRemainder(Items.BUCKET)
                        
                        
                        .stacksTo(1));
                WHEY_BOTTLE = register(
                WHEY_BOTTLE_KEY,
                new Item.Properties()
                        .craftRemainder(net.minecraft.world.item.Items.GLASS_BOTTLE)
                        .stacksTo(16));
                WHEY_BUCKET = register(
                WHEY_BUCKET_KEY,
                new Item.Properties()
                        .craftRemainder(net.minecraft.world.item.Items.BUCKET)
                        .stacksTo(1));
                BOCCONCINO = register(
                BOCCONCINO_KEY,
                new Item.Properties()
                        .food(
                                new FoodProperties.Builder()
                                        .nutrition(2)
                                        .saturationModifier(0.3f)
                                        .build()
                        )
                        .stacksTo(64));
                FRESH_PARMIGIANO_REGGIANO = register(
                FRESH_PARMIGIANO_REGGIANO_KEY,
                properties -> new AgingCheeseItem(
                        AgingCheeseType.PARMIGIANO_REGGIANO,
                        properties
                ),
                new Item.Properties().stacksTo(16));
                PARMIGIANO_REGGIANO = register(
                PARMIGIANO_REGGIANO_KEY,
                properties -> new AgingCheeseItem(AgingCheeseType.PARMIGIANO_REGGIANO, true, properties),
                new Item.Properties().stacksTo(16));
                PARMIGIANO_REGGIANO_WEDGE = register(
                PARMIGIANO_REGGIANO_WEDGE_KEY,
                new Item.Properties()
                        .food(
                                new FoodProperties.Builder()
                                        .nutrition(4)
                                        .saturationModifier(0.5f)
                                        .build()
                        )
                        .stacksTo(64));
                GRATED_PARMIGIANO_REGGIANO = register(
                GRATED_PARMIGIANO_REGGIANO_KEY,
                new Item.Properties().stacksTo(64));
                FRESH_PECORINO_ROMANO = register(
                FRESH_PECORINO_ROMANO_KEY,
                properties -> new AgingCheeseItem(
                        AgingCheeseType.PECORINO_ROMANO,
                        properties
                ),
                new Item.Properties().stacksTo(16));
                PECORINO_ROMANO = register(
                PECORINO_ROMANO_KEY,
                properties -> new AgingCheeseItem(AgingCheeseType.PECORINO_ROMANO, true, properties),
                new Item.Properties().stacksTo(16));
                PECORINO_ROMANO_WEDGE = register(
                PECORINO_ROMANO_WEDGE_KEY,
                new Item.Properties()
                        .food(
                                new FoodProperties.Builder()
                                        .nutrition(4)
                                        .saturationModifier(0.5f)
                                        .build()
                        )
                        .stacksTo(64));
                GRATED_PECORINO_ROMANO = register(
                GRATED_PECORINO_ROMANO_KEY,
                new Item.Properties().stacksTo(64));
                FRESH_GORGONZOLA = register(
                FRESH_GORGONZOLA_KEY,
                properties -> new AgingCheeseItem(
                        AgingCheeseType.GORGONZOLA,
                        properties
                ),
                new Item.Properties().stacksTo(16));
                GORGONZOLA = register(
                GORGONZOLA_KEY,
                properties -> new AgingCheeseItem(AgingCheeseType.GORGONZOLA, true, properties),
                new Item.Properties().stacksTo(16));
                GORGONZOLA_WEDGE = register(
                GORGONZOLA_WEDGE_KEY,
                new Item.Properties()
                        .food(
                                new FoodProperties.Builder()
                                        .nutrition(4)
                                        .saturationModifier(0.4f)
                                        .build()
                        )
                        .stacksTo(64));
                FRESH_PROVOLONE = register(
                FRESH_PROVOLONE_KEY,
                properties -> new AgingCheeseItem(
                        AgingCheeseType.PROVOLONE,
                        properties
                ),
                new Item.Properties().stacksTo(16));
                PROVOLONE = register(
                PROVOLONE_KEY,
                properties -> new AgingCheeseItem(AgingCheeseType.PROVOLONE, true, properties),
                new Item.Properties().stacksTo(16));
                PROVOLONE_SLICE = register(
                PROVOLONE_SLICE_KEY,
                new Item.Properties()
                        .food(
                                new FoodProperties.Builder()
                                        .nutrition(3)
                                        .saturationModifier(0.4f)
                                        .build()
                        )
                        .stacksTo(64));
                FRESH_SCAMORZA = register(
                FRESH_SCAMORZA_KEY,
                properties -> new AgingCheeseItem(AgingCheeseType.SCAMORZA, false, properties),
                new Item.Properties().stacksTo(16));
                SCAMORZA = register(
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
                SMOKED_SCAMORZA = register(
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
                SCAMORZA_SLICE = register(
                SCAMORZA_SLICE_KEY,
                new Item.Properties()
                        .food(
                                new FoodProperties.Builder()
                                        .nutrition(2)
                                        .saturationModifier(0.3f)
                                        .build()
                        )
                        .stacksTo(64));
                SMOKED_SCAMORZA_SLICE = register(
                SMOKED_SCAMORZA_SLICE_KEY,
                new Item.Properties()
                        .food(
                                new FoodProperties.Builder()
                                        .nutrition(2)
                                        .saturationModifier(0.5f)
                                        .build()
                        )
                        .stacksTo(64));
                BURRATA = register(
                BURRATA_KEY,
                new Item.Properties()
                        .food(
                                new FoodProperties.Builder()
                                        .nutrition(7)
                                        .saturationModifier(0.5f)
                                        .build()
                        )
                        .stacksTo(16));
                MASCARPONE = register(
                MASCARPONE_KEY, ConsumableItem::new, new Item.Properties()
                        .food(
                                new FoodProperties.Builder()
                                        .nutrition(5)
                                        .saturationModifier(0.5f)
                                        .build()
                        )
                        .craftRemainder(Items.BOWL)
                        
                        .stacksTo(16));
                SALTED_HAM = registerSimpleItem("salted_ham", 16);
                RAW_SALAME = registerSimpleItem("raw_salame", 16);
                RAW_MORTADELLA = registerSimpleItem("raw_mortadella", 16);
                PREPARED_PANCETTA = registerSimpleItem("prepared_pancetta", 16);
                PREPARED_GUANCIALE = registerSimpleItem("prepared_guanciale", 16);
                PREPARED_BRESAOLA = registerSimpleItem("prepared_bresaola", 16);
                PREPARED_COPPA = registerSimpleItem("prepared_coppa", 16);
                PREPARED_SPECK = registerSimpleItem("prepared_speck", 16);
                SMOKED_PREPARED_SPECK = registerSimpleItem("smoked_prepared_speck", 16);
                PROSCIUTTO_CRUDO = registerSimpleItem("prosciutto_crudo", 16);
                SALAME = registerSimpleItem("salame", 16);
                MORTADELLA = registerSimpleItem("mortadella", 16);
                PANCETTA = registerSimpleItem("pancetta", 16);
                GUANCIALE = registerSimpleItem("guanciale", 16);
                BRESAOLA = registerSimpleItem("bresaola", 16);
                COPPA = registerSimpleItem("coppa", 16);
                SPECK = registerSimpleItem("speck", 16);
                PROSCIUTTO_CRUDO_SLICE = registerFoodItem("prosciutto_crudo_slice", 2, 0.5f);
                SALAME_SLICE = registerFoodItem("salame_slice", 2, 0.6f);
                MORTADELLA_SLICE = registerFoodItem("mortadella_slice", 2, 0.5f);
                PANCETTA_SLICE = registerFoodItem("pancetta_slice", 2, 0.7f);
                GUANCIALE_SLICE = registerFoodItem("guanciale_slice", 2, 0.8f);
                BRESAOLA_SLICE = registerFoodItem("bresaola_slice", 2, 0.4f);
                COPPA_SLICE = registerFoodItem("coppa_slice", 2, 0.6f);
                SPECK_SLICE = registerFoodItem("speck_slice", 2, 0.6f);
                GRAPES = registerFoodItem("grapes", 2, 0.2f);
                WHITE_GRAPES = registerFoodItem("white_grapes", 2, 0.2f);
                CHILI_PEPPER = registerFoodItem("chili_pepper", 3, 0.4f);
                GHOST_PEPPER = registerFoodItem("ghost_pepper", 4, 0.7f);
                APPLE_SEEDS = registerSimpleItem("apple_seeds", 64);
                OLIVE_SEED = registerSimpleItem("olive_seed", 64);
                OLIVE_PITTED = registerFoodItem("olive_pitted", 1, 0.2f);
                OLIVE_PATE = register(
                ResourceKey.create(BuiltInRegistries.ITEM.key(), ModRegistries.id("olive_pate")),
                new Item.Properties().craftRemainder(Items.BOWL).stacksTo(16));
                BRUSCHETTA = registerFoodItem("bruschetta", 5, 0.6f);
                OLIVE_BRUSCHETTA = registerFoodItem("olive_bruschetta", 5, 0.6f);
                OLIVES = registerSimpleItem("olives", 64);
                ALMONDS = registerSimpleItem("almonds", 64);
                CHOPPED_ALMONDS = registerSimpleItem("chopped_almonds", 64);
                ALMOND_PESTO = register(
                ResourceKey.create(BuiltInRegistries.ITEM.key(), ModRegistries.id("almond_pesto")),
                new Item.Properties().craftRemainder(Items.BOWL).stacksTo(16));
                BASIL_PESTO = register(
                ResourceKey.create(BuiltInRegistries.ITEM.key(), ModRegistries.id("basil_pesto")),
                new Item.Properties().craftRemainder(Items.BOWL).stacksTo(16));
                ALMOND_CAKE_SLICE = registerFoodItem("almond_cake_slice", 4, 0.5f);
                PASTA_WITH_PESTO = registerBowlFoodItem("pasta_with_pesto", 10, 0.7f,
                        FoodValues.MEDIUM_DURATION);
                PASTA_WITH_ALMOND_PESTO = registerBowlFoodItem("pasta_with_almond_pesto", 10, 0.7f,
                        FoodValues.MEDIUM_DURATION);
                SPICY_OLIVE_PASTA = registerBowlFoodItem("spicy_olive_pasta", 12, 0.75f,
                        FoodValues.MEDIUM_DURATION);
                PASTA_WITH_RICOTTA_AND_PANCETTA = registerBowlFoodItem("pasta_with_ricotta_and_pancetta", 14, 0.75f,
                        FoodValues.LONG_DURATION);
                RISOTTO_WITH_SCAMORZA_AND_OLIVES = registerBowlFoodItem("risotto_with_scamorza_and_olives", 12, 0.8f,
                        FoodValues.MEDIUM_DURATION);
                PASTA_WITH_ALMOND_PESTO_AND_SPECK = registerBowlFoodItem("pasta_with_almond_pesto_and_speck", 14, 0.75f,
                        FoodValues.LONG_DURATION);
                GRAPE_CHERRY_FRUIT_SALAD = registerBowlFoodItem("grape_cherry_fruit_salad", 6, 0.35f,
                        0);
                ALMOND_BISCUITS = registerFoodItem("almond_biscuits", 3, 0.3f);
                CHERRIES = registerFoodItem("cherries", 2, 0.2f);
                CHERRY_JAM_BOTTLE = register(
                ResourceKey.create(BuiltInRegistries.ITEM.key(), ModRegistries.id("cherry_jam_bottle")),
                new Item.Properties().craftRemainder(Items.GLASS_BOTTLE).stacksTo(16));
                CHERRY_JAM_TOAST = registerFoodItem("cherry_jam_toast", 5, 0.6f);
                CHERRY_TART_SLICE = registerFoodItem("cherry_tart_slice", 4, 0.5f);
                WINE_BUCKET = register(WINE_BUCKET_KEY,
                WineBottleItem::new, new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET)
                        .component(ModDataComponents.WINE_QUALITY, 0));
                WINE_BOTTLE = register(WINE_BOTTLE_KEY,
                WineBottleItem::new, new Item.Properties().stacksTo(1)
                        .craftRemainder(Items.GLASS_BOTTLE)
                        
                        
                        .component(ModDataComponents.WINE_QUALITY, 0));
                GRAPE_JUICE_BUCKET = register(
                GRAPE_JUICE_BUCKET_KEY,
                new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1));
                GRAPE_JUICE_BOTTLE = register(
                GRAPE_JUICE_BOTTLE_KEY,
                DrinkableItem::new, drinkBottleProperties());
                WHITE_GRAPE_JUICE_BUCKET = register(
                WHITE_GRAPE_JUICE_BUCKET_KEY,
                new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1));
                WHITE_GRAPE_JUICE_BOTTLE = register(
                WHITE_GRAPE_JUICE_BOTTLE_KEY,
                DrinkableItem::new, drinkBottleProperties());
                APPLE_JUICE_BUCKET = register(
                APPLE_JUICE_BUCKET_KEY,
                properties -> new BucketItem(ModFluids.APPLE_JUICE_STILL, properties),
                new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1));
                APPLE_JUICE_BOTTLE = register(
                APPLE_JUICE_BOTTLE_KEY,
                DrinkableItem::new, drinkBottleProperties());
                GOLDEN_APPLE_JUICE_BOTTLE = register(
                GOLDEN_APPLE_JUICE_BOTTLE_KEY,
                DrinkableItem::new, drinkBottleProperties().food(new FoodProperties.Builder().alwaysEdible()
                        .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 100, 1), 1.0F)
                        .effect(() -> new MobEffectInstance(MobEffects.ABSORPTION, 2400, 0), 1.0F).build()));
                GOLDEN_APPLE_JUICE_BUCKET = register(
                GOLDEN_APPLE_JUICE_BUCKET_KEY,
                new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1));
                HONEY_BUCKET = register(
                HONEY_BUCKET_KEY,
                new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1));
                OLIVE_OIL_BUCKET = register(
                OLIVE_OIL_BUCKET_KEY,
                properties -> new BucketItem(ModFluids.OLIVE_OIL_STILL, properties),
                new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1));
                OLIVE_OIL_BOTTLE = register(
                OLIVE_OIL_BOTTLE_KEY,
                DrinkableItem::new, drinkBottleProperties());
                SALT = register(
                SALT_KEY,
                new Item.Properties()
                        .stacksTo(64));
                CHERRY_JUICE_BOTTLE = register(ResourceKey.create(BuiltInRegistries.ITEM.key(), ModRegistries.id("cherry_juice_bottle")), DrinkableItem::new, drinkBottleProperties());
                CHERRY_JUICE_BUCKET = register(ResourceKey.create(BuiltInRegistries.ITEM.key(), ModRegistries.id("cherry_juice_bucket")), new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET));
                WHITE_WINE_BOTTLE = register(ResourceKey.create(BuiltInRegistries.ITEM.key(), ModRegistries.id("white_wine_bottle")), WineBottleItem::new, new Item.Properties().stacksTo(1).craftRemainder(Items.GLASS_BOTTLE).component(ModDataComponents.WINE_QUALITY, 0));
                WHITE_WINE_BUCKET = register(ResourceKey.create(BuiltInRegistries.ITEM.key(), ModRegistries.id("white_wine_bucket")), WineBottleItem::new, new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET).component(ModDataComponents.WINE_QUALITY, 0));
                CHERRY_WINE_BOTTLE = register(ResourceKey.create(BuiltInRegistries.ITEM.key(), ModRegistries.id("cherry_wine_bottle")), WineBottleItem::new, new Item.Properties().stacksTo(1).craftRemainder(Items.GLASS_BOTTLE).component(ModDataComponents.WINE_QUALITY, 0));
                CHERRY_WINE_BUCKET = register(ResourceKey.create(BuiltInRegistries.ITEM.key(), ModRegistries.id("cherry_wine_bucket")), WineBottleItem::new, new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET).component(ModDataComponents.WINE_QUALITY, 0));
                MEAD_BOTTLE = register(ResourceKey.create(BuiltInRegistries.ITEM.key(), ModRegistries.id("mead_bottle")), WineBottleItem::new, new Item.Properties().stacksTo(1).craftRemainder(Items.GLASS_BOTTLE).component(ModDataComponents.WINE_QUALITY, 0));
                MEAD_BUCKET = register(ResourceKey.create(BuiltInRegistries.ITEM.key(), ModRegistries.id("mead_bucket")), WineBottleItem::new, new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET).component(ModDataComponents.WINE_QUALITY, 0));
                CIDER_BOTTLE = register(ResourceKey.create(BuiltInRegistries.ITEM.key(), ModRegistries.id("cider_bottle")), WineBottleItem::new, new Item.Properties().stacksTo(1).craftRemainder(Items.GLASS_BOTTLE).component(ModDataComponents.WINE_QUALITY, 0));
                CIDER_BUCKET = register(ResourceKey.create(BuiltInRegistries.ITEM.key(), ModRegistries.id("cider_bucket")), WineBottleItem::new, new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET).component(ModDataComponents.WINE_QUALITY, 0));
                RED_WINE_VINEGAR_BOTTLE = register(ResourceKey.create(BuiltInRegistries.ITEM.key(), ModRegistries.id("red_wine_vinegar_bottle")), new Item.Properties().stacksTo(16).craftRemainder(Items.GLASS_BOTTLE));
                RED_WINE_VINEGAR_BUCKET = register(ResourceKey.create(BuiltInRegistries.ITEM.key(), ModRegistries.id("red_wine_vinegar_bucket")), new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET));
        }
}
