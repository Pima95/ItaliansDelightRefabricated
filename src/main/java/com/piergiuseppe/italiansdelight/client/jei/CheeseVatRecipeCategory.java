package com.piergiuseppe.italiansdelight.client.jei;

import com.piergiuseppe.italiansdelight.recipe.CheeseVatRecipe;
import com.piergiuseppe.italiansdelight.registry.ModRecipes;
import com.piergiuseppe.italiansdelight.registry.ModBlocks;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;

public class CheeseVatRecipeCategory
    implements IRecipeCategory<RecipeHolder<CheeseVatRecipe>> {

    public static final Identifier UID =
        Identifier.fromNamespaceAndPath("italiansdelight", "cheese_vat");

    public static final IRecipeType<RecipeHolder<CheeseVatRecipe>> RECIPE_TYPE =
        CheeseVatJeiRecipeTypes.CHEESE_VAT;

    private final IDrawable icon;

    public CheeseVatRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemLike(
            ModBlocks.CHEESE_VAT
        );
    }

    @Override
    public IRecipeType<RecipeHolder<CheeseVatRecipe>> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(
            "block.italiansdelight.cheese_vat"
        );
    }

    @Override
    public int getWidth() {
        return 100;
    }

    @Override
    public int getHeight() {
        return 50;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(
        IRecipeLayoutBuilder builder,
        RecipeHolder<CheeseVatRecipe> holder,
        IFocusGroup focuses
    ) {
        CheeseVatRecipe recipe = holder.value();

        for (int i = 0; i < recipe.getIngredientsList().size(); i++) {
            builder.addInputSlot(10 + i * 20, 18)
                .addIngredients(recipe.getIngredientsList().get(i));
        }

        builder.addOutputSlot(70, 18)
            .addItemStack(recipe.getResultTemplate().create());
    }
}