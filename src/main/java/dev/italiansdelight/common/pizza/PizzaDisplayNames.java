package dev.italiansdelight.common.pizza;

import dev.italiansdelight.common.registry.ModItems;
import dev.italiansdelight.common.registry.ModRegistries;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

/**
 * Names and descriptions derived from SAVED canonical ingredients, never
 * from the player's current language or the visual cut state.
 *
 * <p>Whole pizzas are named only when all four physical quarters agree.
 * Slices are named independently, even when collected from a mixed pizza.
 * Text is translated client-side; no localized strings are serialized.
 */
public final class PizzaDisplayNames {
    private static final String PREFIX = "pizza.italiansdelight.";
    private static final String CANONICAL_PREFIX = "pizza_toppings/";
    private static final Identifier LEGACY_MOZZARELLA =
            Identifier.parse("italiansdelight:pizza_toppings/legacy_mozzarella");
    // 18 currently possible recipes. Principessa remains SUSPENDED
    // because tomato sauce and cream cannot share a physical quarter.
    private static final Map<Set<Identifier>, String> PRESETS = buildPresets();

    private PizzaDisplayNames() {
    }

    private static Identifier topping(String path) {
        return Identifier.parse("italiansdelight:" + CANONICAL_PREFIX + path);
    }

    private static void add(Map<Set<Identifier>, String> out, String name, String... paths) {
        Set<Identifier> ingredients = Arrays.stream(paths)
                .map(PizzaDisplayNames::topping)
                .collect(Collectors.toUnmodifiableSet());
        if (out.putIfAbsent(ingredients, name) != null) {
            throw new IllegalStateException("Duplicate pizza name recipe: " + name);
        }
    }

    private static Map<Set<Identifier>, String> buildPresets() {
        Map<Set<Identifier>, String> presets = new HashMap<>();
        add(presets, "bianca");
        add(presets, "rossa", "tomato_sauce");
        add(presets, "margherita", "tomato_sauce", "mozzarella_slice", "basil");
        add(presets, "caprese_rossa", "tomato_sauce", "mozzarella_slice", "tomato_slice", "basil");
        add(presets, "caprese_bianca", "mozzarella_slice", "tomato_slice", "basil");
        add(presets, "quattro_formaggi_bianca", "mozzarella_slice", "gorgonzola_wedge",
                "provolone_slice", "grated_parmigiano_reggiano");
        add(presets, "quattro_formaggi_rossa", "tomato_sauce", "mozzarella_slice",
                "gorgonzola_wedge", "provolone_slice", "grated_parmigiano_reggiano");
        add(presets, "prosciutto_crudo", "tomato_sauce", "mozzarella_slice", "prosciutto_crudo_slice");
        add(presets, "salame", "tomato_sauce", "mozzarella_slice", "salame_slice");
        add(presets, "mortadella_ricotta", "mozzarella_slice", "mortadella_slice", "ricotta");
        add(presets, "guanciale_pecorino_rossa", "tomato_sauce", "guanciale_slice",
                "grated_pecorino_romano");
        add(presets, "speck_gorgonzola", "mozzarella_slice", "speck_slice", "gorgonzola_wedge");
        add(presets, "salmone_ricotta", "mozzarella_slice", "salmon_slice", "ricotta", "basil");
        add(presets, "salmone_scamorza", "mozzarella_slice", "salmon_slice", "smoked_scamorza_slice");
        add(presets, "carne_mozzarella", "tomato_sauce", "mozzarella_slice", "minced_beef");
        add(presets, "zucca_speck", "mozzarella_slice", "pumpkin_slice", "speck_slice");
        add(presets, "patate_speck", "mozzarella_slice", "potato_slice", "speck_slice");
        add(presets, "orto_disponibile", "mozzarella_slice", "tomato_slice",
                "cabbage_leaf", "pumpkin_slice", "basil");
        if (presets.size() != 18) {
            throw new IllegalStateException("Unexpected count of active pizza presets");
        }
        return Map.copyOf(presets);
    }

