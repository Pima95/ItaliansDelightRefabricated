package dev.italiansdelight.integration.jei;

import dev.italiansdelight.common.pizza.PizzaCreativeSamples;
import dev.italiansdelight.common.pizza.PizzaDisplayNames;
import dev.italiansdelight.common.pizza.PizzaItemData;
import dev.italiansdelight.common.registry.ModDataComponents;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

/**
 * Use the one canonical named-pizza catalogue to give EVERY recognized flavor
 * the complete seven-step in-world guide (18 * 7 pages).
 *
 * Illustrations use actual raw pizza data and native topping sources, not
 * fake recipes or a second hard-coded Margherita-only example.
 */
public final class PizzaWorldJeiRecipes {
    private static final Identifier DOUGH = Identifier.parse("farmersdelight:wheat_dough");
    private PizzaWorldJeiRecipes() {}

    public static List<PizzaWorldJeiRecipe> createExamples() {
        ItemStack dough = new ItemStack(BuiltInRegistries.ITEM.getValue(DOUGH));
        List<PizzaWorldJeiRecipe> all = new ArrayList<>(
                PizzaDisplayNames.allPresets().size() * PizzaWorldJeiRecipe.Step.values().length);
        for (Map.Entry<java.util.Set<Identifier>, String> preset
                : PizzaDisplayNames.allPresets().entrySet()) {
            ItemStack raw = PizzaCreativeSamples.create(preset.getKey(), false);
            PizzaItemData data = raw.get(ModDataComponents.PIZZA_CONTENTS);
            if (data == null) {
                throw new IllegalStateException("No data for in-world pizza guide " + preset.getValue());
            }
            // The original 1-per-application source is the exact item a
            // player must right-click onto the undivided stretched dough.
            List<ItemStack> ingredients = new ArrayList<>();
            ingredients.add(dough.copy());
            data.composition().applications().forEach(app ->
                    ingredients.add(app.originalStack()));
            for (PizzaWorldJeiRecipe.Step step : PizzaWorldJeiRecipe.Step.values()) {
                all.add(new PizzaWorldJeiRecipe(
                        step, preset.getValue(), ingredients, raw));
            }
        }
        return List.copyOf(all);
    }
}
