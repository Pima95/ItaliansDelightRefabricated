package dev.italiansdelight.common.pizza;

import dev.italiansdelight.ItaliansDelight;
import dev.italiansdelight.common.registry.ModRegistries;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.resources.Identifier;

import static dev.italiansdelight.common.pizza.PizzaToppingType.NutritionCategory.*;
import static dev.italiansdelight.common.pizza.PizzaToppingType.Role.*;

/**
 * Authoritative, read-only 1.0.0 pizza topping catalogue.
 *
 * <p>31 canonical ingredient kinds: 29 currently backed by 33 registered item
 * IDs, plus two reserved future kinds (sliced carrot and potato). Only items
 * listed here are native toppings. In particular, broad c: food tags NEVER
 * cause whole cheeses, whole meats or other unprepared items to be accepted.
 *
 * <p>Later steps will compile verified, optional JSON item mappings into a
 * separate lookup cache; those extensions must never overwrite native items.
 */
public final class PizzaToppingCatalog {
    private static final Map<Identifier, PizzaToppingType> TYPES;
    private static final Map<Identifier, PizzaToppingType> NATIVE_ITEMS;

    static {
        Map<Identifier, PizzaToppingType> types = new LinkedHashMap<>();
        Map<Identifier, PizzaToppingType> nativeItems = new LinkedHashMap<>();

        // Italian's Delight: 20 distinct topping types and 20 item IDs.
        add(types, nativeItems, "mozzarella_slice", DAIRY, SOLID, "italiansdelight", "mozzarella_slice");
        add(types, nativeItems, "bocconcino", DAIRY, SOLID, "italiansdelight", "bocconcino");
        add(types, nativeItems, "grated_parmigiano_reggiano", DAIRY, SOLID, "italiansdelight", "grated_parmigiano_reggiano");
        add(types, nativeItems, "grated_pecorino_romano", DAIRY, SOLID, "italiansdelight", "grated_pecorino_romano");
        add(types, nativeItems, "gorgonzola_wedge", DAIRY, SOLID, "italiansdelight", "gorgonzola_wedge");
        add(types, nativeItems, "provolone_slice", DAIRY, SOLID, "italiansdelight", "provolone_slice");
        add(types, nativeItems, "scamorza_slice", DAIRY, SOLID, "italiansdelight", "scamorza_slice");
        add(types, nativeItems, "smoked_scamorza_slice", DAIRY, SOLID, "italiansdelight", "smoked_scamorza_slice");
        add(types, nativeItems, "prosciutto_crudo_slice", ANIMAL_PROTEIN, SOLID, "italiansdelight", "prosciutto_crudo_slice");
        add(types, nativeItems, "salame_slice", ANIMAL_PROTEIN, SOLID, "italiansdelight", "salame_slice");
        add(types, nativeItems, "mortadella_slice", ANIMAL_PROTEIN, SOLID, "italiansdelight", "mortadella_slice");
        add(types, nativeItems, "pancetta_slice", ANIMAL_PROTEIN, SOLID, "italiansdelight", "pancetta_slice");
        add(types, nativeItems, "guanciale_slice", ANIMAL_PROTEIN, SOLID, "italiansdelight", "guanciale_slice");
        add(types, nativeItems, "bresaola_slice", ANIMAL_PROTEIN, SOLID, "italiansdelight", "bresaola_slice");
        add(types, nativeItems, "coppa_slice", ANIMAL_PROTEIN, SOLID, "italiansdelight", "coppa_slice");
        add(types, nativeItems, "speck_slice", ANIMAL_PROTEIN, SOLID, "italiansdelight", "speck_slice");
        add(types, nativeItems, "tomato_slice", VEGETABLE, SOLID, "italiansdelight", "tomato_slice");
        add(types, nativeItems, "basil", HERB, SOLID, "italiansdelight", "basil");
        add(types, nativeItems, "cream_bowl", DAIRY, CREAMY, "italiansdelight", "cream_bowl");
        add(types, nativeItems, "ricotta", DAIRY, CREAMY, "italiansdelight", "ricotta");

        // Farmer's Delight: 9 types, 13 item IDs. Raw/cooked pairs share a type.
        add(types, nativeItems, "bacon", ANIMAL_PROTEIN, SOLID, "farmersdelight", "bacon", "cooked_bacon");
        add(types, nativeItems, "chicken_cuts", ANIMAL_PROTEIN, SOLID, "farmersdelight", "chicken_cuts", "cooked_chicken_cuts");
        add(types, nativeItems, "cod_slice", ANIMAL_PROTEIN, SOLID, "farmersdelight", "cod_slice", "cooked_cod_slice");
        add(types, nativeItems, "salmon_slice", ANIMAL_PROTEIN, SOLID, "farmersdelight", "salmon_slice", "cooked_salmon_slice");
        add(types, nativeItems, "fried_egg", ANIMAL_PROTEIN, SOLID, "farmersdelight", "fried_egg");
        add(types, nativeItems, "minced_beef", ANIMAL_PROTEIN, SOLID, "farmersdelight", "minced_beef");
        add(types, nativeItems, "cabbage_leaf", VEGETABLE, SOLID, "farmersdelight", "cabbage_leaf");
        add(types, nativeItems, "pumpkin_slice", VEGETABLE, SOLID, "farmersdelight", "pumpkin_slice");
        add(types, nativeItems, "tomato_sauce", SAUCES, SAUCE, "farmersdelight", "tomato_sauce");

        // Approved future toppings: reserve canonical IDs ONLY. The item IDs
        // and Cutting Board recipes are not registered in this step.
        addPlanned(types, "carrot_slice", VEGETABLE, SOLID);
        addPlanned(types, "potato_slice", VEGETABLE, SOLID);

        // Catch accidental omissions, aliases or newly added items before
        // anything relies on the canonical identity mapping.
        if (types.size() != 31 || nativeItems.size() != 33
                || types.values().stream().filter(PizzaToppingType::planned).count() != 2) {
            throw new IllegalStateException("Unexpected pizza topping catalogue size: "
                    + types.size() + " types, " + nativeItems.size() + " native item IDs");
        }

        TYPES = Collections.unmodifiableMap(types);
        NATIVE_ITEMS = Collections.unmodifiableMap(nativeItems);
    }

