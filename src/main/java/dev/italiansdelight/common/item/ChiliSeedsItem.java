package dev.italiansdelight.common.item;

import dev.italiansdelight.common.registry.ModBlocks;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/** Chili seeds must be attached to a reusable crop stake, as in Rustic. */
public final class ChiliSeedsItem extends BlockItem {
    public ChiliSeedsItem(Block crop, Properties props) { super(crop, props); }

    @Override public InteractionResult useOn(UseOnContext ctx) {
        var level=ctx.getLevel();
        var pos=ctx.getClickedPos();
        if (!level.getBlockState(pos).is(ModBlocks.CROP_STAKE)
                || !level.getBlockState(pos.below()).is(Blocks.FARMLAND))
            return InteractionResult.FAIL;
        if (level instanceof ServerLevel server) {
            if (server.setBlock(pos,ModBlocks.CHILI_CROP.defaultBlockState(),Block.UPDATE_ALL)
                    && (ctx.getPlayer()==null || !ctx.getPlayer().getAbilities().instabuild))
                ctx.getItemInHand().shrink(1);
        }
        return InteractionResult.SUCCESS;
    }
}
