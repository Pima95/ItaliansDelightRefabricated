package dev.italiansdelight.common.pizza;

import dev.italiansdelight.common.registry.ModDataComponents;
import dev.italiansdelight.common.registry.ModItems;
import dev.italiansdelight.common.registry.ModRegistries;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Shared, side-effect-free baking rules for the future Pizza Oven, vanilla
 * furnace and smoker integrations.
 *
 * <p>Progress is stored on the RAW_PIZZA's data component (and mirrored in
 * its placed PizzaDoughBlockEntity). It is NORMALIZED to 1200 units so the
 * initially agreed placeholder durations correspond to exact integer rates:
 * Pizza Oven: 400 working ticks; smoker: 600; furnace: 1200.
 *
 * <p>Only an active cooking machine may call advance() and only for ticks
 * during which it actually spent fuel. This class never consumes fuel.
 * The future machine must atomically consume ONE raw pizza before issuing
 * the result and container remainders returned from finish(). Never call
 * finish() twice for the same physical input.
 */
public final class PizzaBaking {
    public static final int MAX_PROGRESS = 1_200;

    private static final Identifier TOMATO_SAUCE =
            ModRegistries.id("pizza_toppings/tomato_sauce");
    private static final Identifier CREAM_BOWL =
            ModRegistries.id("pizza_toppings/cream_bowl");
    private static final Identifier RICOTTA =
            ModRegistries.id("pizza_toppings/ricotta");

    public enum Oven {
        PIZZA_OVEN(3), // 400 active ticks, ~20 sec placeholder
        SMOKER(2),     // 600 active ticks, ~30 sec placeholder
        FURNACE(1);    // 1200 active ticks, ~60 sec placeholder

        private final int unitsPerTick;

        Oven(int unitsPerTick) {
            this.unitsPerTick = unitsPerTick;
        }

        public int unitsPerTick() {
            return unitsPerTick;
        }
    }

    private PizzaBaking() {
    }

    /**
     * Progress stays frozen when a machine is paused, empty or out of fuel:
     * callers simply do not invoke this method for idle ticks.
     * Switching between ovens preserves the same completion FRACTION.
     */
    public static PizzaItemData advance(PizzaItemData data, Oven oven, int activeTicks) {
        if (activeTicks < 0) {
            throw new IllegalArgumentException("Cooking ticks cannot be negative");
        }
        if (activeTicks == 0 || data.bakeProgress() == MAX_PROGRESS) {
            return data;
        }
        long next = (long) data.bakeProgress()
                + (long) oven.unitsPerTick() * activeTicks;
        return data.withBakeProgress((int) Math.min(MAX_PROGRESS, next));
    }

    public static boolean isReady(PizzaItemData data) {
        return data.bakeProgress() == MAX_PROGRESS;
    }

    /**
     * Prepare the immutable outcome of the baking transaction. An oven must
     * check capacity, consume its ONE raw input, then commit both the cooked
     * pizza and the returned bowls exactly once, with no exposed intermediate
     * state or duplicate calls. This helper never modifies the input stack.
     */
    public static Optional<BakingResult> finish(ItemStack rawPizza) {
        if (!rawPizza.is(ModItems.RAW_PIZZA)) {
            return Optional.empty();
        }
        PizzaItemData data = rawPizza.get(ModDataComponents.PIZZA_CONTENTS);
        if (data == null || !isReady(data)) {
            return Optional.empty();
        }

        ItemStack cooked = new ItemStack(ModItems.COOKED_PIZZA);
        cooked.set(ModDataComponents.PIZZA_CONTENTS, data);

        // Every original application consumed ONE source item, regardless of
        // how many physical quarters it covered. Therefore one container
        // belongs to one deposited application, NEVER one per quarter.
        // A partially removed application still has its original bowl until
        // the LAST quarter has been removed/refunded. Only non-empty saved
        // applications remain in composition.applications().
        List<ItemStack> returnedContainers = new ArrayList<>();
        for (PizzaToppingApplication application : data.composition().applications()) {
            if (application.remainingMask() != 0
                    && isBowlBearing(application.canonicalId())) {
                returnedContainers.add(new ItemStack(Items.BOWL));
            }
        }
        return Optional.of(new BakingResult(cooked, returnedContainers));
    }

    private static boolean isBowlBearing(Identifier canonical) {
        return TOMATO_SAUCE.equals(canonical)
                || CREAM_BOWL.equals(canonical)
                || RICOTTA.equals(canonical);
    }

    /** Defensive result; the future oven owns the atomic transfer. */
    public record BakingResult(ItemStack cookedPizza, List<ItemStack> containers) {
        public BakingResult {
            cookedPizza = cookedPizza.copy();
            containers = containers.stream().map(ItemStack::copy).toList();
        }

        @Override
        public ItemStack cookedPizza() {
            return cookedPizza.copy();
        }

        @Override
        public List<ItemStack> containers() {
            return containers.stream().map(ItemStack::copy).toList();
        }
    }
}
