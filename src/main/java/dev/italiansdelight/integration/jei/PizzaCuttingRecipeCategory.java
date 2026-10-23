package dev.italiansdelight.integration.jei;

import dev.italiansdelight.common.pizza.PizzaDisplayNames;
import dev.italiansdelight.common.pizza.PizzaItemData;
import dev.italiansdelight.common.registry.ModDataComponents;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

/**
 * Real Cutting Board operation presented as a JEI guide: no fake static recipe.
 * The outputs are fully component-bearing and can represent four different tastes.
 */
public final class PizzaCuttingRecipeCategory
        extends AbstractRecipeCategory<PizzaCuttingJeiRecipe> {
    public PizzaCuttingRecipeCategory(IGuiHelper helper) {
        super(PizzaCuttingJeiRecipeTypes.PIZZA_CUTTING,
                Component.translatable("gui.italiansdelight.jei.pizza_cutting"),
                helper.createDrawableItemLike(
                        vectorwing.farmersdelight.common.registry.ModItems.CUTTING_BOARD.get()),
                165, 64);
    }

    @Override
    public Identifier getIdentifier(PizzaCuttingJeiRecipe recipe) {
        PizzaItemData data = recipe.whole().get(ModDataComponents.PIZZA_CONTENTS);
        String flavor = data == null ? null : PizzaDisplayNames.wholePresetId(data);
        return Identifier.fromNamespaceAndPath("italiansdelight",
                "/pizza_cutting/" + (flavor == null ? "mixed" : flavor));
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, PizzaCuttingJeiRecipe recipe,
                          IFocusGroup focuses) {
        builder.addInputSlot(6, 18).setStandardSlotBackground().add(recipe.whole());
        builder.addSlot(RecipeIngredientRole.CRAFTING_STATION, 37, 18)
                .setStandardSlotBackground()
                .add(new ItemStack(
                        vectorwing.farmersdelight.common.registry.ModItems.IRON_KNIFE.get()));
        var slices = recipe.slices();
        for (int i = 0; i < slices.size(); i++) {
            builder.addOutputSlot(74 + i * 22, 18)
                    .setOutputSlotBackground().add(slices.get(i));
        }
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder,
                                   PizzaCuttingJeiRecipe recipe, IFocusGroup focuses) {
        builder.addText(
                Component.translatable("gui.italiansdelight.jei.pizza_cutting.note"),
                159, 23).setPosition(3, 42);
    }
}
