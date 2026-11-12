package dev.italiansdelight.common.block;

import dev.italiansdelight.common.registry.ModBlocks;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;

/**
 * The player removes the plant first while the real wooden support stays in
 * exactly the same world block. A second hit can break the bare stake.
 * This prevents the old destroy-and-replace "popping" behavior.
 */
public final class StakeCropBreaking {
    private StakeCropBreaking() {}

    public static void register() {
        AttackBlockCallback.EVENT.register((player, level, hand, pos, side) -> {
            if (player.isSpectator() || !player.getAbilities().mayBuild)
                return InteractionResult.PASS;
            if (!CropStakeBlock.hasCrop(level.getBlockState(pos), CropStakeBlock.CHILI)
                    && !CropStakeBlock.hasCrop(level.getBlockState(pos), CropStakeBlock.TOMATO))
                return InteractionResult.PASS;
            if (level instanceof ServerLevel server) {
                if (!CropStakeBlock.removeCrop(server, pos, player))
                    return InteractionResult.FAIL;
            }
            return InteractionResult.SUCCESS;
        });
    }
}
