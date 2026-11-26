package dev.italiansdelight.integration.jei;

import dev.italiansdelight.ItaliansDelight;

import mezz.jei.api.recipe.RecipeType;

/**
 * JEI recipe type for the combined aging screen.
 *
 * Gameplay still keeps cheese_aging and cheese_drying as two separate recipe
 * types; only their presentation is temporarily unified in JEI.
 */
public final class CheeseAgingJeiRecipeTypes {

    private CheeseAgingJeiRecipeTypes() {
    }

    public static final RecipeType<CheeseAgingJeiRecipe> CHEESE_AGING =
        RecipeType.create(
            ItaliansDelight.MOD_ID,
            "cheese_aging",
            CheeseAgingJeiRecipe.class
        );

    public static final RecipeType<CheeseAgingJeiRecipe> HANGING_AGING_DRYING =
        RecipeType.create(
            ItaliansDelight.MOD_ID,
            "hanging_aging_drying",
            CheeseAgingJeiRecipe.class
        );
}
