package dev.italiansdelight.integration.jei;

import dev.italiansdelight.common.registry.ModItems;
import net.minecraft.ChatFormatting;
import mezz.jei.api.gui.placement.HorizontalAlignment;
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
    // Keep adequate room for four-to-five wrapped description lines and
    // a separate two-to-three-line note, even with scaled fonts.
    private static final int HEIGHT = 136;

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

        // JEI indexes each instructional page by its displayed INPUT/OUTPUT
        // ingredients. Without a shared lookup anchor, pressing R on a raw
        // pizza selects ONLY COLLECT (the one page with a visible raw-pizza
        // output), resulting in 1/1 despite all seven pages being registered.
        //
        // These are SEARCH-ONLY entries; they do not render extra slots or
        // claim that every instruction crafts a raw pizza. They let players
        // navigate the entire handbook starting from either the raw pizza,
        // wheat dough, or rolling pin, while keeping the illustrations below
        // accurate for each step.
        builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT)
                .add(recipe.examplePizza());
        builder.addInvisibleIngredients(RecipeIngredientRole.INPUT)
                .add(dough)
                .add(new ItemStack(ModItems.ROLLING_PIN));

        switch (recipe.step()) {
            case PLACE ->
                builder.addInputSlot(76, 7)
                        .setStandardSlotBackground().add(dough);
            case STRETCH_HAND ->
                builder.addInputSlot(76, 7)
                        .setStandardSlotBackground().add(dough);
            case STRETCH_PIN -> {
                builder.addInputSlot(46, 7)
                        .setStandardSlotBackground().add(dough);
                builder.addSlot(RecipeIngredientRole.CRAFTING_STATION, 106, 7)
                        .setStandardSlotBackground().add(new ItemStack(ModItems.ROLLING_PIN));
            }
            case DIVIDE -> {
                // Dough is a JEI visual reference for the placed base, not
                // a second dough item consumed by dividing an existing pizza.
                builder.addInputSlot(46, 7)
                        .setStandardSlotBackground().add(dough);
                builder.addSlot(RecipeIngredientRole.CRAFTING_STATION, 106, 7)
                        .setStandardSlotBackground().add(new ItemStack(ModItems.ROLLING_PIN));
            }
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
                // An original topping is refunded only once ALL its parts
                // were removed; no output exists after a partial removal.
                builder.addOutputSlot(76, 7)
                        .setOutputSlotBackground().add(mozzarella);
            case COLLECT ->
                builder.addOutputSlot(76, 7)
                        .setOutputSlotBackground().add(recipe.examplePizza());
        }
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder,
                                   PizzaWorldJeiRecipe recipe, IFocusGroup focuses) {
        // Use three distinct, non-overlapping areas. The previous 34-pixel
        // description space truncated multi-line Italian/English translations,
        // while the note began before the description had finished wrapping.
        // No hardcoded line breaks: JEI handles word wrapping for both languages.
        builder.addText(
                Component.translatable(recipe.step().titleKey())
                        .withStyle(ChatFormatting.BOLD),
                WIDTH - 12, 18
        )
                .setPosition(6, 31)
                .setTextAlignment(HorizontalAlignment.CENTER);

        builder.addText(
                Component.translatable(recipe.step().descriptionKey()),
                WIDTH - 12, 51
        ).setPosition(6, 53);

        builder.addText(
                Component.translatable(recipe.step().noteKey())
                        .withStyle(ChatFormatting.DARK_GRAY),
                WIDTH - 12, 24
        ).setPosition(6, 110);
    }
}
