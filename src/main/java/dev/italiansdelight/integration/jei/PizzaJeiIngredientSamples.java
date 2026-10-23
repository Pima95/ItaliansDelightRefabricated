package dev.italiansdelight.integration.jei;

import dev.italiansdelight.common.pizza.PizzaComposition;
import dev.italiansdelight.common.pizza.PizzaCreativeSamples;
import dev.italiansdelight.common.pizza.PizzaDisplayNames;
import dev.italiansdelight.common.pizza.PizzaItemData;
import dev.italiansdelight.common.pizza.PizzaSliceData;
import dev.italiansdelight.common.registry.ModDataComponents;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

/**
 * Concrete ItemStacks displayed in JEI's ingredient sidebar, not just recipe
 * previews. Uses the single immutable 18-flavor catalogue shared by Creative,
 * naming and the Pizza Oven; no second flavor/texture registry to maintain.
 *
 * No new game registry IDs: all examples carry the genuine saved components.
 * Old worlds and manually assembled mixed pizzas keep their existing IDs.
 */
public final class PizzaJeiIngredientSamples {
    private PizzaJeiIngredientSamples() {
    }

    /**
     * Eighteen flavors x raw/cooked/slice, plus a representative mixed pizza
     * in both baking states. Each slice has real nutrition and effects.
     */
    public static List<ItemStack> createAll() {
        List<ItemStack> stacks = new ArrayList<>(3 * 18 + 2);
        for (Set<Identifier> recipe : PizzaDisplayNames.allPresets().keySet()) {
            stacks.add(PizzaCreativeSamples.create(recipe, false));
            stacks.add(PizzaCreativeSamples.create(recipe, true));
            stacks.add(PizzaCreativeSamples.createSlice(recipe));
        }
        stacks.add(PizzaCreativeSamples.createMixed(false));
        stacks.add(PizzaCreativeSamples.createMixed(true));
        return List.copyOf(stacks);
    }

    /** Stable JEI subtype ID: one flavor per sidebar entry, not per saved placement. */
    public static String wholeSubtype(ItemStack stack) {
        PizzaItemData data = stack.get(ModDataComponents.PIZZA_CONTENTS);
        if (data == null) {
            return "unconfigured";
        }
        String preset = PizzaDisplayNames.wholePresetId(data);
        if (preset != null) {
            return "named/" + preset;
        }
        // Freeform or mixed: four ordered quarter signatures distinguish
        // different combinations independently of cutting/render seeds,
        // application order, item names, and metadata of source stacks.
        StringBuilder signature = new StringBuilder("quarters/");
        for (int quarter = 0; quarter < PizzaComposition.QUARTER_COUNT; quarter++) {
            if (quarter > 0) signature.append('|');
            signature.append(signature(data.composition().toppingsForQuarter(quarter)));
        }
        if (data.legacyMozzarella()) signature.append("|legacy_mozzarella");
        return signature.toString();
    }

    /** Same subtype for a recognized slice from a mixed or uniform pizza. */
    public static String sliceSubtype(ItemStack stack) {
        PizzaSliceData data = stack.get(ModDataComponents.PIZZA_SLICE_CONTENTS);
        if (data == null) {
            return "unconfigured";
        }
        String preset = PizzaDisplayNames.slicePresetId(data);
        if (preset != null) {
            return "named/" + preset;
        }
        String signature = signature(data.toppings().stream()
                .map(PizzaSliceData.Topping::canonicalId)
                .collect(Collectors.toSet()));
        return "custom/" + signature + (data.legacyMozzarella() ? "|legacy_mozzarella" : "");
    }

    private static String signature(Set<Identifier> canonicalToppings) {
        return canonicalToppings.stream()
                .map(Identifier::toString)
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.joining(","));
    }
}
