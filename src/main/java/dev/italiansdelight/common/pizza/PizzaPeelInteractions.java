package dev.italiansdelight.common.pizza;

import dev.italiansdelight.common.block.CookedPizzaBlock;
import dev.italiansdelight.common.block.PizzaDoughBlock;
import dev.italiansdelight.common.block.entity.CookedPizzaBlockEntity;
import dev.italiansdelight.common.block.entity.PizzaDoughBlockEntity;
import dev.italiansdelight.common.block.entity.PizzaOvenBlockEntity;
import dev.italiansdelight.common.registry.ModBlocks;
import dev.italiansdelight.common.registry.ModDataComponents;
import dev.italiansdelight.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;

/**
 * The pizza peel is a lossless, reusable transfer tool with one cargo slot.
 *
 * <p>One server-authoritative right click: collect a fully formed raw pizza
 * or a WHOLE cooked pizza, place it on a valid support, or move it to/from
 * the dedicated Pizza Oven. No separate pizza drops, durability, or hunger
 * logic. World pickup requires a free peel and never takes served quarters.
 *
 * <p>All operations run through the existing UseBlockCallback, ahead of
 * ordinary dough/topping interactions and the oven GUI.
 */
public final class PizzaPeelInteractions {
    private PizzaPeelInteractions() {
    }

