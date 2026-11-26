package dev.italiansdelight.integration.jei;

import dev.italiansdelight.common.registry.ModBlocks;
import dev.italiansdelight.common.registry.ModFluids;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * Restore the owner's original Rustic JEI art at native 130x80 pixels.
 * Slot/tank positions and progress/bubble sprites match Rustic's
 * BrewingRecipeCategory (40,24 input; 94,24 output; 4,32 optional culture).
 * No paragraphs are painted over the GUI; details are hover-only tooltips.
 */
public final class BrewingBarrelJeiRecipeCategory
        implements IRecipeCategory<BrewingBarrelJeiGuide> {
    private static final int WIDTH = 130;
    private static final int HEIGHT = 80;
    private static final long MAX_BATCH = FluidConstants.BUCKET * 8;
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
        "italiansdelight", "textures/gui/jei/brewing_barrel.png");

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableAnimated bubbles;
    private final IDrawableAnimated arrow;
    private final IDrawableAnimated cultureArrow;

    public BrewingBarrelJeiRecipeCategory(IGuiHelper helper) {
        background = helper.createDrawable(TEXTURE, 0, 0, WIDTH, HEIGHT);
        icon = helper.createDrawableItemLike(ModBlocks.BREWING_BARREL);
        // Rustic's sprite coordinates are already present in the approved
        // 256x256 JEI texture. Preview cycles match Rustic's JEI timings.
        bubbles = helper.drawableBuilder(TEXTURE, 130, 0, 11, 28)
            .buildAnimated(100, IDrawableAnimated.StartDirection.BOTTOM, false);
        arrow = helper.drawableBuilder(TEXTURE, 130, 28, 24, 16)
            .buildAnimated(1200, IDrawableAnimated.StartDirection.LEFT, false);
        cultureArrow = helper.drawableBuilder(TEXTURE, 130, 44, 10, 10)
            .buildAnimated(1200, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override public IRecipeType<BrewingBarrelJeiGuide> getRecipeType() {
        return BrewingBarrelJeiRecipeTypes.BARREL_GUI_GUIDE;
    }

    @Override public Component getTitle() {
        return Component.translatable("gui.italiansdelight.jei.brewing_barrel.title");
    }

    @Override public int getWidth() { return WIDTH; }
    @Override public int getHeight() { return HEIGHT; }
    @Override public IDrawable getIcon() { return icon; }

    @Override public void setRecipe(IRecipeLayoutBuilder builder,
                                   BrewingBarrelJeiGuide recipe, IFocusGroup focuses) {
        builder.addInputSlot(40, 24)
            .setFluidRenderer(MAX_BATCH, false, 16, 32)
            .add(recipe.input(), MAX_BATCH);
        builder.addOutputSlot(94, 24)
            .setFluidRenderer(MAX_BATCH, false, 16, 32)
            .add(recipe.output(), MAX_BATCH);
        if (recipe.requiresCulture()) {
            // Cider is selected by inoculating apple juice with wine culture;
            // otherwise the old unseeded apple-vinegar recipe remains active.
            builder.addInputSlot(4, 32)
                .setFluidRenderer(FluidConstants.BOTTLE, false, 16, 16)
                .add(ModFluids.WINE, FluidConstants.BOTTLE)
                .add(ModFluids.WHITE_WINE, FluidConstants.BOTTLE)
                .add(ModFluids.CHERRY_WINE, FluidConstants.BOTTLE);
        }
    }

    @Override public void draw(BrewingBarrelJeiGuide recipe, IRecipeSlotsView slots,
                               GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        background.draw(graphics);
        bubbles.draw(graphics, 117, 26);
        arrow.draw(graphics, 63, 32);
        if (!recipe.isVinegar()) {
            cultureArrow.draw(graphics, 23, 35);
        }
    }

    @Override public void getTooltip(ITooltipBuilder tooltip, BrewingBarrelJeiGuide recipe,
                                     IRecipeSlotsView slots, double mouseX, double mouseY) {
        if (mouseX >= 62 && mouseX < 89 && mouseY >= 27 && mouseY < 51) {
            tooltip.add(Component.translatable(
                "gui.italiansdelight.jei.brewing_barrel.duration"));
        } else if (mouseX >= 4 && mouseX < 21 && mouseY >= 32 && mouseY < 49) {
            tooltip.add(Component.translatable(recipe.requiresCulture()
                ? "gui.italiansdelight.jei.brewing_barrel.required_culture"
                : recipe.isVinegar()
                    ? "gui.italiansdelight.jei.brewing_barrel.no_culture"
                    : "gui.italiansdelight.jei.brewing_barrel.optional_culture"));
        }
    }
}
