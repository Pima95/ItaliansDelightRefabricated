package dev.italiansdelight.integration.jei;

import dev.italiansdelight.ItaliansDelight;

import mezz.jei.api.recipe.types.IRecipeType;

/**
 * JEI recipe type for the combined aging screen.
 *
 * Gameplay still keeps cheese_aging and cheese_drying as two separate recipe
 * types; only their presentation is temporarily unified in JEI.
 */
public final class CheeseAgingJeiRecipeTypes {

    private CheeseAgingJeiRecipeTypes() {
    }

    public static final IRecipeType<CheeseAgingJeiRecipe> CHEESE_AGING =
        IRecipeType.create(
            ItaliansDelight.MOD_ID,
            "cheese_aging",
            CheeseAgingJeiRecipe.class
        );
}
