package dev.italiansdelight.common.block;

import dev.italiansdelight.common.registry.ModBlocks;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Plant either of the two supported seeds on a reusable wooden stake.
 * Aiming at the thin stake itself OR at its farmland base both work.
 * Farmer's Delight tomato seeds keep their original item registration;
 * the event runs before their standard BlockItem placement is attempted.
 */
public final class StakeCropPlanting {
    private StakeCropPlanting() {}

    public static void register() {
        UseBlockCallback.EVENT.register((player, level, hand, hit) ->
            tryPlant(player, level, hand, hit.getBlockPos()));
    }

    public static InteractionResult tryPlant(Player player, Level level,
            InteractionHand hand, BlockPos clicked) {
        if (player.isSpectator() || !player.getAbilities().mayBuild) return InteractionResult.PASS;

        ItemStack held = player.getItemInHand(hand);
        boolean chili = held.is(ModBlocks.CHILI_PEPPER_SEEDS);
        boolean tomato = held.is(vectorwing.farmersdelight.common.registry.ModItems.TOMATO_SEEDS.get());
        if (!chili && !tomato) return InteractionResult.PASS;

        // Clicking anywhere on the farmland's top surface should be enough,
        // even though the thin stake occupies the block directly above it.
        BlockPos stakePos = level.getBlockState(clicked).is(ModBlocks.CROP_STAKE)
            ? clicked : level.getBlockState(clicked).is(Blocks.FARMLAND)
                && level.getBlockState(clicked.above()).is(ModBlocks.CROP_STAKE)
                    ? clicked.above() : null;
        if (stakePos == null) return InteractionResult.PASS;
        if (!level.getBlockState(stakePos.below()).is(Blocks.FARMLAND))
            return InteractionResult.FAIL;

        if (level instanceof ServerLevel server) {
            if (!server.mayInteract(player, stakePos)) return InteractionResult.FAIL;
            BlockState planted = chili ? ModBlocks.CHILI_CROP.defaultBlockState()
                : ModBlocks.STAKED_TOMATOES.defaultBlockState();
            // Preserve the loop and anchor after the stake is replaced by crop.
            planted = planted.setValue(CropStakeBlock.ROPE_TIED,
                CropStakeBlock.isRopeTied(server, stakePos));
            if (!server.setBlock(stakePos, planted, Block.UPDATE_ALL))
                return InteractionResult.FAIL;
            if (!player.getAbilities().instabuild) held.shrink(1);
            server.playSound(null, stakePos, SoundEvents.CROP_PLANTED,
                SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        return InteractionResult.SUCCESS;
    }
}
