package dev.italiansdelight.common.block;

import dev.italiansdelight.common.block.entity.FluidCauldronBlockEntity;
import dev.italiansdelight.common.registry.ModBlocks;
import dev.italiansdelight.common.fluid.FluidTransferBridge;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
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
        NeoForge.EVENT_BUS.addListener(FluidCauldronInteractions::onRightClick);
    }

    private static void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        InteractionResult result = interact(event.getEntity(), event.getLevel(), event.getHand(), event.getHitVec());
        if (result != InteractionResult.PASS) {
            event.setCancellationResult(result);
            event.setCanceled(true);
        }
    }

    private static InteractionResult interact(Player player, Level level, InteractionHand hand, BlockHitResult hit) {
            if (player.isSpectator() ||
                !level.getBlockState(hit.getBlockPos()).is(Blocks.CAULDRON))
                return InteractionResult.PASS;
            ItemStack held = player.getItemInHand(hand);
            // Registered fluids from other mods, regardless of bucket/item
            // implementation, arrive via ITEM storage. Vanilla water/lava
            // continue to use the original cauldron maps.
            IFluidHandlerItem external = FluidTransferBridge.itemStorage(player, hand);
            if (external != null) {
                FluidStack heldFluid = FluidTransferBridge.heldSource(player, hand);
                if (!heldFluid.isEmpty()
                    && heldFluid.getFluid() != net.minecraft.world.level.material.Fluids.WATER
                    && heldFluid.getFluid() != net.minecraft.world.level.material.Fluids.LAVA) {
                    if (level.isClientSide()) return InteractionResult.SUCCESS;
                    int levelCount = heldFluid.getAmount() >= 1000 ? 4 : 1;
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
    }
}
