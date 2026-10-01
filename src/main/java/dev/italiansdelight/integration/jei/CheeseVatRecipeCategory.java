package dev.italiansdelight.integration.jei;

import java.util.HashMap;
import java.util.Map;

import dev.italiansdelight.client.gui.WheyTankRenderer;
import dev.italiansdelight.common.block.entity.CheeseVatBlockEntity;
import dev.italiansdelight.common.crafting.CheeseVatRecipe;
import dev.italiansdelight.common.registry.ModBlocks;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
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
 * Describes the Cheese Vat JEI recipe category layout, slots, animation,
 * processing information, and whey requirements.
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

    private static final int ARROW_X = 60;
    private static final int ARROW_Y = 11;

    private static final int HEAT_X = 18;
    private static final int HEAT_Y = 41;

    private static final int TIME_X = 64;
    private static final int TIME_Y = 4;

    private static final int EXPERIENCE_X = 63;
    private static final int EXPERIENCE_Y = 23;

    // The JEI texture keeps the same 16x32 tank interior used by the normal
    // Cheese Vat screen, moved to the right-hand side of the compact layout.
    private static final int WHEY_TANK_X = 128;
    private static final int WHEY_TANK_Y = 1;
    private static final int WHEY_TANK_WIDTH = 16;
    private static final int WHEY_TANK_HEIGHT = 32;

    private static final int WHEY_TANK_HOVER_X = 127;
    private static final int WHEY_TANK_HOVER_Y = 0;
    private static final int WHEY_TANK_HOVER_WIDTH = 18;
    private static final int WHEY_TANK_HOVER_HEIGHT = 34;

    private final IGuiHelper guiHelper;
    private final Identifier interfaceImage;

    private final IDrawable icon;
    private final IDrawable background;
    private final IDrawable wheyBackground;
    private final IDrawable heatIndicator;
    private final IDrawable timeIcon;
    private final IDrawable experienceIcon;

    // Cooking times are data-driven, so each distinct recipe duration gets an
    // animation whose cycle length matches the real machine recipe.
    private final Map<Integer, IDrawableAnimated> arrowsByCookingTime =
        new HashMap<>();

    public CheeseVatRecipeCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;

        this.interfaceImage =
            Identifier.fromNamespaceAndPath(
                "italiansdelight",
                "textures/gui/cheese_vat.png"
            );

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

        // These sprites intentionally mirror Farmer's Delight's Cooking Pot
        // JEI presentation: lit heat source, clock, XP, and animated progress.
        this.heatIndicator = guiHelper.createDrawable(
            interfaceImage,
            176,
            0,
            17,
            15
        );

        this.timeIcon = guiHelper.createDrawable(
            interfaceImage,
            176,
            32,
            8,
            11
        );

        this.experienceIcon = guiHelper.createDrawable(
            interfaceImage,
            176,
            43,
            9,
            9
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
        RecipeHolder<CheeseVatRecipe> holder,
        IRecipeSlotsView recipeSlotsView,
        GuiGraphicsExtractor guiGraphics,
        double mouseX,
        double mouseY
    ) {
        CheeseVatRecipe recipe = holder.value();

        if (recipe.usesWheyFromTank()) {
            wheyBackground.draw(guiGraphics);
            drawRequiredWhey(guiGraphics, recipe.getWheyAmount());
        } else {
            background.draw(guiGraphics);
        }

        // JEI represents a valid recipe, so the heat source is shown lit just
        // like Farmer's Delight's Cooking Pot recipe category.
        heatIndicator.draw(
            guiGraphics,
            HEAT_X,
            HEAT_Y
        );

        getArrow(recipe.getCookingTime()).draw(
            guiGraphics,
            ARROW_X,
            ARROW_Y
        );

        timeIcon.draw(
            guiGraphics,
            TIME_X,
            TIME_Y
        );

        if (recipe.getExperience() > 0.0F) {
            experienceIcon.draw(
                guiGraphics,
                EXPERIENCE_X,
                EXPERIENCE_Y
            );
        }
    }

    private void drawRequiredWhey(
        GuiGraphicsExtractor guiGraphics,
        int wheyAmount
    ) {
        if (wheyAmount <= 0) {
            return;
        }

        int wheyLevel =
            Math.max(
                1,
                Math.min(
                    WHEY_TANK_HEIGHT,
                    (int) Math.ceil(
                        (double) wheyAmount
                            * WHEY_TANK_HEIGHT
                            / CheeseVatBlockEntity.WHEY_TANK_CAPACITY
                    )
                )
            );

        WheyTankRenderer.draw(
            guiGraphics,
            WHEY_TANK_X,
            WHEY_TANK_Y,
            WHEY_TANK_WIDTH,
            WHEY_TANK_HEIGHT,
            wheyLevel
        );
    }

    private IDrawableAnimated getArrow(
        int cookingTime
    ) {
        int animationTicks =
            Math.max(
                1,
                cookingTime
            );

        return arrowsByCookingTime.computeIfAbsent(
            animationTicks,
            ticks ->
                guiHelper.drawableBuilder(
                    interfaceImage,
                    176,
                    15,
                    24,
                    17
                ).buildAnimated(
                    ticks,
                    IDrawableAnimated.StartDirection.LEFT,
                    false
                )
        );
    }

    @Override
    public void getTooltip(
        ITooltipBuilder tooltipBuilder,
        RecipeHolder<CheeseVatRecipe> holder,
        IRecipeSlotsView recipeSlotsView,
        double mouseX,
        double mouseY
    ) {
        CheeseVatRecipe recipe = holder.value();

        // Same compact time/experience hover area used by Farmer's Delight.
        if (
            isCursorInsideBounds(
                61,
                4,
                22,
                28,
                mouseX,
                mouseY
            )
        ) {
            int cookTime = recipe.getCookingTime();

            if (cookTime > 0) {
                int cookTimeSeconds =
                    Math.max(
                        1,
                        (cookTime + 19) / 20
                    );

                tooltipBuilder.add(
                    Component.translatable(
                        "gui.jei.category.smelting.time.seconds",
                        cookTimeSeconds
                    )
                );
            }

            float experience = recipe.getExperience();

            if (experience > 0.0F) {
                tooltipBuilder.add(
                    Component.translatable(
                        "gui.jei.category.smelting.experience",
                        experience
                    )
                );
            }
        }

        // The whey tank is not a registered Minecraft fluid: it is an
        // internal Cheese Vat resource. Give its visual tank a proper JEI
        // hover so the exact recipe requirement is still explicit.
        if (
            recipe.usesWheyFromTank()
            && isCursorInsideBounds(
                WHEY_TANK_HOVER_X,
                WHEY_TANK_HOVER_Y,
                WHEY_TANK_HOVER_WIDTH,
                WHEY_TANK_HOVER_HEIGHT,
                mouseX,
                mouseY
            )
        ) {
            tooltipBuilder.add(
                Component.translatable(
                    "gui.italiansdelight.cheese_vat.whey"
                )
            );

            tooltipBuilder.add(
                Component.translatable(
                    "gui.italiansdelight.cheese_vat.whey_required",
                    recipe.getWheyAmount()
                )
            );
        }
    }

    private static boolean isCursorInsideBounds(
        int x,
        int y,
        int width,
        int height,
        double mouseX,
        double mouseY
    ) {
        return mouseX >= x
            && mouseX < x + width
            && mouseY >= y
            && mouseY < y + height;
    }
}
