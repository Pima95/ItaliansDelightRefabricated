package dev.italiansdelight.common.block;

import net.minecraft.world.effect.MobEffects;
import vectorwing.farmersdelight.common.block.WildCropBlock;

/**
 * Wild chili shrub: use exactly the same placement/offset behavior as
 * Farmer's Delight wild tomatoes (WildCropBlock + TALL_GRASS properties).
 *
 * FDR keeps the selection shape centered rather than applying the
 * randomized block-render offset to the hitbox.
 */
public final class WildChiliBlock extends WildCropBlock {
    public WildChiliBlock(Properties properties) {
        super(MobEffects.POISON, 10, properties);
    }
}
