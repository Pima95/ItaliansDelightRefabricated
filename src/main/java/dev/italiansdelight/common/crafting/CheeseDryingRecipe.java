package dev.italiansdelight.common.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.italiansdelight.common.registry.ModRecipes;

import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

/**
 * Data-driven hanging/drying transformation.
 *
 * Registration is implemented now so JEI and the future Cheese Hook can use
 * the same recipe source. The hook itself is a later implementation step.
 */
public final class CheeseDryingRecipe
    implements Recipe<SingleRecipeInput> {

    public static final MapCodec<CheeseDryingRecipe> CODEC =
        RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                Ingredient.CODEC
                    .fieldOf("ingredient")
                    .forGetter(CheeseDryingRecipe::getIngredient),

                ItemStack.STRICT_CODEC
                    .fieldOf("result")
                    .forGetter(CheeseDryingRecipe::getResultTemplate),

                Codec.INT
                    .fieldOf("dryingtime")
                    .forGetter(CheeseDryingRecipe::getDryingTime)
            ).apply(
                instance,
                CheeseDryingRecipe::new
            )
        );

    public static final StreamCodec<
        RegistryFriendlyByteBuf,
        CheeseDryingRecipe
    > STREAM_CODEC =
        StreamCodec.of(
            CheeseDryingRecipe::toNetwork,
            CheeseDryingRecipe::fromNetwork
        );

    public static final RecipeSerializer<CheeseDryingRecipe>
        SERIALIZER =
            new RecipeSerializer<CheeseDryingRecipe>() {
                @Override public MapCodec<CheeseDryingRecipe> codec() { return CODEC; }
                @Override public StreamCodec<RegistryFriendlyByteBuf, CheeseDryingRecipe> streamCodec() { return STREAM_CODEC; }
            };

    private final Ingredient ingredient;
    private final ItemStack result;
    private final int dryingTime;

    public CheeseDryingRecipe(
        Ingredient ingredient,
        ItemStack result,
        int dryingTime
    ) {
        this.ingredient = ingredient;
        this.result = result.copy();
        this.dryingTime =
            Math.max(
                1,
                dryingTime
            );
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public ItemStack getResultTemplate() {
        return result.copy();
    }

    public int getDryingTime() {
        return dryingTime;
    }

    @Override
    public boolean matches(
        SingleRecipeInput input,
        Level level
    ) {
        return ingredient.test(
            input.item()
        );
    }

    public ItemStack assemble(
        SingleRecipeInput input
    ) {
        return result.copy();
    }

    @Override
    public RecipeSerializer<? extends Recipe<SingleRecipeInput>>
    getSerializer() {
        return ModRecipes.CHEESE_DRYING_SERIALIZER;
    }

    @Override
    public RecipeType<? extends Recipe<SingleRecipeInput>>
    getType() {
        return ModRecipes.CHEESE_DRYING_TYPE;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
        return assemble(input);
    }


    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String getGroup() {
        return "cheese_drying";
    }

    private static CheeseDryingRecipe fromNetwork(
        RegistryFriendlyByteBuf buffer
    ) {
        Ingredient ingredient =
            Ingredient.CONTENTS_STREAM_CODEC
                .decode(buffer);

        ItemStack result =
            ItemStack.STREAM_CODEC
                .decode(buffer);

        int dryingTime =
            buffer.readVarInt();

        return new CheeseDryingRecipe(
            ingredient,
            result,
            dryingTime
        );
    }

    private static void toNetwork(
        RegistryFriendlyByteBuf buffer,
        CheeseDryingRecipe recipe
    ) {
        Ingredient.CONTENTS_STREAM_CODEC
            .encode(
                buffer,
                recipe.ingredient
            );

        ItemStack.STREAM_CODEC
            .encode(
                buffer,
                recipe.result
            );

        buffer.writeVarInt(
            recipe.dryingTime
        );
    }
}
