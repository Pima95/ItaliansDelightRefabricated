package dev.italiansdelight.common.block;

import dev.italiansdelight.common.registry.ModRegistries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.VoxelShape;
import vectorwing.farmersdelight.common.utility.ShapeUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import vectorwing.farmersdelight.common.block.RiceRollMedleyBlock;

/** Eight individual ingredients, in the same order as the staged models. */
public final class AntipastiBoardBlock extends RiceRollMedleyBlock {
    private final String[] ingredients;
    private final VoxelShape[][] servingShapes;

    public AntipastiBoardBlock(Properties properties, String boardId, String... ingredients) {
        super(properties);
        if (ingredients.length != 4) throw new IllegalArgumentException("Expected four ingredient types");
        this.ingredients = ingredients.clone();
        this.servingShapes = buildShapes(boardId);
    }

    @Override
    public ItemStack getServingItem(BlockState state) {
        int remaining = state.getValue(getServingsProperty());
        if (remaining == 0) return ItemStack.EMPTY;
        return new ItemStack(BuiltInRegistries.ITEM.getValue(ModRegistries.id(ingredients[(remaining - 1) / 2])));
    }
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        // The base constructor may ask for a shape before instance fields are initialized.
        if (servingShapes == null) return super.getShape(state, level, pos, context);
        return servingShapes[state.getValue(getServingsProperty())][state.getValue(FACING).get2DDataValue()];
    }

    // North-facing cuboids match the JSON elements and are added in serving order.
    private static VoxelShape[][] buildShapes(String boardId) {
        int[][][] pieces = switch (boardId) {
            case "cold_cuts_board" -> new int[][][] {
                {{2, 3, 3, 6, 4, 7}},
                {{4, 4, 4, 8, 5, 8}},
                {{9, 3, 3, 14, 4, 6}, {9, 4, 3, 11, 5, 6}},
                {{11, 4, 4, 15, 5, 7}, {12, 5, 4, 15, 6, 6}},
                {{2, 3, 10, 6, 4, 14}},
                {{4, 4, 9, 8, 5, 13}},
                {{8, 3, 10, 13, 4, 12}},
                {{9, 4, 11, 14, 5, 13}}
            };
            case "cheese_board" -> new int[][][] {
                {{2, 3, 3, 5, 5, 7}, {2, 5, 3, 4, 6, 7}},
                {{5, 3, 4, 8, 5, 8}, {6, 5, 4, 8, 6, 8}},
                {{9, 3, 4, 12, 5, 8}, {9, 5, 4, 12, 6, 6}},
                {{12, 3, 3, 15, 5, 7}, {12, 5, 3, 15, 6, 5}},
                {{2, 3, 9, 5, 5, 13}, {2, 5, 9, 4, 6, 12}},
                {{5, 3, 10, 8, 5, 14}, {6, 5, 11, 8, 6, 14}},
                {{9, 3, 9, 13, 4, 12}},
                {{11, 4, 10, 15, 5, 13}}
            };
            case "mixed_board" -> new int[][][] {
                {{2, 3, 3, 6, 4, 7}},
                {{4, 4, 4, 8, 5, 8}},
                {{9, 3, 3, 14, 4, 6}, {9, 4, 3, 11, 5, 6}},
                {{11, 4, 4, 15, 5, 7}, {12, 5, 4, 15, 6, 6}},
                {{2, 3, 9, 6, 4, 12}},
                {{4, 4, 10, 8, 5, 13}},
                {{9, 3, 7, 11, 5, 9}, {11, 3, 11, 13, 5, 13}},
                {{12, 3, 8, 14, 5, 10}, {8, 3, 10, 10, 5, 12}}
            };
            default -> throw new IllegalArgumentException("Unknown antipasti board: " + boardId);
        };
        VoxelShape[] stages = new VoxelShape[9];
        VoxelShape current = Shapes.or(Block.box(1, 1, 2, 15, 3, 14),
            Block.box(11, 0, 2, 13, 1, 14), Block.box(3, 0, 2, 5, 1, 14));
        stages[0] = current;
        for (int i = 0; i < pieces.length; i++) {
            for (int[] b : pieces[i]) {
                current = Shapes.join(current, Block.box(b[0], b[1], b[2], b[3], b[4], b[5]), BooleanOp.OR);
            }
            stages[i + 1] = current;
        }
        return ShapeUtils.buildRotatedFoodShapes(stages);
    }
}
