package dev.italiansdelight.common.pizza;

import dev.italiansdelight.ItaliansDelight;
import dev.italiansdelight.common.registry.ModRegistries;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;

import static dev.italiansdelight.common.pizza.PizzaToppingType.NutritionCategory.*;
import static dev.italiansdelight.common.pizza.PizzaToppingType.Role.*;

/**
 * Authoritative, read-only 1.0.0 pizza topping catalogue.
 *
 * <p>34 canonical ingredient kinds, backed by 38 native registered item
 * IDs (including carrot, potato, pitted olive and chopped almond portions). Only items
 * listed here are native toppings. In particular, broad c: food tags NEVER
 * cause whole cheeses, whole meats or other unprepared items to be accepted.
 *
 * <p>Later steps will compile verified, optional JSON item mappings into a
 * separate lookup cache; those extensions must never overwrite native items.
 */
public final class PizzaToppingCatalog {
    private static final Map<ResourceLocation, PizzaToppingType> TYPES;
    private static final Map<ResourceLocation, PizzaToppingType> NATIVE_ITEMS;

    /**
     * Known whole/unprepared or explicitly forbidden items.
     * This is a safety deny-list on top of the positive native whitelist.
     * External equivalences cannot make these approved pizza portions.
     *
     * <p>Unknown third-party items still require deliberate verification of
     * their real form before a mapping may be shipped in a datapack.
     */
    private static final Set<ResourceLocation> EXPLICIT_EXCLUSIONS = Set.of(
            ResourceLocation.parse("italiansdelight:parmigiano_reggiano_wedge"),
            ResourceLocation.parse("italiansdelight:pecorino_romano_wedge"),
            ResourceLocation.parse("italiansdelight:mascarpone"),
            ResourceLocation.parse("italiansdelight:salt"),
            ResourceLocation.parse("italiansdelight:mozzarella"),
            ResourceLocation.parse("italiansdelight:burrata"),
            ResourceLocation.parse("italiansdelight:scamorza"),
            ResourceLocation.parse("italiansdelight:smoked_scamorza"),
            ResourceLocation.parse("italiansdelight:olives"),
            ResourceLocation.parse("italiansdelight:olive_seed"),
            ResourceLocation.parse("italiansdelight:olive_pate"),
            ResourceLocation.parse("italiansdelight:almonds"),
            ResourceLocation.parse("italiansdelight:almond_pesto"),
            ResourceLocation.parse("italiansdelight:almond_cake"),
            ResourceLocation.parse("italiansdelight:almond_cake_slice"),
            ResourceLocation.parse("minecraft:air"),
            ResourceLocation.parse("minecraft:egg"),
            ResourceLocation.parse("minecraft:beef"),
            ResourceLocation.parse("minecraft:cooked_beef"),
            ResourceLocation.parse("minecraft:chicken"),
            ResourceLocation.parse("minecraft:cooked_chicken"),
            ResourceLocation.parse("minecraft:cod"),
            ResourceLocation.parse("minecraft:cooked_cod"),
            ResourceLocation.parse("minecraft:salmon"),
            ResourceLocation.parse("minecraft:cooked_salmon"),
            ResourceLocation.parse("minecraft:carrot"),
            ResourceLocation.parse("minecraft:potato"),
            ResourceLocation.parse("farmersdelight:cabbage"),
            ResourceLocation.parse("farmersdelight:tomato"),
            ResourceLocation.parse("farmersdelight:onion"),
            ResourceLocation.parse("farmersdelight:beef_patty"),
            ResourceLocation.parse("farmersdelight:ham"),
            ResourceLocation.parse("farmersdelight:smoked_ham")
    );

