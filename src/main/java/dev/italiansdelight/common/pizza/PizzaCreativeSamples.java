package dev.italiansdelight.common.pizza;

import dev.italiansdelight.common.registry.ModDataComponents;
import dev.italiansdelight.common.registry.ModItems;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

/**
 * Real data-bearing Creative samples for each known pizza taste (raw/cooked).
 *
 * <p>All recipes come directly from {@link PizzaDisplayNames#allPresets()}.
 * No second list of flavors, no 36 new item registrations and no synthetic
 * ITEM_MODEL-only mockups that would lose toppings when placed or cooked.
 */
public final class PizzaCreativeSamples {
    private PizzaCreativeSamples() {
    }

    /**
     * One edible, fully data-bearing slice from an actual uniform pizza.
     * The same recipe source is used for Creative whole and slice variants.
     */
    public static ItemStack createSlice(Set<Identifier> types) {
        ItemStack whole = create(types, true);
        PizzaItemData pizza = whole.get(ModDataComponents.PIZZA_CONTENTS);
        if (pizza == null) {
            throw new IllegalStateException("Missing pizza_contents on Creative example");
        }
        return PizzaSliceFactory.create(pizza, 0);
    }

    public static ItemStack create(Set<Identifier> types, boolean cooked) {
        List<Identifier> sorted = types.stream()
                .sorted(Comparator.comparing(Identifier::toString)).toList();
        List<PizzaToppingApplication> applications = new ArrayList<>();
        long nextId = 1;

        for (Identifier canonicalId : sorted) {
            Identifier nativeId = PizzaToppingCatalog.nativeItems().entrySet().stream()
                    .filter(entry -> entry.getValue().id().equals(canonicalId))
                    .map(java.util.Map.Entry::getKey)
                    .sorted(Comparator.comparing(Identifier::toString))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            "No native pizza item for Creative sample: " + canonicalId));

            // Source Stack must be the real topping item for the same
            // exact LIFO refund behavior as a pizza made in survival.
            ItemStack source = new ItemStack(BuiltInRegistries.ITEM.getValue(nativeId));
            applications.add(new PizzaToppingApplication(
                    nextId++, canonicalId, source, 0b1111, 0b1111));
        }

        PizzaComposition composition = new PizzaComposition(
                PizzaComposition.FORMAT_VERSION, 0, "north", nextId,
                0x14A11A5L, applications);
        PizzaItemData data = new PizzaItemData(composition, false);
        ItemStack stack = new ItemStack(cooked ? ModItems.COOKED_PIZZA : ModItems.RAW_PIZZA);
        stack.set(ModDataComponents.PIZZA_CONTENTS, data);
        PizzaDisplayNames.nameWholeStack(stack, data);
        return stack;
    }
}
