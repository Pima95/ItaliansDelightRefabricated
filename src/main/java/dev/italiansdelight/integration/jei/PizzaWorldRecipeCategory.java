package dev.italiansdelight.integration.jei;

import dev.italiansdelight.common.registry.ModItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import java.util.List;

/**
 * JEI handbook for the in-world pizza process, with no invented intermediate
 * items or fake shapeless recipes. Inputs are consumed only where applicable;
 * the rolling pin is a non-consumable crafting station for JEI lookup.
 */
public final class PizzaWorldRecipeCategory
        extends AbstractRecipeCategory<PizzaWorldJeiRecipe> {
    private static final int WIDTH = 170;
    private static final int HEIGHT = 100;

    public PizzaWorldRecipeCategory(IGuiHelper helper) {
        super(
                PizzaWorldJeiRecipeTypes.WORLD_PREPARATION,
                Component.translatable("gui.italiansdelight.jei.pizza_world"),
                helper.createDrawableItemLike(ModItems.ROLLING_PIN),
                WIDTH, HEIGHT
        );
    }

    @Override
    public Identifier getIdentifier(PizzaWorldJeiRecipe recipe) {
        // Synthetic, stable IDs let players bookmark every guide page.
        return Identifier.fromNamespaceAndPath(
                "italiansdelight", "/pizza_world/" + recipe.step().id());
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder,
                          PizzaWorldJeiRecipe recipe, IFocusGroup focuses) {
        List<ItemStack> ingredients = recipe.exampleIngredients();
        ItemStack dough = ingredients.get(0);
        ItemStack sauce = ingredients.get(1);
        ItemStack mozzarella = ingredients.get(2);
        ItemStack basil = ingredients.get(3);

        switch (recipe.step()) {
            case PLACE ->
                builder.addInputSlot(76, 7)
                        .setStandardSlotBackground().add(dough);
            case STRETCH_HAND ->
                builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 76, 7)
                        .setStandardSlotBackground().add(dough);
            case STRETCH_PIN -> {
                builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 46, 7)
                        .setStandardSlotBackground().add(dough);
                builder.addSlot(RecipeIngredientRole.CRAFTING_STATION, 106, 7)
                        .setStandardSlotBackground().add(new ItemStack(ModItems.ROLLING_PIN));
            }
            case DIVIDE ->
                builder.addSlot(RecipeIngredientRole.CRAFTING_STATION, 76, 7)
                        .setStandardSlotBackground().add(new ItemStack(ModItems.ROLLING_PIN));
            case TOPPINGS -> {
                // Margherita is a SAMPLE of valid toppings, not a fixed
                // recipe or a mandatory sauce order. Each application
                // consumes only one item on a successful right-click.
                builder.addInputSlot(36, 7)
                        .setStandardSlotBackground().add(sauce);
                builder.addInputSlot(76, 7)
                        .setStandardSlotBackground().add(mozzarella);
                builder.addInputSlot(116, 7)
                        .setStandardSlotBackground().add(basil);
            }
            case REMOVE ->
                builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 76, 7)
                        .setStandardSlotBackground().add(mozzarella);
            case COLLECT ->
                builder.addOutputSlot(76, 7)
                        .setOutputSlotBackground().add(recipe.examplePizza());
        }
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder,
                                   PizzaWorldJeiRecipe recipe, IFocusGroup focuses) {
        // The category is documentation, NOT an alternate crafting station.
        // Rendering text from translations avoids separate images/assets and
        // makes the explanation available to Italian and English clients.
        builder.addText(
                Component.translatable(recipe.step().titleKey()),
                WIDTH - 12, 13
        ).setPosition(6, 34);

        builder.addText(
                Component.translatable(recipe.step().descriptionKey()),
                WIDTH - 12, 34
        ).setPosition(6, 49);

        builder.addText(
                Component.translatable(recipe.step().noteKey()),
                WIDTH - 12, 24
        ).setPosition(6, 78);
    }
}
