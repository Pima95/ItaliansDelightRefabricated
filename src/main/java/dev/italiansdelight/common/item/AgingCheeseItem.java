package dev.italiansdelight.common.item;

import dev.italiansdelight.common.aging.AgingCheeseType;
import dev.italiansdelight.common.block.AgingCheeseBlock;
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
 * Whole cheese, fresh or finished, placeable on a flat, sturdy surface.
 *
 * No aging progress is ever stored on this ItemStack. Placement always starts
 * a new world-side timer from zero.
 */
public final class AgingCheeseItem extends Item {

    private final AgingCheeseType cheeseType;
    private final boolean mature;

    public AgingCheeseItem(
        AgingCheeseType cheeseType,
        Properties properties
    ) {
        this(cheeseType, false, properties);
    }

    public AgingCheeseItem(
        AgingCheeseType cheeseType,
        boolean mature,
        Properties properties
    ) {
        super(properties);
        this.cheeseType = cheeseType;
        this.mature = mature;
    }

    public AgingCheeseType cheeseType() {
        return cheeseType;
    }

    public boolean isMature() {
        return mature;
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

        Player player = context.getPlayer();
        if (player != null && !player.mayUseItemAt(
            cheesePos, context.getClickedFace(), context.getItemInHand()
        )) {
            return InteractionResult.FAIL;
        }

        BlockState placedState = ModBlocks.AGING_CHEESE.defaultBlockState()
            .setValue(AgingCheeseBlock.CHEESE, cheeseType)
            .setValue(AgingCheeseBlock.MATURE, mature);
        if (!level.isUnobstructed(placedState, cheesePos, net.minecraft.world.phys.shapes.CollisionContext.empty())) {
            return InteractionResult.FAIL;
        }

        // Placement also works for finished cheeses and decorative forms;
        // available recipes determine processing, not the ability to place.
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.PASS;
        }

        if (
            !serverLevel.setBlock(
                cheesePos,
                placedState,
                Block.UPDATE_ALL
            )
        ) {
            return InteractionResult.FAIL;
        }

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
