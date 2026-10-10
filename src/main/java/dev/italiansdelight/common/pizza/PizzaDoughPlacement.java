package dev.italiansdelight.common.pizza;

import dev.italiansdelight.common.registry.ModBlocks;
import dev.italiansdelight.common.block.PizzaDoughBlock;
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
            if (hand != InteractionHand.MAIN_HAND || player.isSpectator()) {
                return InteractionResult.PASS;
            }
            ItemStack held = player.getItemInHand(hand);
            BlockPos clickedPos = hit.getBlockPos();
            BlockState clickedState = level.getBlockState(clickedPos);

            // Handle BOTH empty-handed stretching and the rolling pin here.
            // UseBlockCallback runs before vanilla item/block interactions; using
            // one handler prevents inconsistent right-click behavior.
            if (clickedState.is(ModBlocks.PIZZA_DOUGH)) {
                if (player.isShiftKeyDown()) {
                    return InteractionResult.PASS;
                }

                boolean rollingPin = held.is(dev.italiansdelight.common.registry.ModItems.ROLLING_PIN);
                if (!rollingPin && !held.isEmpty()) {
                    return InteractionResult.PASS;
                }

                int stage = clickedState.getValue(PizzaDoughBlock.STAGE);
                if (stage >= PizzaDoughBlock.MAX_STAGE) {
                    return InteractionResult.SUCCESS;
                }

                if (!player.mayUseItemAt(clickedPos, hit.getDirection(), held)) {
                    return InteractionResult.FAIL;
                }

                // On the client, consume the click so a matching interaction
                // packet reaches the server. Only the server updates state/wear.
                if (level.isClientSide()) {
                    return InteractionResult.SUCCESS;
                }

                if (!(level instanceof ServerLevel server)) {
                    return InteractionResult.PASS;
                }

                int nextStage = rollingPin ? PizzaDoughBlock.MAX_STAGE : stage + 1;
                if (!server.setBlock(
                        clickedPos,
                        clickedState.setValue(PizzaDoughBlock.STAGE, nextStage),
                        Block.UPDATE_ALL)) {
                    return InteractionResult.FAIL;
                }

                // One durability per pizza, not one per hand-stretching step.
                // This is invoked only after the state has actually changed.
                if (rollingPin && !player.getAbilities().instabuild) {
                    held.hurtAndBreak(1, player, hand);
                }

                server.playSound(null, clickedPos, SoundEvents.WOOL_PLACE,
                        SoundSource.BLOCKS, 0.55F, rollingPin ? 0.9F : 0.95F);
                return InteractionResult.SUCCESS;
            }

            // Dough placement requires the upper face, but stretching should
            // work on any face of the dough block (including its thin sides).
            if (hit.getDirection() != Direction.UP || !held.is(ModItems.WHEAT_DOUGH.get())) {
                return InteractionResult.PASS;
            }
            BlockPos pos = clickedPos.above();
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
