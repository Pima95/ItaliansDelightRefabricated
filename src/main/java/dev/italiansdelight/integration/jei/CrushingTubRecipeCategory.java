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

/** JEI display of the actual stomping results, using a dedicated clock-free GUI. */
public final class CrushingTubRecipeCategory implements IRecipeCategory<CrushingTubJeiRecipe> {
    private static final int WIDTH = 176;
    private static final int HEIGHT = 60;
    // Stomping recipes express amounts in mB; Fabric/JEI expects droplets.
    private static final long DROPLETS_PER_MB = FluidConstants.BUCKET / 1000L;
    private final IDrawable background;
    private final IDrawable icon;

    public CrushingTubRecipeCategory(IGuiHelper helper) {
        background = helper.createDrawable(
            Identifier.fromNamespaceAndPath("italiansdelight", "textures/gui/jei/crushing_tub.png"),
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
        // One input to the left of the arrow; all outputs remain to its right.
        builder.addInputSlot(13, 27).add(recipe.ingredient());

        // Rendering capacity is one bucket, and the supplied amount is 250 mB in droplets.
        builder.addOutputSlot(120, 27)
            .setFluidRenderer(FluidConstants.BUCKET, false, 16, 16)
            .add(recipe.fluid(), recipe.amount() * DROPLETS_PER_MB);

        // Optional byproduct has a dedicated slot; never rendered over the arrow.
        if (!recipe.byproduct().isEmpty()) {
            builder.addOutputSlot(148, 27).add(recipe.byproduct());
        }
    }

    @Override
    public void draw(CrushingTubJeiRecipe recipe, IRecipeSlotsView slots,
                     GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        background.draw(graphics);
    }
}
