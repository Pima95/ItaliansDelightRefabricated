package dev.italiansdelight.common.registry;

import net.minecraft.world.level.block.ComposterBlock;

/** Minecraft 1.21.1 composting registry. Invoke during enqueued common setup,
 * after all blocks and items have been registered.
 */
public final class ModCompostables {
    private ModCompostables() {}

    public static void register() {
        ComposterBlock.COMPOSTABLES.put(ModBlocks.OLIVE_SAPLING.asItem(), 0.3F);
        ComposterBlock.COMPOSTABLES.put(ModBlocks.ALMOND_SAPLING.asItem(), 0.3F);
        ComposterBlock.COMPOSTABLES.put(ModBlocks.FRUIT_CHERRY_SAPLING.asItem(), 0.3F);
        ComposterBlock.COMPOSTABLES.put(ModBlocks.OLIVE_LEAVES.asItem(), 0.3F);
        ComposterBlock.COMPOSTABLES.put(ModBlocks.ALMOND_LEAVES.asItem(), 0.3F);
        ComposterBlock.COMPOSTABLES.put(ModBlocks.FRUIT_CHERRY_LEAVES.asItem(), 0.3F);
        ComposterBlock.COMPOSTABLES.put(ModItems.APPLE_SEEDS, 0.3F);
        ComposterBlock.COMPOSTABLES.put(ModItems.OLIVES, 0.65F);
        ComposterBlock.COMPOSTABLES.put(ModItems.OLIVE_SEED, 0.3F);
        ComposterBlock.COMPOSTABLES.put(ModItems.OLIVE_PITTED, 0.65F);
        ComposterBlock.COMPOSTABLES.put(ModItems.OLIVE_PATE, 0.65F);
        ComposterBlock.COMPOSTABLES.put(ModItems.BRUSCHETTA, 0.65F);
        ComposterBlock.COMPOSTABLES.put(ModItems.OLIVE_BRUSCHETTA, 0.65F);
        ComposterBlock.COMPOSTABLES.put(ModItems.ALMONDS, 0.65F);
        ComposterBlock.COMPOSTABLES.put(ModItems.CHOPPED_ALMONDS, 0.65F);
        ComposterBlock.COMPOSTABLES.put(ModItems.ALMOND_PESTO, 0.65F);
        ComposterBlock.COMPOSTABLES.put(ModItems.BASIL_PESTO, 0.65F);
        ComposterBlock.COMPOSTABLES.put(ModBlocks.ALMOND_CAKE.asItem(), 0.85F);
        ComposterBlock.COMPOSTABLES.put(ModItems.ALMOND_CAKE_SLICE, 0.85F);
        ComposterBlock.COMPOSTABLES.put(ModItems.CHERRIES, 0.65F);
        ComposterBlock.COMPOSTABLES.put(ModBlocks.BASIL.asItem(), 0.65F);
        ComposterBlock.COMPOSTABLES.put(ModBlocks.WILD_BASIL.asItem(), 0.65F);
        ComposterBlock.COMPOSTABLES.put(ModBlocks.CARDOON.asItem(), 0.65F);
        ComposterBlock.COMPOSTABLES.put(ModBlocks.CULTIVATED_CARDOON.asItem(), 0.65F);
        ComposterBlock.COMPOSTABLES.put(ModBlocks.OLIVE_CRATE.asItem(), 0.85F);
        ComposterBlock.COMPOSTABLES.put(ModBlocks.ALMOND_CRATE.asItem(), 0.85F);
        ComposterBlock.COMPOSTABLES.put(ModBlocks.CHERRY_CRATE.asItem(), 0.85F);
        ComposterBlock.COMPOSTABLES.put(ModBlocks.BASIL_CRATE.asItem(), 0.85F);
        ComposterBlock.COMPOSTABLES.put(ModBlocks.CARDOON_CRATE.asItem(), 0.85F);
    }
}
