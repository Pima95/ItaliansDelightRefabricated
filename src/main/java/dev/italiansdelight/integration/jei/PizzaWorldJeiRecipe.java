package dev.italiansdelight.integration.jei;

import java.util.List;
import net.minecraft.world.item.ItemStack;

/**
 * An instructional stage for a SPECIFIC recognized pizza flavor.
 *
 * Only the one data-bearing raw pizza output and original topping sources
 * differentiate recipes: no new craftable items or gameplay recipes.
 */
public record PizzaWorldJeiRecipe(
        Step step, String flavorId,
        List<ItemStack> exampleIngredients, ItemStack examplePizza) {
    public enum Step {
        PLACE("place"),
        STRETCH_HAND("stretch_hand"),
        STRETCH_PIN("stretch_pin"),
        DIVIDE("divide"),
        TOPPINGS("toppings"),
        REMOVE("remove"),
        COLLECT("collect");

        private final String id;
        Step(String id) { this.id = id; }
        public String id() { return id; }
        public String titleKey() { return "gui.italiansdelight.jei.pizza_world." + id + ".title"; }
        public String descriptionKey() {
            return "gui.italiansdelight.jei.pizza_world." + id + ".description";
        }
        public String noteKey() { return "gui.italiansdelight.jei.pizza_world." + id + ".note"; }
    }

    public PizzaWorldJeiRecipe {
        if (flavorId == null || flavorId.isBlank() || exampleIngredients.isEmpty()) {
            throw new IllegalArgumentException("A named pizza requires dough and a flavor ID");
        }
        exampleIngredients = exampleIngredients.stream().map(ItemStack::copy).toList();
        examplePizza = examplePizza.copy();
    }

    @Override public List<ItemStack> exampleIngredients() {
        return exampleIngredients.stream().map(ItemStack::copy).toList();
    }
    @Override public ItemStack examplePizza() { return examplePizza.copy(); }
}
