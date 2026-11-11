package dev.italiansdelight.integration.jei;

import dev.italiansdelight.common.registry.ModBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
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
 * Crushing Tub layout derived from the original aging.png JEI texture.
 * aging.png remains intact for cheese aging and salt evaporation.
 *
 * The main art occupies (0,0)..(150,44) in a 256x256 texture;
 * an optional 18x18 output-slot sprite is stored at (0,64).
 */
public final class CrushingTubRecipeCategory implements IRecipeCategory<CrushingTubJeiRecipe> {
    private static final int WIDTH = 151;
    private static final int HEIGHT = 45;
    private static final long DROPLETS_PER_MB = FluidConstants.BUCKET / 1000L;
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
        "italiansdelight", "textures/gui/jei/crushing_tub.png");

    private final IDrawable background;
    private final IDrawable byproductFrame;
    private final IDrawable icon;

    public CrushingTubRecipeCategory(IGuiHelper helper) {
        background = helper.createDrawable(TEXTURE, 0, 0, WIDTH, HEIGHT);
        byproductFrame = helper.createDrawable(TEXTURE, 0, 64, 18, 18);
        icon = helper.createDrawableItemLike(ModBlocks.CRUSHING_TUB);
    }

    @Override
    public IRecipeType<CrushingTubJeiRecipe> getRecipeType() {
        return CrushingTubJeiRecipeTypes.CRUSHING_TUB;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gui.italiansdelight.jei.crushing_tub");
    }

    @Override public int getWidth() { return WIDTH; }
    @Override public int getHeight() { return HEIGHT; }
    @Override public IDrawable getIcon() { return icon; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CrushingTubJeiRecipe recipe, IFocusGroup focuses) {
        // The unscaled aging.png has 18x18 frames at (8,25) and (92,25).
        builder.addInputSlot(9, 26).add(recipe.ingredient());

        long fluidAmount = recipe.amount() * DROPLETS_PER_MB;
        if (!recipe.bottlePreview().isEmpty()) {
            // Virtual honey has no registry Fluid: preview the bottle obtainable
            // AFTER transferring 250 mB from the tub into an empty glass bottle.
            builder.addOutputSlot(93, 26).add(recipe.bottlePreview());
        } else {
            builder.addOutputSlot(93, 26)
                .setFluidRenderer(fluidAmount, false, 16, 16)
                .add(recipe.fluid(), fluidAmount);
        }

        if (!recipe.byproduct().isEmpty()) {
            builder.addOutputSlot(126, 26).add(recipe.byproduct());
        }
    }

    @Override
    public void draw(CrushingTubJeiRecipe recipe, IRecipeSlotsView slots,
                     GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        background.draw(graphics);
        if (!recipe.byproduct().isEmpty()) {
            byproductFrame.draw(graphics, 125, 25);
        }
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltipBuilder, CrushingTubJeiRecipe recipe,
                           IRecipeSlotsView slots, double mouseX, double mouseY) {
        // This only annotates the honey output; all approved artwork/coordinates
        // and real-fluid JEI slots remain unchanged.
        if (!recipe.bottlePreview().isEmpty()
                && mouseX >= 92 && mouseX < 110 && mouseY >= 25 && mouseY < 43) {
            tooltipBuilder.add(Component.translatable(
                "gui.italiansdelight.jei.crushing_tub.honey_extraction"));
        }
    }
}
