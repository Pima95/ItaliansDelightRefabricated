package com.piergiuseppe.italiansdelight.recipe;

import java.util.List;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import com.piergiuseppe.italiansdelight.registry.ModRecipes;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class CheeseVatRecipe implements Recipe<CheeseVatRecipeInput> {

    public static final MapCodec<CheeseVatRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            Ingredient.CODEC.listOf().fieldOf("ingredients").forGetter(recipe -> recipe.ingredients),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(recipe -> recipe.result)
        ).apply(instance, CheeseVatRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, CheeseVatRecipe> STREAM_CODEC =
        StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
            recipe -> recipe.ingredients,
            ItemStackTemplate.STREAM_CODEC,
            recipe -> recipe.result,
            CheeseVatRecipe::new
        );

    public static final RecipeSerializer<CheeseVatRecipe> SERIALIZER =
        new RecipeSerializer<>(CODEC, STREAM_CODEC);

    private final List<Ingredient> ingredients;
    private final ItemStackTemplate result;

    public CheeseVatRecipe(List<Ingredient> ingredients, ItemStackTemplate result) {
        if (ingredients.isEmpty() || ingredients.size() > 3) {
            throw new IllegalArgumentException("Cheese Vat recipes must have between 1 and 3 ingredients.");
        }

        this.ingredients = List.copyOf(ingredients);
        this.result = result;
    }

    public List<Ingredient> getIngredientsList() {
        return this.ingredients;
    }

    public ItemStackTemplate getResultTemplate() {
        return this.result;
    }

    @Override
    public boolean matches(CheeseVatRecipeInput input, Level level) {
        for (int i = 0; i < this.ingredients.size(); i++) {
            if (!this.ingredients.get(i).test(input.getItem(i))) {
                return false;
            }
        }

        for (int i = this.ingredients.size(); i < input.size(); i++) {
            if (!input.getItem(i).isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack assemble(CheeseVatRecipeInput input) {
        return this.result.create();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> result = NonNullList.create();
        result.addAll(this.ingredients);
        return result;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return this.result.create();
    }

    @Override
    public RecipeSerializer<? extends Recipe<CheeseVatRecipeInput>> getSerializer() {
        return ModRecipes.CHEESE_VAT_SERIALIZER;
    }

    @Override
    public RecipeType<? extends Recipe<CheeseVatRecipeInput>> getType() {
        return ModRecipes.CHEESE_VAT_TYPE;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public @org.jetbrains.annotations.Nullable net.minecraft.world.item.crafting.RecipeBookCategory recipeBookCategory() {
        return null;
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean showNotification() {
        return true;
    }

    @Override
    public String group() {
        return "cheese_vat";
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean showNotification() {
        return true;
    }
}
