package dev.italiansdelight.integration.jei;

import java.util.List;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

/**
 * Representative examples of the dynamic shapeless pizza recipe.
 *
 * <p>Render them with JEI's own vanilla crafting-grid helper and arrow widget
 * instead of hand-positioned ingredient slots in a custom 141x67 panel.
 * They are NOT new datapack recipes or a whitelist of five fixed pizzas.
 */
public final class PizzaCraftingRecipeCategory
        extends AbstractRecipeCategory<PizzaCraftingJeiRecipe> {
    // Exactly the dimensions JEI 26.2 uses for CraftingRecipeCategory.
    private static final int WIDTH = 116;
    private static final int HEIGHT = 54;

    private final ICraftingGridHelper craftingGrid;

    public PizzaCraftingRecipeCategory(IGuiHelper guiHelper) {
        super(
                PizzaCraftingJeiRecipeTypes.PIZZA_CRAFTING_EXAMPLES,
                Component.translatable("gui.italiansdelight.jei.pizza_crafting_examples"),
                guiHelper.createDrawableItemLike(Blocks.CRAFTING_TABLE),
                WIDTH, HEIGHT
        );
        craftingGrid = guiHelper.createCraftingGridHelper();
    }

    @Override
    public Identifier getIdentifier(PizzaCraftingJeiRecipe recipe) {
        // Stable JEI-only identifier (no Minecraft crafting recipe is added).
        // The list is validated as one dough followed by distinct toppings.
        StringBuilder id = new StringBuilder("/pizza_crafting/");
        List<ItemStack> ingredients = recipe.ingredients();
        for (int i = 1; i < ingredients.size(); i++) {
            if (i > 1) {
                id.append('_');
            }
            id.append(net.minecraft.core.registries.BuiltInRegistries.ITEM
                    .getKey(ingredients.get(i).getItem()).getPath());
        }
        return Identifier.fromNamespaceAndPath("italiansdelight", id.toString());
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, PizzaCraftingJeiRecipe recipe,
                          IFocusGroup focuses) {
        // JEI-native centered 3x3 grid, output position and backgrounds.
        List<List<ItemStack>> inputs = recipe.ingredients().stream()
                .map(stack -> List.of(stack))
                .toList();
        craftingGrid.createAndSetInputs(builder, inputs, 0, 0);
        craftingGrid.createAndSetOutputs(builder, List.of(recipe.result()));
        builder.setShapeless();
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder,
                                   PizzaCraftingJeiRecipe recipe, IFocusGroup focuses) {
        // Identical arrow widget position/alignment to JEI's CraftingRecipeCategory.
        builder.addRecipeArrowWidget()
                .setPosition(61, 0, WIDTH - 61, HEIGHT,
                        HorizontalAlignment.LEFT, VerticalAlignment.CENTER);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, PizzaCraftingJeiRecipe recipe,
                           IRecipeSlotsView slots, double mouseX, double mouseY) {
        // Only the blank area beside the vanilla arrow shows these freeform
        // constraints; the JEI shapeless indicator keeps its native tooltip.
        if (mouseX >= 84 && mouseX < 95 && mouseY >= 1 && mouseY < 17) {
            tooltip.add(Component.translatable(
                    "gui.italiansdelight.jei.pizza_crafting_examples.description"));
            tooltip.add(Component.translatable(
                    "gui.italiansdelight.jei.pizza_crafting_examples.rule"));
        }
    }
}
