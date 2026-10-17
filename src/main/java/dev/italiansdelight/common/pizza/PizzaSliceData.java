package dev.italiansdelight.common.pizza;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

/**
 * One physical cooked pizza quarter, independent of all other quarters.
 *
 * <p>There is one record per ORIGINAL ingredient visible on that quarter,
 * not one per physical piece of its original application. Original ItemStacks
 * are retained as read-only provenance for matching/names/tooltips later:
 * baked toppings are NOT refundable as raw source items.
 */
public record PizzaSliceData(int quarter, List<Topping> toppings, boolean legacyMozzarella) {
    public static final Codec<PizzaSliceData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("quarter").forGetter(PizzaSliceData::quarter),
                    Topping.CODEC.listOf().fieldOf("toppings").forGetter(PizzaSliceData::toppings),
                    Codec.BOOL.optionalFieldOf("legacy_mozzarella", false)
                            .forGetter(PizzaSliceData::legacyMozzarella)
            ).apply(instance, PizzaSliceData::new)
    );

    public PizzaSliceData {
        if (quarter < 0 || quarter >= PizzaComposition.QUARTER_COUNT) {
            throw new IllegalArgumentException("Invalid pizza slice quarter");
        }
        toppings = List.copyOf(Objects.requireNonNull(toppings, "toppings"));
    }

    public record Topping(Identifier canonicalId, ItemStack originalStack) {
        public static final Codec<Topping> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Identifier.CODEC.fieldOf("canonical").forGetter(Topping::canonicalId),
                        ItemStack.CODEC.fieldOf("original_stack").forGetter(Topping::originalStack)
                ).apply(instance, Topping::new)
        );

        public Topping {
            Objects.requireNonNull(canonicalId, "canonicalId");
            if (originalStack.isEmpty() || originalStack.getCount() != 1) {
                throw new IllegalArgumentException("Pizza slice ingredient must be one item");
            }
            originalStack = originalStack.copy();
        }

        @Override
        public ItemStack originalStack() {
            return originalStack.copy();
        }
    }

    public static PizzaSliceData fromPizza(PizzaItemData whole, int quarter) {
        List<Topping> selected = new ArrayList<>();
        for (PizzaToppingApplication application : whole.composition().applications()) {
            if (application.covers(quarter)) {
                selected.add(new Topping(application.canonicalId(), application.originalStack()));
            }
        }
        return new PizzaSliceData(quarter, selected, whole.legacyMozzarella());
    }

    public static PizzaSliceData empty() {
        return new PizzaSliceData(0, List.of(), false);
    }
}
