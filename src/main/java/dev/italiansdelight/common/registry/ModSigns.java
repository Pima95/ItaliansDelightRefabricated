package dev.italiansdelight.common.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.item.HangingSignItem;

/**
 * Sign items deliberately own one ID for ground+wall and ceiling+wall.
 * Register after ModBlocks initializes the corresponding four block types.
 */
public final class ModSigns {
    private ModSigns() {}

    public static final ResourceKey<Item> OLIVE_SIGN_KEY =
        ResourceKey.create(BuiltInRegistries.ITEM.key(), ModRegistries.id("olive_sign"));
    public static final Item OLIVE_SIGN = Registry.register(
        BuiltInRegistries.ITEM, OLIVE_SIGN_KEY,
        new SignItem(ModBlocks.OLIVE_SIGN, ModBlocks.OLIVE_WALL_SIGN,
            new Item.Properties().stacksTo(16).useBlockDescriptionPrefix()
                .setId(OLIVE_SIGN_KEY))
    );

    public static final ResourceKey<Item> OLIVE_HANGING_SIGN_KEY =
        ResourceKey.create(BuiltInRegistries.ITEM.key(), ModRegistries.id("olive_hanging_sign"));
    public static final Item OLIVE_HANGING_SIGN = Registry.register(
        BuiltInRegistries.ITEM, OLIVE_HANGING_SIGN_KEY,
        new HangingSignItem(ModBlocks.OLIVE_HANGING_SIGN,
            ModBlocks.OLIVE_WALL_HANGING_SIGN,
            new Item.Properties().stacksTo(16).useBlockDescriptionPrefix()
                .setId(OLIVE_HANGING_SIGN_KEY))
    );

    public static void register() {
    }
}
