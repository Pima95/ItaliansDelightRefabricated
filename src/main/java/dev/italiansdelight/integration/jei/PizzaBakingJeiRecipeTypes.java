package dev.italiansdelight.integration.jei;

import dev.italiansdelight.ItaliansDelight;
import mezz.jei.api.recipe.types.IRecipeType;

/** JEI guides for the three already implemented pizza cooking methods. */
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
