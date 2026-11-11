package dev.italiansdelight.common.registry;

import net.fabricmc.fabric.api.registry.CompostableRegistry;

/** Registers the original vanilla/Fabric composting chances (30%, 65%, 85%). */
public final class ModCompostables {
    private ModCompostables() {}

    public static void register() {
            CompostableRegistry.INSTANCE.add(ModBlocks.OLIVE_SAPLING.asItem(), 0.30F);
            CompostableRegistry.INSTANCE.add(ModBlocks.ALMOND_SAPLING.asItem(), 0.30F);
            CompostableRegistry.INSTANCE.add(ModBlocks.FRUIT_CHERRY_SAPLING.asItem(), 0.30F);
            CompostableRegistry.INSTANCE.add(ModBlocks.OLIVE_LEAVES.asItem(), 0.30F);
            CompostableRegistry.INSTANCE.add(ModBlocks.ALMOND_LEAVES.asItem(), 0.30F);
            CompostableRegistry.INSTANCE.add(ModBlocks.FRUIT_CHERRY_LEAVES.asItem(), 0.30F);
            CompostableRegistry.INSTANCE.add(ModItems.APPLE_SEEDS, 0.30F);
            CompostableRegistry.INSTANCE.add(ModItems.OLIVES, 0.65F);
            CompostableRegistry.INSTANCE.add(ModItems.OLIVE_SEED, 0.30F);
            CompostableRegistry.INSTANCE.add(ModItems.OLIVE_PITTED, 0.65F);
            CompostableRegistry.INSTANCE.add(ModItems.OLIVE_PATE, 0.65F);
            CompostableRegistry.INSTANCE.add(ModItems.BRUSCHETTA, 0.65F);
            CompostableRegistry.INSTANCE.add(ModItems.OLIVE_BRUSCHETTA, 0.65F);
            CompostableRegistry.INSTANCE.add(ModItems.ALMONDS, 0.65F);
            CompostableRegistry.INSTANCE.add(ModItems.CHOPPED_ALMONDS, 0.65F);
            CompostableRegistry.INSTANCE.add(ModItems.ALMOND_PESTO, 0.65F);
            CompostableRegistry.INSTANCE.add(ModItems.BASIL_PESTO, 0.65F);
            CompostableRegistry.INSTANCE.add(ModBlocks.ALMOND_CAKE.asItem(), 0.85F);
            CompostableRegistry.INSTANCE.add(ModItems.ALMOND_CAKE_SLICE, 0.85F);
            CompostableRegistry.INSTANCE.add(ModItems.CHERRIES, 0.65F);
            CompostableRegistry.INSTANCE.add(ModBlocks.BASIL.asItem(), 0.65F);
            CompostableRegistry.INSTANCE.add(ModBlocks.WILD_BASIL.asItem(), 0.65F);
            CompostableRegistry.INSTANCE.add(ModBlocks.CARDOON.asItem(), 0.65F);
            CompostableRegistry.INSTANCE.add(ModBlocks.CULTIVATED_CARDOON.asItem(), 0.65F);
            CompostableRegistry.INSTANCE.add(ModBlocks.OLIVE_CRATE.asItem(), 0.85F);
            CompostableRegistry.INSTANCE.add(ModBlocks.ALMOND_CRATE.asItem(), 0.85F);
            CompostableRegistry.INSTANCE.add(ModBlocks.CHERRY_CRATE.asItem(), 0.85F);
            CompostableRegistry.INSTANCE.add(ModBlocks.BASIL_CRATE.asItem(), 0.85F);
            CompostableRegistry.INSTANCE.add(ModBlocks.CARDOON_CRATE.asItem(), 0.85F);
    }
}
