package dev.italiansdelight.common.item.group;

import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import dev.italiansdelight.common.registry.ModItems;
import dev.italiansdelight.common.registry.ModBlocks;
import dev.italiansdelight.common.registry.ModSigns;
import dev.italiansdelight.common.registry.ModBoats;
import dev.italiansdelight.common.registry.ModRegistries;
import dev.italiansdelight.common.pizza.PizzaDisplayNames;
import dev.italiansdelight.common.pizza.PizzaCreativeSamples;

/**
 * Dedicated Italian's Delight creative tab, separate from Farmer's Delight.
 * Groups are contiguous and deliberately ordered for easy browsing; there
 * are no fake visual dividers, new tabs, or duplicated items.
 */
public final class ModItemGroups {

    // The creative group is only a view of items that are already registered:
    // adding an item here does not register it automatically.

    private ModItemGroups() {}

    public static final ResourceKey<CreativeModeTab> ITALIANS_DELIGHT_GROUP_KEY =
            ResourceKey.create(
                    BuiltInRegistries.CREATIVE_MODE_TAB.key(),
                    ModRegistries.id("italiansdelight_group")
            );

    public static final CreativeModeTab ITALIANS_DELIGHT_GROUP = FabricCreativeModeTab.builder()
            // The existing cooked_pizza model already uses the approved
            // italiansdelight:item/pizza_margherita 16x16 sprite.
            .icon(() -> new ItemStack(ModItems.COOKED_PIZZA))
            .title(Component.translatable("itemGroup.italiansdelight.main"))
            .displayItems((params, output) -> {
                // A single complete tab, ordered by gameplay progression.
                // No internal-only blocks or new item registry entries.
                // Distinct data-bearing pizza stacks intentionally reuse
                // raw_pizza/cooked_pizza, not fake individual item IDs.
                // WORKSTATIONS AND STORAGE: pizza oven first, then processing,
                // aging racks and crates; excludes non-obtainable technical blocks.
                output.accept(ModBlocks.PIZZA_OVEN);
                output.accept(ModBlocks.CHEESE_VAT);
                // The original workstation IDs are Oak variants.
                output.accept(ModBlocks.CRUSHING_TUB);
                output.accept(ModBlocks.SPRUCE_CRUSHING_TUB);
                output.accept(ModBlocks.BIRCH_CRUSHING_TUB);
                output.accept(ModBlocks.JUNGLE_CRUSHING_TUB);
                output.accept(ModBlocks.ACACIA_CRUSHING_TUB);
                output.accept(ModBlocks.DARK_OAK_CRUSHING_TUB);
                output.accept(ModBlocks.MANGROVE_CRUSHING_TUB);
                output.accept(ModBlocks.CHERRY_CRUSHING_TUB);
                output.accept(ModBlocks.PALE_OAK_CRUSHING_TUB);
                output.accept(ModBlocks.POPLAR_CRUSHING_TUB);
                output.accept(ModBlocks.BAMBOO_CRUSHING_TUB);
                output.accept(ModBlocks.CRIMSON_CRUSHING_TUB);
                output.accept(ModBlocks.WARPED_CRUSHING_TUB);
                output.accept(ModBlocks.OLIVE_CRUSHING_TUB);
                output.accept(ModBlocks.ALMOND_CRUSHING_TUB);
                output.accept(ModBlocks.FRUIT_CHERRY_CRUSHING_TUB);
                output.accept(ModBlocks.BARREL);
                output.accept(ModBlocks.SPRUCE_BARREL);
                output.accept(ModBlocks.BIRCH_BARREL);
                output.accept(ModBlocks.JUNGLE_BARREL);
                output.accept(ModBlocks.ACACIA_BARREL);
                output.accept(ModBlocks.DARK_OAK_BARREL);
                output.accept(ModBlocks.MANGROVE_BARREL);
                output.accept(ModBlocks.CHERRY_BARREL);
                output.accept(ModBlocks.PALE_OAK_BARREL);
                output.accept(ModBlocks.POPLAR_BARREL);
                output.accept(ModBlocks.BAMBOO_BARREL);
                output.accept(ModBlocks.CRIMSON_BARREL);
                output.accept(ModBlocks.WARPED_BARREL);
                output.accept(ModBlocks.OLIVE_BARREL);
                output.accept(ModBlocks.ALMOND_BARREL);
                output.accept(ModBlocks.FRUIT_CHERRY_BARREL);
                // All fermentation barrel woods stay together: legacy ID is oak.
                output.accept(ModBlocks.BREWING_BARREL);
                output.accept(ModBlocks.SPRUCE_BREWING_BARREL);
                output.accept(ModBlocks.BIRCH_BREWING_BARREL);
                output.accept(ModBlocks.JUNGLE_BREWING_BARREL);
                output.accept(ModBlocks.ACACIA_BREWING_BARREL);
                output.accept(ModBlocks.DARK_OAK_BREWING_BARREL);
                output.accept(ModBlocks.MANGROVE_BREWING_BARREL);
                output.accept(ModBlocks.CHERRY_BREWING_BARREL);
                output.accept(ModBlocks.PALE_OAK_BREWING_BARREL);
                output.accept(ModBlocks.POPLAR_BREWING_BARREL);
                output.accept(ModBlocks.BAMBOO_BREWING_BARREL);
                output.accept(ModBlocks.CRIMSON_BREWING_BARREL);
                output.accept(ModBlocks.WARPED_BREWING_BARREL);
                output.accept(ModBlocks.OLIVE_BREWING_BARREL);
                output.accept(ModBlocks.ALMOND_BREWING_BARREL);
                output.accept(ModBlocks.FRUIT_CHERRY_BREWING_BARREL);
                output.accept(ModBlocks.CHEESE_HOOK);

                // Aging racks — all wood types stay together.
                output.accept(ModBlocks.OAK_CHEESE_AGING_RACK);
                output.accept(ModBlocks.SPRUCE_CHEESE_AGING_RACK);
                output.accept(ModBlocks.BIRCH_CHEESE_AGING_RACK);
                output.accept(ModBlocks.JUNGLE_CHEESE_AGING_RACK);
                output.accept(ModBlocks.ACACIA_CHEESE_AGING_RACK);
                output.accept(ModBlocks.DARK_OAK_CHEESE_AGING_RACK);
                output.accept(ModBlocks.MANGROVE_CHEESE_AGING_RACK);
                output.accept(ModBlocks.CHERRY_CHEESE_AGING_RACK);
                output.accept(ModBlocks.PALE_OAK_CHEESE_AGING_RACK);
                output.accept(ModBlocks.POPLAR_CHEESE_AGING_RACK);
                output.accept(ModBlocks.BAMBOO_CHEESE_AGING_RACK);
                output.accept(ModBlocks.CRIMSON_CHEESE_AGING_RACK);
                output.accept(ModBlocks.WARPED_CHEESE_AGING_RACK);
                output.accept(ModBlocks.OLIVE_CHEESE_AGING_RACK);
                output.accept(ModBlocks.ALMOND_CHEESE_AGING_RACK);
                output.accept(ModBlocks.FRUIT_CHERRY_CHEESE_AGING_RACK);

                // DECORATIVE POLES — all 16 wood variants, after aging racks.
                output.accept(ModBlocks.CROP_STAKE);
                output.accept(ModBlocks.SPRUCE_CROP_STAKE);
                output.accept(ModBlocks.BIRCH_CROP_STAKE);
                output.accept(ModBlocks.JUNGLE_CROP_STAKE);
                output.accept(ModBlocks.ACACIA_CROP_STAKE);
                output.accept(ModBlocks.DARK_OAK_CROP_STAKE);
                output.accept(ModBlocks.MANGROVE_CROP_STAKE);
                output.accept(ModBlocks.CHERRY_CROP_STAKE);
                output.accept(ModBlocks.PALE_OAK_CROP_STAKE);
                output.accept(ModBlocks.POPLAR_CROP_STAKE);
                output.accept(ModBlocks.BAMBOO_CROP_STAKE);
                output.accept(ModBlocks.CRIMSON_CROP_STAKE);
                output.accept(ModBlocks.WARPED_CROP_STAKE);
                output.accept(ModBlocks.OLIVE_CROP_STAKE);
                output.accept(ModBlocks.ALMOND_CROP_STAKE);
                output.accept(ModBlocks.FRUIT_CHERRY_CROP_STAKE);

                // CRATES AND CROP STORAGE.
                output.accept(ModBlocks.OLIVE_CRATE);
                output.accept(ModBlocks.ALMOND_CRATE);
                output.accept(ModBlocks.CHERRY_CRATE);
                output.accept(ModBlocks.BASIL_CRATE);
                output.accept(ModBlocks.CARDOON_CRATE);
                output.accept(ModBlocks.GRAPE_CRATE);
                output.accept(ModBlocks.WHITE_GRAPE_CRATE);
                output.accept(ModBlocks.CHILI_PEPPER_CRATE);
                output.accept(ModBlocks.GHOST_PEPPER_CRATE);

                // OLIVE WOOD: identical derivative order; leaves and sapling last.
                output.accept(ModBlocks.OLIVE_LOG);
                output.accept(ModBlocks.OLIVE_WOOD);
                output.accept(ModBlocks.STRIPPED_OLIVE_LOG);
                output.accept(ModBlocks.STRIPPED_OLIVE_WOOD);
                output.accept(ModBlocks.OLIVE_PLANKS);
                output.accept(ModBlocks.OLIVE_STAIRS);
                output.accept(ModBlocks.OLIVE_SLAB);
                output.accept(ModBlocks.OLIVE_FENCE);
                output.accept(ModBlocks.OLIVE_FENCE_GATE);
                output.accept(ModBlocks.OLIVE_DOOR);
                output.accept(ModBlocks.OLIVE_TRAPDOOR);
                output.accept(ModBlocks.OLIVE_PRESSURE_PLATE);
                output.accept(ModBlocks.OLIVE_BUTTON);
                output.accept(ModSigns.OLIVE_SIGN);
                output.accept(ModSigns.OLIVE_HANGING_SIGN);
                output.accept(ModBlocks.OLIVE_CABINET);
                output.accept(ModBlocks.OLIVE_SHELF);
                output.accept(ModBoats.OLIVE_BOAT_ITEM);
                output.accept(ModBoats.OLIVE_CHEST_BOAT_ITEM);
                output.accept(ModBlocks.OLIVE_LEAVES);
                output.accept(ModBlocks.OLIVE_SAPLING);

                // ALMOND WOOD: identical derivative order; leaves and sapling last.
                output.accept(ModBlocks.ALMOND_LOG);
                output.accept(ModBlocks.ALMOND_WOOD);
                output.accept(ModBlocks.STRIPPED_ALMOND_LOG);
                output.accept(ModBlocks.STRIPPED_ALMOND_WOOD);
                output.accept(ModBlocks.ALMOND_PLANKS);
                output.accept(ModBlocks.ALMOND_STAIRS);
                output.accept(ModBlocks.ALMOND_SLAB);
                output.accept(ModBlocks.ALMOND_FENCE);
                output.accept(ModBlocks.ALMOND_FENCE_GATE);
                output.accept(ModBlocks.ALMOND_DOOR);
                output.accept(ModBlocks.ALMOND_TRAPDOOR);
                output.accept(ModBlocks.ALMOND_PRESSURE_PLATE);
                output.accept(ModBlocks.ALMOND_BUTTON);
                output.accept(ModSigns.ALMOND_SIGN);
                output.accept(ModSigns.ALMOND_HANGING_SIGN);
                output.accept(ModBlocks.ALMOND_CABINET);
                output.accept(ModBlocks.ALMOND_SHELF);
                output.accept(ModBoats.ALMOND_BOAT_ITEM);
                output.accept(ModBoats.ALMOND_CHEST_BOAT_ITEM);
                output.accept(ModBlocks.ALMOND_LEAVES);
                output.accept(ModBlocks.ALMOND_SAPLING);

                // FRUIT CHERRY WOOD: identical derivative order; leaves and sapling last.
                output.accept(ModBlocks.FRUIT_CHERRY_LOG);
                output.accept(ModBlocks.FRUIT_CHERRY_WOOD);
                output.accept(ModBlocks.STRIPPED_FRUIT_CHERRY_LOG);
                output.accept(ModBlocks.STRIPPED_FRUIT_CHERRY_WOOD);
                output.accept(ModBlocks.FRUIT_CHERRY_PLANKS);
                output.accept(ModBlocks.FRUIT_CHERRY_STAIRS);
                output.accept(ModBlocks.FRUIT_CHERRY_SLAB);
                output.accept(ModBlocks.FRUIT_CHERRY_FENCE);
                output.accept(ModBlocks.FRUIT_CHERRY_FENCE_GATE);
                output.accept(ModBlocks.FRUIT_CHERRY_DOOR);
                output.accept(ModBlocks.FRUIT_CHERRY_TRAPDOOR);
                output.accept(ModBlocks.FRUIT_CHERRY_PRESSURE_PLATE);
                output.accept(ModBlocks.FRUIT_CHERRY_BUTTON);
                output.accept(ModSigns.FRUIT_CHERRY_SIGN);
                output.accept(ModSigns.FRUIT_CHERRY_HANGING_SIGN);
                output.accept(ModBlocks.FRUIT_CHERRY_CABINET);
                output.accept(ModBlocks.FRUIT_CHERRY_SHELF);
                output.accept(ModBoats.FRUIT_CHERRY_BOAT_ITEM);
                output.accept(ModBoats.FRUIT_CHERRY_CHEST_BOAT_ITEM);
                output.accept(ModBlocks.FRUIT_CHERRY_LEAVES);
                output.accept(ModBlocks.FRUIT_CHERRY_SAPLING);

                // TOOLS: separate them from placeable machinery.
                output.accept(ModItems.CHEESE_GRATER);
                output.accept(ModItems.ROLLING_PIN);
                output.accept(ModItems.PIZZA_PEEL);

                // CROPS AND WILD PLANTS — keep related forms and harvest together.
                output.accept(ModBlocks.CARDOON);
                output.accept(ModBlocks.CULTIVATED_CARDOON);
                output.accept(ModBlocks.BASIL);
                output.accept(ModBlocks.WILD_BASIL);

                // GRAPE FAMILY: seeds, harvest, juice and wine (buckets first).
                output.accept(ModBlocks.GRAPE_SEEDS);
                output.accept(ModBlocks.WILD_GRAPE_VINE);
                output.accept(ModItems.GRAPES);
                output.accept(ModBlocks.WHITE_GRAPE_SEEDS);
                output.accept(ModBlocks.WILD_WHITE_GRAPE_VINE);
                output.accept(ModItems.WHITE_GRAPES);

                // CHILI FAMILY: seed, wild shrub, common and rare fruit.
                output.accept(ModBlocks.CHILI_PEPPER_SEEDS);
                output.accept(ModBlocks.WILD_CHILI);
                output.accept(ModItems.CHILI_PEPPER);
                output.accept(ModItems.GHOST_PEPPER);

                // ORCHARD FRUITS AND RAW INGREDIENTS.
                output.accept(ModItems.OLIVES);
                output.accept(ModItems.OLIVE_SEED);
                output.accept(ModItems.OLIVE_PITTED);
                output.accept(ModItems.ALMONDS);
                output.accept(ModItems.CHOPPED_ALMONDS);
                output.accept(ModItems.CHERRIES);
                output.accept(ModItems.APPLE_SEEDS);




                // SAVOURY SAUCES AND SPREADS.
                output.accept(ModItems.OLIVE_PATE);
                output.accept(ModItems.BASIL_PESTO);
                output.accept(ModItems.ALMOND_PESTO);
                output.accept(ModItems.RAGU);

                // SWEET SPREADS AND CREAMS.
                output.accept(ModItems.CHERRY_JAM_BOTTLE);
                output.accept(ModItems.CUSTARD);

                // COOKING INGREDIENTS AND PREPARED VEGETABLES.
                output.accept(ModItems.BREADCRUMBS);
                output.accept(ModItems.SAUSAGE);
                output.accept(ModItems.PASTA_SHEET);
                output.accept(ModItems.SALT);
                output.accept(ModItems.TOMATO_SLICE);
                output.accept(ModItems.CARROT_SLICE);
                output.accept(ModItems.POTATO_SLICE);
                output.accept(ModItems.COOKED_POTATO_SLICE);

                // LIQUIDS (Creative + JEI item order): buckets before bottles.
                // 1. Animal-derived liquids, including whey and rennet.
                output.accept(ModItems.SHEEP_MILK_BUCKET);
                output.accept(ModItems.SHEEP_MILK_BOTTLE);
                output.accept(ModItems.WHEY_BUCKET);
                output.accept(ModItems.WHEY_BOTTLE);
                output.accept(ModItems.RENNET_BUCKET);
                output.accept(ModItems.RENNET);
                output.accept(ModItems.HONEY_BUCKET); // Vanilla honey bottle belongs to Minecraft.

                // 2. Crushing Tub / non-fermented plant fluids.
                output.accept(ModItems.OLIVE_OIL_BUCKET);
                output.accept(ModItems.OLIVE_OIL_BOTTLE);
                output.accept(ModItems.APPLE_JUICE_BUCKET);
                output.accept(ModItems.APPLE_JUICE_BOTTLE);
                output.accept(ModItems.GOLDEN_APPLE_JUICE_BUCKET);
                output.accept(ModItems.GOLDEN_APPLE_JUICE_BOTTLE);
                output.accept(ModItems.GRAPE_JUICE_BUCKET);
                output.accept(ModItems.GRAPE_JUICE_BOTTLE);
                output.accept(ModItems.WHITE_GRAPE_JUICE_BUCKET);
                output.accept(ModItems.WHITE_GRAPE_JUICE_BOTTLE);
                output.accept(ModItems.CHERRY_JUICE_BUCKET);
                output.accept(ModItems.CHERRY_JUICE_BOTTLE);

                // 3. Wines and other fermented liquids, then vinegar.
                output.accept(ModItems.WINE_BUCKET);
                output.accept(ModItems.WINE_BOTTLE);
                output.accept(ModItems.WHITE_WINE_BUCKET);
                output.accept(ModItems.WHITE_WINE_BOTTLE);
                output.accept(ModItems.CHERRY_WINE_BUCKET);
                output.accept(ModItems.CHERRY_WINE_BOTTLE);
                output.accept(ModItems.MEAD_BUCKET);
                output.accept(ModItems.MEAD_BOTTLE);
                output.accept(ModItems.CIDER_BUCKET);
                output.accept(ModItems.CIDER_BOTTLE);
                output.accept(ModItems.APPLE_CIDER_VINEGAR_BUCKET);
                output.accept(ModItems.APPLE_CIDER_VINEGAR);
                output.accept(ModItems.RED_WINE_VINEGAR_BUCKET);
                output.accept(ModItems.RED_WINE_VINEGAR_BOTTLE);

                // Non-liquid ingredients used in cheese making.
                output.accept(ModItems.CURD);
                output.accept(ModItems.CREAM_BOWL);
                output.accept(ModItems.BLUE_MOLD_CULTURE);

                // CHEESES: fresh products followed by each family's stages.
                output.accept(ModItems.MOZZARELLA);
                output.accept(ModItems.MOZZARELLA_SLICE);
                output.accept(ModItems.BOCCONCINO);
                output.accept(ModItems.BURRATA);
                output.accept(ModItems.RICOTTA);
                output.accept(ModItems.MASCARPONE);

                // Parmigiano Reggiano — fresh, aged, wedge, grated.
                output.accept(ModItems.FRESH_PARMIGIANO_REGGIANO);
                output.accept(ModItems.PARMIGIANO_REGGIANO);
                output.accept(ModItems.PARMIGIANO_REGGIANO_WEDGE);
                output.accept(ModItems.GRATED_PARMIGIANO_REGGIANO);

                // Pecorino Romano — fresh, aged, wedge, grated.
                output.accept(ModItems.FRESH_PECORINO_ROMANO);
                output.accept(ModItems.PECORINO_ROMANO);
                output.accept(ModItems.PECORINO_ROMANO_WEDGE);
                output.accept(ModItems.GRATED_PECORINO_ROMANO);

                // Gorgonzola — fresh, aged, wedge.
                output.accept(ModItems.FRESH_GORGONZOLA);
                output.accept(ModItems.GORGONZOLA);
                output.accept(ModItems.GORGONZOLA_WEDGE);

                // Provolone — fresh, aged and sliced.
                output.accept(ModItems.FRESH_PROVOLONE);
                output.accept(ModItems.PROVOLONE);
                output.accept(ModItems.PROVOLONE_SLICE);

                // Scamorza — fresh, aged, smoked and sliced.
                output.accept(ModItems.FRESH_SCAMORZA);
                output.accept(ModItems.SCAMORZA);
                output.accept(ModItems.SMOKED_SCAMORZA);
                output.accept(ModItems.SCAMORZA_SLICE);
                output.accept(ModItems.SMOKED_SCAMORZA_SLICE);

                // CURED MEATS: every preparation and its finished slices stay
                // adjacent for quick Creative testing.
                output.accept(ModItems.SALTED_HAM);
                output.accept(ModItems.PROSCIUTTO_CRUDO);
                output.accept(ModItems.PROSCIUTTO_CRUDO_SLICE);
                output.accept(ModItems.RAW_SALAME);
                output.accept(ModItems.SALAME);
                output.accept(ModItems.SALAME_SLICE);
                output.accept(ModItems.RAW_MORTADELLA);
                output.accept(ModItems.MORTADELLA);
                output.accept(ModItems.MORTADELLA_SLICE);
                output.accept(ModItems.PREPARED_PANCETTA);
                output.accept(ModItems.PANCETTA);
                output.accept(ModItems.PANCETTA_SLICE);
                output.accept(ModItems.PREPARED_GUANCIALE);
                output.accept(ModItems.GUANCIALE);
                output.accept(ModItems.GUANCIALE_SLICE);
                output.accept(ModItems.PREPARED_BRESAOLA);
                output.accept(ModItems.BRESAOLA);
                output.accept(ModItems.BRESAOLA_SLICE);
                output.accept(ModItems.PREPARED_COPPA);
                output.accept(ModItems.COPPA);
                output.accept(ModItems.COPPA_SLICE);
                output.accept(ModItems.PREPARED_SPECK);
                output.accept(ModItems.SMOKED_PREPARED_SPECK);
                output.accept(ModItems.SPECK);
                output.accept(ModItems.SPECK_SLICE);

                // RAW PIZZAS: one real example for every taste and one mista.
                // Do not add bare RAW_PIZZA: its empty default payload is a
                // second visible "Pizza Bianca", with the wrong generic art.
                for (var preset : PizzaDisplayNames.allPresets().entrySet()) {
                    output.accept(PizzaCreativeSamples.create(preset.getKey(), false));
                }
                output.accept(PizzaCreativeSamples.createMixed(false));

                // COOKED PIZZAS: same taste order, then cooked Pizza mista.
                // The bare COOKED_PIZZA also appears as another Bianca;
                // keep only the correctly named, data-bearing taste examples.
                for (var preset : PizzaDisplayNames.allPresets().entrySet()) {
                    output.accept(PizzaCreativeSamples.create(preset.getKey(), true));
                }
                output.accept(PizzaCreativeSamples.createMixed(true));

                // EDIBLE SLICES: each sample carries its own quarter contents,
                // correct name, nutrition, effect, and approved 16x16 sprite.
                // Avoid the bare PIZZA_SLICE (duplicate unconfigured Bianca).
                for (var preset : PizzaDisplayNames.allPresets().entrySet()) {
                    output.accept(PizzaCreativeSamples.createSlice(preset.getKey()));
                }

                // PREPARED DISHES — pasta recipes first.
                output.accept(ModItems.PASTA_WITH_TOMATO_SAUCE);
                output.accept(ModItems.PASTA_WITH_PESTO);
                output.accept(ModItems.PASTA_WITH_ALMOND_PESTO);
                output.accept(ModItems.PASTA_WITH_ALMOND_PESTO_AND_SPECK);
                output.accept(ModItems.SPICY_OLIVE_PASTA);
                output.accept(ModItems.PASTA_WITH_RICOTTA_AND_PANCETTA);
                output.accept(ModItems.PASTA_ALLA_CARBONARA);
                output.accept(ModItems.PASTA_ALLA_GRICIA);
                output.accept(ModItems.PASTA_ALL_AMATRICIANA);
                output.accept(ModItems.PASTA_WITH_SPECK_AND_GORGONZOLA);

                output.accept(ModItems.PASTA_ALL_ARRABBIATA);
                output.accept(ModItems.PASTA_ALLA_SORRENTINA);
                output.accept(ModItems.PASTA_AI_QUATTRO_FORMAGGI);
                output.accept(ModItems.PASTA_ALLA_BOSCAIOLA);
                output.accept(ModItems.PASTA_ALLA_CRUDAIOLA);
                output.accept(ModItems.PASTA_ALLA_CENERE);
                output.accept(ModItems.PASTA_WITH_MORTADELLA_AND_RICOTTA);
                output.accept(ModItems.PASTA_WITH_MUSHROOM_AND_SCAMORZA);
                output.accept(ModItems.PASTA_WITH_RAGU);
                output.accept(ModItems.PASTA_ALLA_PUTTANESCA);
                output.accept(ModItems.PASTA_WITH_SALMON);
                output.accept(ModItems.PASTA_ALLA_ZOZZONA);

                // PREPARED DISHES — risottos and cold dishes.
                output.accept(ModItems.RISOTTO_WITH_TOMATO_SAUCE);
                output.accept(ModItems.RISOTTO_WITH_MUSHROOM);
                output.accept(ModItems.RISOTTO_WITH_SCAMORZA_AND_OLIVES);
                output.accept(ModItems.RISOTTO_WITH_PARMIGIANO);
                output.accept(ModItems.RISOTTO_WITH_SPECK_AND_GORGONZOLA);
                output.accept(ModItems.RISOTTO_WITH_PUMPKIN);
                output.accept(ModItems.RISOTTO_AI_QUATTRO_FORMAGGI);
                output.accept(ModItems.RISOTTO_WITH_ALMONDS);
                output.accept(ModItems.RISOTTO_WITH_PANCETTA);
                output.accept(ModItems.RISOTTO_WITH_RAGU);
                output.accept(ModItems.RISOTTO_WITH_SALMON);
                output.accept(ModItems.RISOTTO_WITH_SAUSAGE);
                output.accept(ModItems.RISOTTO_WITH_MUSHROOMS_AND_SAUSAGE);
                output.accept(ModItems.RISOTTO_WITH_CHILI_PEPPER);

                // SECONDI — shared feasts, slices and individual meals.
                output.accept(ModBlocks.POLLO_ALLA_CACCIATORA);
                output.accept(ModItems.POLLO_ALLA_CACCIATORA_SERVING);
                output.accept(ModBlocks.POLLO_ALLA_DIAVOLA);
                output.accept(ModItems.POLLO_ALLA_DIAVOLA_SERVING);
                output.accept(ModBlocks.FRITTATA_DI_PATATE);
                output.accept(ModItems.FRITTATA_DI_PATATE_SLICE);
                output.accept(ModBlocks.FRITTATA_AL_FORMAGGIO);
                output.accept(ModItems.FRITTATA_AL_FORMAGGIO_SLICE);
                output.accept(ModBlocks.FRITTATA_CON_PANCETTA);
                output.accept(ModItems.FRITTATA_CON_PANCETTA_SLICE);
                output.accept(ModItems.SPEZZATINO_CON_PATATE);
                output.accept(ModItems.SALSICCIA_E_PATATE);
                output.accept(ModItems.CARNE_ALLA_PIZZAIOLA);
                output.accept(ModItems.MERLUZZO_GRATINATO);
                output.accept(ModItems.SCAMORZA_ALLA_PIZZAIOLA);
                output.accept(ModItems.POLPETTE_AL_SUGO);
                output.accept(ModItems.INVOLTINI_DI_CARNE);
                output.accept(ModItems.POLPETTE_DI_RICOTTA);
                output.accept(ModItems.COTOLETTA_ALLA_MILANESE);
                output.accept(ModItems.COTOLETTA_DI_POLLO);
                output.accept(ModItems.POLPETTE_FRITTE);
                output.accept(ModItems.MOZZARELLA_IN_CARROZZA);

                // ANTIPASTI — all bruschette and shared boards together.
                output.accept(ModItems.BRUSCHETTA);
                output.accept(ModItems.OLIVE_BRUSCHETTA);
                output.accept(ModItems.MUSHROOM_BRUSCHETTA);
                output.accept(ModItems.PROSCIUTTO_BRUSCHETTA);
                output.accept(ModBlocks.COLD_CUTS_BOARD);
                output.accept(ModBlocks.CHEESE_BOARD);
                output.accept(ModBlocks.MIXED_BOARD);
                output.accept(ModItems.BRESAOLA_AND_PARMIGIANO);

                // STREET FOOD — sandwiches together.
                output.accept(ModItems.MORTADELLA_SANDWICH);
                output.accept(ModItems.PROSCIUTTO_SANDWICH);
                output.accept(ModItems.SPECK_SANDWICH);
                output.accept(ModItems.SALAME_SANDWICH);

                // SAVOURY SALADS AND COLD DISHES.
                output.accept(ModItems.MOZZARELLA_SALAD);
                output.accept(ModItems.BURRATA_SALAD);
                output.accept(ModItems.POTATO_SALAD);
                output.accept(ModItems.RICE_SALAD);

                // SWEET SNACKS, FRUIT SALAD AND DESSERTS.
                output.accept(ModItems.GRAPE_CHERRY_FRUIT_SALAD);
                output.accept(ModItems.CHERRY_JAM_TOAST);
                output.accept(ModBlocks.ALMOND_CAKE);
                output.accept(ModItems.ALMOND_CAKE_SLICE);
                output.accept(ModBlocks.CHERRY_TART);
                output.accept(ModItems.CHERRY_TART_SLICE);
                output.accept(ModItems.ALMOND_BISCUITS);
            })
            .build();

    public static void register() {
        Registry.register(
                BuiltInRegistries.CREATIVE_MODE_TAB,
                ITALIANS_DELIGHT_GROUP_KEY,
                ITALIANS_DELIGHT_GROUP
        );
    }
}
