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
import dev.italiansdelight.common.registry.ModRegistries;

/** Dedicated Italian's Delight creative tab, separate from Farmer's Delight. */
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
            .icon(() -> new ItemStack(ModItems.PASTA_WITH_TOMATO_SAUCE)) // Changes the creative tab icon
            .title(Component.translatable("itemGroup.italiansdelight.main"))
            .displayItems((params, output) -> {
                output.accept(ModBlocks.CHEESE_VAT);
                output.accept(ModBlocks.CHEESE_HOOK);
                output.accept(ModItems.CHEESE_GRATER);
                output.accept(ModBlocks.OAK_CHEESE_AGING_RACK);
                output.accept(ModBlocks.SPRUCE_CHEESE_AGING_RACK);
                output.accept(ModBlocks.BIRCH_CHEESE_AGING_RACK);
                output.accept(ModBlocks.JUNGLE_CHEESE_AGING_RACK);
                output.accept(ModBlocks.ACACIA_CHEESE_AGING_RACK);
                output.accept(ModBlocks.DARK_OAK_CHEESE_AGING_RACK);
                output.accept(ModBlocks.MANGROVE_CHEESE_AGING_RACK);
                output.accept(ModBlocks.CHERRY_CHEESE_AGING_RACK);
                output.accept(ModBlocks.PALE_OAK_CHEESE_AGING_RACK);
                output.accept(ModBlocks.BAMBOO_CHEESE_AGING_RACK);
                output.accept(ModBlocks.CRIMSON_CHEESE_AGING_RACK);
                output.accept(ModBlocks.WARPED_CHEESE_AGING_RACK);
                output.accept(ModBlocks.CARDOON);
                output.accept(ModItems.PASTA_WITH_TOMATO_SAUCE);
                output.accept(ModItems.RISOTTO_WITH_TOMATO_SAUCE);
                output.accept(ModItems.PASTA_ALLA_CARBONARA);
                output.accept(ModItems.PASTA_ALLA_GRICIA);
                output.accept(ModItems.PASTA_ALL_AMATRICIANA);
                output.accept(ModItems.PASTA_WITH_SPECK_AND_GORGONZOLA);
                output.accept(ModItems.RISOTTO_WITH_SPECK_AND_GORGONZOLA);
                output.accept(ModItems.RISOTTO_WITH_PARMIGIANO);
                output.accept(ModItems.MOZZARELLA_SALAD);
                output.accept(ModItems.RISOTTO_WITH_MUSHROOM);
                output.accept(ModItems.MOZZARELLA);
                output.accept(ModItems.MOZZARELLA_SLICE);
                output.accept(ModItems.TOMATO_SLICE);
                output.accept(ModItems.RENNET);
                output.accept(ModItems.CURD);
                output.accept(ModItems.CREAM_BOWL);
                output.accept(ModItems.BLUE_MOLD_CULTURE);
                output.accept(ModItems.APPLE_CIDER_VINEGAR);
                output.accept(ModItems.RICOTTA);
                output.accept(ModItems.SHEEP_MILK_BOTTLE);
                output.accept(ModItems.SHEEP_MILK_BUCKET);
                output.accept(ModItems.WHEY_BOTTLE);
                output.accept(ModItems.WHEY_BUCKET);
                output.accept(ModItems.BOCCONCINO);
                output.accept(ModItems.BURRATA);
                output.accept(ModItems.MASCARPONE);
                output.accept(ModItems.FRESH_PARMIGIANO_REGGIANO);
                output.accept(ModItems.PARMIGIANO_REGGIANO);
                output.accept(ModItems.PARMIGIANO_REGGIANO_WEDGE);
                output.accept(ModItems.GRATED_PARMIGIANO_REGGIANO);
                output.accept(ModItems.FRESH_PECORINO_ROMANO);
                output.accept(ModItems.PECORINO_ROMANO);
                output.accept(ModItems.PECORINO_ROMANO_WEDGE);
                output.accept(ModItems.GRATED_PECORINO_ROMANO);
                output.accept(ModItems.FRESH_GORGONZOLA);
                output.accept(ModItems.GORGONZOLA);
                output.accept(ModItems.GORGONZOLA_WEDGE);
                output.accept(ModItems.FRESH_PROVOLONE);
                output.accept(ModItems.PROVOLONE);
                output.accept(ModItems.PROVOLONE_SLICE);
                output.accept(ModItems.FRESH_SCAMORZA);
                output.accept(ModItems.SCAMORZA);
                output.accept(ModItems.SMOKED_SCAMORZA);
                output.accept(ModItems.SCAMORZA_SLICE);
                output.accept(ModItems.SMOKED_SCAMORZA_SLICE);

                // Cured-meat production chain.
                output.accept(ModItems.SALTED_HAM);
                output.accept(ModItems.RAW_SALAME);
                output.accept(ModItems.RAW_MORTADELLA);
                output.accept(ModItems.PREPARED_PANCETTA);
                output.accept(ModItems.PREPARED_GUANCIALE);
                output.accept(ModItems.PREPARED_BRESAOLA);
                output.accept(ModItems.PREPARED_COPPA);
                output.accept(ModItems.PREPARED_SPECK);
                output.accept(ModItems.SMOKED_PREPARED_SPECK);
                output.accept(ModItems.PROSCIUTTO_CRUDO);
                output.accept(ModItems.SALAME);
                output.accept(ModItems.MORTADELLA);
                output.accept(ModItems.PANCETTA);
                output.accept(ModItems.GUANCIALE);
                output.accept(ModItems.BRESAOLA);
                output.accept(ModItems.COPPA);
                output.accept(ModItems.SPECK);
                output.accept(ModItems.PROSCIUTTO_CRUDO_SLICE);
                output.accept(ModItems.SALAME_SLICE);
                output.accept(ModItems.MORTADELLA_SLICE);
                output.accept(ModItems.PANCETTA_SLICE);
                output.accept(ModItems.GUANCIALE_SLICE);
                output.accept(ModItems.BRESAOLA_SLICE);
                output.accept(ModItems.COPPA_SLICE);
                output.accept(ModItems.SPECK_SLICE);

                output.accept(ModItems.SALT);
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
