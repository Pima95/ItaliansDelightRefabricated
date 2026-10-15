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

        // Cream and ricotta remain DAIRY/Creamy for nutrition and model
        // purposes. Their placement nevertheless occupies the same exclusive
        // sauce/spread slot as tomato sauce (see occupiesSauceSlot()).
        if ((role == Role.SAUCE) != (nutritionCategory == NutritionCategory.SAUCES)) {
            throw new IllegalArgumentException("Pizza sauce role/category mismatch: " + id);
        }
    }

    /**
     * Whether this topping occupies the ONE sauce/spread choice per physical
     * pizza quarter. Tomato sauce, cream and ricotta are mutually exclusive
     * for placement, even though cream/ricotta retain DAIRY nutrition and
     * CREAMY rendering rather than receiving a tomato-sauce nutrition bonus.
     *
     * <p>This is intentionally distinct from role() == SAUCE: role and
     * nutrition are part of already saved pizza recipes and must not be
     * rewritten to enforce the player's updated placement rule.
     */
    public boolean occupiesSauceSlot() {
        return role == Role.SAUCE || role == Role.CREAMY;
    }

    /** Food bonus category, independent from anti-duplicate ingredient type. */
    public enum NutritionCategory {
        DAIRY,
        ANIMAL_PROTEIN,
        VEGETABLE,
        HERB,
        SAUCES,
        OILS_FATS
    }

    /** How the ingredient participates in pizza assembly. */
    public enum Role {
        SOLID,
        CREAMY,
        SAUCE
    }
}
