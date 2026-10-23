package dev.italiansdelight.integration.jei;

import dev.italiansdelight.ItaliansDelight;
import mezz.jei.api.recipe.types.IRecipeType;

/** Instructional JEI pages for preparing pizza directly in the world. */
public final class PizzaWorldJeiRecipeTypes {
    private PizzaWorldJeiRecipeTypes() {
    }

    public static final IRecipeType<PizzaWorldJeiRecipe> WORLD_PREPARATION =
            IRecipeType.create(
                    ItaliansDelight.MOD_ID,
                    "pizza_world_preparation",
                    PizzaWorldJeiRecipe.class
            );
}
