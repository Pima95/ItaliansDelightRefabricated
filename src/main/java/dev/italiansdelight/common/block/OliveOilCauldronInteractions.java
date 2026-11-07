package dev.italiansdelight.common.block;

import dev.italiansdelight.common.registry.ModBlocks;
import dev.italiansdelight.common.registry.ModItems;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

/** Intercept vanilla empty cauldrons before an olive oil bucket is poured beside them. */
public final class OliveOilCauldronInteractions {
    private OliveOilCauldronInteractions() {}

    public static void register() {
        UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
            if (player.isSpectator() ||
                !level.getBlockState(hit.getBlockPos()).is(Blocks.CAULDRON))
                return InteractionResult.PASS;
            ItemStack held = player.getItemInHand(hand);
            boolean bucket = held.is(ModItems.OLIVE_OIL_BUCKET);
            boolean bottle = held.is(ModItems.OLIVE_OIL_BOTTLE);
            if (!bucket && !bottle) return InteractionResult.PASS;
            if (level.isClientSide()) return InteractionResult.SUCCESS;
            if (!level.setBlockAndUpdate(hit.getBlockPos(),
                ModBlocks.OLIVE_OIL_CAULDRON.defaultBlockState()
                    .setValue(OliveOilCauldronBlock.LEVEL, bucket ? 4 : 1)))
                return InteractionResult.FAIL;
            player.setItemInHand(hand, ItemUtils.createFilledResult(held, player,
                new ItemStack(bucket ? Items.BUCKET : Items.GLASS_BOTTLE)));
            level.playSound(null, hit.getBlockPos(),
                bucket ? SoundEvents.BUCKET_EMPTY : SoundEvents.BOTTLE_EMPTY,
                SoundSource.BLOCKS, 1f, 1f);
            return InteractionResult.SUCCESS;
        });
    }
}
