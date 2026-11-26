package dev.italiansdelight.integration.jei;

import mezz.jei.api.recipe.RecipeType;
import net.minecraft.world.item.crafting.RecipeHolder;

import dev.italiansdelight.common.crafting.CheeseVatRecipe;
import dev.italiansdelight.common.registry.ModRecipes;

/**
 * Defines the JEI recipe types used to display Cheese Vat processing recipes.
 */

public final class CheeseVatJeiRecipeTypes {

    private CheeseVatJeiRecipeTypes() {
    }

    public static final RecipeType<RecipeHolder<CheeseVatRecipe>> CHEESE_VAT =
        RecipeType.createRecipeHolderType(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("italiansdelight", "cheese_vat"));
}