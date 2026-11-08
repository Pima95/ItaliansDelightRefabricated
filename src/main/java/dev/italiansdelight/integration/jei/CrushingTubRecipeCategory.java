package dev.italiansdelight.integration.jei;

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

/** Displays real Crushing Tub outputs without adding new gameplay recipes. */
public final class CrushingTubRecipeCategory implements IRecipeCategory<CrushingTubJeiRecipe> {
    private static final int WIDTH = 111;
    private static final int HEIGHT = 45;
    private final IDrawable background;
    private final IDrawable icon;

    public CrushingTubRecipeCategory(IGuiHelper helper) {
        background = helper.createDrawable(
            Identifier.fromNamespaceAndPath("italiansdelight", "textures/gui/jei/aging.png"),
            0, 0, WIDTH, HEIGHT);
        icon = helper.createDrawableItemLike(ModBlocks.CRUSHING_TUB);
    }

    @Override
    public IRecipeType<CrushingTubJeiRecipe> getRecipeType() {
        return CrushingTubJeiRecipeTypes.CRUSHING_TUB;
    }

    @Override
    public Component getTitle() {
        return ModBlocks.CRUSHING_TUB.getName();
    }

    @Override
    public int getWidth() { return WIDTH; }

    @Override
    public int getHeight() { return HEIGHT; }

    @Override
    public IDrawable getIcon() { return icon; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CrushingTubJeiRecipe recipe, IFocusGroup focuses) {
        builder.addInputSlot(9, 26).add(recipe.ingredient());
        builder.addOutputSlot(93, 26)
            .setFluidRenderer(1000L, false, 16, 16)
            .add(recipe.fluid(), recipe.amount());
        if (!recipe.byproduct().isEmpty()) {
            builder.addOutputSlot(69, 26).add(recipe.byproduct());
        }
    }

    @Override
    public void draw(CrushingTubJeiRecipe recipe, IRecipeSlotsView slots,
                     GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        background.draw(graphics);
    }
}
