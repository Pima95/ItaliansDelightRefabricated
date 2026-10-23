package dev.italiansdelight.common.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.entity.vehicle.boat.ChestBoat;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.Item;

/**
 * Vanilla-style Olive Boat and Chest Boat. Both are actual vehicle entities,
 * not decorative inventory items, and reuse the approved entity PNGs.
 *
 * Using lazy item suppliers avoids static entity<->item registration cycles.
 */
public final class ModBoats {
    private ModBoats() {}

    public static final ResourceKey<EntityType<?>> OLIVE_BOAT_ENTITY_KEY =
        ResourceKey.create(BuiltInRegistries.ENTITY_TYPE.key(), ModRegistries.id("olive_boat"));
    public static final ResourceKey<EntityType<?>> OLIVE_CHEST_BOAT_ENTITY_KEY =
        ResourceKey.create(BuiltInRegistries.ENTITY_TYPE.key(), ModRegistries.id("olive_chest_boat"));

    public static final EntityType<Boat> OLIVE_BOAT_ENTITY = Registry.register(
        BuiltInRegistries.ENTITY_TYPE,
        OLIVE_BOAT_ENTITY_KEY,
        EntityType.Builder.<Boat>of(
            (type, level) -> new Boat(type, level, () -> OLIVE_BOAT_ITEM),
            MobCategory.MISC
        ).noLootTable().sized(1.375F, 0.5625F).eyeHeight(0.5625F)
            .clientTrackingRange(10).build(OLIVE_BOAT_ENTITY_KEY)
    );

    public static final EntityType<ChestBoat> OLIVE_CHEST_BOAT_ENTITY = Registry.register(
        BuiltInRegistries.ENTITY_TYPE,
        OLIVE_CHEST_BOAT_ENTITY_KEY,
        EntityType.Builder.<ChestBoat>of(
            (type, level) -> new ChestBoat(type, level, () -> OLIVE_CHEST_BOAT_ITEM),
            MobCategory.MISC
        ).noLootTable().sized(1.375F, 0.5625F).eyeHeight(0.5625F)
            .clientTrackingRange(10).build(OLIVE_CHEST_BOAT_ENTITY_KEY)
    );

    public static final ResourceKey<Item> OLIVE_BOAT_ITEM_KEY =
        ResourceKey.create(BuiltInRegistries.ITEM.key(), ModRegistries.id("olive_boat"));
    public static final Item OLIVE_BOAT_ITEM = Registry.register(
        BuiltInRegistries.ITEM,
        OLIVE_BOAT_ITEM_KEY,
        new BoatItem(OLIVE_BOAT_ENTITY, new Item.Properties().stacksTo(1).setId(OLIVE_BOAT_ITEM_KEY))
    );

    public static final ResourceKey<Item> OLIVE_CHEST_BOAT_ITEM_KEY =
        ResourceKey.create(BuiltInRegistries.ITEM.key(), ModRegistries.id("olive_chest_boat"));
    public static final Item OLIVE_CHEST_BOAT_ITEM = Registry.register(
        BuiltInRegistries.ITEM,
        OLIVE_CHEST_BOAT_ITEM_KEY,
        new BoatItem(OLIVE_CHEST_BOAT_ENTITY, new Item.Properties().stacksTo(1).setId(OLIVE_CHEST_BOAT_ITEM_KEY))
    );

    public static void register() {
        // Force registration of boat entities and their placeable inventory items.
    }
}
