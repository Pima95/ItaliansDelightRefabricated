package dev.italiansdelight.integration.jei;

import dev.italiansdelight.common.registry.ModBlocks;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import java.util.List;

/**
 * One JEI information page for all 16 decorative pole wood variants.
 * JEI's final ingredient-sidebar sorting remains controlled by the user's
 * selected sorting mode; these entries deliberately add no duplicates.
 */
public final class DecorativePoleJeiInfo {
    private DecorativePoleJeiInfo() {}

    public static void register(IRecipeRegistration registration) {
        registration.addItemStackInfo(
            List.of(
                new ItemStack(ModBlocks.CROP_STAKE),
                new ItemStack(ModBlocks.SPRUCE_CROP_STAKE),
                new ItemStack(ModBlocks.BIRCH_CROP_STAKE),
                new ItemStack(ModBlocks.JUNGLE_CROP_STAKE),
                new ItemStack(ModBlocks.ACACIA_CROP_STAKE),
                new ItemStack(ModBlocks.DARK_OAK_CROP_STAKE),
                new ItemStack(ModBlocks.MANGROVE_CROP_STAKE),
                new ItemStack(ModBlocks.CHERRY_CROP_STAKE),
                new ItemStack(ModBlocks.PALE_OAK_CROP_STAKE),
                new ItemStack(ModBlocks.POPLAR_CROP_STAKE),
                new ItemStack(ModBlocks.BAMBOO_CROP_STAKE),
                new ItemStack(ModBlocks.CRIMSON_CROP_STAKE),
                new ItemStack(ModBlocks.WARPED_CROP_STAKE),
                new ItemStack(ModBlocks.OLIVE_CROP_STAKE),
                new ItemStack(ModBlocks.ALMOND_CROP_STAKE),
                new ItemStack(ModBlocks.FRUIT_CHERRY_CROP_STAKE)
            ),
            Component.translatable("jei.italiansdelight.decorative_stakes")
        );
    }
}
