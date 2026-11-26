package dev.italiansdelight.common.pizza;

import dev.italiansdelight.common.registry.ModDataComponents;
import dev.italiansdelight.common.registry.ModItems;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
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
    public static ItemStack createSlice(Set<ResourceLocation> types) {
        ItemStack whole = create(types, true);
        PizzaItemData pizza = whole.get(ModDataComponents.PIZZA_CONTENTS);
        if (pizza == null) {
            throw new IllegalStateException("Missing pizza_contents on Creative example");
        }
        return PizzaSliceFactory.create(pizza, 0);
    }

    /**
     * Genuine four-flavor sample (Margherita, Salame, Rossa, Quattro Formaggi).
     * Each original topping is stored on exactly one physical quarter, with
     * valid sequence IDs and refund provenance. This showcases "Pizza mista"
     * and its approved Margherita icon directly in Creative.
     */
    public static ItemStack createMixed(boolean cooked) {
        String[] flavors = {"margherita", "salame", "rossa", "quattro_formaggi_bianca"};
        List<PizzaToppingApplication> applications = new ArrayList<>();
        long nextId = 1;
        for (int quarter = 0; quarter < flavors.length; quarter++) {
            final String flavor = flavors[quarter];
            Set<ResourceLocation> types = PizzaDisplayNames.allPresets().entrySet().stream()
                    .filter(entry -> entry.getValue().equals(flavor))
                    .map(java.util.Map.Entry::getKey)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            "Missing mixed pizza Creative preset: " + flavor));
            for (ResourceLocation canonicalId : types.stream()
                    .sorted(Comparator.comparing(ResourceLocation::toString)).toList()) {
                int mask = 1 << quarter;
                applications.add(new PizzaToppingApplication(
                        nextId++, canonicalId, nativeSource(canonicalId), mask, mask));
            }
        }
        return createStack(applications, nextId, cooked);
    }

    public static ItemStack create(Set<ResourceLocation> types, boolean cooked) {
        List<ResourceLocation> sorted = types.stream()
                .sorted(Comparator.comparing(ResourceLocation::toString)).toList();
        List<PizzaToppingApplication> applications = new ArrayList<>();
        long nextId = 1;

        for (ResourceLocation canonicalId : sorted) {
            applications.add(new PizzaToppingApplication(
                    nextId++, canonicalId, nativeSource(canonicalId), 0b1111, 0b1111));
        }
        return createStack(applications, nextId, cooked);
    }

    private static ItemStack nativeSource(ResourceLocation canonicalId) {
        ResourceLocation nativeId = PizzaToppingCatalog.nativeItems().entrySet().stream()
                .filter(entry -> entry.getValue().id().equals(canonicalId))
                .map(java.util.Map.Entry::getKey)
                .sorted(Comparator.comparing(ResourceLocation::toString))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No native pizza item for Creative sample: " + canonicalId));
        return new ItemStack(BuiltInRegistries.ITEM.get(nativeId));
    }

    private static ItemStack createStack(
            List<PizzaToppingApplication> applications, long nextId, boolean cooked
    ) {
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
