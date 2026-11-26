package dev.italiansdelight.common.fluid;

import dev.italiansdelight.common.registry.ModFluids;
import dev.italiansdelight.common.registry.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import java.util.IdentityHashMap;
import java.util.Map;

/** Fixed-volume, lossless container swaps. Alcoholic quality remains in
 * the brewer-specific IO; native vanilla bucket capabilities are not replaced.
 */
public final class NeoForgeFluidItemContainers {
    private NeoForgeFluidItemContainers() {}
    private record Portion(Item full, Item empty, Fluid fluid, int amount) {}
    private static final Map<Fluid, Portion> BOTTLES = new IdentityHashMap<>();

    public static void register(RegisterCapabilitiesEvent event) {
        storageOnly(event, ModFluids.SHEEP_MILK, ModItems.SHEEP_MILK_BUCKET, ModItems.SHEEP_MILK_BOTTLE);
        storageOnly(event, ModFluids.WHEY, ModItems.WHEY_BUCKET, ModItems.WHEY_BOTTLE);
        storageOnly(event, ModFluids.RENNET, ModItems.RENNET_BUCKET, ModItems.RENNET);
        storageOnly(event, ModFluids.APPLE_CIDER_VINEGAR, ModItems.APPLE_CIDER_VINEGAR_BUCKET, ModItems.APPLE_CIDER_VINEGAR);
        storageOnly(event, ModFluids.CHERRY_JUICE, ModItems.CHERRY_JUICE_BUCKET, ModItems.CHERRY_JUICE_BOTTLE);
        storageOnly(event, ModFluids.RED_WINE_VINEGAR, ModItems.RED_WINE_VINEGAR_BUCKET, ModItems.RED_WINE_VINEGAR_BOTTLE);
        bottle(event, ModFluids.OLIVE_OIL_STILL, ModItems.OLIVE_OIL_BOTTLE);
        bottle(event, ModFluids.APPLE_JUICE_STILL, ModItems.APPLE_JUICE_BOTTLE);
        storageOnly(event, ModFluids.GOLDEN_APPLE_JUICE, ModItems.GOLDEN_APPLE_JUICE_BUCKET, ModItems.GOLDEN_APPLE_JUICE_BOTTLE);
        storageOnly(event, ModFluids.GRAPE_JUICE, ModItems.GRAPE_JUICE_BUCKET, ModItems.GRAPE_JUICE_BOTTLE);
        storageOnly(event, ModFluids.WHITE_GRAPE_JUICE, ModItems.WHITE_GRAPE_JUICE_BUCKET, ModItems.WHITE_GRAPE_JUICE_BOTTLE);
        event.registerItem(Capabilities.FluidHandler.ITEM,
            (stack, ctx) -> new FixedContainer(stack, null, 250), Items.GLASS_BOTTLE);
    }

    private static void storageOnly(RegisterCapabilitiesEvent event, Fluid fluid, Item fullBucket, Item fullBottle) {
        full(event, new Portion(fullBucket, Items.BUCKET, fluid, 1000));
        bottle(event, fluid, fullBottle);
    }

    private static void bottle(RegisterCapabilitiesEvent event, Fluid fluid, Item item) {
        Portion portion = new Portion(item, Items.GLASS_BOTTLE, fluid, 250);
        BOTTLES.put(fluid, portion);
        full(event, portion);
    }

    private static void full(RegisterCapabilitiesEvent event, Portion portion) {
        event.registerItem(Capabilities.FluidHandler.ITEM,
            (stack, ctx) -> new FixedContainer(stack, portion, portion.amount()), portion.full());
    }

    private static final class FixedContainer implements IFluidHandlerItem {
        private ItemStack container;
        private Portion portion;
        private final int capacity;

        private FixedContainer(ItemStack container, Portion portion, int capacity) {
            this.container = container;
            this.portion = portion;
            this.capacity = capacity;
        }

        @Override public ItemStack getContainer() { return container; }
        @Override public int getTanks() { return 1; }
        @Override public int getTankCapacity(int tank) { return capacity; }
        @Override public FluidStack getFluidInTank(int tank) {
            return portion == null ? FluidStack.EMPTY : new FluidStack(portion.fluid(), portion.amount());
        }
        @Override public boolean isFluidValid(int tank, FluidStack fluid) {
            return capacity == 250 && !fluid.isEmpty() && fluid.isComponentsPatchEmpty()
                && BOTTLES.containsKey(fluid.getFluid());
        }
        @Override public int fill(FluidStack fluid, FluidAction action) {
            if (container.getCount() != 1 || portion != null || fluid.getAmount() < capacity
                || !isFluidValid(0, fluid)) return 0;
            Portion next = BOTTLES.get(fluid.getFluid());
            if (action.execute()) {
                container = new ItemStack(next.full());
                portion = next;
            }
            return capacity;
        }
        @Override public FluidStack drain(FluidStack fluid, FluidAction action) {
            if (fluid.isEmpty() || !FluidStack.isSameFluidSameComponents(fluid, getFluidInTank(0)))
                return FluidStack.EMPTY;
            return drain(fluid.getAmount(), action);
        }
        @Override public FluidStack drain(int amount, FluidAction action) {
            if (container.getCount() != 1 || portion == null || amount < portion.amount())
                return FluidStack.EMPTY;
            FluidStack result = getFluidInTank(0);
            if (action.execute()) {
                container = new ItemStack(portion.empty());
                portion = null;
            }
            return result;
        }
    }
}
