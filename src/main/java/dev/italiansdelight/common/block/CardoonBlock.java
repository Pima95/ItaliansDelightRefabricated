package dev.italiansdelight.common.block;

import net.minecraft.world.effect.MobEffects;
import vectorwing.farmersdelight.common.block.WildCropBlock;

/**
 * Wild cardoon, preserving its old ID and drops. Like wild tomatoes and
 * chilies its hitbox stays centered while the renderer applies plant offset.
 */
public final class CardoonBlock extends WildCropBlock {
    public CardoonBlock(Properties properties) {
        super(MobEffects.STRENGTH, 6, properties);
    }
}
