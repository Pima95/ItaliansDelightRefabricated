package dev.italiansdelight.common.item;

import dev.italiansdelight.common.aging.AgingCheeseType;
import dev.italiansdelight.common.block.AgingCheeseBlock;
import dev.italiansdelight.common.block.entity.AgingCheeseBlockEntity;
import dev.italiansdelight.common.registry.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

/**
 * Fresh cheese wheel that can be placed directly on a flat, sturdy surface.
 *
 * No aging progress is ever stored on this ItemStack. Placement always starts
 * a new world-side timer from zero.
 */
public final class AgingCheeseItem extends Item {

    private final AgingCheeseType cheeseType;

    public AgingCheeseItem(
        AgingCheeseType cheeseType,
        Properties properties
    ) {
        super(properties);
        this.cheeseType = cheeseType;
    }

    public AgingCheeseType cheeseType() {
        return cheeseType;
    }

    @Override
    public InteractionResult useOn(
        UseOnContext context
    ) {
        if (context.getClickedFace() != Direction.UP) {
            return InteractionResult.PASS;
        }

        Level level = context.getLevel();
        BlockPos supportPos = context.getClickedPos();
        BlockState supportState =
            level.getBlockState(supportPos);

        // "Flat surface" in gameplay terms: the upper face must fully support
        // a normal block. This excludes leaves, fences, open trapdoors, etc.
        if (
            !supportState.isFaceSturdy(
                level,
                supportPos,
                Direction.UP
            )
        ) {
            return InteractionResult.PASS;
        }

        BlockPos cheesePos =
            supportPos.above();

        if (!level.getBlockState(cheesePos).isAir()) {
            return InteractionResult.PASS;
        }

        // Client predicts the successful interaction; the authoritative recipe
        // validation and placement happen on the server.
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.PASS;
        }

        if (
            !AgingCheeseBlockEntity.hasRecipeFor(
                serverLevel,
                cheeseType
            )
        ) {
            return InteractionResult.FAIL;
        }

        BlockState placedState =
            ModBlocks.AGING_CHEESE
                .defaultBlockState()
                .setValue(
                    AgingCheeseBlock.CHEESE,
                    cheeseType
                )
                .setValue(
                    AgingCheeseBlock.MATURE,
                    false
                );

        if (
            !serverLevel.setBlock(
                cheesePos,
                placedState,
                Block.UPDATE_ALL
            )
        ) {
            return InteractionResult.FAIL;
        }

        Player player =
            context.getPlayer();

        ItemStack held =
            context.getItemInHand();

        if (
            player == null
            || !player.getAbilities().instabuild
        ) {
            held.shrink(1);
        }

        serverLevel.playSound(
            null,
            cheesePos,
            SoundEvents.WOOL_PLACE,
            SoundSource.BLOCKS,
            0.7F,
            1.0F
        );

        serverLevel.gameEvent(
            player,
            GameEvent.BLOCK_PLACE,
            cheesePos
        );

        return InteractionResult.SUCCESS;
    }
}
