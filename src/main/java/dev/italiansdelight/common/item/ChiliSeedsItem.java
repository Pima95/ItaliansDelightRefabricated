package dev.italiansdelight.common.item;

import dev.italiansdelight.common.block.StakeCropPlanting;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;

/** Chili seeds plant on a thin stake or its farmland base (same as tomato). */
public final class ChiliSeedsItem extends BlockItem {
    public ChiliSeedsItem(Block crop, Properties props) { super(crop, props); }

    @Override public InteractionResult useOn(UseOnContext ctx) {
        if (ctx.getPlayer() == null) return InteractionResult.FAIL;
        InteractionResult result = StakeCropPlanting.tryPlant(
            ctx.getPlayer(), ctx.getLevel(), ctx.getHand(), ctx.getClickedPos());
        // Let the normal BlockItem placement plant a free-standing chili crop
        // on farmland when no reusable stake was clicked.
        return result == InteractionResult.PASS ? super.useOn(ctx) : result;
    }
}
