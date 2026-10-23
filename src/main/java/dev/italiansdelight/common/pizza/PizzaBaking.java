package dev.italiansdelight.common.pizza;

import dev.italiansdelight.common.registry.ModDataComponents;
import dev.italiansdelight.common.registry.ModItems;
import dev.italiansdelight.common.registry.ModRegistries;
import java.util.List;
import java.util.Optional;
import net.minecraft.resources.Identifier;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Server-side helper for baking pizzas while preserving all topping data.
 *
 * <p>Cooking progress is now owned ONLY by the furnace BlockEntity, never
 * by raw_pizza or by a placed pizza. Oven progress resets when the input is
 * removed, just as in a vanilla furnace. The oven is responsible for fuel,
 * ticking, inventory capacity and committing the result exactly once.
 *
 * <p>Bowls are counted from original deposited APPLICATIONS, not from the
 * number of sauce kinds or the number of quarters. Thus tomato sauce on one
 * half + cream on the other gives TWO bowls even though they occupy one
 * pizza and may ultimately appear in one stacked ItemStack.
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
        PIZZA_OVEN(3), // 400 active ticks, 20 sec FINAL
        SMOKER(2),     // 600 active ticks, 30 sec FINAL
        FURNACE(1);    // 1200 active ticks, 60 sec FINAL

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
     * Progress calculation only. The caller stores the result in its OWN
     * BlockEntity, never on an ItemStack. No fuel is spent by this helper.
     */
    public static int advance(int current, Oven oven, int activeTicks) {
        if (current < 0 || activeTicks < 0) {
            throw new IllegalArgumentException("Cooking progress/ticks cannot be negative");
        }
        long next = (long) current + (long) oven.unitsPerTick() * activeTicks;
        return (int) Math.min(MAX_PROGRESS, next);
    }

    /**
     * Vanilla furnaces call assemble repeatedly while testing whether their
     * output slot can receive a result. Do not count/create bowl remainders
     * until a real cooking transaction finishes.
     *
     * Only cosmetic/custom metadata approved for transfer is copied, never
     * RAW_PIZZA item-specific behavior, max stack size, food, or ITEM_MODEL.
     */
    public static ItemStack previewCookedPizza(ItemStack rawPizza) {
        if (!rawPizza.is(ModItems.RAW_PIZZA)) {
            return ItemStack.EMPTY;
        }
        PizzaItemData data = rawPizza.get(ModDataComponents.PIZZA_CONTENTS);
        if (data == null) {
            return ItemStack.EMPTY;
        }

        ItemStack cooked = new ItemStack(ModItems.COOKED_PIZZA);
        cooked.set(ModDataComponents.PIZZA_CONTENTS, data);
        PizzaDisplayNames.nameWholeStack(cooked, data);

        // ITEM_NAME remains derived from the four real quarters. An explicit
        // anvil name takes precedence, and arbitrary mod/datapack annotations
        // kept in CUSTOM_DATA or LORE must survive cooking all three ways.
        if (rawPizza.has(DataComponents.CUSTOM_NAME)) {
            cooked.set(DataComponents.CUSTOM_NAME, rawPizza.get(DataComponents.CUSTOM_NAME));
        }
        if (rawPizza.has(DataComponents.LORE)) {
            cooked.set(DataComponents.LORE, rawPizza.get(DataComponents.LORE));
        }
        if (rawPizza.has(DataComponents.CUSTOM_DATA)) {
            cooked.set(DataComponents.CUSTOM_DATA, rawPizza.get(DataComponents.CUSTOM_DATA));
        }
        return cooked;
    }

    /**
     * An oven may finish ONLY once it has completed its internal cook time.
     * Input remains untouched here; the oven performs the authoritative
     * transaction of one raw pizza -> one cooked pizza + bowl remainders.
     */
    public static Optional<BakingResult> finish(ItemStack rawPizza, int ovenProgress) {
        if (ovenProgress < MAX_PROGRESS) {
            return Optional.empty();
        }
        ItemStack cooked = previewCookedPizza(rawPizza);
        if (cooked.isEmpty()) {
            return Optional.empty();
        }
        PizzaItemData data = rawPizza.get(ModDataComponents.PIZZA_CONTENTS);
        int bowls = 0;
        for (PizzaToppingApplication application : data.composition().applications()) {
            if (application.remainingMask() != 0
                    && !application.bowlReturnedOnCrafting()
                && isBowlBearing(application.canonicalId())) {
                bowls++;
            }
        }

        // Consolidate into a stack with the CORRECT count; a single visible
        // drop may represent multiple bowls, not just one item.
        List<ItemStack> remainders = bowls > 0
                ? List.of(new ItemStack(Items.BOWL, bowls))
                : List.of();
        return Optional.of(new BakingResult(cooked, remainders));
    }

    /** Same container classification for baking and shapeless crafting. */
    public static boolean isBowlBearing(Identifier canonical) {
        return TOMATO_SAUCE.equals(canonical)
                || CREAM_BOWL.equals(canonical)
                || RICOTTA.equals(canonical);
    }

    /** Immutable, defensive output prepared for one authoritative transaction. */
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
