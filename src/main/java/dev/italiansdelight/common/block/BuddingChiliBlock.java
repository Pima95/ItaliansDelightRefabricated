package dev.italiansdelight.common.block;

import dev.italiansdelight.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockState;
import vectorwing.farmersdelight.common.block.BuddingBushBlock;
import vectorwing.farmersdelight.common.block.BuddingTomatoBlock;

/**
 * The missing first half of Farmer's Delight tomato growth.
 *
 * Chili seeds place this budding bush (four visual stages), then it transitions
 * into the existing ChiliCropBlock adult vine. Both blocks inherit FDR's
 * actual random tick, crop survival, age-based outline and bonemeal rules.
 *
 * Upstream BuddingTomatoBlock hardcodes the original tomato registry target in
 * these three conversion paths; redirect ONLY those paths to our chili vine.
 */
public final class BuddingChiliBlock extends BuddingTomatoBlock {
    public BuddingChiliBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return ModBlocks.CHILI_PEPPER_SEEDS;
    }

    @Override
    public void growPastMaxAge(BlockState state, ServerLevel level,
            BlockPos pos, RandomSource random) {
        level.setBlockAndUpdate(pos, ModBlocks.CHILI_CROP.defaultBlockState());
    }

    @Override
    public BlockState updateShape(BlockState state, LevelReader level,
            ScheduledTickAccess ticks, BlockPos pos, Direction direction,
            BlockPos neighbourPos, BlockState neighbour, RandomSource random) {
        if (state.getValue(BuddingBushBlock.AGE) == 4) {
            return ModBlocks.CHILI_CROP.defaultBlockState();
        }
        return super.updateShape(state, level, ticks, pos, direction,
            neighbourPos, neighbour, random);
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random,
            BlockPos pos, BlockState state) {
        // Same BuddingTomatoBlock growth calculation, but conversion targets chili.
        int grownAge = Math.min(getAge(state) + getBonemealAgeIncrease(level), 7);
        if (grownAge <= getMaxAge()) {
            level.setBlockAndUpdate(pos, state.setValue(BuddingBushBlock.AGE, grownAge));
        } else {
            int vineAge = grownAge - getMaxAge() - 1;
            level.setBlockAndUpdate(pos, ModBlocks.CHILI_CROP.defaultBlockState()
                .setValue(ChiliCropBlock.CHILI_AGE, vineAge));
        }
    }
}
