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

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/**
 * JEI category for products that process while hanging from the Hanging Hook:
 * Provolone, Scamorza and cured meats.
 */
public final class HangingAgingDryingRecipeCategory
    implements IRecipeCategory<CheeseAgingJeiRecipe> {

    private static final int WIDTH = 111;
    private static final int HEIGHT = 45;

    private static final int INPUT_X = 9;
    private static final int INPUT_Y = 26;

    private static final int OUTPUT_X = 93;
    private static final int OUTPUT_Y = 26;

    private static final int CLOCK_X = 49;
    private static final int CLOCK_Y = 0;
    private static final int CLOCK_WIDTH = 20;
    private static final int CLOCK_HEIGHT = 20;

    private final IDrawable background;
    private final IDrawable icon;

    public HangingAgingDryingRecipeCategory(
        IGuiHelper guiHelper
    ) {
        this.background =
            guiHelper.createDrawable(
                Identifier.fromNamespaceAndPath(
                    "italiansdelight",
                    "textures/gui/jei/aging.png"
                ),
                0,
                0,
                WIDTH,
                HEIGHT
            );

        this.icon =
            guiHelper.createDrawableItemLike(
                ModBlocks.CHEESE_HOOK
            );
    }

    @Override
    public IRecipeType<CheeseAgingJeiRecipe>
    getRecipeType() {
        return CheeseAgingJeiRecipeTypes.HANGING_AGING_DRYING;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(
            "gui.italiansdelight.jei.hanging_aging_drying"
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
        CheeseAgingJeiRecipe recipe,
        IFocusGroup focuses
    ) {
        builder.addInputSlot(
            INPUT_X,
            INPUT_Y
        ).add(
            recipe.ingredient()
        );

        builder.addOutputSlot(
            OUTPUT_X,
            OUTPUT_Y
        ).add(
            recipe.result()
        );
    }

    @Override
    public void draw(
        CheeseAgingJeiRecipe recipe,
        IRecipeSlotsView recipeSlotsView,
        GuiGraphicsExtractor guiGraphics,
        double mouseX,
        double mouseY
    ) {
        background.draw(
            guiGraphics
        );
    }

    @Override
    public void getTooltip(
        ITooltipBuilder tooltipBuilder,
        CheeseAgingJeiRecipe recipe,
        IRecipeSlotsView recipeSlotsView,
        double mouseX,
        double mouseY
    ) {
        if (
            mouseX < CLOCK_X
            || mouseX >= CLOCK_X + CLOCK_WIDTH
            || mouseY < CLOCK_Y
            || mouseY >= CLOCK_Y + CLOCK_HEIGHT
        ) {
            return;
        }

        tooltipBuilder.add(
            Component.translatable(
                recipe.process()
                    == CheeseAgingJeiRecipe.Process.DRYING
                    ? "gui.italiansdelight.jei.cheese_aging.process.drying"
                    : "gui.italiansdelight.jei.cheese_aging.process.aging"
            )
        );

        int minutes =
            Math.max(
                1,
                (recipe.processingTime() + 1199)
                    / 1200
            );

        tooltipBuilder.add(
            Component.translatable(
                "gui.italiansdelight.jei.cheese_aging.time",
                minutes
            )
        );
    }
}
