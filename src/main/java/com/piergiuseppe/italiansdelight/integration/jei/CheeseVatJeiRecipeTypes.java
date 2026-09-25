package com.piergiuseppe.italiansdelight.integration.jei;

import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.world.item.crafting.RecipeHolder;

import com.piergiuseppe.italiansdelight.common.crafting.CheeseVatRecipe;
import com.piergiuseppe.italiansdelight.common.registry.ModRecipes;

/**
 * Defines the JEI recipe types used to display Cheese Vat processing recipes.
 */

public final class CheeseVatJeiRecipeTypes {

    private CheeseVatJeiRecipeTypes() {
    }

    public static final IRecipeType<RecipeHolder<CheeseVatRecipe>> CHEESE_VAT =
        IRecipeType.create(ModRecipes.CHEESE_VAT_TYPE);
}