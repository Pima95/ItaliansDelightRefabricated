package dev.italiansdelight.common.pizza;

import dev.italiansdelight.common.registry.ModBlocks;
import dev.italiansdelight.common.block.PizzaDoughBlock;
import dev.italiansdelight.common.block.CookedPizzaBlock;
import dev.italiansdelight.common.block.entity.PizzaDoughBlockEntity;
import dev.italiansdelight.common.block.entity.CookedPizzaBlockEntity;
import dev.italiansdelight.common.registry.ModDataComponents;
import net.minecraft.world.level.block.Blocks;
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
            if (player.isSpectator()) {
                return InteractionResult.PASS;
            }
            ItemStack held = player.getItemInHand(hand);
            BlockPos clickedPos = hit.getBlockPos();
            BlockState clickedState = level.getBlockState(clickedPos);

            // The peel may be used in EITHER hand. Main-hand pizza tools,
            // toppings and other item interactions keep normal precedence.
            // A genuinely empty main hand delegates a click on an existing
            // pizza/oven to the offhand peel BEFORE empty-hand slicing or
            // stretching intercepts it. This avoids collecting a slice when
            // trying to lift an intact pizza with the offhand peel.
            if (hand == InteractionHand.MAIN_HAND && held.isEmpty()) {
                ItemStack offhandPeel = player.getItemInHand(InteractionHand.OFF_HAND);
                if (offhandPeel.is(dev.italiansdelight.common.registry.ModItems.PIZZA_PEEL)
                        && (clickedState.is(ModBlocks.PIZZA_DOUGH)
                            || clickedState.is(ModBlocks.COOKED_PIZZA)
                            || clickedState.is(ModBlocks.PIZZA_OVEN))) {
                    InteractionResult peelResult = PizzaPeelInteractions.use(
                            player, level, hit, offhandPeel);
                    if (peelResult != InteractionResult.PASS) {
                        return peelResult;
                    }
                }
            }

            if (held.is(dev.italiansdelight.common.registry.ModItems.PIZZA_PEEL)) {
                return PizzaPeelInteractions.use(player, level, hit, held);
            }
            if (hand != InteractionHand.MAIN_HAND) {
                return InteractionResult.PASS;
            }

            // Handle BOTH empty-handed stretching and the rolling pin here.
            // UseBlockCallback runs before vanilla item/block interactions; using
            // one handler prevents inconsistent right-click behavior.
            if (clickedState.is(ModBlocks.PIZZA_DOUGH)) {
                int stage = clickedState.getValue(PizzaDoughBlock.STAGE);
                boolean rollingPin = held.is(
                        dev.italiansdelight.common.registry.ModItems.ROLLING_PIN);

                // A fully stretched pizza can be divided into logical zones:
                // right-click = split, Shift + right-click = join. This is
                // only a persisted selection layout for now. It does not
                // change the block model, consume durability or move toppings.
                if (stage == PizzaDoughBlock.MAX_STAGE && rollingPin) {
                    if (!player.mayUseItemAt(clickedPos, hit.getDirection(), held)) {
                        return InteractionResult.FAIL;
                    }
                    if (level.isClientSide()) {
                        return InteractionResult.SUCCESS;
                    }
                    if (!(level instanceof ServerLevel server)
                            || !(server.getBlockEntity(clickedPos)
                                    instanceof PizzaDoughBlockEntity pizzaEntity)) {
                        return InteractionResult.FAIL;
                    }

                    PizzaComposition current = pizzaEntity.composition();
                    int clickedMask = PizzaQuarterSelection.selectMask(
                            current, clickedPos, hit.getLocation());
                    PizzaComposition updated = player.isShiftKeyDown()
                            ? PizzaQuarterSelection.join(current, clickedMask)
                            : PizzaQuarterSelection.split(current, clickedMask,
                                    PizzaQuarterSelection.nearestClockwiseFacing(player.getYRot()));
                    if (updated != current) {
                        pizzaEntity.setComposition(updated);
                        server.playSound(null, clickedPos, SoundEvents.WOOL_PLACE,
                                SoundSource.BLOCKS, 0.55F, 1.15F);
                    }
                    return InteractionResult.SUCCESS;
                }

                // Phase 3G: server-authoritative pizza topping composition,
                // including SOLID, CREAMY and SAUCE approved roles.
                // Shift + empty hand peels only the newest application in
                // the selected physical zone, with refund on LAST removal.
                if (stage == PizzaDoughBlock.MAX_STAGE && held.isEmpty()
                        && player.isShiftKeyDown()) {
                    return PizzaToppingInteractions.remove(
                            player, level, clickedPos, hit);
                }

                // The native allowlist is available on BOTH sides without a
                // resource reload; the authoritative server lookup resolves
                // optional datapack equivalents. The client receives the
                // canonical composition through the pizza BlockEntity.
                if (stage == PizzaDoughBlock.MAX_STAGE
                        && !player.isShiftKeyDown()
                        && !held.isEmpty()
                        && (level.isClientSide()
                            ? PizzaToppingCatalog.findNativeItem(
                                    net.minecraft.core.registries.BuiltInRegistries.ITEM
                                            .getKey(held.getItem()))
                                    .filter(type -> !type.planned()
                                            && PizzaToppingCatalog.isActiveType(type.id()))
                                    .isPresent()
                            : PizzaToppingInteractions.isImplementedTopping(held))) {
                    return PizzaToppingInteractions.add(
                            player, level, clickedPos, hit, held);
                }

                if (player.isShiftKeyDown()) {
                    return InteractionResult.PASS;
                }

                // Temporary legacy proof of concept: WHOLE mozzarella toggles
                // a BlockState overlay. New prepared approved toppings above use
                // the canonical persistent quarter-based composition instead.
                if (stage == PizzaDoughBlock.MAX_STAGE
                        && held.is(dev.italiansdelight.common.registry.ModItems.MOZZARELLA)) {
                    if (clickedState.getValue(PizzaDoughBlock.MOZZARELLA)) {
                        return InteractionResult.SUCCESS;
                    }
                    if (!player.mayUseItemAt(clickedPos, hit.getDirection(), held)) {
                        return InteractionResult.FAIL;
                    }
                    if (level.isClientSide()) {
                        return InteractionResult.SUCCESS;
                    }
                    if (!(level instanceof ServerLevel server)) {
                        return InteractionResult.PASS;
                    }
                    if (!server.setBlock(clickedPos,
                            clickedState.setValue(PizzaDoughBlock.MOZZARELLA, true),
                            Block.UPDATE_ALL)) {
                        return InteractionResult.FAIL;
                    }
                    if (!player.getAbilities().instabuild) {
                        held.shrink(1);
                    }
                    server.playSound(null, clickedPos, SoundEvents.WOOL_PLACE,
                            SoundSource.BLOCKS, 0.65F, 1.1F);
                    return InteractionResult.SUCCESS;
                }

                if (!rollingPin && !held.isEmpty()) {
                    return InteractionResult.PASS;
                }
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

            // Place ONE fully baked pizza for serving four physical quarters.
            // The item remains non-edible; taking its slices is a completely
            // separate empty-hand block interaction on CookedPizzaBlock.
            // The original ingredient applications and legacy mozzarella
            // flag are persisted in the BlockEntity, not copied or refunded.
            if (held.is(dev.italiansdelight.common.registry.ModItems.COOKED_PIZZA)) {
                if (hit.getDirection() != Direction.UP) {
                    return InteractionResult.PASS;
                }
                PizzaItemData baked = held.get(ModDataComponents.PIZZA_CONTENTS);
                if (baked == null) {
                    return InteractionResult.FAIL;
                }
                BlockPos target = clickedPos.above();
                Direction facing = switch (baked.composition().orientation()) {
                    case "north" -> Direction.NORTH;
                    case "east" -> Direction.EAST;
                    case "south" -> Direction.SOUTH;
                    case "west" -> Direction.WEST;
                    default -> Direction.NORTH;
                };
                BlockState placed = ModBlocks.COOKED_PIZZA.defaultBlockState()
                        .setValue(CookedPizzaBlock.FACING, facing);
                if (!level.getBlockState(target).isAir() || !placed.canSurvive(level, target)) {
                    return InteractionResult.PASS;
                }
                if (!player.mayUseItemAt(target, Direction.UP, held)
                        || !level.isUnobstructed(placed, target, CollisionContext.empty())) {
                    return InteractionResult.FAIL;
                }
                if (level.isClientSide()) {
                    return InteractionResult.SUCCESS;
                }
                if (!(level instanceof ServerLevel server)
                        || !server.setBlock(target, placed, Block.UPDATE_ALL)) {
                    return InteractionResult.FAIL;
                }
                if (!(server.getBlockEntity(target) instanceof CookedPizzaBlockEntity cooked)) {
                    server.setBlock(target, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                    return InteractionResult.FAIL;
                }
                cooked.setPizza(baked);
                if (!player.getAbilities().instabuild) {
                    held.shrink(1);
                }
                server.playSound(null, target, SoundEvents.WOOL_PLACE,
                        SoundSource.BLOCKS, 0.7F, 1.0F);
                server.gameEvent(player, GameEvent.BLOCK_PLACE, target);
                return InteractionResult.SUCCESS;
            }

            // A formed pizza ItemStack can be placed back into the world with
            // its exact persisted composition, original ingredient stacks,
            // division/orientation and temporary legacy mozzarella topping.
            // This is separate from placing Farmer's Delight wheat dough.
            if (held.is(dev.italiansdelight.common.registry.ModItems.RAW_PIZZA)) {
                if (hit.getDirection() != Direction.UP) {
                    return InteractionResult.PASS;
                }
                PizzaItemData pizzaData = held.get(ModDataComponents.PIZZA_CONTENTS);
                if (pizzaData == null) {
                    return InteractionResult.FAIL;
                }
                BlockPos target = clickedPos.above();
                BlockState placed = ModBlocks.PIZZA_DOUGH.defaultBlockState()
                        .setValue(PizzaDoughBlock.STAGE, PizzaDoughBlock.MAX_STAGE)
                        .setValue(PizzaDoughBlock.MOZZARELLA, pizzaData.legacyMozzarella());

                if (!level.getBlockState(target).isAir() || !placed.canSurvive(level, target)) {
                    return InteractionResult.PASS;
                }
                if (!player.mayUseItemAt(target, Direction.UP, held)
                        || !level.isUnobstructed(placed, target, CollisionContext.empty())) {
                    return InteractionResult.FAIL;
                }
                if (level.isClientSide()) {
                    return InteractionResult.SUCCESS;
                }
                if (!(level instanceof ServerLevel server)
                        || !server.setBlock(target, placed, Block.UPDATE_ALL)) {
                    return InteractionResult.FAIL;
                }
                if (!(server.getBlockEntity(target) instanceof PizzaDoughBlockEntity pizzaEntity)) {
                    // Do not consume an item unless both the block and its
                    // data holder exist. Undo the partial placement safely.
                    server.setBlock(target, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                    return InteractionResult.FAIL;
                }
                pizzaEntity.setComposition(pizzaData.composition());
                if (!player.getAbilities().instabuild) {
                    held.shrink(1);
                }
                server.playSound(null, target, SoundEvents.WOOL_PLACE,
                        SoundSource.BLOCKS, 0.7F, 1.0F);
                server.gameEvent(player, GameEvent.BLOCK_PLACE, target);
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
