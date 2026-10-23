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
public record PizzaItemData(PizzaComposition composition, boolean legacyMozzarella) {
    public static final Codec<PizzaItemData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    PizzaComposition.CODEC.fieldOf("composition")
                            .forGetter(PizzaItemData::composition),
                    Codec.BOOL.fieldOf("legacy_mozzarella")
                            .forGetter(PizzaItemData::legacyMozzarella)
            ).apply(instance, PizzaItemData::new)
    );

    public PizzaItemData {
        Objects.requireNonNull(composition, "composition");
    }

    public static PizzaItemData empty() {
        return new PizzaItemData(PizzaComposition.empty(), false);
    }
}
