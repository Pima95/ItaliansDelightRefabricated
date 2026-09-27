package dev.italiansdelight.integration.jei;

import dev.italiansdelight.common.crafting.CheeseVatRecipe;
import dev.italiansdelight.common.registry.ModBlocks;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;

/**
 * Describes the Cheese Vat JEI recipe category layout, slots, icon, and background.
 */

public class CheeseVatRecipeCategory
    implements IRecipeCategory<RecipeHolder<CheeseVatRecipe>> {

    public static final Identifier UID =
        Identifier.fromNamespaceAndPath(
            "italiansdelight",
            "cheese_vat"
        );

    public static final IRecipeType<RecipeHolder<CheeseVatRecipe>> RECIPE_TYPE =
        CheeseVatJeiRecipeTypes.CHEESE_VAT;

    private static final int WIDTH = 145;
    private static final int HEIGHT = 58;

    private final IDrawable icon;
    private final IDrawable background;
    private final IDrawable wheyBackground;

    public CheeseVatRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemLike(
            ModBlocks.CHEESE_VAT
        );

        this.background = guiHelper.createDrawable(
            Identifier.fromNamespaceAndPath(
                "italiansdelight",
                "textures/gui/jei/cheese_vat.png"
            ),
            0,
            0,
            WIDTH,
            HEIGHT
        );

        this.wheyBackground = guiHelper.createDrawable(
            Identifier.fromNamespaceAndPath(
                "italiansdelight",
                "textures/gui/jei/cheese_vat_whey.png"
            ),
            0,
            0,
            WIDTH,
            HEIGHT
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
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
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

        // Ingredients
        for (
            int i = 0;
            i < recipe.getIngredientsList().size();
            i++
        ) {
            builder.addInputSlot(
                1 + i * 18,
                12
            ).add(
                recipe
                    .getIngredientsList()
                    .get(i)
            );
        }

        // Optional container
        recipe.getContainerTemplate().ifPresent(
            container ->
                builder.addInputSlot(
                    63,
                    41
                ).add(container)
        );

        // Result
        builder.addOutputSlot(
            95,
            41
        ).add(
            recipe.getResultTemplate()
        );
    }

    @Override
    public void draw(
        RecipeHolder<CheeseVatRecipe> recipe,
        IRecipeSlotsView recipeSlotsView,
        GuiGraphicsExtractor guiGraphics,
        double mouseX,
        double mouseY
    ) {
        if (recipe.value().usesWheyFromTank()) {
            wheyBackground.draw(guiGraphics);
        } else {
            background.draw(guiGraphics);
        }
    }
}