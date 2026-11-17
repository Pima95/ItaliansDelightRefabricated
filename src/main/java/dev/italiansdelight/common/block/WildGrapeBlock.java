package dev.italiansdelight.common.block;

import net.minecraft.world.effect.MobEffects;
import vectorwing.farmersdelight.common.block.WildCropBlock;

/** Farmer's Delight wild crop: break for seeds, shear to obtain the wild block,
 * and use bone meal for wild spreading. No right-click harvesting. */
public final class WildGrapeBlock extends WildCropBlock {
    public WildGrapeBlock(Properties properties) {
        super(MobEffects.POISON, 10, properties);
    }
}