    private static Set<Identifier> quarter(PizzaItemData whole, int index) {
        Set<Identifier> ingredients = new HashSet<>(whole.composition().toppingsForQuarter(index));
        // Preserve the legacy proof-of-concept topping in the description.
        // Never mislabel an old whole-mozzarella pizza as Bianca or a preset.
        if (whole.legacyMozzarella()) {
            ingredients.add(LEGACY_MOZZARELLA);
        }
        return Set.copyOf(ingredients);
    }

    private static Set<Identifier> quarter(PizzaSliceData slice) {
        Set<Identifier> ingredients = new HashSet<>();
        for (PizzaSliceData.Topping topping : slice.toppings()) {
            ingredients.add(topping.canonicalId());
        }
        if (slice.legacyMozzarella()) {
            ingredients.add(LEGACY_MOZZARELLA);
        }
        return Set.copyOf(ingredients);
    }

    private static Component preset(String id) {
        return Component.translatable(PREFIX + "taste." + id);
    }

    private static Component ingredient(Identifier id) {
        if (id.getNamespace().equals("italiansdelight")
                && id.getPath().startsWith(CANONICAL_PREFIX)) {
            String key = id.getPath().substring(CANONICAL_PREFIX.length());
            if (PizzaToppingCatalog.findType(id).isPresent() || id.equals(LEGACY_MOZZARELLA)) {
                return Component.translatable(PREFIX + "ingredient." + key);
            }
        }
        // Missing optional mods / unknown old datapack types: retain the ID.
        return Component.literal(id.toString());
    }

    private static List<Identifier> sorted(Set<Identifier> types) {
        List<Identifier> ids = new ArrayList<>(types);
        ids.sort(Comparator.comparing(Identifier::toString));
        return ids;
    }

    /** Complete, stable list, independent of placement order and locale. */
    private static Component ingredients(Set<Identifier> types) {
        if (types.isEmpty()) {
            return Component.translatable(PREFIX + "none");
        }
        List<Identifier> ids = sorted(types);
        return join(ids, ids.size());
    }

    private static Component join(List<Identifier> ids, int count) {
        MutableComponent text = Component.empty();
        for (int i = 0; i < count; i++) {
            if (i > 0) {
                text.append(i == count - 1
                        ? Component.translatable(PREFIX + "and")
                        : Component.literal(", "));
            }
            text.append(ingredient(ids.get(i)));
        }
        return text;
    }

    /** Abbreviate only the inventory name; the tooltip always remains complete. */
    private static Component shortIngredients(Set<Identifier> types) {
        List<Identifier> ids = sorted(types);
        if (ids.size() <= 3) {
            return ingredients(types);
        }
        return Component.translatable(PREFIX + "short_more",
                join(ids, 2), ids.size() - 2);
    }

    private static Component quarterDescription(Set<Identifier> types) {
        String id = PRESETS.get(types);
        return id == null ? ingredients(types) : preset(id);
    }

    /**
     * Canonical whole-pizza preset shared by display names and ITEM_MODEL.
     * The four saved physical quarters must match exactly, never by union,
     * substring or translated inventory name.
     *
     * @return exact recognized preset ID, or null for mixed/freeform pizzas.
     */
    public static String wholePresetId(PizzaItemData whole) {
        Set<Identifier> first = quarter(whole, 0);
        for (int q = 1; q < PizzaComposition.QUARTER_COUNT; q++) {
            if (!first.equals(quarter(whole, q))) {
                return null;
            }
        }
        return PRESETS.get(first);
    }

