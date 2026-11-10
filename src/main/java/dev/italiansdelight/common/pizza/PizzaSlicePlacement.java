package dev.italiansdelight.common.pizza;

import dev.italiansdelight.common.block.CookedPizzaBlock;
import dev.italiansdelight.common.block.entity.CookedPizzaBlockEntity;
import dev.italiansdelight.common.registry.ModBlocks;
import dev.italiansdelight.common.registry.ModDataComponents;
import dev.italiansdelight.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
 * A cooked pizza slice can be put into an empty position of any baked pizza,
 * or placed by itself on the same approved supports used for a whole pizza.
 * Only the authoritative server changes the world and consumes the slice.
 */
public final class PizzaSlicePlacement {
    private PizzaSlicePlacement() {
    }

    public static InteractionResult use(
            Player player, Level level, BlockHitResult hit, ItemStack held
    ) {
        if (!held.is(ModItems.PIZZA_SLICE)) {
            return InteractionResult.PASS;
        }
        PizzaSliceData slice = held.get(ModDataComponents.PIZZA_SLICE_CONTENTS);
        if (slice == null) {
            return InteractionResult.FAIL;
        }

        BlockPos clicked = hit.getBlockPos();
        BlockState clickedState = level.getBlockState(clicked);
        boolean hitPizza = clickedState.is(ModBlocks.COOKED_PIZZA);
        if (!hitPizza && hit.getDirection() != Direction.UP) {
            return InteractionResult.PASS;
        }

        // An empty quadrant has no collision shape: the ray may hit the
        // support BELOW it. Check the target position before placing a block.
        BlockPos target = hitPizza ? clicked : clicked.above();
        BlockState current = level.getBlockState(target);
        if (current.is(ModBlocks.COOKED_PIZZA)) {
            int remaining = current.getValue(CookedPizzaBlock.REMAINING);
            if (remaining == PizzaQuarterSelection.ALL_QUARTERS) {
                return InteractionResult.PASS; // Do not overwrite a whole pizza.
            }
            if (!player.mayUseItemAt(target, Direction.UP, held)) {
                return InteractionResult.FAIL;
            }

            String orientation = current.getValue(CookedPizzaBlock.FACING).getName();
            int preferred = PizzaQuarterSelection.physicalQuarter(
                    orientation,
                    hit.getLocation().x - target.getX(),
                    hit.getLocation().z - target.getZ());
            int quarter = findOpenQuarter(preferred, remaining);
            if (quarter < 0) {
                return InteractionResult.PASS;
            }
            if (level.isClientSide()) {
                return InteractionResult.SUCCESS;
            }
            if (!(level instanceof ServerLevel server)
                    || !(server.getBlockEntity(target) instanceof CookedPizzaBlockEntity entity)) {
                return InteractionResult.FAIL;
            }
            // Commit exactly one previously empty bit. Insertion records
            // the actual source toppings and permanently revokes whole drops.
            if (!server.setBlock(target,
                    current.setValue(CookedPizzaBlock.REMAINING, remaining | (1 << quarter)),
                    Block.UPDATE_ALL)) {
                return InteractionResult.FAIL;
            }
            entity.insertSlice(quarter, slice);
            consumeAndSound(player, server, target, held);
            return InteractionResult.SUCCESS;
        }

        if (!current.isAir()) {
            return InteractionResult.PASS; // Never overwrite other blocks.
        }
        Direction facing = switch (PizzaQuarterSelection.nearestClockwiseFacing(player.getYRot())) {
            case "east" -> Direction.EAST;
            case "south" -> Direction.SOUTH;
            case "west" -> Direction.WEST;
            default -> Direction.NORTH;
        };
        int quarter = PizzaQuarterSelection.physicalQuarter(
                facing.getName(),
                hit.getLocation().x - target.getX(),
                hit.getLocation().z - target.getZ());
        BlockState placed = ModBlocks.COOKED_PIZZA.defaultBlockState()
                .setValue(CookedPizzaBlock.FACING, facing)
                .setValue(CookedPizzaBlock.REMAINING, 1 << quarter);
        if (!placed.canSurvive(level, target)) {
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
        if (!(server.getBlockEntity(target) instanceof CookedPizzaBlockEntity entity)) {
            // Fail closed: avoid consuming or duplicating an untracked item.
            server.setBlock(target, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
            return InteractionResult.FAIL;
        }
        entity.insertSlice(quarter, slice);
        consumeAndSound(player, server, target, held);
        server.gameEvent(player, GameEvent.BLOCK_PLACE, target);
        return InteractionResult.SUCCESS;
    }

    private static int findOpenQuarter(int preferred, int remaining) {
        if ((remaining & (1 << preferred)) == 0) {
            return preferred;
        }
        // Clicking the visible portion also works when the missing portion
        // has no hitbox. Choose the first free quarter deterministically.
        for (int offset = 1; offset < 4; offset++) {
            int candidate = (preferred + offset) % 4;
            if ((remaining & (1 << candidate)) == 0) {
                return candidate;
            }
        }
        return -1;
    }

    private static void consumeAndSound(
            Player player, ServerLevel server, BlockPos pos, ItemStack held
    ) {
        if (!player.getAbilities().instabuild) {
            held.shrink(1);
        }
        server.playSound(null, pos, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.7F, 1.0F);
    }
}
