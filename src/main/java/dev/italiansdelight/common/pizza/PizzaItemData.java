package dev.italiansdelight.common.pizza;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;

/**
 * Complete payload of a formed pizza while it is an ItemStack.
 *
 * <p>Versioned composition, legacy mozzarella flag and normalized baking
 * progress survive item drops, inventory transfers, placement in the world,
 * and later pizza oven/peel transfers. There is no independent "partially
 * baked pizza" item; until completion it remains RAW_PIZZA.
 *
 * <p>The baking field is optional in the codec: old pizzas without it
 * deserialize as zero progress, without migrating/losing their toppings.
 */
public record PizzaItemData(
        PizzaComposition composition,
        boolean legacyMozzarella,
        int bakeProgress
) {
    public static final Codec<PizzaItemData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    PizzaComposition.CODEC.fieldOf("composition")
                            .forGetter(PizzaItemData::composition),
                    Codec.BOOL.fieldOf("legacy_mozzarella")
                            .forGetter(PizzaItemData::legacyMozzarella),
                    Codec.INT.optionalFieldOf("bake_progress", 0)
                            .forGetter(PizzaItemData::bakeProgress)
            ).apply(instance, PizzaItemData::new)
    );

    public PizzaItemData {
        Objects.requireNonNull(composition, "composition");
        if (bakeProgress < 0 || bakeProgress > PizzaBaking.MAX_PROGRESS) {
            throw new IllegalArgumentException(
                    "Invalid normalized pizza baking progress: " + bakeProgress);
        }
    }

    /** Existing world-item conversions still start with no cooking progress. */
    public PizzaItemData(PizzaComposition composition, boolean legacyMozzarella) {
        this(composition, legacyMozzarella, 0);
    }

    public PizzaItemData withBakeProgress(int progress) {
        return new PizzaItemData(composition, legacyMozzarella, progress);
    }

    public static PizzaItemData empty() {
        return new PizzaItemData(PizzaComposition.empty(), false, 0);
    }
}
