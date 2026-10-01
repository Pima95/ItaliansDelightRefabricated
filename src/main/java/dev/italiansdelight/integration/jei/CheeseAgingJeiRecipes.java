package dev.italiansdelight.integration.jei;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import dev.italiansdelight.common.crafting.CheeseAgingRecipe;
import dev.italiansdelight.common.crafting.CheeseDryingRecipe;
import dev.italiansdelight.common.registry.ModRecipes;

import net.fabricmc.fabric.api.recipe.v1.sync.SynchronizedRecipes;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.crafting.RecipeHolder;

/**
 * Collects both aging and drying recipes for the single JEI Aging category.
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

    public List<CheeseAgingJeiRecipe> getRecipes() {
        List<CheeseAgingJeiRecipe> recipes =
            new ArrayList<>();

        for (
            RecipeHolder<CheeseAgingRecipe> holder :
            synchronizedRecipes.getAllOfType(
                ModRecipes.CHEESE_AGING_TYPE
            )
        ) {
            recipes.add(
                CheeseAgingJeiRecipe.fromAging(
                    holder.value()
                )
            );
        }

        // For now Scamorza is deliberately shown in the same GUI as the aged
        // cheeses. Its gameplay recipe remains cheese_drying, ready for the
        // future hanging-hook implementation.
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

        recipes.sort(
            Comparator.comparingInt(
                CheeseAgingJeiRecipe::processingTime
            )
        );

        return List.copyOf(
            recipes
        );
    }
}
