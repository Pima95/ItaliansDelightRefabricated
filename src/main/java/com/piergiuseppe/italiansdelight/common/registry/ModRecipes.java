package com.piergiuseppe.italiansdelight.common.registry;

import com.piergiuseppe.italiansdelight.common.crafting.CheeseVatRecipe;

import net.fabricmc.fabric.api.recipe.v1.sync.RecipeSynchronization;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

/**
 * Registro del tipo e del serializer delle ricette personalizzate della Cheese Vat.
 */

public final class ModRecipes {

    private ModRecipes() {
    }

    public static final RecipeSerializer<CheeseVatRecipe> CHEESE_VAT_SERIALIZER =
        Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER,
            ModRegistries.id("cheese_vat"),
            CheeseVatRecipe.SERIALIZER
        );

    public static final RecipeType<CheeseVatRecipe> CHEESE_VAT_TYPE =
        Registry.register(
            BuiltInRegistries.RECIPE_TYPE,
            ModRegistries.id("cheese_vat"),
            new RecipeType<>() {
            }
        );

    public static void register() {
        RecipeSynchronization.synchronizeRecipeSerializer(CHEESE_VAT_SERIALIZER);
    }
}
