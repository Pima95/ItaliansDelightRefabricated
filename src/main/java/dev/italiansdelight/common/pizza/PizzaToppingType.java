package dev.italiansdelight.common.pizza;

import java.util.Objects;
import net.minecraft.resources.Identifier;

/**
 * Immutable logical pizza ingredient. An ingredient may have multiple item IDs
 * (for example raw and cooked bacon) without becoming two different toppings.
 *
 * <p>Canonical IDs are NOT registered Minecraft item IDs. They identify
 * ingredient kinds for quarter composition, matching and anti-duplication.
 */
public record PizzaToppingType(
        Identifier id,
        NutritionCategory nutritionCategory,
        Role role,
        boolean planned
) {
    public PizzaToppingType {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(nutritionCategory, "nutritionCategory");
        Objects.requireNonNull(role, "role");

        // Cream and ricotta are dairy toppings, never sauces. Only actual
        // sauces occupy the pizza's one-sauce-per-quarter slot.
        if ((role == Role.SAUCE) != (nutritionCategory == NutritionCategory.SAUCE)) {
            throw new IllegalArgumentException("Pizza sauce role/category mismatch: " + id);
        }
    }

    /** Food bonus category, independent from anti-duplicate ingredient type. */
    public enum NutritionCategory {
        DAIRY,
        ANIMAL_PROTEIN,
        VEGETABLE,
        HERB,
        SAUCE,
        OILS_FATS
    }

    /** How the ingredient participates in pizza assembly. */
    public enum Role {
        SOLID,
        CREAMY,
        SAUCE
    }
}
