package dev.italiansdelight.integration.jei;

import dev.italiansdelight.ItaliansDelight;
import mezz.jei.api.recipe.types.IRecipeType;

/** Real wine-fermentation recipes; preserve the original JEI category identifier. */
public final class BrewingBarrelJeiRecipeTypes {
    private BrewingBarrelJeiRecipeTypes() {}

    public static final IRecipeType<BrewingBarrelJeiGuide> BARREL_GUI_GUIDE =
        IRecipeType.create(ItaliansDelight.MOD_ID, "brewing_barrel_gui_guide",
            BrewingBarrelJeiGuide.class);
}
