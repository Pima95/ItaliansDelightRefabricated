package dev.italiansdelight.common.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.RegisterEvent;

public final class ModParticles {
    public static SimpleParticleType WHITE_BUBBLE_POP;
    private ModParticles() {}

    public static void onRegister(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.PARTICLE_TYPE)) {
            WHITE_BUBBLE_POP = Registry.register(BuiltInRegistries.PARTICLE_TYPE,
                    ModRegistries.id("white_bubble_pop"), new SimpleParticleType(false));
        }
    }
}
