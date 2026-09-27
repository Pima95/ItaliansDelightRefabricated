package dev.italiansdelight.mixin;

import dev.italiansdelight.common.registry.ModItems;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Adds sheep milking while keeping the same interaction model used for cows:
 * adult sheep are milked with a bucket only. Sheep milk bottles are obtained
 * afterwards through crafting, matching Farmer's Delight's milk-bottle flow.
 */
@Mixin(Sheep.class)
public abstract class SheepMixin {

    @Inject(
        method = "mobInteract",
        at = @At("HEAD"),
        cancellable = true
    )
    private void italiansDelight$milkSheep(
        Player player,
        InteractionHand hand,
        CallbackInfoReturnable<InteractionResult> cir
    ) {
        Sheep sheep =
            (Sheep) (Object) this;

        if (sheep.isBaby()) {
            return;
        }

        ItemStack heldItem =
            player.getItemInHand(hand);

        if (!heldItem.is(Items.BUCKET)) {
            return;
        }

        player.playSound(
            SoundEvents.COW_MILK,
            1.0F,
            1.0F
        );

        player.setItemInHand(
            hand,
            ItemUtils.createFilledResult(
                heldItem,
                player,
                new ItemStack(ModItems.SHEEP_MILK_BUCKET)
            )
        );

        cir.setReturnValue(
            InteractionResult.SUCCESS
        );
    }
}
