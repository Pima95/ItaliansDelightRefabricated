package dev.italiansdelight.integration.jei;

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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

/**
 * Representative examples of the shapeless 1-6 topping recipe.
 *
 * <p>This is NOT a second recipe matcher. Gameplay uses PizzaCraftingRecipe;
 * JEI examples show real output data and are intentionally labeled as samples.
 * No new GUI textures or approved item models are modified.
 */
public final class PizzaCraftingRecipeCategory
        implements IRecipeCategory<PizzaCraftingJeiRecipe> {
    private static final int WIDTH = 141;
    private static final int HEIGHT = 67;
    private static final int GRID_X = 7;
    private static final int GRID_Y = 6;
    private static final int SLOT_STEP = 18;
    private static final int RESULT_X = 105;
    private static final int RESULT_Y = 24;

    private final IDrawable icon;

    public PizzaCraftingRecipeCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableItemLike(Blocks.CRAFTING_TABLE);
    }

    @Override
    public IRecipeType<PizzaCraftingJeiRecipe> getRecipeType() {
        return PizzaCraftingJeiRecipeTypes.PIZZA_CRAFTING_EXAMPLES;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gui.italiansdelight.jei.pizza_crafting_examples");
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
    public void setRecipe(IRecipeLayoutBuilder builder, PizzaCraftingJeiRecipe recipe,
                          IFocusGroup focuses) {
        List<ItemStack> ingredients = recipe.ingredients();
        for (int i = 0; i < ingredients.size(); i++) {
            // One dough plus 1-6 toppings: no more than seven visible slots.
            builder.addInputSlot(
                    GRID_X + (i % 3) * SLOT_STEP,
                    GRID_Y + (i / 3) * SLOT_STEP
            ).setStandardSlotBackground().add(ingredients.get(i));
        }
        builder.addOutputSlot(RESULT_X, RESULT_Y)
                .setOutputSlotBackground().add(recipe.result());
    }

    @Override
    public void draw(PizzaCraftingJeiRecipe recipe, IRecipeSlotsView slots,
                     GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        // Standard JEI slot backgrounds make this 3x3 layout clear without
        // adding an extra PNG or modifying existing Minecraft UI assets.
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, PizzaCraftingJeiRecipe recipe,
                           IRecipeSlotsView slots, double mouseX, double mouseY) {
        // Hover between the 3x3 ingredient grid and the output to explain that
        // this is an example of a FREEFORM recipe, not its sole combination.
        if (mouseX >= 65 && mouseX < 99 && mouseY >= 16 && mouseY < 49) {
            tooltip.add(Component.translatable(
                    "gui.italiansdelight.jei.pizza_crafting_examples.description"));
            tooltip.add(Component.translatable(
                    "gui.italiansdelight.jei.pizza_crafting_examples.rule"));
        }
    }
}
