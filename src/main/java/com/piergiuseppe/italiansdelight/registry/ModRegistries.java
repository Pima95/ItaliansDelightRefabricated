package com.piergiuseppe.italiansdelight.registry;

import net.minecraft.resources.Identifier;

import com.piergiuseppe.italiansdelight.ItaliansDelight;

/** Utility per creare Identifier nel namespace della mod. */
public final class ModRegistries {

    private ModRegistries() {
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(ItaliansDelight.MOD_ID, path);
    }
}