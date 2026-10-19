package dev.italiansdelight.integration.jei;

import dev.italiansdelight.ItaliansDelight;
import mezz.jei.api.recipe.types.IRecipeType;

/** Informational JEI previews for the existing dynamic shapeless recipe. */
public final class PizzaCraftingJeiRecipeTypes {
    private PizzaCraftingJeiRecipeTypes() {
    }

    public static final IRecipeType<PizzaCraftingJeiRecipe> PIZZA_CRAFTING_EXAMPLES =
            IRecipeType.create(
                    ItaliansDelight.MOD_ID,
                    "pizza_crafting_examples",
                    PizzaCraftingJeiRecipe.class
            );
}
