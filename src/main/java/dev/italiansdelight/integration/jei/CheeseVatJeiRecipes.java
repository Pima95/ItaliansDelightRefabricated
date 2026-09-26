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

    private final SynchronizedRecipes synchronizedRecipes;

    public CheeseVatJeiRecipes() {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;

        if (level != null) {
            this.synchronizedRecipes = level.recipeAccess().getSynchronizedRecipes();
        } else {
            throw new NullPointerException("Minecraft level must not be null.");
        }
    }

    public List<RecipeHolder<CheeseVatRecipe>> getRecipes() {
        return List.copyOf(
            synchronizedRecipes.getAllOfType(ModRecipes.CHEESE_VAT_TYPE)
        );
    }
}