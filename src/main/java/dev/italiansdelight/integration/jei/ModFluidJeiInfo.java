package dev.italiansdelight.integration.jei;

import dev.italiansdelight.common.registry.ModItems;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import java.util.List;

/**
 * Native JEI info tabs as used for OrchardPlantJeiInfo. Describes only
 * IMPLEMENTED acquisition methods, not hypothetical processing recipes.
 */
public final class ModFluidJeiInfo {
    private ModFluidJeiInfo() {}

    public static void register(IRecipeRegistration registration) {
        registration.addItemStackInfo(List.of(
                new ItemStack(ModItems.OLIVE_OIL_BOTTLE),
                new ItemStack(ModItems.OLIVE_OIL_BUCKET)),
            Component.translatable("jei.italiansdelight.fluids.olive_oil"));

        registration.addItemStackInfo(List.of(
                new ItemStack(ModItems.APPLE_JUICE_BOTTLE),
                new ItemStack(ModItems.APPLE_JUICE_BUCKET)),
            Component.translatable("jei.italiansdelight.fluids.apple_juice"));

        registration.addItemStackInfo(List.of(
                new ItemStack(ModItems.GRAPE_JUICE_BOTTLE),
                new ItemStack(ModItems.GRAPE_JUICE_BUCKET)),
            Component.translatable("jei.italiansdelight.fluids.grape_juice"));

        registration.addItemStackInfo(List.of(new ItemStack(ModItems.WINE_BOTTLE)),
            Component.translatable("jei.italiansdelight.fluids.wine"));

        registration.addItemStackInfo(List.of(
                new ItemStack(ModItems.GOLDEN_APPLE_JUICE_BOTTLE),
                new ItemStack(ModItems.GOLDEN_APPLE_JUICE_BUCKET)),
            Component.translatable("jei.italiansdelight.fluids.golden_apple_juice"));

        registration.addItemStackInfo(List.of(
                new ItemStack(net.minecraft.world.item.Items.HONEY_BOTTLE),
                new ItemStack(ModItems.HONEY_BUCKET)),
            Component.translatable("jei.italiansdelight.fluids.honey"));

        registration.addItemStackInfo(List.of(
                new ItemStack(ModItems.SHEEP_MILK_BOTTLE),
                new ItemStack(ModItems.SHEEP_MILK_BUCKET)),
            Component.translatable("jei.italiansdelight.fluids.sheep_milk"));

        registration.addItemStackInfo(List.of(
                new ItemStack(ModItems.WHEY_BOTTLE),
                new ItemStack(ModItems.WHEY_BUCKET)),
            Component.translatable("jei.italiansdelight.fluids.whey"));
        registration.addItemStackInfo(List.of(
                new ItemStack(ModItems.RENNET),
                new ItemStack(ModItems.RENNET_BUCKET)),
            Component.translatable("jei.italiansdelight.fluids.rennet"));

        registration.addItemStackInfo(List.of(
                new ItemStack(ModItems.APPLE_CIDER_VINEGAR),
                new ItemStack(ModItems.APPLE_CIDER_VINEGAR_BUCKET)),
            Component.translatable("jei.italiansdelight.fluids.apple_cider_vinegar"));
    }
}