    /**
     * First approved whole-pizza artwork batch. The remaining active named
     * pizzas deliberately use the neutral fallback until their dedicated
     * 16x16 assets are drawn and approved. Never lie with Margherita art.
     */
    private static Identifier wholeItemModel(PizzaItemData whole) {
        String id = wholePresetId(whole);
        if (id == null) {
            return ModRegistries.id("pizza_custom");
        }
        return switch (id) {
            case "bianca" -> ModRegistries.id("pizza_bianca");
            case "rossa" -> ModRegistries.id("pizza_rossa");
            case "margherita" -> ModRegistries.id("pizza_margherita");
            case "patate_speck" -> ModRegistries.id("pizza_patate_speck");
            default -> ModRegistries.id("pizza_custom");
        };
    }

    public static Component wholeName(PizzaItemData whole) {
        Set<Identifier> first = quarter(whole, 0);
        for (int q = 1; q < PizzaComposition.QUARTER_COUNT; q++) {
            if (!first.equals(quarter(whole, q))) {
                return Component.translatable(PREFIX + "assorted");
            }
        }
        String id = PRESETS.get(first);
        if (id != null) {
            return Component.translatable(PREFIX + "named_whole", preset(id));
        }
        return Component.translatable(PREFIX + "with_whole", shortIngredients(first));
    }

    public static Component sliceName(PizzaSliceData slice) {
        Set<Identifier> types = quarter(slice);
        String id = PRESETS.get(types);
        if ("bianca".equals(id)) {
            return Component.translatable(PREFIX + "slice_bianca");
        }
        if ("rossa".equals(id)) {
            return Component.translatable(PREFIX + "slice_rossa");
        }
        if (id != null) {
            return Component.translatable(PREFIX + "named_slice", preset(id));
        }
        return Component.translatable(PREFIX + "with_slice", shortIngredients(types));
    }

    /**
     * Minecraft 26.2 gives all stacks a built-in item_name component, so
     * overriding Item.getName alone does not replace the inventory display.
     * Write a TRANSLATABLE Component alongside the existing pizza payload.
     * The pizza's canonical ingredients are never modified by naming.
     */
    public static void nameWholeStack(ItemStack stack, PizzaItemData data) {
        stack.set(DataComponents.ITEM_NAME, wholeName(data));
        // Vanilla ITEM_MODEL is a synced/persisted Identifier. The model
        // selection is server-authored from the SAME canonical saved topping
        // data as the localized name. No new item registrations, item names
        // stored as identifiers or client-only lookups are necessary.
        //
        // Raw pizza uses its existing own graphic; only BAKED whole pizzas
        // gain per-preset art. This also works on pizza peel cargo stacks.
        if (stack.is(ModItems.COOKED_PIZZA)) {
            stack.set(DataComponents.ITEM_MODEL, wholeItemModel(data));
        }
    }

    public static void nameSliceStack(ItemStack stack, PizzaSliceData data) {
        stack.set(DataComponents.ITEM_NAME, sliceName(data));
    }

    public static void appendWholeTooltip(PizzaItemData whole, Consumer<Component> lines) {
        Set<Identifier> first = quarter(whole, 0);
        boolean uniform = true;
        for (int q = 1; q < PizzaComposition.QUARTER_COUNT; q++) {
            if (!first.equals(quarter(whole, q))) {
                uniform = false;
                break;
            }
        }
        if (uniform) {
            lines.accept(Component.translatable(PREFIX + "tooltip_ingredients", ingredients(first))
                    .withStyle(ChatFormatting.GRAY));
        } else {
            for (int q = 0; q < PizzaComposition.QUARTER_COUNT; q++) {
                lines.accept(Component.translatable(PREFIX + "tooltip_quarter", q + 1,
                        quarterDescription(quarter(whole, q))).withStyle(ChatFormatting.GRAY));
            }
        }
    }

    public static void appendSliceTooltip(PizzaSliceData slice, Consumer<Component> lines) {
        lines.accept(Component.translatable(PREFIX + "tooltip_ingredients", ingredients(quarter(slice)))
                .withStyle(ChatFormatting.GRAY));
    }
}
