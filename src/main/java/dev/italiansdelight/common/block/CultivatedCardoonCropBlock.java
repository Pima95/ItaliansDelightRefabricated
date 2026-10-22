package dev.italiansdelight.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Cultivated cardoon: wheat/basil-compatible age 0..7 and bone meal. */
public final class CultivatedCardoonCropBlock extends CropBlock {
    private static final VoxelShape[] SHAPES = {
        Block.box(4, 0, 4, 12, 3, 12),
        Block.box(4, 0, 4, 12, 4, 12),
        Block.box(3, 0, 3, 13, 5, 13),
        Block.box(3, 0, 3, 13, 6, 13),
        Block.box(2, 0, 2, 14, 8, 14),
        Block.box(2, 0, 2, 14, 10, 14),
        Block.box(2, 0, 2, 14, 12, 14),
        Block.box(2, 0, 2, 14, 15, 14)
    };
    public CultivatedCardoonCropBlock(Properties properties) { super(properties); }
    @Override
    protected ItemLike getBaseSeedId() { return this; }
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos,
                               CollisionContext context) {
        return SHAPES[state.getValue(this.getAgeProperty())];
    }
}
