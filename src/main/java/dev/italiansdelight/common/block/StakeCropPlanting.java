package dev.italiansdelight.common.block;

import dev.italiansdelight.common.registry.ModBlocks;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Intercept planting on a crop stake before the seed's BlockItem placement
 * tries to put a crop in the adjacent (occupied) world position. Keep the
 * original Farmer's Delight tomato seeds and our existing chili seed item.
 * The stake is part of the crop's rendered block model after planting.
 */
public final class StakeCropPlanting {
    private StakeCropPlanting() {}

    public static void register() {
        UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
            BlockPos pos = hit.getBlockPos();
            if (player.isSpectator() || !level.getBlockState(pos).is(ModBlocks.CROP_STAKE)) {
                return InteractionResult.PASS;
            }

            ItemStack seed = player.getItemInHand(hand);
            boolean chili = seed.is(ModBlocks.CHILI_PEPPER_SEEDS);
            boolean tomato = seed.is(vectorwing.farmersdelight.common.registry.ModItems.TOMATO_SEEDS.get());
            if (!chili && !tomato) return InteractionResult.PASS;
            if (!level.getBlockState(pos.below()).is(Blocks.FARMLAND)) return InteractionResult.FAIL;

            // Return success on both logical sides; only the server alters
            // the block, consumes one seed, and emits the planting sound.
            if (level instanceof ServerLevel server) {
                if (!server.mayInteract(player, pos)) return InteractionResult.FAIL;
                BlockState planted = chili ? ModBlocks.CHILI_CROP.defaultBlockState()
                    : ModBlocks.STAKED_TOMATOES.defaultBlockState();
                if (!server.setBlock(pos, planted, Block.UPDATE_ALL)) return InteractionResult.FAIL;
                if (!player.getAbilities().instabuild) seed.shrink(1);
                server.playSound(null, pos, SoundEvents.CROP_PLANTED,
                    SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return InteractionResult.SUCCESS;
        });
    }
}
