package com.piergiuseppe.italiansdelight.client.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import net.minecraft.resources.Identifier;

@JeiPlugin
public class ItaliansDelightJeiPlugin implements IModPlugin {

    private static final Identifier UID =
        Identifier.fromNamespaceAndPath("italiansdelight", "jei_plugin");

    @Override
    public Identifier getPluginUid() {
        return UID;
    }
}