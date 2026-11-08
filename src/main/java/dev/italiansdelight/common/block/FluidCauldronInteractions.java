package dev.italiansdelight.common.block;

import dev.italiansdelight.common.block.entity.FluidCauldronBlockEntity;
import dev.italiansdelight.common.registry.ModBlocks;
import dev.italiansdelight.common.fluid.FluidTransferBridge;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

/**
 * Insert custom liquids into the actual vanilla EMPTY cauldron without
 * changing the vanilla block class, CauldronInteraction maps or mixins.
 * Vanilla water and lava must keep their original cauldron mechanics.
 */
public final class FluidCauldronInteractions {
    private FluidCauldronInteractions() {}

    public static void register() {
        UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
            if (player.isSpectator() ||
                !level.getBlockState(hit.getBlockPos()).is(Blocks.CAULDRON))
                return InteractionResult.PASS;
            ItemStack held = player.getItemInHand(hand);
            // Registered fluids from other mods, regardless of bucket/item
            // implementation, arrive via ITEM storage. Vanilla water/lava
            // continue to use the original cauldron maps.
            Storage<FluidVariant> external = FluidTransferBridge.itemStorage(player, hand);
            if (external != null) {
                FluidVariant heldFluid = FluidTransferBridge.heldSource(player, hand);
                if (!heldFluid.isBlank()
                    && heldFluid.getFluid() != net.minecraft.world.level.material.Fluids.WATER
                    && heldFluid.getFluid() != net.minecraft.world.level.material.Fluids.LAVA) {
                    if (level.isClientSide()) return InteractionResult.SUCCESS;
                    long available = 0;
                    for (StorageView<FluidVariant> view : external.nonEmptyViews())
                        if (heldFluid.equals(view.getResource())) available += view.getAmount();
                    int levelCount = available >= FluidConstants.BUCKET ? 4 : 1;
                    if (!level.setBlockAndUpdate(hit.getBlockPos(),
                            ModBlocks.FLUID_CAULDRON.defaultBlockState()
                                .setValue(FluidCauldronBlock.LEVEL, levelCount)))
                        return InteractionResult.FAIL;
                    if (!(level.getBlockEntity(hit.getBlockPos())
                            instanceof FluidCauldronBlockEntity target) ||
                        !FluidTransferBridge.transfer(player, hand, target.getFluidStorage())) {
                        level.setBlockAndUpdate(hit.getBlockPos(), Blocks.CAULDRON.defaultBlockState());
                        return InteractionResult.FAIL;
                    }
                    level.playSound(null, hit.getBlockPos(), levelCount == 4
                        ? SoundEvents.BUCKET_EMPTY : SoundEvents.BOTTLE_EMPTY,
                        SoundSource.BLOCKS, 1F, 1F);
                    return InteractionResult.SUCCESS;
                }
            }
            BarrelFluidContainers.Portion fluid = BarrelFluidContainers.filledContainer(held);
            if (fluid == null ||
                "minecraft:water".equals(fluid.id()) ||
                "minecraft:lava".equals(fluid.id()) ||
                fluid.amount() <= 0 || fluid.amount() > FluidCauldronBlockEntity.CAPACITY)
                return InteractionResult.PASS;
            if (level.isClientSide()) return InteractionResult.SUCCESS;
            int targetLevel = fluid.amount() / 250;
            if (!level.setBlockAndUpdate(hit.getBlockPos(),
                ModBlocks.FLUID_CAULDRON.defaultBlockState().setValue(
                    FluidCauldronBlock.LEVEL, targetLevel)))
                return InteractionResult.FAIL;
            if (!(level.getBlockEntity(hit.getBlockPos()) instanceof FluidCauldronBlockEntity tank) ||
                !tank.insert(fluid.id(), fluid.amount())) {
                level.setBlockAndUpdate(hit.getBlockPos(), Blocks.CAULDRON.defaultBlockState());
                return InteractionResult.FAIL;
            }
            player.setItemInHand(hand, ItemUtils.createFilledResult(held, player,
                new ItemStack(fluid.empty())));
            level.playSound(null, hit.getBlockPos(),
                fluid.amount() == 1000 ? SoundEvents.BUCKET_EMPTY : SoundEvents.BOTTLE_EMPTY,
                SoundSource.BLOCKS, 1F, 1F);
            return InteractionResult.SUCCESS;
        });
    }
}
