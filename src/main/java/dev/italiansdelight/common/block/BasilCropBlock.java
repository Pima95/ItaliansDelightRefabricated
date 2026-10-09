package dev.italiansdelight.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Cultivated basil.
 *
 * The crop intentionally keeps vanilla's 0-7 crop age so its growth speed
 * remains comparable to wheat. The blockstate maps those eight ages onto the
 * four basil textures already present in the project.
 */
public class BasilCropBlock extends CropBlock {
    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
        Block.box(2.0D, 0.0D, 2.0D, 14.0D, 3.0D, 14.0D),
        Block.box(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D),
        Block.box(2.0D, 0.0D, 2.0D, 14.0D, 5.0D, 14.0D),
        Block.box(2.0D, 0.0D, 2.0D, 14.0D, 6.0D, 14.0D),
        Block.box(2.0D, 0.0D, 2.0D, 14.0D, 8.0D, 14.0D),
        Block.box(2.0D, 0.0D, 2.0D, 14.0D, 10.0D, 14.0D),
        Block.box(2.0D, 0.0D, 2.0D, 14.0D, 11.0D, 14.0D),
        Block.box(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D)
    };

    public BasilCropBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected ItemLike getBaseSeedId() {
        // Basil is both the harvested ingredient and the replantable crop item.
        return this;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos,
                               CollisionContext context) {
        return SHAPE_BY_AGE[state.getValue(this.getAgeProperty())];
    }
}
