package dev.italiansdelight.integration.jei;

import java.util.List;
import net.minecraft.world.item.ItemStack;

/**
 * One instructional stage of preparing pizza in the world.
 *
 * This is JEI documentation, not a new crafting or cooking recipe.
 * The same Margherita is reused as an illustration throughout the guide.
 */
public record PizzaWorldJeiRecipe(
        Step step, List<ItemStack> exampleIngredients, ItemStack examplePizza) {

    public enum Step {
        PLACE("place"),
        STRETCH_HAND("stretch_hand"),
        STRETCH_PIN("stretch_pin"),
        DIVIDE("divide"),
        TOPPINGS("toppings"),
        REMOVE("remove"),
        COLLECT("collect");

        private final String id;

        Step(String id) {
            this.id = id;
        }

        public String id() {
            return id;
        }

        public String titleKey() {
            return "gui.italiansdelight.jei.pizza_world." + id + ".title";
        }

        public String descriptionKey() {
            return "gui.italiansdelight.jei.pizza_world." + id + ".description";
        }

        public String noteKey() {
            return "gui.italiansdelight.jei.pizza_world." + id + ".note";
        }
    }

    public PizzaWorldJeiRecipe {
        exampleIngredients = exampleIngredients.stream().map(ItemStack::copy).toList();
        examplePizza = examplePizza.copy();
    }

    @Override
    public List<ItemStack> exampleIngredients() {
        return exampleIngredients.stream().map(ItemStack::copy).toList();
    }

    @Override
    public ItemStack examplePizza() {
        return examplePizza.copy();
    }
}
