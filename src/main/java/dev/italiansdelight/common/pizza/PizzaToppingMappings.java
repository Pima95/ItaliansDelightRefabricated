package dev.italiansdelight.common.pizza;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.italiansdelight.ItaliansDelight;
import dev.italiansdelight.common.registry.ModRegistries;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.fabricmc.fabric.api.resource.v1.DataResourceLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Server-data datapack extensions for the 31 approved pizza ingredient kinds.
 *
 * <p>Exactly one JSON object per mapping, with only "item" and "canonical".
 * Native item IDs are protected. External IDs are accepted only via explicit
 * mappings: c: tags are NEVER scanned or expanded to add ingredients.
 *
 * <p>This listener deliberately runs only during data reloads, never in
 * per-tick or click handlers. A complete immutable item lookup is published
 * atomically after all files have been checked.
 */
public final class PizzaToppingMappings {
    // Datapacks: data/<pack_namespace>/italiansdelight/pizza_equivalences/<name>.json
    private static final String DIRECTORY = "italiansdelight/pizza_equivalences";

    /** Replaced atomically at the end of every successful server-data reload. */
    private static volatile Map<Item, PizzaToppingType> cachedByItem = Map.of();

    private PizzaToppingMappings() {
    }

    public static void register() {
        DataResourceLoader.get().registerReloadListener(
                ModRegistries.id("pizza_topping_equivalences"),
                (ResourceManagerReloadListener) PizzaToppingMappings::reload
        );
    }

    /** Direct server lookup for the later ingredient-interaction implementation. */
    public static Optional<PizzaToppingType> find(Item item) {
        return Optional.ofNullable(cachedByItem.get(item));
    }

    /** Never consumes or modifies the original stack. */
    public static Optional<PizzaToppingType> find(ItemStack stack) {
        return stack.isEmpty() ? Optional.empty() : find(stack.getItem());
    }

