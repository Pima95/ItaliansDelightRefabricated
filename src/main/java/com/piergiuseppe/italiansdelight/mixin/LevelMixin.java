package com.piergiuseppe.italiansdelight.mixin;

import com.piergiuseppe.italiansdelight.common.salt.SaltCauldronManager;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Server-side mixin that observes vanilla cauldron state changes without
 * replacing vanilla interactions, and keeps the salt evaporation system updated.
 */
@Mixin(Level.class)
public abstract class LevelMixin {

    // The injection runs at RETURN, so the manager is notified only after
    // Minecraft has actually applied the new BlockState.
    @Inject(
        method =
            "setBlock(Lnet/minecraft/core/BlockPos;"
                + "Lnet/minecraft/world/level/block/state/BlockState;"
                + "II)Z",
        at = @At("RETURN")
    )
    private void italiansDelight$trackSaltCauldron(
        BlockPos pos,
        BlockState state,
        int updateFlags,
        int updateLimit,
        CallbackInfoReturnable<Boolean> cir
    ) {
        if (
            !Boolean.TRUE.equals(
                cir.getReturnValue()
            )
            || !((Object) this
                instanceof ServerLevel serverLevel)
        ) {
            return;
        }

        // Full/partial water and other vanilla cauldron states are the only
        // normal gameplay transitions that need immediate bookkeeping.
        // A completely replaced/broken tracked cauldron is also validated and
        // removed by the end-of-level tick.
        if (
            state.is(Blocks.WATER_CAULDRON)
            || state.is(Blocks.CAULDRON)
            || state.is(Blocks.LAVA_CAULDRON)
            || state.is(Blocks.POWDER_SNOW_CAULDRON)
        ) {
            SaltCauldronManager
                .onCauldronStateChanged(
                    serverLevel,
                    pos,
                    state
                );
        }
    }
}
