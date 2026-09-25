package com.piergiuseppe.italiansdelight.common.registry;

import net.minecraft.resources.Identifier;

import com.piergiuseppe.italiansdelight.ItaliansDelight;

/**
 * Utility per creare Identifier sempre nel namespace di Italian's Delight.
 * Centralizzare questa operazione evita di ripetere il mod id e riduce il
 * rischio di registrare accidentalmente risorse con un namespace errato.
 */
public final class ModRegistries {

    private ModRegistries() {
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(ItaliansDelight.MOD_ID, path);
    }
}