    private static void reload(ResourceManager resources) {
        // Build the full replacement locally. A failed individual entry cannot
        // expose a partially updated map to another thread or game event.
        Map<Item, PizzaToppingType> next = new HashMap<>();
        int missingNativeItems = 0;

        // Protected built-in mappings always take precedence, even if an
        // optional pack explicitly attempts to replace one.
        for (Map.Entry<Identifier, PizzaToppingType> nativeEntry
                : PizzaToppingCatalog.nativeItems().entrySet()) {
            Item nativeItem = BuiltInRegistries.ITEM.getOptional(nativeEntry.getKey()).orElse(null);
            if (nativeItem == null) {
                missingNativeItems++;
                ItaliansDelight.LOGGER.error(
                        "[Pizza] Registered native topping item not found: {}", nativeEntry.getKey());
                continue;
            }
            next.put(nativeItem, nativeEntry.getValue());
        }

        // List *all* resource stacks, rather than just each topmost JSON:
        // lower-priority packs must not silently hide conflicts at the same
        // resource path. Sort resource paths for predictable diagnostics.
        var stacks = resources.listResourceStacks(
                DIRECTORY, id -> id.getPath().endsWith(".json")
        );
        List<Identifier> resourceIds = new ArrayList<>(stacks.keySet());
        resourceIds.sort(Comparator.comparing(Identifier::toString));

        Map<Identifier, PizzaToppingType> proposed = new HashMap<>();
        Set<Identifier> conflicted = new HashSet<>();
        int acceptedFiles = 0;
        int rejectedFiles = 0;
        int absentItems = 0;

        for (Identifier resourceId : resourceIds) {
            List<Resource> stack = stacks.get(resourceId);
            for (int index = 0; index < stack.size(); index++) {
                Resource file = stack.get(index);
                String origin = resourceId + " (pack entry " + (index + 1) + ")";
                try (Reader reader = file.openAsReader()) {
                    JsonElement root = JsonParser.parseReader(reader);
                    Mapping mapping = decode(root);

                    if (PizzaToppingCatalog.nativeItems().containsKey(mapping.itemId())) {
                        rejectedFiles++;
                        ItaliansDelight.LOGGER.warn(
                                "[Pizza] Ignoring datapack override of protected native item {} from {}",
                                mapping.itemId(), origin);
                        continue;
                    }

                    // Never allow a mapping to legalize explicitly forbidden
                    // whole foods, processing items or Minecraft air.
                    if (PizzaToppingCatalog.isExplicitlyExcluded(mapping.itemId())) {
                        rejectedFiles++;
                        ItaliansDelight.LOGGER.warn(
                                "[Pizza] Ignoring explicitly forbidden pizza item {} from {}",
                                mapping.itemId(), origin);
                        continue;
                    }

                    PizzaToppingType type = PizzaToppingCatalog.findType(mapping.canonicalId())
                            .orElse(null);
                    if (type == null || type.planned()) {
                        rejectedFiles++;
                        ItaliansDelight.LOGGER.warn(
                                "[Pizza] Unknown or not-yet-active canonical type {} in {}",
                                mapping.canonicalId(), origin);
                        continue;
                    }

                    // Optional dependencies: missing items do not crash or
                    // cause a runtime fallback to an unrelated item.
                    if (BuiltInRegistries.ITEM.getOptional(mapping.itemId()).isEmpty()) {
                        absentItems++;
                        continue;
                    }

                    acceptedFiles++;
                    PizzaToppingType earlier = proposed.putIfAbsent(mapping.itemId(), type);
                    if (earlier != null && !earlier.id().equals(type.id())) {
                        conflicted.add(mapping.itemId());
                        ItaliansDelight.LOGGER.warn(
                                "[Pizza] Conflicting datapack equivalents for {} ({} vs {}), "
                                        + "excluding that item",
                                mapping.itemId(), earlier.id(), type.id());
                    }
                } catch (IOException | RuntimeException exception) {
                    rejectedFiles++;
                    ItaliansDelight.LOGGER.warn(
                            "[Pizza] Invalid pizza equivalence JSON in {}: {}",
                            origin, exception.getMessage());
                }
            }
        }

        // Reject only the conflicting optional item IDs. All native mappings
        // and unrelated external equivalents remain usable.
        for (Map.Entry<Identifier, PizzaToppingType> entry : proposed.entrySet()) {
            if (conflicted.contains(entry.getKey())) {
                continue;
            }
            Item item = BuiltInRegistries.ITEM.getOptional(entry.getKey()).orElse(null);
            if (item != null) {
                next.put(item, entry.getValue());
            }
        }

        cachedByItem = Map.copyOf(next);
        ItaliansDelight.LOGGER.info(
                "[Pizza] Loaded {} topping item mappings ({} native expected, {} optional; "
                        + "{} accepted files, {} invalid/blocked, {} absent optional IDs, "
                        + "{} conflicting external IDs, {} missing native IDs)",
                cachedByItem.size(), PizzaToppingCatalog.nativeItems().size(),
                proposed.size() - conflicted.size(), acceptedFiles, rejectedFiles, absentItems,
                conflicted.size(), missingNativeItems);
    }

    /**
     * Strict parser: no implicit namespace, no extra properties and no tag
     * selectors. This is NOT a general-purpose ingredient-definition codec.
     */
    private static Mapping decode(JsonElement root) {
        if (!root.isJsonObject()) {
            throw new IllegalArgumentException("expected JSON object");
        }
        JsonObject obj = root.getAsJsonObject();
        if (obj.size() != 2 || !isString(obj.get("item"))
                || !isString(obj.get("canonical"))) {
            throw new IllegalArgumentException("expected exactly two string fields: item, canonical");
        }
        Identifier itemId = readId(obj.get("item").getAsString(), "item");
        Identifier canonicalId = readId(obj.get("canonical").getAsString(), "canonical");
        return new Mapping(itemId, canonicalId);
    }

    private static boolean isString(JsonElement value) {
        return value != null && value.isJsonPrimitive()
                && value.getAsJsonPrimitive().isString();
    }

    private static Identifier readId(String text, String field) {
        // Require fully qualified IDs: e.g. 'othermod:mozzarella_slice',
        // not 'mozzarella_slice' which would silently become minecraft:...
        Identifier id = text.contains(":") ? Identifier.tryParse(text) : null;
        if (id == null || !id.toString().equals(text)) {
            throw new IllegalArgumentException("invalid fully qualified " + field + " ID: " + text);
        }
        return id;
    }

    private record Mapping(Identifier itemId, Identifier canonicalId) {
    }
}
