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
import net.minecraft.world.level.block.state.BlockState;

/** Plant onto the persistent stake; NEVER replace the post with a crop block. */
public final class StakeCropPlanting {
    private StakeCropPlanting() {}

    public static void register() {
        UseBlockCallback.EVENT.register((player, level, hand, hit) ->
            tryPlant(player, level, hand, hit.getBlockPos()));
    }

    public static InteractionResult tryPlant(Player player, Level level,
            InteractionHand hand, BlockPos clicked) {
        if (player.isSpectator() || !player.getAbilities().mayBuild) return InteractionResult.PASS;

        ItemStack seed = player.getItemInHand(hand);
        boolean chili = seed.is(ModBlocks.CHILI_PEPPER_SEEDS);
        boolean tomato = seed.is(vectorwing.farmersdelight.common.registry.ModItems.TOMATO_SEEDS.get());
        if (!chili && !tomato) return InteractionResult.PASS;

        // Aiming at the soil also works: the post is a narrow solid shape.
        BlockPos pos = level.getBlockState(clicked).is(ModBlocks.CROP_STAKE)
            ? clicked : level.getBlockState(clicked.above()).is(ModBlocks.CROP_STAKE)
                ? clicked.above() : null;
        if (pos == null) return InteractionResult.PASS;
        BlockState support = level.getBlockState(pos);
        if (support.getValue(CropStakeBlock.CROP) != CropStakeBlock.EMPTY)
            return InteractionResult.FAIL;
        if (!StakeSoilTilling.isFarmland(level.getBlockState(pos.below())))
            return InteractionResult.FAIL;

        if (level instanceof ServerLevel server) {
            if (!server.mayInteract(player, pos)) return InteractionResult.FAIL;
            int kind = chili ? CropStakeBlock.CHILI : CropStakeBlock.TOMATO;
            // Both crops start from age zero using their ORIGINAL growth models.
            BlockState planted = support.setValue(CropStakeBlock.CROP, kind)
                .setValue(CropStakeBlock.CROP_AGE, 0);
            if (!server.setBlock(pos, planted, Block.UPDATE_ALL))
                return InteractionResult.FAIL;
            if (!player.getAbilities().instabuild) seed.shrink(1);
            server.playSound(null, pos, SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        return InteractionResult.SUCCESS;
    }
}
