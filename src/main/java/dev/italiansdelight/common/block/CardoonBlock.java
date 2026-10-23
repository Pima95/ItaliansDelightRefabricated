package dev.italiansdelight.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Legacy Cardoon block ID = the wild form, keeping old saves and flower pots.
 * Bonemeal now spreads a bounded number of wild plants, never dropping
 * harvested items directly (which previously generated infinite extra items).
 */
public final class CardoonBlock extends VegetationBlock implements BonemealableBlock {
    private static final MapCodec<CardoonBlock> CODEC = simpleCodec(CardoonBlock::new);
    private static final VoxelShape SHAPE = box(3, 0, 3, 13, 16, 13);

    public CardoonBlock(Properties properties) { super(properties); }
    @Override protected MapCodec<CardoonBlock> codec() { return CODEC; }
    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos,
                                  CollisionContext context) {
        return SHAPE.move(state.getOffset(pos));
    }
    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return true;
    }
    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return random.nextFloat() < 0.8F;
    }
    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        int plants = 0;
        for (BlockPos nearby : BlockPos.betweenClosed(
                pos.offset(-4, -1, -4), pos.offset(4, 1, 4))) {
            if (level.getBlockState(nearby).is(this) && ++plants >= 10) {
                return;
            }
        }
        // Four placement attempts, at most one additional wild plant.
        for (int attempt = 0; attempt < 4; attempt++) {
            BlockPos candidate = pos.offset(random.nextInt(7) - 3,
                    random.nextInt(3) - 1, random.nextInt(7) - 3);
            if (level.isEmptyBlock(candidate) && state.canSurvive(level, candidate)) {
                level.setBlock(candidate, state, 2);
                return;
            }
        }
    }
}
