package dev.italiansdelight.common.fluid;

import dev.italiansdelight.common.registry.ModBlockEntities;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;

/** Native NeoForge fluid capabilities and inventory-safe hand transfers. */
public final class FluidTransferBridge {
    private FluidTransferBridge() {}

    public static void register(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK,
            ModBlockEntities.BARREL, (be, side) -> be.getFluidStorage());
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK,
            ModBlockEntities.FLUID_CAULDRON, (be, side) -> be.getFluidStorage());
    }

    public static IFluidHandlerItem itemStorage(Player player, InteractionHand hand) {
        // Capabilities for stackable containers must be queried on one item.
        return FluidUtil.getFluidHandler(player.getItemInHand(hand).copyWithCount(1)).orElse(null);
    }

    public static boolean transfer(Player player, InteractionHand hand, CompatibleFluidTank tank) {
        if (itemStorage(player, hand) == null) return false;
        FluidStack source = heldSource(player, hand);
        if (!source.isEmpty() && (source.is(Fluids.LAVA) || source.is(Fluids.FLOWING_LAVA))) return false;
        // A vanilla bucket or fixed bottle cannot encode arbitrary fluid components.
        if (!tank.currentVariant().isComponentsPatchEmpty() &&
            (player.getItemInHand(hand).is(Items.BUCKET) ||
             player.getItemInHand(hand).is(Items.GLASS_BOTTLE))) return false;
        return FluidUtil.interactWithFluidHandler(player, hand, tank);
    }

    public static FluidStack heldSource(Player player, InteractionHand hand) {
        IFluidHandlerItem handler = itemStorage(player, hand);
        if (handler == null) return FluidStack.EMPTY;
        for (int i = 0; i < handler.getTanks(); i++) {
            FluidStack fluid = handler.getFluidInTank(i);
            if (!fluid.isEmpty() && fluid.getAmount() >= 250) return fluid.copy();
        }
        return FluidStack.EMPTY;
    }
}
