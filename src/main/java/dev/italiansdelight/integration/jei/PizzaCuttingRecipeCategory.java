package dev.italiansdelight.integration.jei;

import dev.italiansdelight.common.pizza.PizzaDisplayNames;
import dev.italiansdelight.common.pizza.PizzaItemData;
import dev.italiansdelight.common.registry.ModDataComponents;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import vectorwing.farmersdelight.common.registry.ModItems;
import vectorwing.farmersdelight.common.utility.TextUtils;

/**
 * Exactly the visual layout of Farmer's Delight Refabricated 26.2
 * CuttingRecipeCategory: 117x57 native background, knife at (16,8),
 * ingredient at (16,27), four results in the standard 2x2 output grid.
 *
 * The separate recipe type is necessary because its four output stacks have
 * independent quarter components that static CuttingBoardRecipe JSON cannot
 * reconstruct. Rendering/layout is deliberately shared with the native FD
 * category, using its existing assets without copying sprites.
 */
public final class PizzaCuttingRecipeCategory implements IRecipeCategory<PizzaCuttingJeiRecipe> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            "farmersdelight", "textures/gui/jei/cutting_board.png");
    private static final int WIDTH = 117;
    private static final int HEIGHT = 57;
    private static final int OUTPUT_GRID_X = 76;
    private static final int OUTPUT_GRID_Y = 10;

    private final IDrawable background;
    private final IDrawable outputSlot;
    private final IDrawable icon;
    private final Component title;

    public PizzaCuttingRecipeCategory(IGuiHelper helper) {
        this.title = TextUtils.JEI("cutting");
        this.background = helper.createDrawable(TEXTURE, 0, 0, WIDTH, HEIGHT);
        this.outputSlot = helper.createDrawable(TEXTURE, 0, 58, 18, 18);
        this.icon = helper.createDrawableItemLike(ModItems.CUTTING_BOARD.get());
    }

    @Override public IRecipeType<PizzaCuttingJeiRecipe> getRecipeType() {
        return PizzaCuttingJeiRecipeTypes.PIZZA_CUTTING;
    }

    @Override public Component getTitle() { return title; }
    @Override public int getWidth() { return WIDTH; }
    @Override public int getHeight() { return HEIGHT; }
    @Override public IDrawable getIcon() { return icon; }

    @Override
    public Identifier getIdentifier(PizzaCuttingJeiRecipe recipe) {
        PizzaItemData pizza = recipe.whole().get(ModDataComponents.PIZZA_CONTENTS);
        String flavor = pizza == null ? null : PizzaDisplayNames.wholePresetId(pizza);
        return Identifier.fromNamespaceAndPath("italiansdelight",
                "/pizza_cutting/" + (flavor == null ? "mixed" : flavor));
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, PizzaCuttingJeiRecipe recipe,
                          IFocusGroup focuses) {
        // Native Farmer's Delight coordinates/ingredient roles.
        builder.addSlot(RecipeIngredientRole.INPUT, 16, 8)
                .add(new ItemStack(ModItems.IRON_KNIFE.get()));
        builder.addSlot(RecipeIngredientRole.INPUT, 16, 27)
                .add(recipe.whole());

        var results = recipe.slices();
        for (int i = 0; i < results.size(); i++) {
            int x = OUTPUT_GRID_X + 1 + (i % 2) * 19;
            int y = OUTPUT_GRID_Y + 1 + (i / 2) * 19;
            builder.addSlot(RecipeIngredientRole.OUTPUT, x, y).add(results.get(i));
        }
    }

    @Override
    public void draw(PizzaCuttingJeiRecipe recipe, IRecipeSlotsView slots,
                     GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        background.draw(graphics);
        for (int i = 0; i < recipe.slices().size(); i++) {
            int x = OUTPUT_GRID_X + (i % 2) * 19;
            int y = OUTPUT_GRID_Y + (i / 2) * 19;
            outputSlot.draw(graphics, x, y);
        }
    }
}
