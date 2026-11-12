package dev.italiansdelight.common.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

/** Ordinary chili seeds, with no special interaction on decorative poles. */
public final class ChiliSeedsItem extends BlockItem {
    public ChiliSeedsItem(Block crop, Properties properties) { super(crop, properties); }
}
