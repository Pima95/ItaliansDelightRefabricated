package com.piergiuseppe.italiansdelight.recipe;

import java.util.List;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import com.piergiuseppe.italiansdelight.registry.ModRecipes;

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

    public static final MapCodec<CheeseVatRecipe> CODEC =
        RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                Ingredient.CODEC
                    .listOf(1, 3)
                    .fieldOf("ingredients")
                    .forGetter(recipe -> recipe.ingredients),

                ItemStackTemplate.CODEC
                    .fieldOf("result")
                    .forGetter(recipe -> recipe.result),

                ItemStackTemplate.CODEC
                    .optionalFieldOf("container")
                    .forGetter(CheeseVatRecipe::getContainerTemplate),

                Codec.INT
                    .optionalFieldOf("cookingtime", 200)
                    .forGetter(CheeseVatRecipe::getCookingTime)
            ).apply(instance, CheeseVatRecipe::new)
        );

    public static final StreamCodec<
        RegistryFriendlyByteBuf,
        CheeseVatRecipe
    > STREAM_CODEC =
        StreamCodec.of(
            CheeseVatRecipe::toNetwork,
            CheeseVatRecipe::fromNetwork
        );

    public static final RecipeSerializer<CheeseVatRecipe> SERIALIZER =
        new RecipeSerializer<>(CODEC, STREAM_CODEC);

    private final List<Ingredient> ingredients;
    private final ItemStackTemplate result;
    private final Optional<ItemStackTemplate> container;
    private final int cookingTime;

    public CheeseVatRecipe(
        List<Ingredient> ingredients,
        ItemStackTemplate result,
        Optional<ItemStackTemplate> container,
        int cookingTime
    ) {
        this.ingredients = List.copyOf(ingredients);
        this.result = result;
        this.container = container;
        this.cookingTime = cookingTime;
    }

    public List<Ingredient> getIngredientsList() {
        return ingredients;
    }

    public ItemStackTemplate getResultTemplate() {
        return result;
    }

    public Optional<ItemStackTemplate> getContainerTemplate() {
        return container;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    @Override
    public boolean matches(
        CheeseVatRecipeInput input,
        Level level
    ) {
        for (int i = 0; i < ingredients.size(); i++) {
            if (!ingredients.get(i).test(input.getItem(i))) {
                return false;
            }
        }

        // Gli slot ingredienti non usati devono essere vuoti.
        for (int i = ingredients.size(); i < 3; i++) {
            if (!input.getItem(i).isEmpty()) {
                return false;
            }
        }

        // Se la ricetta richiede un contenitore,
        // deve essere presente nello slot 3.
        if (container.isPresent()) {
            ItemStack required = container.get().create();
            ItemStack provided = input.container();

            if (provided.isEmpty()) {
                return false;
            }

            if (!ItemStack.isSameItemSameComponents(
                provided,
                required
            )) {
                return false;
            }

            if (provided.getCount() < required.getCount()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack assemble(CheeseVatRecipeInput input) {
        return result.create();
    }

    @Override
    public RecipeSerializer<? extends Recipe<CheeseVatRecipeInput>>
    getSerializer() {
        return ModRecipes.CHEESE_VAT_SERIALIZER;
    }

    @Override
    public RecipeType<? extends Recipe<CheeseVatRecipeInput>>
    getType() {
        return ModRecipes.CHEESE_VAT_TYPE;
    }

    @Override
    public @org.jetbrains.annotations.Nullable
    net.minecraft.world.item.crafting.RecipeBookCategory
    recipeBookCategory() {
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

    private static CheeseVatRecipe fromNetwork(
        RegistryFriendlyByteBuf buffer
    ) {
        List<Ingredient> ingredients =
            Ingredient.CONTENTS_STREAM_CODEC
                .apply(ByteBufCodecs.list())
                .decode(buffer);

        ItemStackTemplate result =
            ItemStackTemplate.STREAM_CODEC.decode(buffer);

        Optional<ItemStackTemplate> container =
            ByteBufCodecs
                .optional(ItemStackTemplate.STREAM_CODEC)
                .decode(buffer);

        int cookingTime = buffer.readVarInt();

        return new CheeseVatRecipe(
            ingredients,
            result,
            container,
            cookingTime
        );
    }

    private static void toNetwork(
        RegistryFriendlyByteBuf buffer,
        CheeseVatRecipe recipe
    ) {
        Ingredient.CONTENTS_STREAM_CODEC
            .apply(ByteBufCodecs.list())
            .encode(buffer, recipe.ingredients);

        ItemStackTemplate.STREAM_CODEC.encode(
            buffer,
            recipe.result
        );

        ByteBufCodecs
            .optional(ItemStackTemplate.STREAM_CODEC)
            .encode(buffer, recipe.container);

        buffer.writeVarInt(recipe.cookingTime);
    }
}