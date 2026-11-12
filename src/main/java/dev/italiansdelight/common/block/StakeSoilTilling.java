package dev.italiansdelight.common.block;

import dev.italiansdelight.common.registry.ModBlocks;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Vanilla hoes only till soil with air above; the narrow crop stake occupies
 * that space. Let a hoe aimed at the stake (or the soil beneath it) cultivate
 * the soil without breaking, replacing, or duplicating the wooden stake.
 */
public final class StakeSoilTilling {
    private StakeSoilTilling() {}

    public static void register() {
        UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
            if (player.isSpectator() || !player.getAbilities().mayBuild)
                return InteractionResult.PASS;

            ItemStack tool = player.getItemInHand(hand);
            if (!tool.is(ItemTags.HOES)) return InteractionResult.PASS;

            BlockPos clicked = hit.getBlockPos();
            BlockState at = level.getBlockState(clicked);
            BlockPos soil;
            if (isSupportedPost(at)) {
                soil = clicked.below();
            } else if (isSupportedPost(level.getBlockState(clicked.above()))) {
                soil = clicked;
            } else {
                return InteractionResult.PASS;
            }

            BlockState dirt = level.getBlockState(soil);
            BlockState tilled;
            if (dirt.is(Blocks.DIRT) || dirt.is(Blocks.GRASS_BLOCK)
                    || dirt.is(Blocks.DIRT_PATH)) {
                tilled = Blocks.FARMLAND.defaultBlockState();
            } else if (dirt.is(Blocks.COARSE_DIRT)) {
                // Keep vanilla's first hoe operation: coarse dirt -> dirt.
                tilled = Blocks.DIRT.defaultBlockState();
            } else {
                return InteractionResult.PASS;
            }

            if (level instanceof ServerLevel server) {
                if (!server.mayInteract(player, soil)) return InteractionResult.FAIL;
                if (!server.setBlock(soil, tilled, Block.UPDATE_ALL))
                    return InteractionResult.FAIL;
                server.playSound(null, soil, SoundEvents.HOE_TILL,
                    SoundSource.BLOCKS, 1.0F, 1.0F);
                if (!player.getAbilities().instabuild)
                    tool.hurtAndBreak(1, player, hand);
            }
            return InteractionResult.SUCCESS;
        });
    }

    private static boolean isSupportedPost(BlockState state) {
        return state.is(ModBlocks.CROP_STAKE)
            || state.is(ModBlocks.STAKED_TOMATOES)
            || (state.is(ModBlocks.CHILI_CROP)
                && state.getValue(ChiliCropBlock.STAKED));
    }
}
