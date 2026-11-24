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
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
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

        public static final Item CHEESE_GRATER = register(
                CHEESE_GRATER_KEY,
                new Item.Properties()
                        // Farmer's Delight's iron knife uses this same material durability.
                        .durability(ToolMaterial.IRON.durability())
                        .repairable(ToolMaterial.IRON.repairItems()));

        // Formed raw pizza item: retains only the full ingredient composition.
        // Baking progress is held by the oven and never by this ItemStack.
        public static final ResourceKey<Item> RAW_PIZZA_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("raw_pizza"));

        public static final Item RAW_PIZZA = register(
                RAW_PIZZA_KEY,
                properties -> new PizzaNamedItem(properties, false),
                new Item.Properties()
                        .stacksTo(1)
                        .component(ModDataComponents.PIZZA_CONTENTS, PizzaItemData.empty()));

        // Baked pizza stays non-edible AS A WHOLE. Place it in the world
        // to retrieve four independent edible pizza_slice items.
        // Approved 16x16 cooked item models are chosen dynamically from the
        // four quarters by PizzaDisplayNames. Mixed pizzas deliberately keep
        // the original Margherita icon, without changing the saved ingredients.
        public static final ResourceKey<Item> COOKED_PIZZA_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("cooked_pizza"));

        public static final Item COOKED_PIZZA = register(
                COOKED_PIZZA_KEY,
                properties -> new PizzaNamedItem(properties, false),
                new Item.Properties()
                        .stacksTo(1)
                        .component(ModDataComponents.PIZZA_CONTENTS, PizzaItemData.empty()));

        /**
         * Edible quarter of a baked pizza. Food values are replaced on each
         * created stack by PizzaSliceFactory using that quarter's categories.
         * The original ingredient data is persisted for localized names/tooltips.
         */
        public static final ResourceKey<Item> PIZZA_SLICE_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("pizza_slice"));

        public static final Item PIZZA_SLICE = register(
                PIZZA_SLICE_KEY,
                properties -> new PizzaNamedItem(properties, true),
                new Item.Properties()
                        .stacksTo(16)
                        .food(new FoodProperties.Builder()
                                .nutrition(1).saturationModifier(0.5f).build())
                        .component(ModDataComponents.PIZZA_SLICE_CONTENTS, PizzaSliceData.empty()));

        // Uses the owner's existing 16x16 pizza_peel texture.
        // A loaded peel stores one whole pizza in ModDataComponents.PEEL_CARGO.
        // Crafting costs are not yet approved: test via the creative tab.
        public static final ResourceKey<Item> PIZZA_PEEL_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(), ModRegistries.id("pizza_peel"));

        public static final Item PIZZA_PEEL = register(
                PIZZA_PEEL_KEY, PizzaPeelItem::new,
                // Same damage, speed and knockback as the FDR 26.2 skillet,
                // but only when the peel is EMPTY. A loaded stack overrides
                // ATTRIBUTE_MODIFIERS with EMPTY; PizzaPeelItem restores them.
                // No durability: the peel remains endlessly reusable.
                new Item.Properties().stacksTo(1)
                        .attributes(PizzaPeelItem.EMPTY_PEEL_WEAPON_ATTRIBUTES));

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
                                        1.5F + ToolMaterial.WOOD.attackDamageBonus(),
                                        AttributeModifier.Operation.ADD_VALUE),
                                EquipmentSlotGroup.MAINHAND)
                        .add(Attributes.ATTACK_SPEED,
                                new AttributeModifier(Item.BASE_ATTACK_SPEED_ID,
                                        -3.0F,
                                        AttributeModifier.Operation.ADD_VALUE),
                                EquipmentSlotGroup.MAINHAND)
                        .build();

        public static final Item ROLLING_PIN = register(
                ROLLING_PIN_KEY,
                new Item.Properties()
                        .durability(64)
                        .attributes(ROLLING_PIN_COMBAT_ATTRIBUTES));

        // Pasta with tomato sauce
        public static final ResourceKey<Item> PASTA_WITH_TOMATO_SAUCE_KEY = ResourceKey.create(
                        BuiltInRegistries.ITEM.key(),
                        ModRegistries.id("pasta_with_tomato_sauce"));

        public static final Item PASTA_WITH_TOMATO_SAUCE = register(
                        PASTA_WITH_TOMATO_SAUCE_KEY,
                        properties -> new ConsumableItem(properties, true),
                        new Item.Properties()
                                        .food(
                                                new FoodProperties.Builder()
                                                        .nutrition(8)
                                                        .saturationModifier(0.7f)
                                                        .build()
                                        )
                                        .component(
                                                DataComponents.CONSUMABLE,
                                                FoodValues.ConsumableValues.NOURISHMENT_SHORT_DURATION
                                        )
                                        .craftRemainder(Items.BOWL)
                                        .stacksTo(16));

        // Risotto with tomato sauce
        public static final ResourceKey<Item> RISOTTO_WITH_TOMATO_SAUCE_KEY = ResourceKey.create(
                        BuiltInRegistries.ITEM.key(),
                        ModRegistries.id("risotto_with_tomato_sauce"));

        public static final Item RISOTTO_WITH_TOMATO_SAUCE = register(
                        RISOTTO_WITH_TOMATO_SAUCE_KEY,
                        properties -> new ConsumableItem(properties, true),
                        new Item.Properties()
                                        .food(
                                                new FoodProperties.Builder()
                                                        .nutrition(8)
                                                        .saturationModifier(0.7f)
                                                        .build()
                                        )
                                        .component(
                                                DataComponents.CONSUMABLE,
                                                FoodValues.ConsumableValues.NOURISHMENT_SHORT_DURATION
                                        )
                                        .craftRemainder(Items.BOWL)
                                        .stacksTo(16));

        // -----------------------------------------------------------------
        // Prepared dishes — cheese + cured-meat integration
        // -----------------------------------------------------------------
        // Balance follows Farmer's Delight Refabricated 26.2 tiers:
        // simple meals -> Short, complete meals -> Medium, rich meals -> Long.

        public static final Item PASTA_ALLA_CARBONARA =
                registerBowlFoodItem("pasta_alla_carbonara", 14, 0.75f, FoodValues.ConsumableValues.NOURISHMENT_LONG_DURATION);

        public static final Item PASTA_ALLA_GRICIA =
                registerBowlFoodItem("pasta_alla_gricia", 12, 0.8f, FoodValues.ConsumableValues.NOURISHMENT_MEDIUM_DURATION);

        public static final Item PASTA_ALL_AMATRICIANA =
                registerBowlFoodItem("pasta_all_amatriciana", 14, 0.75f, FoodValues.ConsumableValues.NOURISHMENT_LONG_DURATION);

        public static final Item PASTA_WITH_SPECK_AND_GORGONZOLA =
                registerBowlFoodItem("pasta_with_speck_and_gorgonzola", 14, 0.75f, FoodValues.ConsumableValues.NOURISHMENT_LONG_DURATION);

        public static final Item RISOTTO_WITH_SPECK_AND_GORGONZOLA =
                registerBowlFoodItem("risotto_with_speck_and_gorgonzola", 14, 0.75f, FoodValues.ConsumableValues.NOURISHMENT_LONG_DURATION);

        public static final Item RISOTTO_WITH_PARMIGIANO =
                registerBowlFoodItem("risotto_with_parmigiano", 10, 0.7f, FoodValues.ConsumableValues.NOURISHMENT_SHORT_DURATION);

        public static final Item MOZZARELLA_SALAD =
                registerBowlFoodItem("mozzarella_salad", 8, 0.6f, FoodValues.ConsumableValues.NOURISHMENT_SHORT_DURATION);

        public static final Item RISOTTO_WITH_MUSHROOM =
                registerBowlFoodItem("risotto_with_mushroom", 12, 0.8f, FoodValues.ConsumableValues.NOURISHMENT_MEDIUM_DURATION);

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

        /**
         * Farmer's Delight-style drinkable bottle. ConsumableItem returns
         * one empty bottle per use even from stacks; Fabric fluid transfer
         * providers still handle filling/emptying via machines.
         */
        private static Item.Properties drinkBottleProperties(Consumable consumable) {
                return new Item.Properties()
                        .craftRemainder(Items.GLASS_BOTTLE)
                        .component(DataComponents.CONSUMABLE, consumable)
                        .stacksTo(16);
        }

        // Complete handheld foods: Nourishment, but no bowl remainder.
        private static Item registerSecondoFoodItem(String id, int nutrition, float saturation, Consumable consumable) {
                ResourceKey<Item> key = ResourceKey.create(BuiltInRegistries.ITEM.key(), ModRegistries.id(id));
                return register(key, new Item.Properties()
                        .food(new FoodProperties.Builder().nutrition(nutrition)
                                .saturationModifier(saturation).build())
                        .component(DataComponents.CONSUMABLE, consumable)
                        .stacksTo(64));
        }

        private static Item registerBowlFoodItem(
                String id,
                int nutrition,
                float saturation,
                Consumable consumable
        ) {
                ResourceKey<Item> key = ResourceKey.create(
                        BuiltInRegistries.ITEM.key(),
                        ModRegistries.id(id)
                );

                return register(
                        key,
                        properties -> new ConsumableItem(properties, true),
                        new Item.Properties()
                                .food(
                                        new FoodProperties.Builder()
                                                .nutrition(nutrition)
                                                .saturationModifier(saturation)
                                                .build()
                                )
                                .component(DataComponents.CONSUMABLE, consumable)
                                .craftRemainder(Items.BOWL)
                                .stacksTo(16)
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
        
        // Pizza 1.0.0: prepared vegetable slices made on Farmer's Delight\'s
        // Cutting Board (one whole vegetable -> four slices with a knife).
        // These are the only approved carrot/potato pizza forms. Whole
        // minecraft:carrot and minecraft:potato remain explicitly excluded.
        public static final ResourceKey<Item> CARROT_SLICE_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("carrot_slice"));

        public static final Item CARROT_SLICE = register(
                CARROT_SLICE_KEY,
                new Item.Properties().food(
                        new FoodProperties.Builder()
                                .nutrition(1)
                                .saturationModifier(0.2f)
                                .build()));

        public static final ResourceKey<Item> POTATO_SLICE_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("potato_slice"));

        public static final Item POTATO_SLICE = register(
                POTATO_SLICE_KEY,
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

        // Four 250 mB rennet bottles combine into one 1000 mB bucket.
        public static final ResourceKey<Item> RENNET_BUCKET_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(), ModRegistries.id("rennet_bucket"));
        public static final Item RENNET_BUCKET = register(
                RENNET_BUCKET_KEY,
                new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1));

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

        // Four 250 mB apple_cider_vinegar bottles combine into one 1000 mB bucket.
        public static final ResourceKey<Item> APPLE_CIDER_VINEGAR_BUCKET_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(), ModRegistries.id("apple_cider_vinegar_bucket"));
        public static final Item APPLE_CIDER_VINEGAR_BUCKET = register(
                APPLE_CIDER_VINEGAR_BUCKET_KEY,
                new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1));

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
        // Cured meats — balance v1
        // -----------------------------------------------------------------
        // Intermediate and whole products stack to 16; finished edible slices
        // stack to 64 through registerFoodItem().

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
                registerFoodItem("prosciutto_crudo_slice", 2, 0.5f);

        public static final Item SALAME_SLICE =
                registerFoodItem("salame_slice", 2, 0.6f);

        public static final Item MORTADELLA_SLICE =
                registerFoodItem("mortadella_slice", 2, 0.5f);

        public static final Item PANCETTA_SLICE =
                registerFoodItem("pancetta_slice", 2, 0.7f);

        public static final Item GUANCIALE_SLICE =
                registerFoodItem("guanciale_slice", 2, 0.8f);

        public static final Item BRESAOLA_SLICE =
                registerFoodItem("bresaola_slice", 2, 0.4f);

        public static final Item COPPA_SLICE =
                registerFoodItem("coppa_slice", 2, 0.6f);

        public static final Item SPECK_SLICE =
                registerFoodItem("speck_slice", 2, 0.6f);

        // Orchard fruits: Creative-only acquisition until harvesting is added.
        // Only reversible storage crafting is provided; no foods or tree drops.
        // New vineyard/chili crop produce; seed BlockItems live in ModBlocks.
        public static final Item GRAPES = registerFoodItem("grapes", 2, 0.2f);
        public static final Item WHITE_GRAPES = registerFoodItem("white_grapes", 2, 0.2f);
        public static final Item CHILI_PEPPER = registerFoodItem("chili_pepper", 3, 0.4f);
        public static final Item GHOST_PEPPER = registerFoodItem("ghost_pepper", 4, 0.7f);

        // Rustic byproduct: apple pips. Tree planting will be added with the apple orchard.
        public static final Item APPLE_SEEDS = registerSimpleItem("apple_seeds", 64);

        // Olive pitting: one edible flesh and one pit per whole Olive.
        // The existing olive_seed texture is a non-plantable byproduct.
        public static final Item OLIVE_SEED = registerSimpleItem("olive_seed", 64);
        public static final Item OLIVE_PITTED = registerFoodItem("olive_pitted", 1, 0.2f);
        // A bowl of pate returns the bowl when spread on the bread.
        public static final Item OLIVE_PATE = register(
                ResourceKey.create(BuiltInRegistries.ITEM.key(), ModRegistries.id("olive_pate")),
                new Item.Properties().craftRemainder(Items.BOWL).stacksTo(16));
        public static final Item BRUSCHETTA = registerFoodItem("bruschetta", 5, 0.6f);
        public static final Item OLIVE_BRUSCHETTA = registerFoodItem("olive_bruschetta", 5, 0.6f);
        public static final Item OLIVES = registerSimpleItem("olives", 64);
        public static final Item ALMONDS = registerSimpleItem("almonds", 64);
        // Orchard 1.1.0 almond preparations. Uncut almonds cannot go on pizzas.
        public static final Item CHOPPED_ALMONDS = registerSimpleItem("chopped_almonds", 64);
        public static final Item ALMOND_PESTO = register(
                ResourceKey.create(BuiltInRegistries.ITEM.key(), ModRegistries.id("almond_pesto")),
                new Item.Properties().craftRemainder(Items.BOWL).stacksTo(16));
        public static final Item BASIL_PESTO = register(
                ResourceKey.create(BuiltInRegistries.ITEM.key(), ModRegistries.id("basil_pesto")),
                new Item.Properties().craftRemainder(Items.BOWL).stacksTo(16));
        // The almond_cake ITEM ID stays exactly the same, now registered as
        // a placeable BlockItem by ModBlocks to serve 4 portions in-world.
        // Registering an Item here as well would duplicate that registry ID.
        public static final Item ALMOND_CAKE_SLICE = registerFoodItem("almond_cake_slice", 4, 0.5f);
        // Historical pasta_with_pesto art/ID = BASIL pesto, not almond.
        public static final Item PASTA_WITH_PESTO =
                registerBowlFoodItem("pasta_with_pesto", 10, 0.7f,
                        FoodValues.ConsumableValues.NOURISHMENT_MEDIUM_DURATION);
        public static final Item PASTA_WITH_ALMOND_PESTO =
                registerBowlFoodItem("pasta_with_almond_pesto", 10, 0.7f,
                        FoodValues.ConsumableValues.NOURISHMENT_MEDIUM_DURATION);
        // 2.0.0 cooking ingredients. Prepared sauces return their bowls.
        public static final Item RAGU = register(
                ResourceKey.create(BuiltInRegistries.ITEM.key(), ModRegistries.id("ragu")),
                new Item.Properties().craftRemainder(Items.BOWL).stacksTo(16));
        public static final Item CUSTARD = register(
                ResourceKey.create(BuiltInRegistries.ITEM.key(), ModRegistries.id("custard")),
                new Item.Properties().craftRemainder(Items.BOWL).stacksTo(16));
        public static final Item BREADCRUMBS = registerSimpleItem("breadcrumbs", 64);
        public static final Item SAUSAGE = registerSimpleItem("sausage", 64);
        public static final Item PASTA_SHEET = registerSimpleItem("pasta_sheet", 64);

        // 1.2.0 finished dishes. All required ingredients already exist.
        public static final Item SPICY_OLIVE_PASTA =
                registerBowlFoodItem("spicy_olive_pasta", 12, 0.75f,
                        FoodValues.ConsumableValues.NOURISHMENT_MEDIUM_DURATION);
        public static final Item PASTA_WITH_RICOTTA_AND_PANCETTA =
                registerBowlFoodItem("pasta_with_ricotta_and_pancetta", 14, 0.75f,
                        FoodValues.ConsumableValues.NOURISHMENT_LONG_DURATION);
        public static final Item RISOTTO_WITH_SCAMORZA_AND_OLIVES =
                registerBowlFoodItem("risotto_with_scamorza_and_olives", 12, 0.8f,
                        FoodValues.ConsumableValues.NOURISHMENT_MEDIUM_DURATION);
        public static final Item PASTA_WITH_ALMOND_PESTO_AND_SPECK =
                registerBowlFoodItem("pasta_with_almond_pesto_and_speck", 14, 0.75f,
                        FoodValues.ConsumableValues.NOURISHMENT_LONG_DURATION);
        public static final Item GRAPE_CHERRY_FRUIT_SALAD =
                registerBowlFoodItem("grape_cherry_fruit_salad", 6, 0.35f,
                        Consumables.DEFAULT_FOOD);
        // 2.0.0 pasta dishes (Cooking Pot only).
        public static final Item PASTA_ALL_ARRABBIATA =
                registerBowlFoodItem("pasta_all_arrabbiata", 10, 0.7f,
                        FoodValues.ConsumableValues.NOURISHMENT_MEDIUM_DURATION);
        public static final Item PASTA_ALLA_SORRENTINA =
                registerBowlFoodItem("pasta_alla_sorrentina", 12, 0.75f,
                        FoodValues.ConsumableValues.NOURISHMENT_MEDIUM_DURATION);
        public static final Item PASTA_AI_QUATTRO_FORMAGGI =
                registerBowlFoodItem("pasta_ai_quattro_formaggi", 14, 0.8f,
                        FoodValues.ConsumableValues.NOURISHMENT_LONG_DURATION);
        public static final Item PASTA_ALLA_BOSCAIOLA =
                registerBowlFoodItem("pasta_alla_boscaiola", 14, 0.75f,
                        FoodValues.ConsumableValues.NOURISHMENT_LONG_DURATION);
        public static final Item PASTA_ALLA_CRUDAIOLA =
                registerBowlFoodItem("pasta_alla_crudaiola", 10, 0.7f,
                        FoodValues.ConsumableValues.NOURISHMENT_MEDIUM_DURATION);
        public static final Item PASTA_ALLA_CENERE =
                registerBowlFoodItem("pasta_alla_cenere", 12, 0.75f,
                        FoodValues.ConsumableValues.NOURISHMENT_MEDIUM_DURATION);
        public static final Item PASTA_WITH_MORTADELLA_AND_RICOTTA =
                registerBowlFoodItem("pasta_with_mortadella_and_ricotta", 12, 0.75f,
                        FoodValues.ConsumableValues.NOURISHMENT_MEDIUM_DURATION);
        public static final Item PASTA_WITH_MUSHROOM_AND_SCAMORZA =
                registerBowlFoodItem("pasta_with_mushroom_and_scamorza", 12, 0.75f,
                        FoodValues.ConsumableValues.NOURISHMENT_MEDIUM_DURATION);
        public static final Item PASTA_WITH_RAGU =
                registerBowlFoodItem("pasta_with_ragu", 14, 0.75f,
                        FoodValues.ConsumableValues.NOURISHMENT_LONG_DURATION);
        public static final Item PASTA_ALLA_PUTTANESCA =
                registerBowlFoodItem("pasta_alla_puttanesca", 14, 0.75f,
                        FoodValues.ConsumableValues.NOURISHMENT_LONG_DURATION);
        public static final Item PASTA_WITH_SALMON =
                registerBowlFoodItem("pasta_with_salmon", 12, 0.75f,
                        FoodValues.ConsumableValues.NOURISHMENT_MEDIUM_DURATION);
        public static final Item PASTA_ALLA_ZOZZONA =
                registerBowlFoodItem("pasta_alla_zozzona", 16, 0.8f,
                        FoodValues.ConsumableValues.NOURISHMENT_LONG_DURATION);

        // 2.0.0 risotto dishes (Cooking Pot only).
        public static final Item RISOTTO_WITH_PUMPKIN =
                registerBowlFoodItem("risotto_with_pumpkin", 12, 0.75f,
                        FoodValues.ConsumableValues.NOURISHMENT_MEDIUM_DURATION);
        public static final Item RISOTTO_AI_QUATTRO_FORMAGGI =
                registerBowlFoodItem("risotto_ai_quattro_formaggi", 14, 0.8f,
                        FoodValues.ConsumableValues.NOURISHMENT_LONG_DURATION);
        public static final Item RISOTTO_WITH_ALMONDS =
                registerBowlFoodItem("risotto_with_almonds", 12, 0.75f,
                        FoodValues.ConsumableValues.NOURISHMENT_MEDIUM_DURATION);
        public static final Item RISOTTO_WITH_PANCETTA =
                registerBowlFoodItem("risotto_with_pancetta", 12, 0.8f,
                        FoodValues.ConsumableValues.NOURISHMENT_MEDIUM_DURATION);
        public static final Item RISOTTO_WITH_RAGU =
                registerBowlFoodItem("risotto_with_ragu", 14, 0.75f,
                        FoodValues.ConsumableValues.NOURISHMENT_LONG_DURATION);
        public static final Item RISOTTO_WITH_SALMON =
                registerBowlFoodItem("risotto_with_salmon", 12, 0.75f,
                        FoodValues.ConsumableValues.NOURISHMENT_MEDIUM_DURATION);
        public static final Item RISOTTO_WITH_SAUSAGE =
                registerBowlFoodItem("risotto_with_sausage", 12, 0.8f,
                        FoodValues.ConsumableValues.NOURISHMENT_MEDIUM_DURATION);
        public static final Item RISOTTO_WITH_MUSHROOMS_AND_SAUSAGE =
                registerBowlFoodItem("risotto_with_mushrooms_and_sausage", 14, 0.75f,
                        FoodValues.ConsumableValues.NOURISHMENT_LONG_DURATION);
        public static final Item RISOTTO_WITH_CHILI_PEPPER =
                registerBowlFoodItem("risotto_with_chili_pepper", 10, 0.7f,
                        FoodValues.ConsumableValues.NOURISHMENT_MEDIUM_DURATION);

        // 2.0.0 second courses and feast servings.
        public static final Item POLLO_ALLA_CACCIATORA_SERVING =
                registerBowlFoodItem("pollo_alla_cacciatora_serving", 12, 0.75f,
                        FoodValues.ConsumableValues.NOURISHMENT_MEDIUM_DURATION);
        public static final Item POLLO_ALLA_DIAVOLA_SERVING =
                registerBowlFoodItem("pollo_alla_diavola_serving", 12, 0.75f,
                        FoodValues.ConsumableValues.NOURISHMENT_MEDIUM_DURATION);
        public static final Item FRITTATA_DI_PATATE_SLICE =
                registerSecondoFoodItem("frittata_di_patate_slice", 5, 0.6f,
                        FoodValues.ConsumableValues.NOURISHMENT_SHORT_DURATION);
        public static final Item FRITTATA_AL_FORMAGGIO_SLICE =
                registerSecondoFoodItem("frittata_al_formaggio_slice", 5, 0.65f,
                        FoodValues.ConsumableValues.NOURISHMENT_SHORT_DURATION);
        public static final Item FRITTATA_CON_PANCETTA_SLICE =
                registerSecondoFoodItem("frittata_con_pancetta_slice", 6, 0.65f,
                        FoodValues.ConsumableValues.NOURISHMENT_SHORT_DURATION);
        public static final Item SPEZZATINO_CON_PATATE =
                registerBowlFoodItem("spezzatino_con_patate", 14, 0.8f,
                        FoodValues.ConsumableValues.NOURISHMENT_LONG_DURATION);
        public static final Item SALSICCIA_E_PATATE =
                registerBowlFoodItem("salsiccia_e_patate", 12, 0.75f,
                        FoodValues.ConsumableValues.NOURISHMENT_MEDIUM_DURATION);
        public static final Item CARNE_ALLA_PIZZAIOLA =
                registerBowlFoodItem("carne_alla_pizzaiola", 12, 0.75f,
                        FoodValues.ConsumableValues.NOURISHMENT_MEDIUM_DURATION);
        public static final Item MERLUZZO_GRATINATO =
                registerBowlFoodItem("merluzzo_gratinato", 12, 0.75f,
                        FoodValues.ConsumableValues.NOURISHMENT_MEDIUM_DURATION);
        public static final Item SCAMORZA_ALLA_PIZZAIOLA =
                registerBowlFoodItem("scamorza_alla_pizzaiola", 10, 0.7f,
                        FoodValues.ConsumableValues.NOURISHMENT_MEDIUM_DURATION);
        public static final Item POLPETTE_AL_SUGO =
                registerSecondoFoodItem("polpette_al_sugo", 8, 0.65f,
                        FoodValues.ConsumableValues.NOURISHMENT_SHORT_DURATION);
        public static final Item INVOLTINI_DI_CARNE =
                registerSecondoFoodItem("involtini_di_carne", 8, 0.7f,
                        FoodValues.ConsumableValues.NOURISHMENT_SHORT_DURATION);
        public static final Item POLPETTE_DI_RICOTTA =
                registerSecondoFoodItem("polpette_di_ricotta", 6, 0.6f,
                        FoodValues.ConsumableValues.NOURISHMENT_SHORT_DURATION);
        public static final Item COTOLETTA_ALLA_MILANESE =
                registerSecondoFoodItem("cotoletta_alla_milanese", 8, 0.7f,
                        FoodValues.ConsumableValues.NOURISHMENT_SHORT_DURATION);
        public static final Item COTOLETTA_DI_POLLO =
                registerSecondoFoodItem("cotoletta_di_pollo", 8, 0.7f,
                        FoodValues.ConsumableValues.NOURISHMENT_SHORT_DURATION);
        public static final Item POLPETTE_FRITTE =
                registerSecondoFoodItem("polpette_fritte", 8, 0.65f,
                        FoodValues.ConsumableValues.NOURISHMENT_SHORT_DURATION);
        public static final Item MOZZARELLA_IN_CARROZZA =
                registerSecondoFoodItem("mozzarella_in_carrozza", 7, 0.65f,
                        FoodValues.ConsumableValues.NOURISHMENT_SHORT_DURATION);

        // 2.0.0 salads, bruschette and antipasti board servings.
        public static final Item POTATO_SALAD =
                registerBowlFoodItem("potato_salad", 8, 0.65f,
                        FoodValues.ConsumableValues.NOURISHMENT_SHORT_DURATION);
        public static final Item RICE_SALAD =
                registerBowlFoodItem("rice_salad", 10, 0.7f,
                        FoodValues.ConsumableValues.NOURISHMENT_MEDIUM_DURATION);
        public static final Item BURRATA_SALAD =
                registerBowlFoodItem("burrata_salad", 10, 0.7f,
                        FoodValues.ConsumableValues.NOURISHMENT_MEDIUM_DURATION);
        public static final Item BRESAOLA_AND_PARMIGIANO =
                registerBowlFoodItem("bresaola_and_parmigiano", 10, 0.7f,
                        FoodValues.ConsumableValues.NOURISHMENT_MEDIUM_DURATION);
        public static final Item MUSHROOM_BRUSCHETTA =
                registerSecondoFoodItem("mushroom_bruschetta", 6, 0.6f,
                        FoodValues.ConsumableValues.NOURISHMENT_SHORT_DURATION);
        public static final Item PROSCIUTTO_BRUSCHETTA =
                registerSecondoFoodItem("prosciutto_bruschetta", 7, 0.65f,
                        FoodValues.ConsumableValues.NOURISHMENT_SHORT_DURATION);
        public static final Item COLD_CUTS_BOARD_SERVING =
                registerBowlFoodItem("cold_cuts_board_serving", 8, 0.7f,
                        FoodValues.ConsumableValues.NOURISHMENT_SHORT_DURATION);
        public static final Item CHEESE_BOARD_SERVING =
                registerBowlFoodItem("cheese_board_serving", 8, 0.7f,
                        FoodValues.ConsumableValues.NOURISHMENT_SHORT_DURATION);
        public static final Item MIXED_BOARD_SERVING =
                registerBowlFoodItem("mixed_board_serving", 8, 0.7f,
                        FoodValues.ConsumableValues.NOURISHMENT_SHORT_DURATION);

        public static final Item ALMOND_BISCUITS = registerFoodItem("almond_biscuits", 3, 0.3f);

        public static final Item CHERRIES = registerFoodItem("cherries", 2, 0.2f);

        // The Cooking Pot serves this jam into a glass bottle; crafting with
        // it leaves an empty bottle.
        public static final Item CHERRY_JAM_BOTTLE = register(
                ResourceKey.create(BuiltInRegistries.ITEM.key(), ModRegistries.id("cherry_jam_bottle")),
                new Item.Properties().craftRemainder(Items.GLASS_BOTTLE).stacksTo(16));
        public static final Item CHERRY_JAM_TOAST = registerFoodItem("cherry_jam_toast", 5, 0.6f);
        // Cherry Tart itself is registered as a placeable BlockItem by ModBlocks.
        public static final Item CHERRY_TART_SLICE = registerFoodItem("cherry_tart_slice", 4, 0.5f);

        public static final ResourceKey<Item> WINE_BUCKET_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(), ModRegistries.id("wine_bucket"));
        public static final Item WINE_BUCKET = register(WINE_BUCKET_KEY,
                WineBottleItem::new, new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET)
                        .component(ModDataComponents.WINE_QUALITY, 0));

        public static final ResourceKey<Item> WINE_BOTTLE_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(), ModRegistries.id("wine_bottle"));
        public static final Item WINE_BOTTLE = register(WINE_BOTTLE_KEY,
                WineBottleItem::new, new Item.Properties().stacksTo(1)
                        .craftRemainder(Items.GLASS_BOTTLE)
                        .component(DataComponents.CONSUMABLE, Consumables.DEFAULT_DRINK)
                        .usingConvertsTo(Items.GLASS_BOTTLE)
                        .component(ModDataComponents.WINE_QUALITY, 0));

        // Grape juice: a real Fabric storage fluid, intentionally not world-placeable.
        // The buckets and bottles participate in the existing Barrel/Cauldron API.
        public static final ResourceKey<Item> GRAPE_JUICE_BUCKET_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(), ModRegistries.id("grape_juice_bucket"));
        public static final Item GRAPE_JUICE_BUCKET = register(
                GRAPE_JUICE_BUCKET_KEY,
                new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1));
        public static final ResourceKey<Item> GRAPE_JUICE_BOTTLE_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(), ModRegistries.id("grape_juice_bottle"));
        public static final Item GRAPE_JUICE_BOTTLE = register(
                GRAPE_JUICE_BOTTLE_KEY,
                ConsumableItem::new,
                drinkBottleProperties(Consumables.DEFAULT_DRINK));

        // White grape juice uses a distinct, non-placeable fluid ID while
        // its bottle remains interchangeable with the fluid transfer API.
        public static final ResourceKey<Item> WHITE_GRAPE_JUICE_BUCKET_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(), ModRegistries.id("white_grape_juice_bucket"));
        public static final Item WHITE_GRAPE_JUICE_BUCKET = register(
                WHITE_GRAPE_JUICE_BUCKET_KEY,
                new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1));
        public static final ResourceKey<Item> WHITE_GRAPE_JUICE_BOTTLE_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(), ModRegistries.id("white_grape_juice_bottle"));
        public static final Item WHITE_GRAPE_JUICE_BOTTLE = register(
                WHITE_GRAPE_JUICE_BOTTLE_KEY,
                ConsumableItem::new,
                drinkBottleProperties(Consumables.DEFAULT_DRINK));

        // Apple juice: a placeable fluid bucket and a drinkable bottle.
        public static final ResourceKey<Item> APPLE_JUICE_BUCKET_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(), ModRegistries.id("apple_juice_bucket"));
        public static final Item APPLE_JUICE_BUCKET = register(
                APPLE_JUICE_BUCKET_KEY,
                properties -> new BucketItem(ModFluids.APPLE_JUICE_STILL, properties),
                new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1));

        public static final ResourceKey<Item> APPLE_JUICE_BOTTLE_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(), ModRegistries.id("apple_juice_bottle"));
        public static final Item APPLE_JUICE_BOTTLE = register(
                APPLE_JUICE_BOTTLE_KEY,
                ConsumableItem::new,
                drinkBottleProperties(Consumables.DEFAULT_DRINK));

        // Golden apple juice is non-placeable; drinking grants the NORMAL
        // golden apple effects, not the enchanted golden apple effects.
        public static final ResourceKey<Item> GOLDEN_APPLE_JUICE_BOTTLE_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(), ModRegistries.id("golden_apple_juice_bottle"));
        public static final Item GOLDEN_APPLE_JUICE_BOTTLE = register(
                GOLDEN_APPLE_JUICE_BOTTLE_KEY,
                ConsumableItem::new,
                drinkBottleProperties(Consumables.defaultDrink()
                        .onConsume(new ApplyStatusEffectsConsumeEffect(
                                new MobEffectInstance(MobEffects.REGENERATION, 100, 1)))
                        .onConsume(new ApplyStatusEffectsConsumeEffect(
                                new MobEffectInstance(MobEffects.ABSORPTION, 2400, 0)))
                        .build()));

        // These are NON-PLACEABLE container items: no BucketItem.placeLiquid.
        // They still exchange 1000 mB in the Crushing Tub, Barrel and Cauldron.
        public static final ResourceKey<Item> GOLDEN_APPLE_JUICE_BUCKET_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(), ModRegistries.id("golden_apple_juice_bucket"));
        public static final Item GOLDEN_APPLE_JUICE_BUCKET = register(
                GOLDEN_APPLE_JUICE_BUCKET_KEY,
                new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1));

        // Existing virtual honey uses minecraft:honey, NOT a new registered
        // world fluid. Its bucket is handled by the legacy liquid containers.
        public static final ResourceKey<Item> HONEY_BUCKET_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(), ModRegistries.id("honey_bucket"));
        public static final Item HONEY_BUCKET = register(
                HONEY_BUCKET_KEY,
                new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1));

        public static final ResourceKey<Item> OLIVE_OIL_BUCKET_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(), ModRegistries.id("olive_oil_bucket"));
        public static final Item OLIVE_OIL_BUCKET = register(
                OLIVE_OIL_BUCKET_KEY,
                properties -> new BucketItem(ModFluids.OLIVE_OIL_STILL, properties),
                new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1));
        public static final ResourceKey<Item> OLIVE_OIL_BOTTLE_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(), ModRegistries.id("olive_oil_bottle"));
        public static final Item OLIVE_OIL_BOTTLE = register(
                OLIVE_OIL_BOTTLE_KEY,
                ConsumableItem::new,
                drinkBottleProperties(Consumables.DEFAULT_DRINK));

        // Salt
        public static final ResourceKey<Item> SALT_KEY = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ModRegistries.id("salt"));

        public static final Item SALT = register(
                SALT_KEY,
                new Item.Properties()
                        .stacksTo(64));


        // Rustic-style wine, cider, mead and cherry juice families.
        public static final Item CHERRY_JUICE_BOTTLE = register(ResourceKey.create(BuiltInRegistries.ITEM.key(), ModRegistries.id("cherry_juice_bottle")), ConsumableItem::new, drinkBottleProperties(Consumables.DEFAULT_DRINK));
        public static final Item CHERRY_JUICE_BUCKET = register(ResourceKey.create(BuiltInRegistries.ITEM.key(), ModRegistries.id("cherry_juice_bucket")), new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET));
        public static final Item WHITE_WINE_BOTTLE = register(ResourceKey.create(BuiltInRegistries.ITEM.key(), ModRegistries.id("white_wine_bottle")), WineBottleItem::new, new Item.Properties().stacksTo(1).craftRemainder(Items.GLASS_BOTTLE).component(DataComponents.CONSUMABLE, Consumables.DEFAULT_DRINK).usingConvertsTo(Items.GLASS_BOTTLE).component(ModDataComponents.WINE_QUALITY, 0));
        public static final Item WHITE_WINE_BUCKET = register(ResourceKey.create(BuiltInRegistries.ITEM.key(), ModRegistries.id("white_wine_bucket")), WineBottleItem::new, new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET).component(ModDataComponents.WINE_QUALITY, 0));
        public static final Item CHERRY_WINE_BOTTLE = register(ResourceKey.create(BuiltInRegistries.ITEM.key(), ModRegistries.id("cherry_wine_bottle")), WineBottleItem::new, new Item.Properties().stacksTo(1).craftRemainder(Items.GLASS_BOTTLE).component(DataComponents.CONSUMABLE, Consumables.DEFAULT_DRINK).usingConvertsTo(Items.GLASS_BOTTLE).component(ModDataComponents.WINE_QUALITY, 0));
        public static final Item CHERRY_WINE_BUCKET = register(ResourceKey.create(BuiltInRegistries.ITEM.key(), ModRegistries.id("cherry_wine_bucket")), WineBottleItem::new, new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET).component(ModDataComponents.WINE_QUALITY, 0));
        public static final Item MEAD_BOTTLE = register(ResourceKey.create(BuiltInRegistries.ITEM.key(), ModRegistries.id("mead_bottle")), WineBottleItem::new, new Item.Properties().stacksTo(1).craftRemainder(Items.GLASS_BOTTLE).component(DataComponents.CONSUMABLE, Consumables.DEFAULT_DRINK).usingConvertsTo(Items.GLASS_BOTTLE).component(ModDataComponents.WINE_QUALITY, 0));
        public static final Item MEAD_BUCKET = register(ResourceKey.create(BuiltInRegistries.ITEM.key(), ModRegistries.id("mead_bucket")), WineBottleItem::new, new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET).component(ModDataComponents.WINE_QUALITY, 0));
        public static final Item CIDER_BOTTLE = register(ResourceKey.create(BuiltInRegistries.ITEM.key(), ModRegistries.id("cider_bottle")), WineBottleItem::new, new Item.Properties().stacksTo(1).craftRemainder(Items.GLASS_BOTTLE).component(DataComponents.CONSUMABLE, Consumables.DEFAULT_DRINK).usingConvertsTo(Items.GLASS_BOTTLE).component(ModDataComponents.WINE_QUALITY, 0));
        public static final Item CIDER_BUCKET = register(ResourceKey.create(BuiltInRegistries.ITEM.key(), ModRegistries.id("cider_bucket")), WineBottleItem::new, new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET).component(ModDataComponents.WINE_QUALITY, 0));
        public static final Item RED_WINE_VINEGAR_BOTTLE = register(ResourceKey.create(BuiltInRegistries.ITEM.key(), ModRegistries.id("red_wine_vinegar_bottle")), new Item.Properties().stacksTo(16).craftRemainder(Items.GLASS_BOTTLE));
        public static final Item RED_WINE_VINEGAR_BUCKET = register(ResourceKey.create(BuiltInRegistries.ITEM.key(), ModRegistries.id("red_wine_vinegar_bucket")), new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET));

        public static void register() {
        }
}
