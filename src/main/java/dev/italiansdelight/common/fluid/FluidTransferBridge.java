package dev.italiansdelight.common.fluid;

import dev.italiansdelight.common.block.entity.BarrelBlockEntity;
import dev.italiansdelight.common.block.entity.FluidCauldronBlockEntity;
import dev.italiansdelight.common.registry.ModBlockEntities;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluids;

/**
 * Shared Fabric Transfer API bridge. Any mod exposing FluidStorage.ITEM works
 * without hardcoded fluid IDs, item classes, and without loss of component data.
 * Both world tanks expose FluidStorage.SIDED for compatible pipes/machines.
 */
public final class FluidTransferBridge {
    private FluidTransferBridge() {}

    public static void register() {
        FluidStorage.SIDED.registerForBlockEntity(
            (BarrelBlockEntity be, net.minecraft.core.Direction side) -> be.getFluidStorage(),
            ModBlockEntities.BARREL);
        FluidStorage.SIDED.registerForBlockEntity(
            (FluidCauldronBlockEntity be, net.minecraft.core.Direction side) -> be.getFluidStorage(),
            ModBlockEntities.FLUID_CAULDRON);
    }

    public static Storage<FluidVariant> itemStorage(Player player, InteractionHand hand) {
        return ContainerItemContext.ofPlayerHand(player, hand).find(FluidStorage.ITEM);
    }

    /**
     * Transactionally moves from held container into tank, or from tank into
     * container. No stack substitution is done manually: ITEM providers
     * perform their own item replacement in the same atomic transaction.
     */
    public static boolean transfer(Player player, InteractionHand hand, CompatibleFluidTank tank) {
        Storage<FluidVariant> item = itemStorage(player, hand);
        if (item == null) return false;
        // Prefer putting an actually filled held container into the tank.
        for (StorageView<FluidVariant> view : item.nonEmptyViews()) {
            FluidVariant resource = view.getResource();
            if (resource.isBlank() || resource.getFluid() == Fluids.LAVA) continue;
            long transfer = Math.min(view.getAmount(), tank.getCapacity() - tank.amountDroplets());
            if (transfer <= 0) continue;
            try (Transaction tx = Transaction.openOuter()) {
                long taken = item.extract(resource, transfer, tx);
                if (taken <= 0) continue;
                long accepted = tank.insert(resource, taken, tx);
                if (taken == accepted) { tx.commit(); return true; }
            }
        }
        // A container capable of taking fluid can be filled by the same API.
        FluidVariant resource = tank.currentVariant();
        if (!resource.isBlank() && tank.amountDroplets() > 0) {
            try (Transaction tx = Transaction.openOuter()) {
                long inserted = item.insert(resource, tank.amountDroplets(), tx);
                if (inserted > 0 && tank.extract(resource, inserted, tx) == inserted) {
                    tx.commit();
                    return true;
                }
            }
        }
        return false;
    }

    /** Peek at the held fluid without changing the inventory. */
    public static FluidVariant heldSource(Player player, InteractionHand hand) {
        Storage<FluidVariant> storage = itemStorage(player, hand);
        if (storage == null) return FluidVariant.blank();
        for (StorageView<FluidVariant> view : storage.nonEmptyViews()) {
            if (!view.getResource().isBlank() && view.getAmount() >= FluidConstants.BOTTLE)
                return view.getResource();
        }
        return FluidVariant.blank();
    }
}
