package dev.italiansdelight.mixin.client;

import dev.italiansdelight.common.aging.HangingCheeseType;
import dev.italiansdelight.common.block.CheeseHookBlock;
import dev.italiansdelight.common.registry.ModBlocks;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Keeps vanilla lantern-like debris limited to the hook, even with cheese attached. */
@Mixin(ParticleEngine.class)
public abstract class CheeseHookParticlesMixin {

    @Redirect(
        method = {"destroy", "crack"},
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;"
                + "getShape(Lnet/minecraft/world/level/BlockGetter;"
                + "Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/shapes/VoxelShape;"
        ),
        require = 1
    )
    private VoxelShape italiansDelight$hookParticleShape(
        BlockState state,
        BlockGetter level,
        BlockPos pos
    ) {
        if (state.is(ModBlocks.CHEESE_HOOK)) {
            // The combined selection shape also includes cheese below the hook.
            // Use only the metal volume for both mining chips and the final burst;
            // vanilla still controls particle positions, velocity and gravity.
            state = state.setValue(CheeseHookBlock.CHEESE, HangingCheeseType.EMPTY);
        }
        return state.getShape(level, pos);
    }
}
