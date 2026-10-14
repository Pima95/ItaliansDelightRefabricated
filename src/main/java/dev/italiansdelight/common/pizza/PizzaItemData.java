package dev.italiansdelight.common.pizza;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;

/**
 * Complete payload of a formed, uncooked pizza while it is an ItemStack.
 *
 * <p>Keep the old mozzarella proof-of-concept flag until the general topping
 * system replaces it; otherwise collecting/replacing existing test pizzas
 * would delete a consumed ingredient. The canonical composition includes
 * division state, orientation, all applications, masks and original stacks.
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
