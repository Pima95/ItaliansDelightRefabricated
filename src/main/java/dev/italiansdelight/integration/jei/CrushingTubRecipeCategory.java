package dev.italiansdelight.integration.jei;

import dev.italiansdelight.common.registry.ModBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/**
 * Compact Crushing Tub JEI layout with no full-screen painted container or clock.
 *
 * The transparent 176x60 GUI contains only an input frame, arrow and fluid frame.
 * A separate 22x22 sprite in the same PNG draws the optional byproduct frame.
 * Slot contents are 3 pixels inside the frame and never touch the arrow.
 */
public final class CrushingTubRecipeCategory implements IRecipeCategory<CrushingTubJeiRecipe> {
    private static final int WIDTH = 176;
    private static final int HEIGHT = 60;
    private static final int INPUT_X = 14;
    private static final int FLUID_X = 111;
    private static final int BYPRODUCT_X = 145;
    private static final int SLOT_Y = 22;
    private static final int BYPRODUCT_FRAME_X = 142;
    private static final int BYPRODUCT_FRAME_Y = 19;
    private static final long DROPLETS_PER_MB = FluidConstants.BUCKET / 1000L;

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
        "italiansdelight", "textures/gui/jei/crushing_tub.png");

    private final IDrawable background;
    private final IDrawable byproductFrame;
    private final IDrawable icon;

    public CrushingTubRecipeCategory(IGuiHelper helper) {
        background = helper.createDrawable(TEXTURE, 0, 0, WIDTH, HEIGHT);
        // This sprite is outside the visible background and only drawn on demand.
        byproductFrame = helper.createDrawable(TEXTURE, 0, 64, 22, 22);
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

    @Override public int getWidth() { return WIDTH; }
    @Override public int getHeight() { return HEIGHT; }
    @Override public IDrawable getIcon() { return icon; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CrushingTubJeiRecipe recipe, IFocusGroup focuses) {
        builder.addInputSlot(INPUT_X, SLOT_Y).add(recipe.ingredient());

        long fluidAmount = recipe.amount() * DROPLETS_PER_MB;
        // One stomp outputs 250 mB: render its result as a full fluid slot,
        // rather than a tiny 1/4-height strip for a 1000 mB bucket.
        builder.addOutputSlot(FLUID_X, SLOT_Y)
            .setFluidRenderer(fluidAmount, false, 16, 16)
            .add(recipe.fluid(), fluidAmount);

        if (!recipe.byproduct().isEmpty()) {
            builder.addOutputSlot(BYPRODUCT_X, SLOT_Y).add(recipe.byproduct());
        }
    }

    @Override
    public void draw(CrushingTubJeiRecipe recipe, IRecipeSlotsView slots,
                     GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        background.draw(graphics);
        if (!recipe.byproduct().isEmpty()) {
            byproductFrame.draw(graphics, BYPRODUCT_FRAME_X, BYPRODUCT_FRAME_Y);
        }
    }
}
