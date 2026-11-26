package dev.italiansdelight.integration.jei;

import dev.italiansdelight.ItaliansDelight;

import mezz.jei.api.recipe.RecipeType;

/** JEI recipe type for salt evaporation in a vanilla cauldron. */
public final class SaltEvaporationJeiRecipeTypes {

    private SaltEvaporationJeiRecipeTypes() {
    }

    public static final RecipeType<SaltEvaporationJeiRecipe> SALT_EVAPORATION =
        RecipeType.create(
            ItaliansDelight.MOD_ID,
            "salt_evaporation",
            SaltEvaporationJeiRecipe.class
        );
}
