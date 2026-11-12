package dev.italiansdelight.mixin;

import dev.italiansdelight.common.block.CropStakeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.block.TomatoBlock;

/**
 * Keep original FDR hanging tomato ropes compatible with the persistent
 * Rustic-style stake + tomato crop directly underneath. No upstream rope
 * code or texture is changed.
 */
@Mixin(TomatoBlock.class)
public abstract class TomatoOnStakeSupportMixin {
    @Inject(method = "canSurvive", at = @At("HEAD"), cancellable = true)
    private void italiansdelight$allowTomatoesOnStake(BlockState state,
            LevelReader level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (CropStakeBlock.hasCrop(level.getBlockState(pos.below()), CropStakeBlock.TOMATO)) {
            cir.setReturnValue(level.getRawBrightness(pos, 0) >= 8 || level.canSeeSky(pos));
        }
    }
}
