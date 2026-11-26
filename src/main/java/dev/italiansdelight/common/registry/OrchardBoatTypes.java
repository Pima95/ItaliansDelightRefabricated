package dev.italiansdelight.common.registry;

import java.util.function.Supplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

/** Parameters are resolved lazily by the NeoForge FML enum extension.
 * Suppliers must not read the block/item registries during class loading.
 */
public final class OrchardBoatTypes {
    private OrchardBoatTypes() {}
    public static Object olive(int index, Class<?> type) {
        return parameter(index, type, "italiansdelight:olive",
            () -> ModBlocks.OLIVE_PLANKS,
            () -> ModBoats.OLIVE_BOAT_ITEM,
            () -> ModBoats.OLIVE_CHEST_BOAT_ITEM);
    }

    public static Object almond(int index, Class<?> type) {
        return parameter(index, type, "italiansdelight:almond",
            () -> ModBlocks.ALMOND_PLANKS,
            () -> ModBoats.ALMOND_BOAT_ITEM,
            () -> ModBoats.ALMOND_CHEST_BOAT_ITEM);
    }

    public static Object fruitcherry(int index, Class<?> type) {
        return parameter(index, type, "italiansdelight:fruit_cherry",
            () -> ModBlocks.FRUIT_CHERRY_PLANKS,
            () -> ModBoats.FRUIT_CHERRY_BOAT_ITEM,
            () -> ModBoats.FRUIT_CHERRY_CHEST_BOAT_ITEM);
    }

    private static Object parameter(int index, Class<?> type, String name,
            Supplier<Block> planks, Supplier<Item> boat, Supplier<Item> chestBoat) {
        return switch (index) {
            case 0 -> type.cast(planks);
            case 1 -> type.cast(name);
            case 2 -> type.cast(boat);
            case 3 -> type.cast(chestBoat);
            case 4 -> type.cast((Supplier<Item>) () -> Items.STICK);
            case 5 -> false;
            default -> throw new IllegalArgumentException("Invalid boat enum parameter " + index);
        };
    }
}
