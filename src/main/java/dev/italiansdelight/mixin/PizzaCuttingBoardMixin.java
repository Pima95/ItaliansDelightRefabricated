package dev.italiansdelight.mixin;

import dev.italiansdelight.common.pizza.PizzaItemData;
import dev.italiansdelight.common.pizza.PizzaSliceFactory;
import dev.italiansdelight.common.registry.ModDataComponents;
import dev.italiansdelight.common.registry.ModItems;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.block.CuttingBoardBlock;
import vectorwing.farmersdelight.common.block.entity.CuttingBoardBlockEntity;
import vectorwing.farmersdelight.common.registry.ModAdvancements;
import vectorwing.farmersdelight.common.tag.CommonTags;
import vectorwing.farmersdelight.common.utility.ItemUtils;

/**
 * Vanilla FD cutting recipes have static outputs. Generate each cooked
 * pizza's actual four quarters instead of four flavorless default slices.
 * Only the server commits item removal/output and damages the knife once.
 */
@Mixin(CuttingBoardBlockEntity.class)
public abstract class PizzaCuttingBoardMixin {
    @Inject(method = "processStoredItemUsingTool", at = @At("HEAD"), cancellable = true)
    private void italiansdelight$cutPizzaWithKnife(
            ItemStack tool, Player player, CallbackInfoReturnable<Boolean> cir) {
        CuttingBoardBlockEntity board = (CuttingBoardBlockEntity) (Object) this;
        ItemStack stored = board.getStoredItem();
        if (board.isItemCarvingBoard() || !stored.is(ModItems.COOKED_PIZZA)
                || !tool.is(CommonTags.Items.TOOLS_KNIFE)) {
            return;
        }

        PizzaItemData pizza = stored.get(ModDataComponents.PIZZA_CONTENTS);
        if (pizza == null) {
            // Never silently turn corrupt old data into four blank slices.
            return;
        }

        if (!(board.getLevel() instanceof ServerLevel serverLevel)) {
            // Client recipe property sets do not include dynamic pizza data.
            cir.setReturnValue(true);
            return;
        }
        if (!(player instanceof ServerPlayer serverPlayer)) {
            // Automation without a valid knife-wielding player is not enabled.
            return;
        }

        List<ItemStack> slices = new ArrayList<>(4);
        for (int quarter = 0; quarter < 4; quarter++) {
            slices.add(PizzaSliceFactory.create(pizza, quarter));
        }

        // Commit first, then spawn: two users cannot cut the same item twice.
        ItemStack taken = board.getInventory().extractItem(0, 1, false);
        if (taken.isEmpty()) {
            cir.setReturnValue(false);
            return;
        }

        Direction direction = board.getBlockState()
                .getValue(CuttingBoardBlock.FACING).getCounterClockWise();
        for (ItemStack slice : slices) {
            ItemUtils.spawnItemEntity(serverLevel, slice,
                    board.getBlockPos().getX() + 0.5 + direction.getStepX() * 0.2,
                    board.getBlockPos().getY() + 0.2,
                    board.getBlockPos().getZ() + 0.5 + direction.getStepZ() * 0.2,
                    direction.getStepX() * 0.2F, 0.0F,
                    direction.getStepZ() * 0.2F);
        }

        tool.hurtAndBreak(1, serverLevel, serverPlayer, item -> {});
        serverPlayer.awardStat(Stats.ITEM_USED.get(tool.getItem()));
        board.spawnCuttingParticles(serverLevel, board.getBlockPos(), taken);
        board.playProcessingSound(null, tool, taken);
        ModAdvancements.USE_CUTTING_BOARD.get().trigger(serverPlayer);
        cir.setReturnValue(true);
    }
}