    public static InteractionResult use(
            Player player, Level level, BlockHitResult hit, ItemStack peel
    ) {
        if (!peel.is(ModItems.PIZZA_PEEL)) {
            return InteractionResult.PASS;
        }
        BlockPos clicked = hit.getBlockPos();
        BlockState state = level.getBlockState(clicked);
        PizzaPeelCargo cargo = peel.get(ModDataComponents.PEEL_CARGO);

        if (state.is(ModBlocks.PIZZA_OVEN)) {
            return useOnOven(player, level, clicked, hit, peel, cargo);
        }

        if (cargo == null) {
            // Pick up a STRETCHED pizza; partially stretched dough remains
            // editable in the world. A cooked pizza must still be complete.
            boolean raw = state.is(ModBlocks.PIZZA_DOUGH)
                    && state.getValue(PizzaDoughBlock.STAGE) == PizzaDoughBlock.MAX_STAGE;
            boolean cooked = state.is(ModBlocks.COOKED_PIZZA)
                    && state.getValue(CookedPizzaBlock.REMAINING) == 15;
            if (!raw && !cooked) {
                return InteractionResult.PASS;
            }
            if (!player.mayUseItemAt(clicked, hit.getDirection(), peel)) {
                return InteractionResult.FAIL;
            }
            if (level.isClientSide()) {
                return InteractionResult.SUCCESS;
            }
            if (!(level instanceof ServerLevel server)) {
                return InteractionResult.FAIL;
            }
            PizzaItemData data;
            if (raw && server.getBlockEntity(clicked) instanceof PizzaDoughBlockEntity entity) {
                data = new PizzaItemData(entity.composition(),
                        state.getValue(PizzaDoughBlock.MOZZARELLA));
            } else if (cooked
                    && server.getBlockEntity(clicked) instanceof CookedPizzaBlockEntity entity) {
                data = entity.pizza();
            } else {
                return InteractionResult.FAIL;
            }

            ItemStack pizza = new ItemStack(raw ? ModItems.RAW_PIZZA : ModItems.COOKED_PIZZA);
            pizza.set(ModDataComponents.PIZZA_CONTENTS, data);
            PizzaDisplayNames.nameWholeStack(pizza, data);

            // Prepare the sole cargo before deleting the source; revert if
            // Minecraft refuses removal. Do NOT trigger the block loot table.
            peel.set(ModDataComponents.PEEL_CARGO, new PizzaPeelCargo(pizza));
            if (!server.removeBlock(clicked, false)) {
                peel.remove(ModDataComponents.PEEL_CARGO);
                return InteractionResult.FAIL;
            }
            server.playSound(null, clicked, SoundEvents.WOOL_PLACE,
                    SoundSource.BLOCKS, 0.65F, 1.15F);
            return InteractionResult.SUCCESS;
        }

        // An occupied peel cannot take a second pizza. On a non-oven
        // surface its loaded pizza is placed ABOVE the clicked support.
        if (state.is(ModBlocks.PIZZA_DOUGH) || state.is(ModBlocks.COOKED_PIZZA)) {
            // Do not take, replace or damage an existing pizza with a full
            // peel. The player keeps both preparations intact.
            notifyOnServer(player, level, "item.italiansdelight.pizza_peel.already_loaded");
            return InteractionResult.SUCCESS;
        }
        if (hit.getDirection() != Direction.UP) {
            return InteractionResult.PASS;
        }
        ItemStack pizza = cargo.pizza();
        boolean raw = pizza.is(ModItems.RAW_PIZZA);
        if (!raw && !pizza.is(ModItems.COOKED_PIZZA)) {
            return InteractionResult.FAIL;
        }
        PizzaItemData data = pizza.get(ModDataComponents.PIZZA_CONTENTS);
        if (data == null) {
            return InteractionResult.FAIL;
        }

        BlockPos target = clicked.above();
        BlockState placed;
        if (raw) {
            placed = ModBlocks.PIZZA_DOUGH.defaultBlockState()
                    .setValue(PizzaDoughBlock.STAGE, PizzaDoughBlock.MAX_STAGE)
                    .setValue(PizzaDoughBlock.MOZZARELLA, data.legacyMozzarella());
        } else {
            placed = ModBlocks.COOKED_PIZZA.defaultBlockState()
                    .setValue(CookedPizzaBlock.FACING,
                            switch (data.composition().orientation()) {
                                case "north" -> Direction.NORTH;
                                case "east" -> Direction.EAST;
                                case "south" -> Direction.SOUTH;
                                case "west" -> Direction.WEST;
                                default -> Direction.NORTH;
                            });
        }
        if (!level.getBlockState(target).isAir() || !placed.canSurvive(level, target)) {
            return InteractionResult.PASS;
        }
        if (!player.mayUseItemAt(target, Direction.UP, peel)
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
        if (raw && server.getBlockEntity(target) instanceof PizzaDoughBlockEntity entity) {
            entity.setComposition(data.composition());
        } else if (!raw
                && server.getBlockEntity(target) instanceof CookedPizzaBlockEntity entity) {
            entity.setPizza(data);
        } else {
            server.setBlock(target, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
            return InteractionResult.FAIL;
        }

        // Commit the release only AFTER the target has its BlockEntity data.
        peel.remove(ModDataComponents.PEEL_CARGO);
        server.playSound(null, target, SoundEvents.WOOL_PLACE,
                SoundSource.BLOCKS, 0.7F, 1.0F);
        server.gameEvent(player, GameEvent.BLOCK_PLACE, target);
        return InteractionResult.SUCCESS;
    }

    private static InteractionResult useOnOven(
            Player player, Level level, BlockPos pos, BlockHitResult hit,
            ItemStack peel, PizzaPeelCargo cargo
    ) {
        // With a peel, direct transfer takes priority over the oven menu.
        // The vanilla-style GUI still opens with any ordinary item or hand.
        if (!player.mayUseItemAt(pos, hit.getDirection(), peel)) {
            return InteractionResult.FAIL;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (!(level.getBlockEntity(pos) instanceof PizzaOvenBlockEntity oven)) {
            return InteractionResult.FAIL;
        }
        if (cargo != null) {
            // A cooked pizza cannot be baked twice, and an occupied oven
            // cannot be overwritten. Keep the peel unchanged on failure.
            ItemStack pizza = cargo.pizza();
            if (!oven.insertFromPeel(pizza)) {
                notifyOnServer(player, level, pizza.is(ModItems.COOKED_PIZZA)
                        ? "item.italiansdelight.pizza_peel.already_cooked"
                        : "item.italiansdelight.pizza_peel.oven_occupied");
                return InteractionResult.SUCCESS;
            }
            peel.remove(ModDataComponents.PEEL_CARGO);
        } else {
            // Finished output first; otherwise a raw/part-cooked input.
            // Cook progress belongs to the oven and resets upon removal.
            ItemStack taken = oven.takeForPeel();
            if (taken.isEmpty()) {
                notifyOnServer(player, level, "item.italiansdelight.pizza_peel.oven_empty");
                return InteractionResult.SUCCESS;
            }
            peel.set(ModDataComponents.PEEL_CARGO, new PizzaPeelCargo(taken));
        }
        if (level instanceof ServerLevel server) {
            server.playSound(null, pos, SoundEvents.WOOL_PLACE,
                    SoundSource.BLOCKS, 0.7F, 1.05F);
        }
        return InteractionResult.SUCCESS;
    }

    /** Status text is server-authoritative and respects each client's locale. */
    private static void notifyOnServer(Player player, Level level, String key) {
        if (!level.isClientSide()) {
            player.displayClientMessage(Component.translatable(key), true);
        }
    }
}
