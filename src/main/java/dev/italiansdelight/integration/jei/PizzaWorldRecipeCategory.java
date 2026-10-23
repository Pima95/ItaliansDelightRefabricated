package dev.italiansdelight.integration.jei;

import dev.italiansdelight.common.registry.ModItems;
import java.util.List;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

/** Seven in-world instructions for each of the eighteen recognized tastes. */
public final class PizzaWorldRecipeCategory
        extends AbstractRecipeCategory<PizzaWorldJeiRecipe> {
    private static final int WIDTH = 170;
    private static final int HEIGHT = 136;

    public PizzaWorldRecipeCategory(IGuiHelper helper) {
        super(PizzaWorldJeiRecipeTypes.WORLD_PREPARATION,
                Component.translatable("gui.italiansdelight.jei.pizza_world"),
                helper.createDrawableItemLike(ModItems.ROLLING_PIN), WIDTH, HEIGHT);
    }

    @Override
    public Identifier getIdentifier(PizzaWorldJeiRecipe recipe) {
        return Identifier.fromNamespaceAndPath("italiansdelight",
                "/pizza_world/" + recipe.flavorId() + "/" + recipe.step().id());
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder,
                          PizzaWorldJeiRecipe recipe, IFocusGroup focuses) {
        List<ItemStack> ingredients = recipe.exampleIngredients();
        ItemStack dough = ingredients.getFirst();
        List<ItemStack> toppings = ingredients.subList(1, ingredients.size());

        // Index ALL seven stages by the exact data-bearing named pizza.
        // Lookups R/U therefore find the corresponding flavor-specific guide
        // rather than the former seven Margherita-only illustrations.
        builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT)
                .add(recipe.examplePizza());
        builder.addInvisibleIngredients(RecipeIngredientRole.INPUT)
                .add(dough)
                .add(new ItemStack(ModItems.ROLLING_PIN));

        switch (recipe.step()) {
            case PLACE, STRETCH_HAND ->
                builder.addInputSlot(76, 7)
                        .setStandardSlotBackground().add(dough);
            case STRETCH_PIN, DIVIDE -> {
                builder.addInputSlot(46, 7)
                        .setStandardSlotBackground().add(dough);
                builder.addSlot(RecipeIngredientRole.CRAFTING_STATION, 106, 7)
                        .setStandardSlotBackground().add(new ItemStack(ModItems.ROLLING_PIN));
            }
            case TOPPINGS -> {
                if (toppings.isEmpty()) {
                    // Bianca: no toppings are needed! This illustrates the
                    // correctly stretched, undecorated finished base.
                    builder.addOutputSlot(76, 7)
                            .setOutputSlotBackground().add(recipe.examplePizza());
                } else {
                    // Max six, one physical application per item on an
                    // undivided pizza. Unlike the old Margherita-only page,
                    // these slots reflect the precise saved canonical preset.
                    int left = (WIDTH - (toppings.size() * 22 - 4)) / 2;
                    for (int i = 0; i < toppings.size(); i++) {
                        builder.addInputSlot(left + i * 22, 7)
                                .setStandardSlotBackground().add(toppings.get(i));
                    }
                }
            }
            case REMOVE -> {
                if (!toppings.isEmpty()) {
                    builder.addOutputSlot(76, 7)
                            .setOutputSlotBackground().add(toppings.getLast());
                } else {
                    builder.addOutputSlot(76, 7)
                            .setOutputSlotBackground().add(recipe.examplePizza());
                }
            }
            case COLLECT ->
                builder.addOutputSlot(76, 7)
                        .setOutputSlotBackground().add(recipe.examplePizza());
        }
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder,
                                   PizzaWorldJeiRecipe recipe, IFocusGroup focuses) {
        Component pizzaName = recipe.examplePizza().getHoverName();
        List<ItemStack> ingredients = recipe.exampleIngredients();
        List<ItemStack> toppings = ingredients.subList(1, ingredients.size());

        // Every page is explicitly titled with its flavor. The toppings step
        // additionally lists exact ingredient names in the client language.
        builder.addText(
                Component.translatable("gui.italiansdelight.jei.pizza_world.flavor_title",
                        pizzaName, Component.translatable(recipe.step().titleKey()))
                        .withStyle(ChatFormatting.BOLD),
                WIDTH - 12, 30)
                .setPosition(6, 28)
                .setTextAlignment(HorizontalAlignment.CENTER);

        Component description;
        if (recipe.step() == PizzaWorldJeiRecipe.Step.TOPPINGS) {
            Component names = Component.empty();
            for (int i = 0; i < toppings.size(); i++) {
                if (i > 0) names = names.copy().append(Component.literal(", "));
                names = names.copy().append(toppings.get(i).getHoverName());
            }
            description = toppings.isEmpty()
                    ? Component.translatable("gui.italiansdelight.jei.pizza_world.bianca_description")
                    : Component.translatable("gui.italiansdelight.jei.pizza_world.named_toppings_description",
                            pizzaName, names);
        } else {
            description = Component.translatable(recipe.step().descriptionKey());
        }
        builder.addText(description, WIDTH - 12, 48).setPosition(6, 61);

        Component note = recipe.step() == PizzaWorldJeiRecipe.Step.TOPPINGS
                ? Component.translatable("gui.italiansdelight.jei.pizza_world.named_toppings_note")
                : Component.translatable(recipe.step().noteKey());
        builder.addText(note.copy().withStyle(ChatFormatting.DARK_GRAY), WIDTH - 12, 24)
                .setPosition(6, 111);
    }
}
