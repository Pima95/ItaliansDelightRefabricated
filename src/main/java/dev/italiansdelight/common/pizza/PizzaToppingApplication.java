package dev.italiansdelight.common.pizza;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

/**
 * One ingredient consumed once and painted over one or more physical quarters.
 * Kept in chronological order by {@link PizzaComposition}.
 *
 * <p>Original and remaining masks use four bits (1-15). Partial removal may
 * change the remaining mask, but must NOT return the original stack until the
 * whole application has been removed, exactly once.
 */
public record PizzaToppingApplication(
        long sequence,
        Identifier canonicalId,
        ItemStack originalStack,
        int originalMask,
        int remainingMask
) {
    public static final Codec<PizzaToppingApplication> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.LONG.fieldOf("sequence").forGetter(PizzaToppingApplication::sequence),
                    Identifier.CODEC.fieldOf("canonical").forGetter(PizzaToppingApplication::canonicalId),
                    ItemStack.CODEC.fieldOf("original_stack").forGetter(PizzaToppingApplication::originalStack),
                    Codec.INT.fieldOf("original_mask").forGetter(PizzaToppingApplication::originalMask),
                    Codec.INT.fieldOf("remaining_mask").forGetter(PizzaToppingApplication::remainingMask)
            ).apply(instance, PizzaToppingApplication::new)
    );

    public PizzaToppingApplication {
        Objects.requireNonNull(canonicalId, "canonicalId");
        Objects.requireNonNull(originalStack, "originalStack");
        if (sequence <= 0) {
            throw new IllegalArgumentException("Pizza topping sequence must be positive");
        }
        if (originalStack.isEmpty() || originalStack.getCount() != 1) {
            throw new IllegalArgumentException("Exactly one original item per topping application");
        }
        if (originalMask < 1 || originalMask > 15 || remainingMask < 1
                || (remainingMask & ~originalMask) != 0) {
            throw new IllegalArgumentException("Invalid pizza quarter coverage masks");
        }
        // Keep the source stack's full component data, but never leak a mutable
        // reference to the stack owned by the saved pizza.
        originalStack = originalStack.copy();
    }

    @Override
    public ItemStack originalStack() {
        return originalStack.copy();
    }

    public boolean covers(int quarterIndex) {
        if (quarterIndex < 0 || quarterIndex >= 4) {
            throw new IndexOutOfBoundsException("Pizza quarter must be 0..3");
        }
        return (remainingMask & (1 << quarterIndex)) != 0;
    }

    /** Keep the application identity and refundable source when partly removed. */
    public PizzaToppingApplication withRemainingMask(int mask) {
        return new PizzaToppingApplication(sequence, canonicalId, originalStack, originalMask, mask);
    }
}
