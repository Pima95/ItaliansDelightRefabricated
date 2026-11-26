package dev.italiansdelight.common.block;

import dev.italiansdelight.common.registry.ModBlocks;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Treat normal and Farmer's Delight rich soil as equivalent for stake crops.
 * Never overwrite rich soil with vanilla farmland during tilling.
 */
public final class StakeSoilTilling {
    private StakeSoilTilling() {}

    public static boolean isFarmland(BlockState state) {
        return state.is(Blocks.FARMLAND)
            || state.is(vectorwing.farmersdelight.common.registry.ModBlocks.RICH_SOIL_FARMLAND.get());
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(StakeSoilTilling::onRightClick);
    }

    private static void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        InteractionResult result = interact(event.getEntity(), event.getLevel(), event.getHand(), event.getHitVec());
        if (result != InteractionResult.PASS) {
            event.setCancellationResult(result);
            event.setCanceled(true);
        }
    }

    private static InteractionResult interact(Player player, Level level, InteractionHand hand, BlockHitResult hit) {
            if (player.isSpectator() || !player.getAbilities().mayBuild)
                return InteractionResult.PASS;
            ItemStack tool = player.getItemInHand(hand);
            if (!tool.is(ItemTags.HOES)) return InteractionResult.PASS;

            BlockPos clicked = hit.getBlockPos();
            BlockState at = level.getBlockState(clicked);
            BlockPos soil;
            if (CropStakeBlock.isPost(at)) soil = clicked.below();
            else if (CropStakeBlock.isPost(level.getBlockState(clicked.above()))) soil = clicked;
            else return InteractionResult.PASS;

            BlockState dirt = level.getBlockState(soil);
            BlockState tilled;
            if (dirt.is(Blocks.DIRT) || dirt.is(Blocks.GRASS_BLOCK)
                    || dirt.is(Blocks.DIRT_PATH)) {
                tilled = Blocks.FARMLAND.defaultBlockState();
            } else if (dirt.is(vectorwing.farmersdelight.common.registry.ModBlocks.RICH_SOIL.get())) {
                tilled = vectorwing.farmersdelight.common.registry.ModBlocks.RICH_SOIL_FARMLAND
                    .get().defaultBlockState();
            } else if (dirt.is(Blocks.COARSE_DIRT)) {
                tilled = Blocks.DIRT.defaultBlockState();
            } else return InteractionResult.PASS;

            if (level instanceof ServerLevel server) {
                if (!server.mayInteract(player, soil) || !server.setBlock(soil, tilled, Block.UPDATE_ALL))
                    return InteractionResult.FAIL;
                server.playSound(null, soil, SoundEvents.HOE_TILL,
                    SoundSource.BLOCKS, 1.0F, 1.0F);
                if (!player.getAbilities().instabuild)
                    tool.hurtAndBreak(1, player, net.minecraft.world.entity.LivingEntity.getSlotForHand(hand));
            }
            return InteractionResult.SUCCESS;
    }
}
