package com.piergiuseppe.italiansdelight.integration.jei;

import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.world.item.crafting.RecipeHolder;

import com.piergiuseppe.italiansdelight.common.crafting.CheeseVatRecipe;
import com.piergiuseppe.italiansdelight.common.registry.ModRecipes;

/**
 * Definisce i tipi ricetta JEI utilizzati per mostrare le lavorazioni della Cheese Vat.
 */

public final class CheeseVatJeiRecipeTypes {

    private CheeseVatJeiRecipeTypes() {
    }

    public static final IRecipeType<RecipeHolder<CheeseVatRecipe>> CHEESE_VAT =
        IRecipeType.create(ModRecipes.CHEESE_VAT_TYPE);
}