    static {
        Map<ResourceLocation, PizzaToppingType> types = new LinkedHashMap<>();
        Map<ResourceLocation, PizzaToppingType> nativeItems = new LinkedHashMap<>();

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

        // Prepared vegetable slices are approved. Whole carrots/potatoes
        // remain in EXPLICIT_EXCLUSIONS and cannot be used as toppings.
        add(types, nativeItems, "carrot_slice", VEGETABLE, SOLID, "italiansdelight", "carrot_slice");
        add(types, nativeItems, "potato_slice", VEGETABLE, SOLID, "italiansdelight", "potato_slice");
        // Whole olives and olive pate are NOT valid topping portions.
        add(types, nativeItems, "olive_pitted", VEGETABLE, SOLID, "italiansdelight", "olive_pitted");
        // Chopped almonds are the only approved almond pizza topping.
        add(types, nativeItems, "chopped_almonds", OILS_FATS, SOLID, "italiansdelight", "chopped_almonds");
        // Native chili is already edible; now it can top the spicy olive pizza.
        add(types, nativeItems, "chili_pepper", VEGETABLE, SOLID, "italiansdelight", "chili_pepper");

        // Catch accidental omissions, aliases or newly added items before
        // anything relies on the canonical identity mapping.
        if (types.size() != 34 || nativeItems.size() != 38
                || types.values().stream().filter(PizzaToppingType::planned).count() != 0) {
            throw new IllegalStateException("Unexpected pizza topping catalogue size: "
                    + types.size() + " types, " + nativeItems.size() + " native item IDs");
        }

        for (ResourceLocation id : EXPLICIT_EXCLUSIONS) {
            if (nativeItems.containsKey(id)) {
                throw new IllegalStateException("Forbidden pizza topping registered: " + id);
            }
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
                "[Italian's Delight] Pizza topping catalogue: {} canonical types, {} native item IDs (0 planned)",
                TYPES.size(), NATIVE_ITEMS.size());
    }

    /** Includes both active and planned types. Never mutable. */
    public static Map<ResourceLocation, PizzaToppingType> types() {
        return TYPES;
    }

    /** Exactly the 37 approved native item IDs. Never mutable. */
    public static Map<ResourceLocation, PizzaToppingType> nativeItems() {
        return NATIVE_ITEMS;
    }

    public static Optional<PizzaToppingType> findType(ResourceLocation canonicalId) {
        return Optional.ofNullable(TYPES.get(Objects.requireNonNull(canonicalId, "canonicalId")));
    }

    public static Optional<PizzaToppingType> findNativeItem(ResourceLocation itemId) {
        return Optional.ofNullable(NATIVE_ITEMS.get(Objects.requireNonNull(itemId, "itemId")));
    }

    /** Only live canonical topping types may be applied to a pizza. */
    public static boolean isActiveType(ResourceLocation canonicalId) {
        PizzaToppingType topping = TYPES.get(Objects.requireNonNull(canonicalId, "canonicalId"));
        return topping != null && !topping.planned();
    }

    /** Known forbidden items remain blocked even in optional datapack mappings. */
    public static boolean isExplicitlyExcluded(ResourceLocation itemId) {
        return EXPLICIT_EXCLUSIONS.contains(Objects.requireNonNull(itemId, "itemId"));
    }

    private static void add(
            Map<ResourceLocation, PizzaToppingType> types,
            Map<ResourceLocation, PizzaToppingType> nativeItems,
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
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(namespace, itemPath);
            if (nativeItems.putIfAbsent(id, type) != null) {
                throw new IllegalStateException("Duplicate native pizza topping item: " + id);
            }
        }
    }

    private static void addPlanned(
            Map<ResourceLocation, PizzaToppingType> types,
            String name,
            PizzaToppingType.NutritionCategory category,
            PizzaToppingType.Role role
    ) {
        createType(types, name, category, role, true);
    }

    private static PizzaToppingType createType(
            Map<ResourceLocation, PizzaToppingType> types,
            String name,
            PizzaToppingType.NutritionCategory category,
            PizzaToppingType.Role role,
            boolean planned
    ) {
        ResourceLocation id = ModRegistries.id("pizza_toppings/" + name);
        PizzaToppingType type = new PizzaToppingType(id, category, role, planned);
        if (types.putIfAbsent(id, type) != null) {
            throw new IllegalStateException("Duplicate canonical pizza topping identity: " + id);
        }
        return type;
    }
}
