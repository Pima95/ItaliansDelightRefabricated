package dev.italiansdelight.common.registry;

import dev.italiansdelight.ItaliansDelight;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;

import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

/**
 * Registers custom particle types used by Italian's Delight.
 */
public final class ModParticles {

    public static final SimpleParticleType WHITE_BUBBLE_POP =
        FabricParticleTypes.simple();

    private ModParticles() {
    }

    public static void register() {
        Registry.register(
            BuiltInRegistries.PARTICLE_TYPE,
            ModRegistries.id("white_bubble_pop"),
            WHITE_BUBBLE_POP
        );

        ItaliansDelight.LOGGER.info(
            "[Italian's Delight] Registrata particella white_bubble_pop."
        );
    }
}
