package dev.italiansdelight.common.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.RegisterEvent;
import dev.italiansdelight.common.item.OrchardBoatItem;
import java.util.function.Supplier;

public final class ModBoats {
    private ModBoats() {}
    public static EntityType<Boat> OLIVE_BOAT_ENTITY;
    public static EntityType<ChestBoat> OLIVE_CHEST_BOAT_ENTITY;
    public static Item OLIVE_BOAT_ITEM, OLIVE_CHEST_BOAT_ITEM;
    public static EntityType<Boat> ALMOND_BOAT_ENTITY;
    public static EntityType<ChestBoat> ALMOND_CHEST_BOAT_ENTITY;
    public static Item ALMOND_BOAT_ITEM, ALMOND_CHEST_BOAT_ITEM;
    public static EntityType<Boat> FRUIT_CHERRY_BOAT_ENTITY;
    public static EntityType<ChestBoat> FRUIT_CHERRY_CHEST_BOAT_ENTITY;
    public static Item FRUIT_CHERRY_BOAT_ITEM, FRUIT_CHERRY_CHEST_BOAT_ITEM;

    private static EntityType<Boat> boat(String id, Supplier<Item> item) {
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, ModRegistries.id(id),
            EntityType.Builder.<Boat>of((type, level) -> new Boat(type, level) {
                @Override public Item getDropItem() { return item.get(); }
            }, MobCategory.MISC).noLootTable().sized(1.375F, 0.5625F)
                .clientTrackingRange(10).build(id));
    }

    private static EntityType<ChestBoat> chestBoat(String id, Supplier<Item> item) {
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, ModRegistries.id(id),
            EntityType.Builder.<ChestBoat>of((type, level) -> new ChestBoat(type, level) {
                @Override public Item getDropItem() { return item.get(); }
            }, MobCategory.MISC).noLootTable().sized(1.375F, 0.5625F)
                .clientTrackingRange(10).build(id));
    }

    public static void onRegister(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.ENTITY_TYPE)) {
            OLIVE_BOAT_ENTITY = boat("olive_boat", () -> OLIVE_BOAT_ITEM);
            OLIVE_CHEST_BOAT_ENTITY = chestBoat("olive_chest_boat", () -> OLIVE_CHEST_BOAT_ITEM);
            ALMOND_BOAT_ENTITY = boat("almond_boat", () -> ALMOND_BOAT_ITEM);
            ALMOND_CHEST_BOAT_ENTITY = chestBoat("almond_chest_boat", () -> ALMOND_CHEST_BOAT_ITEM);
            FRUIT_CHERRY_BOAT_ENTITY = boat("fruit_cherry_boat", () -> FRUIT_CHERRY_BOAT_ITEM);
            FRUIT_CHERRY_CHEST_BOAT_ENTITY = chestBoat("fruit_cherry_chest_boat", () -> FRUIT_CHERRY_CHEST_BOAT_ITEM);
        }
        if (event.getRegistryKey().equals(Registries.ITEM)) {
            OLIVE_BOAT_ITEM = Registry.register(BuiltInRegistries.ITEM, ModRegistries.id("olive_boat"), new OrchardBoatItem(() -> OLIVE_BOAT_ENTITY, new Item.Properties().stacksTo(1)));
            OLIVE_CHEST_BOAT_ITEM = Registry.register(BuiltInRegistries.ITEM, ModRegistries.id("olive_chest_boat"), new OrchardBoatItem(() -> OLIVE_CHEST_BOAT_ENTITY, new Item.Properties().stacksTo(1)));
            ALMOND_BOAT_ITEM = Registry.register(BuiltInRegistries.ITEM, ModRegistries.id("almond_boat"), new OrchardBoatItem(() -> ALMOND_BOAT_ENTITY, new Item.Properties().stacksTo(1)));
            ALMOND_CHEST_BOAT_ITEM = Registry.register(BuiltInRegistries.ITEM, ModRegistries.id("almond_chest_boat"), new OrchardBoatItem(() -> ALMOND_CHEST_BOAT_ENTITY, new Item.Properties().stacksTo(1)));
            FRUIT_CHERRY_BOAT_ITEM = Registry.register(BuiltInRegistries.ITEM, ModRegistries.id("fruit_cherry_boat"), new OrchardBoatItem(() -> FRUIT_CHERRY_BOAT_ENTITY, new Item.Properties().stacksTo(1)));
            FRUIT_CHERRY_CHEST_BOAT_ITEM = Registry.register(BuiltInRegistries.ITEM, ModRegistries.id("fruit_cherry_chest_boat"), new OrchardBoatItem(() -> FRUIT_CHERRY_CHEST_BOAT_ENTITY, new Item.Properties().stacksTo(1)));
        }
    }
}