    private PizzaToppingCatalog() {
    }

    /**
     * Initialize and validate the static catalogue once at mod startup.
     * No gameplay hooks, tick callbacks, recipes or renderers are registered.
     */
    public static void register() {
        ItaliansDelight.LOGGER.info(
                "[Italian's Delight] Pizza topping catalogue: {} canonical types, {} native item IDs (2 planned)",
                TYPES.size(), NATIVE_ITEMS.size());
    }

    /** Includes both active and planned types. Never mutable. */
    public static Map<Identifier, PizzaToppingType> types() {
        return TYPES;
    }

    /** Exactly the 33 approved native item IDs. Never mutable. */
    public static Map<Identifier, PizzaToppingType> nativeItems() {
        return NATIVE_ITEMS;
    }

    public static Optional<PizzaToppingType> findType(Identifier canonicalId) {
        return Optional.ofNullable(TYPES.get(Objects.requireNonNull(canonicalId, "canonicalId")));
    }

    public static Optional<PizzaToppingType> findNativeItem(Identifier itemId) {
        return Optional.ofNullable(NATIVE_ITEMS.get(Objects.requireNonNull(itemId, "itemId")));
    }

    /** Planned-only kinds must not be enabled until their new items exist. */
    public static boolean isActiveType(Identifier canonicalId) {
        PizzaToppingType topping = TYPES.get(Objects.requireNonNull(canonicalId, "canonicalId"));
        return topping != null && !topping.planned();
    }

    private static void add(
            Map<Identifier, PizzaToppingType> types,
            Map<Identifier, PizzaToppingType> nativeItems,
            String name,
            PizzaToppingType.NutritionCategory category,
            PizzaToppingType.Role role,
            String namespace,
            String... itemPaths
    ) {
        if (itemPaths.length == 0) {
            throw new IllegalArgumentException("Native topping must have at least one item: " + name);
        }
        PizzaToppingType type = createType(types, name, category, role, false);
        for (String itemPath : itemPaths) {
            Identifier id = Identifier.fromNamespaceAndPath(namespace, itemPath);
            if (nativeItems.putIfAbsent(id, type) != null) {
                throw new IllegalStateException("Duplicate native pizza topping item: " + id);
            }
        }
    }

    private static void addPlanned(
            Map<Identifier, PizzaToppingType> types,
            String name,
            PizzaToppingType.NutritionCategory category,
            PizzaToppingType.Role role
    ) {
        createType(types, name, category, role, true);
    }

    private static PizzaToppingType createType(
            Map<Identifier, PizzaToppingType> types,
            String name,
            PizzaToppingType.NutritionCategory category,
            PizzaToppingType.Role role,
            boolean planned
    ) {
        Identifier id = ModRegistries.id("pizza_toppings/" + name);
        PizzaToppingType type = new PizzaToppingType(id, category, role, planned);
        if (types.putIfAbsent(id, type) != null) {
            throw new IllegalStateException("Duplicate canonical pizza topping identity: " + id);
        }
        return type;
    }
}
