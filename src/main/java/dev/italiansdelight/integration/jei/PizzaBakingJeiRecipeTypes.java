package dev.italiansdelight.integration.jei;

import dev.italiansdelight.ItaliansDelight;
import mezz.jei.api.recipe.RecipeType;

/** JEI category for the custom Pizza Oven; vanilla furnaces use vanilla JEI tabs. */
public final class PizzaBakingJeiRecipeTypes {
    private PizzaBakingJeiRecipeTypes() {
    }

    public static final RecipeType<PizzaBakingJeiRecipe> PIZZA_BAKING =
            RecipeType.create(
                    ItaliansDelight.MOD_ID,
                    "pizza_baking_guide",
                    PizzaBakingJeiRecipe.class
            );
}
