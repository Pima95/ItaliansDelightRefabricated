package dev.italiansdelight.common.crafting;

import java.util.List;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.italiansdelight.common.registry.ModRecipes;

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

/**
 * Defines a Cheese Vat recipe, including shapeless matching, JSON serialization, and network synchronization.
 */

public class CheeseVatRecipe implements Recipe<CheeseVatRecipeInput> {

    // -------------------- Datapack serialization --------------------
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

    // -------------------- Client-server synchronization --------------------
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

    // -------------------- Immutable recipe data --------------------
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

    /**
     * Finds the machine input slots that match this recipe.
     * Ingredient order is intentionally shapeless, like Farmer's Delight's Cooking Pot.
     */
    public int[] findMatchingIngredientSlots(
        CheeseVatRecipeInput input
    ) {
        int nonEmptySlots = 0;

        for (int slot = 0; slot < 3; slot++) {
            if (!input.getItem(slot).isEmpty()) {
                nonEmptySlots++;
            }
        }

        if (nonEmptySlots != ingredients.size()) {
            return null;
        }

        int[] matchedSlots =
            new int[ingredients.size()];

        boolean[] usedSlots =
            new boolean[3];

        if (findMatches(
            input,
            0,
            usedSlots,
            matchedSlots
        )) {
            return matchedSlots;
        }

        return null;
    }

    // Backtracking: each ingredient is tested against an unused slot,
    // so the ingredient order across the three inputs does not matter.
    private boolean findMatches(
        CheeseVatRecipeInput input,
        int ingredientIndex,
        boolean[] usedSlots,
        int[] matchedSlots
    ) {
        if (ingredientIndex >= ingredients.size()) {
            return true;
        }

        Ingredient ingredient =
            ingredients.get(ingredientIndex);

        for (int slot = 0; slot < 3; slot++) {
            if (usedSlots[slot]) {
                continue;
            }

            ItemStack stack =
                input.getItem(slot);

            if (
                !stack.isEmpty()
                && ingredient.test(stack)
            ) {
                usedSlots[slot] = true;
                matchedSlots[ingredientIndex] = slot;

                if (findMatches(
                    input,
                    ingredientIndex + 1,
                    usedSlots,
                    matchedSlots
                )) {
                    return true;
                }

                usedSlots[slot] = false;
            }
        }

        return false;
    }

    // -------------------- Minecraft Recipe contract --------------------
    @Override
    public boolean matches(
        CheeseVatRecipeInput input,
        Level level
    ) {
        /*
         * The container is deliberately NOT part of recipe matching.
         * The vat cooks the ingredients first and stores the completed
         * serving as a preview until the required container is supplied.
         */
        return findMatchingIngredientSlots(input) != null;
    }

    @Override
    public ItemStack assemble(
        CheeseVatRecipeInput input
    ) {
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

    // -------------------- Network codec --------------------
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