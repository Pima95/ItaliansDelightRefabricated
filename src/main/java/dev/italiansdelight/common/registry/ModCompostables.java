package dev.italiansdelight.common.registry;

import net.fabricmc.fabric.api.registry.CompostableRegistry;

/**
 * Vanilla-equivalent composting for every growable/harvestable natural
 * Italian's Delight item. Wood/planks are deliberately not compostable:
 * vanilla does not compost logs or crafted wooden boards either.
 *
 * The chance is the probability of increasing the vanilla compost level,
 * not the bone meal yield or a drop rate.
 */
public final class ModCompostables {
    private ModCompostables() {}

    public static void register() {
        // Tree saplings and leaves follow vanilla sapling/leaf (30%) odds.
        CompostableRegistry.INSTANCE.add(ModBlocks.OLIVE_SAPLING.asItem(), 0.30f);
        CompostableRegistry.INSTANCE.add(ModBlocks.ALMOND_SAPLING.asItem(), 0.30f);
        CompostableRegistry.INSTANCE.add(ModBlocks.FRUIT_CHERRY_SAPLING.asItem(), 0.30f);
        CompostableRegistry.INSTANCE.add(ModBlocks.OLIVE_LEAVES.asItem(), 0.30f);
        CompostableRegistry.INSTANCE.add(ModBlocks.ALMOND_LEAVES.asItem(), 0.30f);
        CompostableRegistry.INSTANCE.add(ModBlocks.FRUIT_CHERRY_LEAVES.asItem(), 0.30f);

        // Planting material and fruits: seeds (30%); fruit/nuts (65%).
        CompostableRegistry.INSTANCE.add(ModItems.APPLE_SEEDS, 0.30f);
        CompostableRegistry.INSTANCE.add(ModItems.OLIVES, 0.65f);
        CompostableRegistry.INSTANCE.add(ModItems.ALMONDS, 0.65f);
        CompostableRegistry.INSTANCE.add(ModItems.CHERRIES, 0.65f);

        // Wild and cultivated herbs: both variants are actual BlockItems.
        CompostableRegistry.INSTANCE.add(ModBlocks.BASIL.asItem(), 0.65f);
        CompostableRegistry.INSTANCE.add(ModBlocks.WILD_BASIL.asItem(), 0.65f);
        CompostableRegistry.INSTANCE.add(ModBlocks.CARDOON.asItem(), 0.65f);
        CompostableRegistry.INSTANCE.add(ModBlocks.CULTIVATED_CARDOON.asItem(), 0.65f);

        // Packed natural produce, like vanilla hay/dried kelp blocks (85%).
        CompostableRegistry.INSTANCE.add(ModBlocks.OLIVE_CRATE.asItem(), 0.85f);
        CompostableRegistry.INSTANCE.add(ModBlocks.ALMOND_CRATE.asItem(), 0.85f);
        CompostableRegistry.INSTANCE.add(ModBlocks.CHERRY_CRATE.asItem(), 0.85f);
        CompostableRegistry.INSTANCE.add(ModBlocks.BASIL_CRATE.asItem(), 0.85f);
        CompostableRegistry.INSTANCE.add(ModBlocks.CARDOON_CRATE.asItem(), 0.85f);
    }
}
