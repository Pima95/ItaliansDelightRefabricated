package dev.italiansdelight.common.aging;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Selection/collision volumes matching the cheese block model geometry. */
public final class CheeseShapes {
    private CheeseShapes() {}

    public static final VoxelShape PARMIGIANO_REGGIANO = Shapes.or(
        Block.box(2, 0, 2, 14, 4, 14)
    ).optimize();

    public static final VoxelShape PECORINO_ROMANO = Shapes.or(
        Block.box(4, 0, 4, 12, 4.5, 12)
    ).optimize();

    public static final VoxelShape GORGONZOLA = Shapes.or(
        Block.box(3, 0, 3, 13, 3.5, 13)
    ).optimize();

    public static final VoxelShape PROVOLONE = Shapes.or(
        Block.box(4, 0, 2, 12, 4.5, 11),
        Block.box(5, 0.5, 11, 11, 4, 13),
        Block.box(6.5, 1, 13, 9.5, 3.5, 15)
    ).optimize();

    public static final VoxelShape SCAMORZA = Shapes.or(
        Block.box(5, 0, 3, 11, 3.5, 9),
        Block.box(6.5, 0.75, 9, 9.5, 2.75, 11),
        Block.box(6, 0.5, 11, 10, 3, 13)
    ).optimize();

    public static final VoxelShape SMOKED_SCAMORZA = Shapes.or(
        Block.box(5, 0, 3, 11, 3.5, 9),
        Block.box(6.5, 0.75, 9, 9.5, 2.75, 11),
        Block.box(6, 0.5, 11, 10, 3, 13)
    ).optimize();

    public static final VoxelShape HANGING_PROVOLONE = Shapes.or(
        Block.box(7, -9, 6, 9, -8, 7),
        Block.box(6, -9, 7, 10, -8, 9),
        Block.box(7, -9, 9, 9, -8, 10),
        Block.box(6, -8, 5, 10, -7, 6),
        Block.box(5, -8, 6, 11, -7, 10),
        Block.box(6, -8, 10, 10, -7, 11),
        Block.box(6, -7, 4, 10, -3, 5),
        Block.box(5, -7, 5, 11, -3, 6),
        Block.box(4, -7, 6, 12, -3, 10),
        Block.box(5, -7, 10, 11, -3, 11),
        Block.box(6, -7, 11, 10, -3, 12),
        Block.box(6, -3, 5, 10, 0, 6),
        Block.box(5, -3, 6, 11, 0, 10),
        Block.box(6, -3, 10, 10, 0, 11),
        Block.box(7, 0, 6, 9, 2, 7),
        Block.box(6, 0, 7, 10, 2, 9),
        Block.box(7, 0, 9, 9, 2, 10),
        Block.box(7, 2, 7, 9, 3, 9),
        Block.box(6.75, 2, 6.75, 9.25, 6, 9.25)
    ).optimize();

    public static final VoxelShape HANGING_SCAMORZA = Shapes.or(
        Block.box(7, -3.5, 7, 9, -3, 9),
        Block.box(7, -3, 6, 9, -2.5, 7),
        Block.box(6, -3, 7, 10, -2.5, 9),
        Block.box(7, -3, 9, 9, -2.5, 10),
        Block.box(6, -2.5, 5, 10, 0.5, 6),
        Block.box(5, -2.5, 6, 11, 0.5, 10),
        Block.box(6, -2.5, 10, 10, 0.5, 11),
        Block.box(7, 0.5, 6, 9, 1.5, 7),
        Block.box(6, 0.5, 7, 10, 1.5, 9),
        Block.box(7, 0.5, 9, 9, 1.5, 10),
        Block.box(7, 1.5, 7, 9, 2.5, 9),
        Block.box(7, 2.5, 6, 9, 4, 7),
        Block.box(6, 2.5, 7, 10, 4, 9),
        Block.box(7, 2.5, 9, 9, 4, 10),
        Block.box(7, 4, 7, 9, 4.5, 9),
        Block.box(6.75, 2, 6.75, 9.25, 6, 9.25)
    ).optimize();

    public static VoxelShape placed(AgingCheeseType type) {
        return switch (type) {
            case PARMIGIANO_REGGIANO -> PARMIGIANO_REGGIANO;
            case PECORINO_ROMANO -> PECORINO_ROMANO;
            case GORGONZOLA -> GORGONZOLA;
            case PROVOLONE -> PROVOLONE;
            case SCAMORZA -> SCAMORZA;
            case SMOKED_SCAMORZA -> SMOKED_SCAMORZA;
        };
    }
}
