package dev.italiansdelight.integration.jei;

import dev.italiansdelight.common.registry.ModItems;

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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;

/**
 * JEI presentation of salt production from a full vanilla water cauldron.
 *
 * It deliberately reuses the compact aging background so passive processes
 * share one visual language across cheese, cured meats, and salt.
 */
public final class SaltEvaporationRecipeCategory
    implements IRecipeCategory<SaltEvaporationJeiRecipe> {

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

    private static final int FLUID_SLOT_SIZE = 16;
    private static final long FULL_CAULDRON_WATER_MB = 1000L;

    private final IDrawable background;
    private final IDrawable icon;

    public SaltEvaporationRecipeCategory(
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
                Blocks.CAULDRON
            );
    }

    @Override
    public IRecipeType<SaltEvaporationJeiRecipe>
    getRecipeType() {
        return SaltEvaporationJeiRecipeTypes.SALT_EVAPORATION;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(
            "gui.italiansdelight.jei.salt_evaporation"
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
        SaltEvaporationJeiRecipe recipe,
        IFocusGroup focuses
    ) {
        // Render actual water as a JEI fluid ingredient, not a bucket/bottle.
        builder.addInputSlot(
            INPUT_X,
            INPUT_Y
        )
            .setFluidRenderer(
                FULL_CAULDRON_WATER_MB,
                false,
                FLUID_SLOT_SIZE,
                FLUID_SLOT_SIZE
            )
            .add(
                Fluids.WATER,
                recipe.waterAmount()
            );

        // The real process yields a random 3-7 items, so the slot stays at one
        // representative Salt item and the exact range is explained on hover.
        builder.addOutputSlot(
            OUTPUT_X,
            OUTPUT_Y
        ).add(
            ModItems.SALT
        );
    }

    @Override
    public void draw(
        SaltEvaporationJeiRecipe recipe,
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
        SaltEvaporationJeiRecipe recipe,
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
                "gui.italiansdelight.jei.salt_evaporation.process"
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

        tooltipBuilder.add(
            Component.translatable(
                "gui.italiansdelight.jei.salt_evaporation.yield",
                recipe.minSalt(),
                recipe.maxSalt()
            )
        );

        tooltipBuilder.add(
            Component.translatable(
                "gui.italiansdelight.jei.salt_evaporation.conditions"
            )
        );
    }
}
