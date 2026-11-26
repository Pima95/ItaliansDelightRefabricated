package dev.italiansdelight.integration.jei;

import dev.italiansdelight.ItaliansDelight;
import mezz.jei.api.recipe.RecipeType;

/** JEI index for the actual dynamic Cutting Board quarter-slicing behavior. */
public final class PizzaCuttingJeiRecipeTypes {
    private PizzaCuttingJeiRecipeTypes() {}

    public static final RecipeType<PizzaCuttingJeiRecipe> PIZZA_CUTTING =
            RecipeType.create(ItaliansDelight.MOD_ID, "pizza_cutting", PizzaCuttingJeiRecipe.class);
}
