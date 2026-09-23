package com.piergiuseppe.italiansdelight.registry;

import net.minecraft.util.Identifier;
import com.piergiuseppe.italiansdelight.ItaliansDelight;

/** Piccola utility per creare Identifier nel namespace della mod. */
public final class ModRegistries {

    private ModRegistries() {}

    public static Identifier id(String path) {
        return Identifier.of(ItaliansDelight.MOD_ID, path);
    }
}
