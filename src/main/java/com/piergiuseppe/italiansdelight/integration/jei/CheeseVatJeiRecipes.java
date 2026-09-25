package com.piergiuseppe.italiansdelight.integration.jei;

import java.util.List;

import net.fabricmc.fabric.api.recipe.v1.sync.SynchronizedRecipes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.crafting.RecipeHolder;

import com.piergiuseppe.italiansdelight.common.crafting.CheeseVatRecipe;
import com.piergiuseppe.italiansdelight.common.registry.ModRecipes;

/**
 * Adatta e raccoglie le ricette della Cheese Vat che devono essere esposte all'integrazione JEI.
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