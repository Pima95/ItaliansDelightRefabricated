package dev.italiansdelight.common.registry;

import dev.italiansdelight.common.crafting.CheeseVatRecipe;
import dev.italiansdelight.common.crafting.CheeseAgingRecipe;
import dev.italiansdelight.common.crafting.CheeseDryingRecipe;

import net.fabricmc.fabric.api.recipe.v1.sync.RecipeSynchronization;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

/**
 * Registry for the Cheese Vat custom recipe type and serializer.
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

    public static final RecipeSerializer<CheeseAgingRecipe> CHEESE_AGING_SERIALIZER =
        Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER,
            ModRegistries.id("cheese_aging"),
            CheeseAgingRecipe.SERIALIZER
        );

    public static final RecipeType<CheeseAgingRecipe> CHEESE_AGING_TYPE =
        Registry.register(
            BuiltInRegistries.RECIPE_TYPE,
            ModRegistries.id("cheese_aging"),
            new RecipeType<>() {
            }
        );

    public static final RecipeSerializer<CheeseDryingRecipe> CHEESE_DRYING_SERIALIZER =
        Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER,
            ModRegistries.id("cheese_drying"),
            CheeseDryingRecipe.SERIALIZER
        );

    public static final RecipeType<CheeseDryingRecipe> CHEESE_DRYING_TYPE =
        Registry.register(
            BuiltInRegistries.RECIPE_TYPE,
            ModRegistries.id("cheese_drying"),
            new RecipeType<>() {
            }
        );

    public static void register() {
        RecipeSynchronization.synchronizeRecipeSerializer(CHEESE_VAT_SERIALIZER);
        RecipeSynchronization.synchronizeRecipeSerializer(CHEESE_AGING_SERIALIZER);
        RecipeSynchronization.synchronizeRecipeSerializer(CHEESE_DRYING_SERIALIZER);
    }
}
