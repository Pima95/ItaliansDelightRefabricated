package dev.italiansdelight.integration.jei;

import dev.italiansdelight.common.registry.ModBlocks;
import dev.italiansdelight.common.registry.ModItems;

import mezz.jei.api.registration.IRecipeRegistration;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Native JEI "Information" pages for the seven added plant families.
 *
 * <p>Like the Farmer's Delight tomato/shrub example, each entry groups
 * related item stacks under one informational page. JEI supplies the normal
 * blue information tab, rotating ingredient icon, text wrapping and scroll.
 * These are descriptions of existing gameplay, NOT additional recipes.
 */
public final class OrchardPlantJeiInfo {
    private OrchardPlantJeiInfo() {}

    public static void register(IRecipeRegistration registration) {
        // Fruit, sapling and natural leaves open the same information page.
        registration.addItemStackInfo(
            List.of(
                new ItemStack(ModItems.OLIVES),
                new ItemStack(ModBlocks.OLIVE_SAPLING),
                new ItemStack(ModBlocks.OLIVE_LEAVES)
            ),
            Component.translatable("jei.italiansdelight.plants.olive")
        );
        registration.addItemStackInfo(
            List.of(
                new ItemStack(ModItems.ALMONDS),
                new ItemStack(ModBlocks.ALMOND_SAPLING),
                new ItemStack(ModBlocks.ALMOND_LEAVES)
            ),
            Component.translatable("jei.italiansdelight.plants.almond")
        );
        registration.addItemStackInfo(
            List.of(
                new ItemStack(ModItems.CHERRIES),
                new ItemStack(ModBlocks.FRUIT_CHERRY_SAPLING),
                new ItemStack(ModBlocks.FRUIT_CHERRY_LEAVES)
            ),
            Component.translatable("jei.italiansdelight.plants.fruit_cherry")
        );

        // One page per herb, reachable from both wild and cultivated forms.
        // They are one species but two different blocks in game.
        registration.addItemStackInfo(
            List.of(
                new ItemStack(ModBlocks.BASIL),
                new ItemStack(ModBlocks.WILD_BASIL)
            ),
            Component.translatable("jei.italiansdelight.plants.basil")
        );
        registration.addItemStackInfo(
            List.of(
                new ItemStack(ModBlocks.CULTIVATED_CARDOON),
                new ItemStack(ModBlocks.CARDOON)
            ),
            Component.translatable("jei.italiansdelight.plants.cardoon")
        );
        // The full grape and chili families share JEI Information pages,
        // so their seeds, natural forms, harvest and processing stay linked.
        registration.addItemStackInfo(
            List.of(
                new ItemStack(ModBlocks.GRAPE_SEEDS),
                new ItemStack(ModItems.GRAPES),
                new ItemStack(ModItems.GRAPE_JUICE_BUCKET),
                new ItemStack(ModItems.GRAPE_JUICE_BOTTLE),
                new ItemStack(ModItems.WINE_BUCKET),
                new ItemStack(ModItems.WINE_BOTTLE)
            ),
            Component.translatable("jei.italiansdelight.plants.grape")
        );
        registration.addItemStackInfo(
            List.of(
                new ItemStack(ModBlocks.CHILI_PEPPER_SEEDS),
                new ItemStack(ModBlocks.WILD_CHILI),
                new ItemStack(ModItems.CHILI_PEPPER),
                new ItemStack(ModItems.GHOST_PEPPER)
            ),
            Component.translatable("jei.italiansdelight.plants.chili")
        );
    }
}
