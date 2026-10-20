package dev.italiansdelight.integration.jei;

import dev.italiansdelight.ItaliansDelight;
import mezz.jei.api.recipe.types.IRecipeType;

/** JEI category for the custom Pizza Oven; vanilla furnaces use vanilla JEI tabs. */
public final class PizzaBakingJeiRecipeTypes {
    private PizzaBakingJeiRecipeTypes() {
    }

    public static final IRecipeType<PizzaBakingJeiRecipe> PIZZA_BAKING =
            IRecipeType.create(
                    ItaliansDelight.MOD_ID,
                    "pizza_baking_guide",
                    PizzaBakingJeiRecipe.class
            );
}
