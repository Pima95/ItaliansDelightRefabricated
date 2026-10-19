package dev.italiansdelight.integration.jei;

import dev.italiansdelight.common.registry.ModBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Visual guide only: one sample pizza, three existing ways of cooking it.
 * Machine and coal are render-only (not mandatory consumed ingredients).
 * The same pizza data and translated taste name survive all three methods.
 */
public final class PizzaBakingRecipeCategory
        implements IRecipeCategory<PizzaBakingJeiRecipe> {
    private static final int WIDTH = 141;
    private static final int HEIGHT = 76;
    private final IDrawable icon;

    public PizzaBakingRecipeCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableItemLike(ModBlocks.PIZZA_OVEN);
    }

    @Override
    public IRecipeType<PizzaBakingJeiRecipe> getRecipeType() {
        return PizzaBakingJeiRecipeTypes.PIZZA_BAKING;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gui.italiansdelight.jei.pizza_baking");
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
    public void setRecipe(IRecipeLayoutBuilder builder, PizzaBakingJeiRecipe recipe,
                          IFocusGroup focuses) {
        builder.addInputSlot(8, 20)
                .setStandardSlotBackground().add(recipe.rawPizza());
        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 8, 49)
                .setStandardSlotBackground().add(new ItemStack(Items.COAL));
        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 62, 20)
                .setStandardSlotBackground().add(recipe.station());
        builder.addOutputSlot(112, 20)
                .setOutputSlotBackground().add(recipe.cookedPizza());
    }

    @Override
    public void draw(PizzaBakingJeiRecipe recipe, IRecipeSlotsView slots,
                     GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        // The JEI-standard slots provide the display; no new texture is needed.
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, PizzaBakingJeiRecipe recipe,
                           IRecipeSlotsView slots, double mouseX, double mouseY) {
        if (mouseX >= 59 && mouseX < 99 && mouseY >= 18 && mouseY < 46) {
            tooltip.add(Component.translatable(recipe.methodKey()));
            tooltip.add(Component.translatable(
                    "gui.italiansdelight.jei.pizza_baking.time",
                    recipe.cookingTicks() / 20));
        } else if (mouseX >= 6 && mouseX < 35 && mouseY >= 45 && mouseY < 75) {
            tooltip.add(Component.translatable(
                    "gui.italiansdelight.jei.pizza_baking.fuel"));
        } else if (mouseX >= 35 && mouseX < 110 && mouseY >= 47 && mouseY < 73) {
            tooltip.add(Component.translatable(
                    "gui.italiansdelight.jei.pizza_baking.note"));
            tooltip.add(Component.translatable(
                    "gui.italiansdelight.jei.pizza_baking.bowls"));
        }
    }
}
