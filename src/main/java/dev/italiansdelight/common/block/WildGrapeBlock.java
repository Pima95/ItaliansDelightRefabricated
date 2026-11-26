package dev.italiansdelight.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.tags.BlockTags;

/**
 * FDR WildRiceBlock-style double plant. No right-click harvest:
 * break either half, with shears recovering the complete plant.
 * The loot tables check both halves and never duplicate drops.
 */
public final class WildGrapeBlock extends DoublePlantBlock implements BonemealableBlock {
    public WildGrapeBlock(Properties properties) {
        super(properties);
    }

    @Override public boolean mayPlaceOn(BlockState ground, BlockGetter level, BlockPos pos) {
        return ground.is(BlockTags.DIRT) || ground.is(BlockTags.SAND);
    }

    @Override public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return false;
    }

    @Override public boolean isValidBonemealTarget(LevelReader level, BlockPos pos,
            BlockState state) {
        return true;
    }

    @Override public boolean isBonemealSuccess(Level level, RandomSource random,
            BlockPos pos, BlockState state) {
        return random.nextFloat() < 0.3F;
    }

    @Override public void performBonemeal(ServerLevel level, RandomSource random,
            BlockPos pos, BlockState state) {
        // The same 30% whole-plant bonemeal behavior as wild rice.
        BlockPos root = state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos;
        BlockState low = level.getBlockState(root);
        BlockState high = level.getBlockState(root.above());
        if (low.is(this) && low.getValue(HALF) == DoubleBlockHalf.LOWER
                && high.is(this) && high.getValue(HALF) == DoubleBlockHalf.UPPER) {
            popResource(level, root, new ItemStack(this));
        }
    }
}
