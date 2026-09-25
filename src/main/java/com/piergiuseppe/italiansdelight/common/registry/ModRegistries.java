package com.piergiuseppe.italiansdelight.common.registry;

import net.minecraft.resources.Identifier;

import com.piergiuseppe.italiansdelight.ItaliansDelight;

/**
 * Utility for creating Identifiers in the Italian's Delight namespace.
 * Centralizing this operation avoids repeating the mod id and reduces the
 * risk of accidentally registering resources under the wrong namespace.
 */
public final class ModRegistries {

    private ModRegistries() {
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(ItaliansDelight.MOD_ID, path);
    }
}