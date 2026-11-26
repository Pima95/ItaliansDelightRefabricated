package dev.italiansdelight.common.pizza;

import java.util.Objects;
import net.minecraft.resources.ResourceLocation;

/**
 * Immutable logical pizza ingredient. An ingredient may have multiple item IDs
 * (for example raw and cooked bacon) without becoming two different toppings.
 *
 * <p>Canonical IDs are NOT registered Minecraft item IDs. They identify
 * ingredient kinds for quarter composition, matching and anti-duplication.
 */
public record PizzaToppingType(
        ResourceLocation id,
        NutritionCategory nutritionCategory,
        Role role,
        boolean planned
) {
    public PizzaToppingType {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(nutritionCategory, "nutritionCategory");
        Objects.requireNonNull(role, "role");

        // Cream and ricotta retain DAIRY/Creamy nutrition and model roles,
        // but distinct spreads are now allowed to share a physical quarter.
        if ((role == Role.SAUCE) != (nutritionCategory == NutritionCategory.SAUCES)) {
            throw new IllegalArgumentException("Pizza sauce role/category mismatch: " + id);
        }
    }

    /**
     * Historical helper name: whether this ingredient is a spread.
     * It does NOT enforce exclusive placement any more. The latest spread
     * becomes the base, older spreads become garnishes on each quarter.
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
