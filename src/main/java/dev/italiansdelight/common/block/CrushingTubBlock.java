package dev.italiansdelight.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Placeable, hollow wooden Crushing Tub adapted from the supplied Rustic model.
 *
 * <p>The bounds reproduce the original five model elements exactly: a
 * 14x14 bottom board (1 pixel high) and four walls (9 pixels high).
 * This stage intentionally adds no crushing inventory, liquid or recipes.
 */
public final class CrushingTubBlock extends Block {
    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(1, 0, 1, 15, 1, 15),   // bottom board
            Block.box(1, 0, 0, 15, 9, 1),   // north wall
            Block.box(1, 0, 15, 15, 9, 16), // south wall
            Block.box(0, 0, 1, 1, 9, 15),   // west wall
            Block.box(15, 0, 1, 16, 9, 15) // east wall
    ).optimize();

    public CrushingTubBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos,
                               CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level,
                                        BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
