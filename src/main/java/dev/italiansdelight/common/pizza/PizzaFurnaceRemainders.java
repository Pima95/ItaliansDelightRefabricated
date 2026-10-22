package dev.italiansdelight.common.pizza;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.HopperBlockEntity;

/**
 * Return emptied sauce/cream bowls from vanilla furnaces and smokers.
 *
 * <p>This is the single transfer policy used by Pizza Oven, Furnace and
 * Smoker. Invoked ONLY at the corresponding oven's successful cooking commit
 * (never during recipe previews or input changes).
 * One original bowl-bearing topping APPLICATION yields exactly one bowl,
 * irrespective of the number of quarters it covers.
 */
public final class PizzaFurnaceRemainders {
    private PizzaFurnaceRemainders() {
    }

    public static void deliver(ServerLevel level, BlockPos furnacePos, ItemStack remainder) {
        ItemStack left = remainder.copy();

        // Respect the pre-existing rule: try the hopper below first and eject
        // only the excess. Never insert containers into the pizza output slot.
        if (level.getBlockEntity(furnacePos.below()) instanceof HopperBlockEntity hopper) {
            for (int slot = 0; slot < hopper.getContainerSize() && !left.isEmpty(); slot++) {
                ItemStack existing = hopper.getItem(slot);
                // Respect the full component identity of a stored bowl.
                // Merging with a renamed/custom bowl would silently discard
                // the returned bowls' own stack identity.
                if (!existing.isEmpty()
                        && !ItemStack.isSameItemSameComponents(existing, left)) {
                    continue;
                }
                int max = existing.isEmpty() ? left.getMaxStackSize() : existing.getMaxStackSize();
                int moved = Math.min(left.getCount(), max - existing.getCount());
                if (moved <= 0) {
                    continue;
                }
                if (existing.isEmpty()) {
                    hopper.setItem(slot, left.copyWithCount(moved));
                } else {
                    existing.grow(moved);
                    hopper.setChanged();
                }
                left.shrink(moved);
            }
        }

        if (!left.isEmpty()) {
            Containers.dropItemStack(level,
                    furnacePos.getX() + 0.5,
                    furnacePos.getY() + 1.0,
                    furnacePos.getZ() + 0.5,
                    left);
        }
    }
}
