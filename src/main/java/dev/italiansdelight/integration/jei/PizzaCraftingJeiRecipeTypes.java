package dev.italiansdelight.integration.jei;

import dev.italiansdelight.ItaliansDelight;
import mezz.jei.api.recipe.RecipeType;

/** Informational JEI previews for the existing dynamic shapeless recipe. */
public final class PizzaCraftingJeiRecipeTypes {
    private PizzaCraftingJeiRecipeTypes() {
    }

    public static final RecipeType<PizzaCraftingJeiRecipe> PIZZA_CRAFTING_EXAMPLES =
            RecipeType.create(
                    ItaliansDelight.MOD_ID,
                    "pizza_crafting_examples",
                    PizzaCraftingJeiRecipe.class
            );
}
