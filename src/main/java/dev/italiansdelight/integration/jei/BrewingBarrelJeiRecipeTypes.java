package dev.italiansdelight.integration.jei;

import dev.italiansdelight.ItaliansDelight;
import mezz.jei.api.recipe.RecipeType;

/** Real wine-fermentation recipes; preserve the original JEI category identifier. */
public final class BrewingBarrelJeiRecipeTypes {
    private BrewingBarrelJeiRecipeTypes() {}

    public static final RecipeType<BrewingBarrelJeiGuide> BARREL_GUI_GUIDE =
        RecipeType.create(ItaliansDelight.MOD_ID, "brewing_barrel_gui_guide",
            BrewingBarrelJeiGuide.class);
}
