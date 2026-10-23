package dev.italiansdelight.common.crafting;

import com.mojang.serialization.MapCodec;
import dev.italiansdelight.common.pizza.PizzaBaking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeBookCategories;

/** Smoker counterpart of PizzaSmeltingRecipe, with the same lossless result. */
public final class PizzaSmokingRecipe extends AbstractCookingRecipe {
    public static final MapCodec<PizzaSmokingRecipe> CODEC =
            AbstractCookingRecipe.cookingMapCodec(PizzaSmokingRecipe::new, 600);
    public static final StreamCodec<RegistryFriendlyByteBuf, PizzaSmokingRecipe> STREAM_CODEC =
            AbstractCookingRecipe.cookingStreamCodec(PizzaSmokingRecipe::new);
    public static final RecipeSerializer<PizzaSmokingRecipe> SERIALIZER =
            new RecipeSerializer<>(CODEC, STREAM_CODEC);

    public PizzaSmokingRecipe(Recipe.CommonInfo info,
                              AbstractCookingRecipe.CookingBookInfo book,
                              Ingredient ingredient, ItemStackTemplate result,
                              float experience, int cookingTime) {
        super(info, book, ingredient, result, experience, cookingTime);
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input) {
        return PizzaBaking.previewCookedPizza(input.item());
    }

    @Override
    protected Item furnaceIcon() {
        return Items.SMOKER;
    }

    @Override
    public RecipeType<? extends AbstractCookingRecipe> getType() {
        return RecipeType.SMOKING;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.SMOKER_FOOD;
    }

    @Override
    public RecipeSerializer<PizzaSmokingRecipe> getSerializer() {
        return SERIALIZER;
    }
}
