package dev.italiansdelight.common.block;

import net.minecraft.world.effect.MobEffects;
import vectorwing.farmersdelight.common.block.WildCropBlock;

/**
 * Same centered selection shape, randomized visual offset, supported soils
 * and bounded bonemeal propagation as Farmer's Delight wild crops.
 * Keeps the existing wild_basil ID, models, items and loot table.
 */
public final class WildBasilBlock extends WildCropBlock {
    public WildBasilBlock(Properties properties) {
        super(MobEffects.REGENERATION, 6, properties);
    }
}
