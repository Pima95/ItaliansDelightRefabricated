package dev.italiansdelight.integration.jei;

import dev.italiansdelight.ItaliansDelight;
import mezz.jei.api.recipe.types.IRecipeType;

/** JEI index for the actual dynamic Cutting Board quarter-slicing behavior. */
public final class PizzaCuttingJeiRecipeTypes {
    private PizzaCuttingJeiRecipeTypes() {}

    public static final IRecipeType<PizzaCuttingJeiRecipe> PIZZA_CUTTING =
            IRecipeType.create(ItaliansDelight.MOD_ID, "pizza_cutting", PizzaCuttingJeiRecipe.class);
}
