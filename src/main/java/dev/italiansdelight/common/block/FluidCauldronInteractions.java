package dev.italiansdelight.common.block;

import dev.italiansdelight.common.block.entity.FluidCauldronBlockEntity;
import dev.italiansdelight.common.registry.ModBlocks;
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
