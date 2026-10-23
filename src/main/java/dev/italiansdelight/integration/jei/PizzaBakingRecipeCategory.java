package dev.italiansdelight.integration.jei;

import dev.italiansdelight.common.registry.ModBlocks;
import dev.italiansdelight.common.registry.ModDataComponents;
import dev.italiansdelight.common.pizza.PizzaDisplayNames;
import dev.italiansdelight.common.pizza.PizzaItemData;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.common.gui.JeiGuiColors;
import mezz.jei.common.gui.JeiGuiColors.GuiColor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.display.SlotDisplay;

/**
 * Pizza Oven JEI display using the same public slot positions and animated
 * recipe widgets as JEI 26.2's vanilla smelting and smoking categories.
 *
 * <p>Smelting/smoking pizzas are already displayed by JEI's own vanilla
 * categories; this category covers ONLY the custom 20-second Pizza Oven.
 * It does not register additional gameplay recipes or require coal.
 */
public final class PizzaBakingRecipeCategory
        extends AbstractRecipeCategory<PizzaBakingJeiRecipe> {
    private static final int WIDTH = 82;
    private static final int HEIGHT = 54;

    /** Each named flavor gets its own stable bookmarkable JEI identity. */
    public PizzaBakingRecipeCategory(IGuiHelper guiHelper) {
        super(
                PizzaBakingJeiRecipeTypes.PIZZA_BAKING,
                Component.translatable("gui.italiansdelight.jei.pizza_baking"),
                guiHelper.createDrawableItemLike(ModBlocks.PIZZA_OVEN),
                WIDTH, HEIGHT
        );
    }

    /**
     * JEI requires a stable identifier for recipe bookmarks. Without it the
     * bookmark button disappears and JEI may pack the sole example into the
     * leftmost column instead of centering it on wide displays.
     *
     * This is a synthetic display, not a new Minecraft cooking recipe.
     */
    @Override
    public Identifier getIdentifier(PizzaBakingJeiRecipe recipe) {
        PizzaItemData data = recipe.rawPizza().get(ModDataComponents.PIZZA_CONTENTS);
        String flavor = data == null ? null : PizzaDisplayNames.wholePresetId(data);
        if (flavor == null) {
            throw new IllegalStateException("Expected a recognized pizza flavor in JEI oven");
        }
        return Identifier.fromNamespaceAndPath("italiansdelight", "/pizza_oven/" + flavor);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, PizzaBakingJeiRecipe recipe,
                          IFocusGroup focuses) {
        // Positions match JEI's AbstractCookingCategory: 82x54 with the
        // ingredient above, rotating vanilla fuels below, output on the right.
        builder.addInputSlot(1, 1)
                .setStandardSlotBackground()
                .add(recipe.rawPizza());

        // AnyFuel is JEI's native, automatically cycling list of usable
        // furnace fuels, NOT one mandatory coal stack or consumed material.
        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 1, 37)
                .setStandardSlotBackground()
                .add(SlotDisplay.AnyFuel.INSTANCE);

        builder.addOutputSlot(WIDTH - 21, 19)
                .setOutputSlotBackground()
                .add(recipe.cookedPizza());
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder,
                                   PizzaBakingJeiRecipe recipe, IFocusGroup focuses) {
        // Reuse the very same JEI 26.2 widgets as vanilla Smelting / Smoking.
        builder.addAnimatedRecipeArrowWidget(recipe.cookingTicks())
                .setPosition(26, 17);
        builder.addAnimatedRecipeFlameWidget(300)
                .setPosition(1, 20);

        // Reuse the native translated duration label and its alignment.
        builder.addText(
                Component.translatable("gui.jei.category.smelting.time.seconds",
                        recipe.cookingTicks() / 20),
                WIDTH - 42, 10
        )
                .setPosition(0, 0, WIDTH, HEIGHT,
                        HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM)
                .setTextAlignment(HorizontalAlignment.CENTER)
                .setTextAlignment(VerticalAlignment.BOTTOM)
                .setColor(JeiGuiColors.getColor(GuiColor.RECIPE_COOKING_TIME_TEXT));
    }
}
