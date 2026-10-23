package dev.italiansdelight.common.pizza;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.world.item.ItemStack;

/**
 * One full pizza stored INSIDE a nonstackable peel. Storing the complete
 * ItemStack (rather than reconstructing its ingredients or item name) keeps
 * future pizza components and custom names intact across transfers and Q.
 *
 * <p>Absent PEEL_CARGO means the peel is empty. There is no separate pizza
 * item in the player's inventory while the peel is occupied.
 */
public record PizzaPeelCargo(ItemStack pizza) {
    public static final Codec<PizzaPeelCargo> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(ItemStack.CODEC.fieldOf("pizza").forGetter(PizzaPeelCargo::pizza))
                    .apply(instance, PizzaPeelCargo::new));

    public PizzaPeelCargo {
        Objects.requireNonNull(pizza, "pizza");
        if (pizza.isEmpty() || pizza.getCount() != 1) {
            throw new IllegalArgumentException("Pizza peel can only contain a single pizza");
        }
        pizza = pizza.copy();
    }

    @Override
    public ItemStack pizza() {
        return pizza.copy();
    }
}
