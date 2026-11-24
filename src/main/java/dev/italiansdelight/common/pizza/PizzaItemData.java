package dev.italiansdelight.common.pizza;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;

/**
 * Complete immutable pizza payload. Cooking progress belongs to the OVEN,
 * not to a raw pizza ItemStack (same rule as a vanilla furnace).
 *
 * <p>Older 4A/4B stacks may contain "bake_progress". Mojang's record
 * MapCodec ignores unrecognized fields when decoding old saved components;
 * newly encoded pizzas contain only composition and legacy_mozzarella.
 */
public record PizzaItemData(PizzaComposition composition, boolean legacyMozzarella, int legacyMozzarellaMask) {
    public static final Codec<PizzaItemData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    PizzaComposition.CODEC.fieldOf("composition")
                            .forGetter(PizzaItemData::composition),
                    Codec.BOOL.fieldOf("legacy_mozzarella")
                            .forGetter(PizzaItemData::legacyMozzarella),
                    Codec.intRange(0, 15).optionalFieldOf("legacy_mozzarella_mask", 0)
                            .forGetter(PizzaItemData::legacyMozzarellaMask)
            ).apply(instance, PizzaItemData::new)
    );

    public PizzaItemData {
        Objects.requireNonNull(composition, "composition");
        if (legacyMozzarellaMask < 0 || legacyMozzarellaMask > 15) {
            throw new IllegalArgumentException("Invalid legacy mozzarella coverage");
        }
        // Old saves only contain the whole-pizza boolean. New mixed pizzas
        // may preserve that historical topping on individual quarters.
        if (legacyMozzarella) legacyMozzarellaMask = 15;
        legacyMozzarella = legacyMozzarellaMask == 15;
    }

    public PizzaItemData(PizzaComposition composition, boolean legacyMozzarella) {
        this(composition, legacyMozzarella, legacyMozzarella ? 15 : 0);
    }

    public boolean legacyMozzarellaAt(int quarter) {
        if (quarter < 0 || quarter >= PizzaComposition.QUARTER_COUNT) {
            throw new IndexOutOfBoundsException("Pizza quarter must be 0..3");
        }
        return (legacyMozzarellaMask & (1 << quarter)) != 0;
    }

    public static PizzaItemData empty() {
        return new PizzaItemData(PizzaComposition.empty(), false);
    }
}
