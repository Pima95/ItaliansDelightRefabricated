package dev.italiansdelight.common.registry;

import dev.italiansdelight.common.crafting.CheeseVatRecipe;
import dev.italiansdelight.common.crafting.CheeseAgingRecipe;
import dev.italiansdelight.common.crafting.CheeseDryingRecipe;
import dev.italiansdelight.common.crafting.PizzaSmeltingRecipe;
import dev.italiansdelight.common.crafting.PizzaSmokingRecipe;
import dev.italiansdelight.common.crafting.PizzaCraftingRecipe;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

/**
 * Registry for the Cheese Vat custom recipe type and serializer.
 */

public final class ModRecipes {

    private ModRecipes() {
    }

    public static RecipeSerializer<CheeseVatRecipe> CHEESE_VAT_SERIALIZER;

    public static RecipeType<CheeseVatRecipe> CHEESE_VAT_TYPE;

    public static RecipeSerializer<CheeseAgingRecipe> CHEESE_AGING_SERIALIZER;

    public static RecipeType<CheeseAgingRecipe> CHEESE_AGING_TYPE;

    public static RecipeSerializer<CheeseDryingRecipe> CHEESE_DRYING_SERIALIZER;

    public static RecipeType<CheeseDryingRecipe> CHEESE_DRYING_TYPE;

    // These serializers provide dynamic components to vanilla furnace/smoker
    // recipes. The recipe TYPES are vanilla SMELTING and SMOKING; the custom
    // serializers only change assembly, not normal fuel/menu automation.
    public static RecipeSerializer<PizzaSmeltingRecipe> PIZZA_SMELTING_SERIALIZER;

    public static RecipeSerializer<PizzaSmokingRecipe> PIZZA_SMOKING_SERIALIZER;

    // Dynamic shapeless crafting preserves the entire pizza composition
    // rather than generating a fixed generic raw_pizza from a JSON result.
    public static RecipeSerializer<PizzaCraftingRecipe> PIZZA_CRAFTING_SERIALIZER;

    public static void onRegister(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.RECIPE_SERIALIZER)) {
            CHEESE_VAT_SERIALIZER = Registry.register(
                        BuiltInRegistries.RECIPE_SERIALIZER,
                        ModRegistries.id("cheese_vat"),
                        CheeseVatRecipe.SERIALIZER
                    );
            CHEESE_AGING_SERIALIZER = Registry.register(
                        BuiltInRegistries.RECIPE_SERIALIZER,
                        ModRegistries.id("cheese_aging"),
                        CheeseAgingRecipe.SERIALIZER
                    );
            CHEESE_DRYING_SERIALIZER = Registry.register(
                        BuiltInRegistries.RECIPE_SERIALIZER,
                        ModRegistries.id("cheese_drying"),
                        CheeseDryingRecipe.SERIALIZER
                    );
            PIZZA_SMELTING_SERIALIZER = Registry.register(
                        BuiltInRegistries.RECIPE_SERIALIZER,
                        ModRegistries.id("pizza_smelting"),
                        PizzaSmeltingRecipe.SERIALIZER
                    );
            PIZZA_SMOKING_SERIALIZER = Registry.register(
                        BuiltInRegistries.RECIPE_SERIALIZER,
                        ModRegistries.id("pizza_smoking"),
                        PizzaSmokingRecipe.SERIALIZER
                    );
            PIZZA_CRAFTING_SERIALIZER = Registry.register(
                        BuiltInRegistries.RECIPE_SERIALIZER,
                        ModRegistries.id("pizza_crafting"),
                        PizzaCraftingRecipe.SERIALIZER
                    );
        }
        if (event.getRegistryKey().equals(Registries.RECIPE_TYPE)) {
            CHEESE_VAT_TYPE = Registry.register(
                        BuiltInRegistries.RECIPE_TYPE,
                        ModRegistries.id("cheese_vat"),
                        new RecipeType<>() {
                        }
                    );
            CHEESE_AGING_TYPE = Registry.register(
                        BuiltInRegistries.RECIPE_TYPE,
                        ModRegistries.id("cheese_aging"),
                        new RecipeType<>() {
                        }
                    );
            CHEESE_DRYING_TYPE = Registry.register(
                        BuiltInRegistries.RECIPE_TYPE,
                        ModRegistries.id("cheese_drying"),
                        new RecipeType<>() {
                        }
                    );
        }
    }
}
