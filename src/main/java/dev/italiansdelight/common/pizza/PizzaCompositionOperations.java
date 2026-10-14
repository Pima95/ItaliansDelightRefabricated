package dev.italiansdelight.common.pizza;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.world.item.ItemStack;

/**
 * Pure, all-or-nothing operations on the immutable pizza composition.
 *
 * <p>These operations deliberately do NOT change inventories or the world:
 * callers must check permissions and item availability, then commit the new
 * composition and item consumption/refund on the server as one interaction.
 * In particular, do not enable live ingredient consumption before pizza
 * block drops and transfer-to-item persistence are implemented.
 */
public final class PizzaCompositionOperations {
    private PizzaCompositionOperations() {
    }

    /**
     * Add one original item to previously uncovered physical quarters only.
     * A differently named sauce anywhere in the target zone rejects the
     * entire click even if some other quarters are free.
     *
     * @return updated composition, or empty if invalid/duplicated/overfull
     */
    public static Optional<PizzaComposition> add(
            PizzaComposition pizza,
            PizzaToppingType type,
            ItemStack ingredient,
            int zoneMask
    ) {
        if (!validZone(zoneMask) || ingredient.isEmpty() || type.planned()
                || !PizzaToppingCatalog.isActiveType(type.id())
                || pizza.nextApplicationId() == Long.MAX_VALUE) {
            return Optional.empty();
        }

        int uncovered = zoneMask;
        for (PizzaToppingApplication application : pizza.applications()) {
            int overlapping = application.remainingMask() & zoneMask;
            if (overlapping == 0) {
                continue;
            }
            if (application.canonicalId().equals(type.id())) {
                uncovered &= ~overlapping;
            }
            // Reject DIFFERENT sauce in any part of the entire intended zone.
            PizzaToppingType existing = PizzaToppingCatalog.findType(
                    application.canonicalId()).orElse(null);
            if (type.role() == PizzaToppingType.Role.SAUCE
                    && existing != null && existing.role() == PizzaToppingType.Role.SAUCE
                    && !existing.id().equals(type.id())) {
                return Optional.empty();
            }
        }
        if (uncovered == 0) {
            return Optional.empty(); // Exact duplicate: consume nothing.
        }

        // Check ALL affected physical quarters before changing anything.
        for (int quarter = 0; quarter < PizzaComposition.QUARTER_COUNT; quarter++) {
            if ((uncovered & (1 << quarter)) == 0) {
                continue;
            }
            if (pizza.toppingsForQuarter(quarter).size()
                    >= PizzaComposition.MAX_TOPPINGS_PER_QUARTER) {
                return Optional.empty();
            }
        }

        ItemStack originalOne = ingredient.copy();
        originalOne.setCount(1);
        PizzaToppingApplication application = new PizzaToppingApplication(
                pizza.nextApplicationId(), type.id(), originalOne, uncovered, uncovered);
        List<PizzaToppingApplication> updated = new ArrayList<>(pizza.applications());
        updated.add(application);
        return Optional.of(pizza.withApplications(updated, pizza.nextApplicationId() + 1));
    }

    /**
     * Remove coverage of the most recent application visible in this zone.
     * A partial removal does not refund anything; the original stack is
     * returned exactly once when its last covered quarter is removed.
     */
    public static Optional<Removal> removeLast(PizzaComposition pizza, int zoneMask) {
        if (!validZone(zoneMask)) {
            return Optional.empty();
        }
        List<PizzaToppingApplication> updated = new ArrayList<>(pizza.applications());
        for (int i = updated.size() - 1; i >= 0; i--) {
            PizzaToppingApplication existing = updated.get(i);
            int newMask = existing.remainingMask() & ~zoneMask;
            if (newMask == existing.remainingMask()) {
                continue;
            }

            ItemStack refund = ItemStack.EMPTY;
            if (newMask == 0) {
                updated.remove(i);
                refund = existing.originalStack();
            } else {
                updated.set(i, existing.withRemainingMask(newMask));
            }
            return Optional.of(new Removal(
                    pizza.withApplications(updated, pizza.nextApplicationId()), refund));
        }
        return Optional.empty();
    }

    private static boolean validZone(int zoneMask) {
        return zoneMask > 0 && (zoneMask & ~PizzaQuarterSelection.ALL_QUARTERS) == 0;
    }

    /** Returned ItemStack is a copy and must only be granted after commit. */
    public record Removal(PizzaComposition composition, ItemStack refund) {
        public Removal {
            refund = refund.copy();
        }

        @Override
        public ItemStack refund() {
            return refund.copy();
        }
    }
}
