package dev.italiansdelight.common.pizza;

import dev.italiansdelight.common.registry.ModBlocks;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.CollisionContext;
import vectorwing.farmersdelight.common.registry.ModItems;

/** Right-click with FDR wheat dough on the top of a block to place it. */
public final class PizzaDoughPlacement {
    private PizzaDoughPlacement() {
    }

    public static void register() {
        UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
            if (hand != InteractionHand.MAIN_HAND || player.isSpectator()
                    || hit.getDirection() != Direction.UP) {
                return InteractionResult.PASS;
            }
            ItemStack held = player.getItemInHand(hand);
            if (!held.is(ModItems.WHEAT_DOUGH.get())) {
                return InteractionResult.PASS;
            }
            BlockPos pos = hit.getBlockPos().above();
            BlockState state = ModBlocks.PIZZA_DOUGH.defaultBlockState();
            if (!level.getBlockState(pos).isAir() || !state.canSurvive(level, pos)) {
                return InteractionResult.PASS;
            }
            if (!player.mayUseItemAt(pos, Direction.UP, held)
                    || !level.isUnobstructed(state, pos, CollisionContext.empty())) {
                return InteractionResult.FAIL;
            }
            if (level.isClientSide()) {
                return InteractionResult.SUCCESS;
            }
            if (!(level instanceof ServerLevel server) || !server.setBlock(pos, state, Block.UPDATE_ALL)) {
                return InteractionResult.FAIL;
            }
            if (!player.getAbilities().instabuild) {
                held.shrink(1);
            }
            server.playSound(null, pos, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.7F, 1.0F);
            server.gameEvent(player, GameEvent.BLOCK_PLACE, pos);
            return InteractionResult.SUCCESS;
        });
    }
}
