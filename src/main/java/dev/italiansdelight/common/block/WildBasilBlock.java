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
 * Naturally generated basil used to bootstrap the cultivated crop.
 * Bone meal spreads nearby wild basil without creating a separate seed item.
 */
public class WildBasilBlock extends VegetationBlock implements BonemealableBlock {
    private static final MapCodec<WildBasilBlock> CODEC = simpleCodec(WildBasilBlock::new);
    private static final VoxelShape SHAPE = box(2, 0, 2, 14, 12, 14);

    public WildBasilBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<WildBasilBlock> codec() {
        return CODEC;
    }

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
        int remainingPlants = 10;

        for (BlockPos nearbyPos : BlockPos.betweenClosed(
                pos.offset(-4, -1, -4),
                pos.offset(4, 1, 4))) {
            if (level.getBlockState(nearbyPos).is(this)) {
                remainingPlants--;
                if (remainingPlants <= 0) {
                    return;
                }
            }
        }

        BlockPos candidate = pos.offset(
                random.nextInt(3) - 1,
                random.nextInt(2) - random.nextInt(2),
                random.nextInt(3) - 1
        );

        for (int attempt = 0; attempt < 4; attempt++) {
            if (level.isEmptyBlock(candidate) && state.canSurvive(level, candidate)) {
                pos = candidate;
            }

            candidate = pos.offset(
                    random.nextInt(3) - 1,
                    random.nextInt(2) - random.nextInt(2),
                    random.nextInt(3) - 1
            );
        }

        if (level.isEmptyBlock(candidate) && state.canSurvive(level, candidate)) {
            level.setBlock(candidate, state, 2);
        }
    }
}
