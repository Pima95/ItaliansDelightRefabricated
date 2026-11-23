package dev.italiansdelight.common.block;

import dev.italiansdelight.common.registry.ModBlocks;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SoundType;

/**
 * Shift+right-click a horizontal FACE with the original FDR rope to extend
 * a level trellis. Shift+click the exposed end of the new span to extend it
 * again. This is an opt-in interaction: ordinary FDR vertical rope and
 * TomatoBlock rope growing are completely unchanged.
 *
 * We create a separate WORLD BLOCK with no BlockItem, so there are no
 * duplicate crafting recipes or rope items in JEI.
 */
public final class HorizontalRopePlacement {
    private HorizontalRopePlacement() {}

    public static void register() {
        UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
            ItemStack rope = player.getItemInHand(hand);
            Direction face = hit.getDirection();
            if (!player.isSecondaryUseActive()
                    || !rope.is(vectorwing.farmersdelight.common.registry.ModItems.ROPE.get())
                    || player.isSpectator()) {
                return InteractionResult.PASS;
            }

            // Clicking the TOP of a crop stake (or an existing span) also
            // works; use the player's facing to choose the horizontal axis.
            // Ordinary blocks keep their existing vanilla top-click behavior.
            BlockPos anchor = hit.getBlockPos();
            if (face == Direction.UP && (CropStakeBlock.isPost(level.getBlockState(anchor))
                    || level.getBlockState(anchor).is(ModBlocks.HORIZONTAL_ROPE))) {
                face = player.getDirection();
            }
            if (!face.getAxis().isHorizontal()) return InteractionResult.PASS;

            BlockPos target = anchor.relative(face);
            if (!level.isLoaded(target) || !level.isEmptyBlock(target)) {
                return InteractionResult.PASS;
            }

            // Placing into air next to an existing block is allowed; later
            // segments can be placed by targeting the end of the previous span.
            if (level instanceof ServerLevel server) {
                if (!server.mayInteract(player, target)) return InteractionResult.FAIL;
                BlockState newState = HorizontalRopeBlock.withConnections(
                    ModBlocks.HORIZONTAL_ROPE.defaultBlockState()
                        .setValue(HorizontalRopeBlock.AXIS, face.getAxis()), server, target);
                if (!server.setBlock(target, newState, Block.UPDATE_ALL)) {
                    return InteractionResult.FAIL;
                }
                if (!player.getAbilities().instabuild) rope.shrink(1);
                server.playSound(null, target, SoundType.WOOL.getPlaceSound(),
                    SoundSource.BLOCKS, 0.8F, 1.0F);
            }
            return InteractionResult.SUCCESS;
        });
    }
}
