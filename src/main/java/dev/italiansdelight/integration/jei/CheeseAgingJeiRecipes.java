package dev.italiansdelight.integration.jei;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import dev.italiansdelight.common.crafting.CheeseAgingRecipe;
import dev.italiansdelight.common.crafting.CheeseDryingRecipe;
import dev.italiansdelight.common.registry.ModItems;
import dev.italiansdelight.common.registry.ModRecipes;

import net.fabricmc.fabric.api.recipe.v1.sync.SynchronizedRecipes;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

/**
 * Collects aging and drying recipes for JEI and assigns them to the station
 * that can actually perform the process.
 *
 * Wheel cheeses that fit the aging racks are shown in the Cheese Aging
 * category. Provolone and all drying recipes are shown in the Hanging Aging &
 * Drying category because they use the Hanging Hook.
 */
public final class CheeseAgingJeiRecipes {

    private final SynchronizedRecipes synchronizedRecipes;

    public CheeseAgingJeiRecipes() {
        Minecraft minecraft =
            Minecraft.getInstance();

        ClientLevel level =
            minecraft.level;

        if (level == null) {
            throw new NullPointerException(
                "Minecraft level must not be null."
            );
        }

        this.synchronizedRecipes =
            level.recipeAccess()
                .getSynchronizedRecipes();
    }

    public List<CheeseAgingJeiRecipe> getRackAgingRecipes() {
        List<CheeseAgingJeiRecipe> recipes =
            new ArrayList<>();

        for (
            RecipeHolder<CheeseAgingRecipe> holder :
            synchronizedRecipes.getAllOfType(
                ModRecipes.CHEESE_AGING_TYPE
            )
        ) {
            CheeseAgingRecipe recipe =
                holder.value();

            // Provolone is the aging recipe that processes on the Hanging Hook.
            // Other current cheese-aging recipes use a rack or a flat surface.
            if (
                recipe.getIngredient().test(
                    new ItemStack(
                        ModItems.FRESH_PROVOLONE
                    )
                )
            ) {
                continue;
            }

            recipes.add(
                CheeseAgingJeiRecipe.fromAging(
                    recipe
                )
            );
        }

        sortByProcessingTime(recipes);
        return List.copyOf(recipes);
    }

    public List<CheeseAgingJeiRecipe> getHangingRecipes() {
        List<CheeseAgingJeiRecipe> recipes =
            new ArrayList<>();

        for (
            RecipeHolder<CheeseAgingRecipe> holder :
            synchronizedRecipes.getAllOfType(
                ModRecipes.CHEESE_AGING_TYPE
            )
        ) {
            CheeseAgingRecipe recipe =
                holder.value();

            if (
                recipe.getIngredient().test(
                    new ItemStack(
                        ModItems.FRESH_PROVOLONE
                    )
                )
            ) {
                recipes.add(
                    CheeseAgingJeiRecipe.fromAging(
                        recipe
                    )
                );
            }
        }

        for (
            RecipeHolder<CheeseDryingRecipe> holder :
            synchronizedRecipes.getAllOfType(
                ModRecipes.CHEESE_DRYING_TYPE
            )
        ) {
            recipes.add(
                CheeseAgingJeiRecipe.fromDrying(
                    holder.value()
                )
            );
        }

        sortByProcessingTime(recipes);
        return List.copyOf(recipes);
    }

    private static void sortByProcessingTime(
        List<CheeseAgingJeiRecipe> recipes
    ) {
        recipes.sort(
            Comparator.comparingInt(
                CheeseAgingJeiRecipe::processingTime
            )
        );
    }
}
