package dev.italiansdelight.integration.jei;

import java.util.List;

import net.fabricmc.fabric.api.recipe.v1.sync.SynchronizedRecipes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.crafting.RecipeHolder;

import dev.italiansdelight.common.crafting.CheeseVatRecipe;
import dev.italiansdelight.common.registry.ModRecipes;

/**
 * Collects and adapts Cheese Vat recipes that must be exposed through the JEI integration.
 */

public class CheeseVatJeiRecipes {

    /**
     * JEI may query us during client world unload/reload. Do not capture an
     * old ClientLevel in the constructor or throw before the new one exists.
     * JEI rebuilds the recipe list when the game joins the next world.
     */
    public List<RecipeHolder<CheeseVatRecipe>> getRecipes() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return List.of();
        }
        SynchronizedRecipes recipes = level.recipeAccess().getSynchronizedRecipes();
        return List.copyOf(recipes.getAllOfType(ModRecipes.CHEESE_VAT_TYPE));
    }
}