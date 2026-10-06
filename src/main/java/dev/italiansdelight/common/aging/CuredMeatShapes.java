package dev.italiansdelight.common.aging;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Selection volumes matching the seven hanging meat model profiles. */
public final class CuredMeatShapes {
    private CuredMeatShapes() {}

    public static final VoxelShape PROSCIUTTO = Shapes.or(
        Block.box(7, -12, 7, 11, -11, 11),
        Block.box(7.5, -12, 6.5, 10.5, -11, 7),
        Block.box(7.5, -12, 11, 10.5, -11, 11.5),
        Block.box(5.5, -11, 6.5, 12, -10, 10.5),
        Block.box(6.5, -11, 5.5, 11, -10, 6.5),
        Block.box(6.5, -11, 10.5, 11, -10, 11.5),
        Block.box(4, -10, 5.5, 13, -7, 10.5),
        Block.box(5, -10, 4.5, 12, -7, 5.5),
        Block.box(5, -10, 10.5, 12, -7, 11.5),
        Block.box(4, -7, 5.5, 12.5, -4, 10.5),
        Block.box(5, -7, 4.5, 11.5, -4, 5.5),
        Block.box(5, -7, 10.5, 11.5, -4, 11.5),
        Block.box(5, -4, 6, 11.5, -2, 10.5),
        Block.box(6, -4, 5, 10.5, -2, 6),
        Block.box(6, -4, 10.5, 10.5, -2, 11.5),
        Block.box(6.5, -2, 6.5, 10.5, 0, 9.5),
        Block.box(7, -2, 6, 10, 0, 6.5),
        Block.box(7, -2, 9.5, 10, 0, 10),
        Block.box(7, 0, 7.25, 9.5, 2, 8.75),
        Block.box(7.5, 0, 6.75, 9, 2, 7.25),
        Block.box(7.5, 0, 8.75, 9, 2, 9.25),
        Block.box(7.5, 2, 7.35, 8.75, 4.2, 8.65),
        Block.box(7.15, 3.6, 7, 9.1, 4.6, 9),
        Block.box(6.5, 1.5, 6.5, 9.5, 6, 9.5)
    ).optimize();

    public static final VoxelShape SALAME = Shapes.or(
        Block.box(7, -10.5, 7.25, 9, -10, 8.75),
        Block.box(7.25, -10.5, 7, 8.75, -10, 7.25),
        Block.box(7.25, -10.5, 8.75, 8.75, -10, 9),
        Block.box(6.5, -10, 7, 9.5, -9, 9),
        Block.box(7, -10, 6.5, 9, -9, 7),
        Block.box(7, -10, 9, 9, -9, 9.5),
        Block.box(6, -9, 6.75, 10, 1, 9.25),
        Block.box(6.75, -9, 6, 9.25, 1, 6.75),
        Block.box(6.75, -9, 9.25, 9.25, 1, 10),
        Block.box(6.5, 1, 7, 9.5, 2, 9),
        Block.box(7, 1, 6.5, 9, 2, 7),
        Block.box(7, 1, 9, 9, 2, 9.5),
        Block.box(7.25, 2, 7.5, 8.75, 2.5, 8.5),
        Block.box(7.5, 2, 7.25, 8.5, 2.5, 7.5),
        Block.box(7.5, 2, 8.5, 8.5, 2.5, 8.75),
        Block.box(6.5, 2, 6.5, 9.5, 6, 9.5)
    ).optimize();

    public static final VoxelShape PANCETTA = Shapes.or(
        Block.box(4.5, -7, 7, 11.5, -6, 9.5),
        Block.box(5, -7, 6.5, 11, -6, 7),
        Block.box(5, -7, 9.5, 11, -6, 10),
        Block.box(4, -6, 6.5, 12, 1.5, 9.5),
        Block.box(4.5, -6, 6, 11.5, 1.5, 6.5),
        Block.box(4.5, -6, 9.5, 11.5, 1.5, 10),
        Block.box(4.5, 1.5, 7, 11.5, 2.5, 9),
        Block.box(5, 1.5, 6.5, 11, 2.5, 7),
        Block.box(5, 1.5, 9, 11, 2.5, 9.5),
        Block.box(6.5, 2, 6.5, 9.5, 6, 9.5)
    ).optimize();

    public static final VoxelShape GUANCIALE = Shapes.or(
        Block.box(7, -8, 7.5, 9, -7, 10),
        Block.box(7.25, -8, 7.25, 8.75, -7, 7.5),
        Block.box(7.25, -8, 10, 8.75, -7, 10.25),
        Block.box(6.5, -7, 7.25, 10, -5.5, 9.75),
        Block.box(7, -7, 6.75, 9.5, -5.5, 7.25),
        Block.box(7, -7, 9.75, 9.5, -5.5, 10.25),
        Block.box(5.5, -5.5, 6.75, 11, -3, 9.75),
        Block.box(6, -5.5, 6.25, 10.5, -3, 6.75),
        Block.box(6, -5.5, 9.75, 10.5, -3, 10.25),
        Block.box(4.5, -3, 6.5, 11.5, 0, 9.5),
        Block.box(5.25, -3, 5.75, 10.75, 0, 6.5),
        Block.box(5.25, -3, 9.5, 10.75, 0, 10.25),
        Block.box(5, 0, 6.5, 11, 1.5, 9.75),
        Block.box(5.5, 0, 6, 10.5, 1.5, 6.5),
        Block.box(5.5, 0, 9.75, 10.5, 1.5, 10.25),
        Block.box(6.25, 1.5, 7.25, 9.75, 2.5, 8.75),
        Block.box(6.75, 1.5, 6.75, 9.25, 2.5, 7.25),
        Block.box(6.75, 1.5, 8.75, 9.25, 2.5, 9.25),
        Block.box(6.5, 2, 6.5, 9.5, 6, 9.5)
    ).optimize();

    public static final VoxelShape BRESAOLA = Shapes.or(
        Block.box(6.75, -10.5, 7.25, 9.25, -10, 8.75),
        Block.box(7.25, -10.5, 6.75, 8.75, -10, 7.25),
        Block.box(7.25, -10.5, 8.75, 8.75, -10, 9.25),
        Block.box(6, -10, 6.75, 10, -9, 9.25),
        Block.box(6.5, -10, 6.25, 9.5, -9, 6.75),
        Block.box(6.5, -10, 9.25, 9.5, -9, 9.75),
        Block.box(5.25, -9, 6.5, 10.75, 0, 9.5),
        Block.box(6, -9, 5.75, 10, 0, 6.5),
        Block.box(6, -9, 9.5, 10, 0, 10.25),
        Block.box(6, 0, 6.75, 10, 1.5, 9.25),
        Block.box(6.5, 0, 6.25, 9.5, 1.5, 6.75),
        Block.box(6.5, 0, 9.25, 9.5, 1.5, 9.75),
        Block.box(7, 1.5, 7.25, 9, 2.5, 8.75),
        Block.box(7.25, 1.5, 7, 8.75, 2.5, 7.25),
        Block.box(7.25, 1.5, 8.75, 8.75, 2.5, 9),
        Block.box(6.5, 2, 6.5, 9.5, 6, 9.5)
    ).optimize();

    public static final VoxelShape COPPA = Shapes.or(
        Block.box(6.25, -8, 6.75, 9.75, -7, 10.5),
        Block.box(6.75, -8, 6.25, 9.25, -7, 6.75),
        Block.box(6.75, -8, 10.5, 9.25, -7, 11),
        Block.box(5, -7, 6.25, 11, -5.5, 10.25),
        Block.box(5.75, -7, 5.5, 10.25, -5.5, 6.25),
        Block.box(5.75, -7, 10.25, 10.25, -5.5, 11),
        Block.box(4.25, -5.5, 6, 11.75, 0, 10),
        Block.box(5.25, -5.5, 5, 10.75, 0, 6),
        Block.box(5.25, -5.5, 10, 10.75, 0, 11),
        Block.box(5.25, 0, 6.5, 10.75, 1.5, 9.5),
        Block.box(6, 0, 5.75, 10, 1.5, 6.5),
        Block.box(6, 0, 9.5, 10, 1.5, 10.25),
        Block.box(6.5, 1.5, 7.25, 9.5, 2.5, 8.75),
        Block.box(7, 1.5, 6.75, 9, 2.5, 7.25),
        Block.box(7, 1.5, 8.75, 9, 2.5, 9.25),
        Block.box(6.5, 2, 6.5, 9.5, 6, 9.5)
    ).optimize();

    public static final VoxelShape SPECK = Shapes.or(
        Block.box(4.5, -8, 6.5, 11.5, -7, 10),
        Block.box(5, -8, 6, 11, -7, 6.5),
        Block.box(5, -8, 10, 11, -7, 10.5),
        Block.box(4, -7, 6, 12, 1, 10),
        Block.box(4.5, -7, 5.5, 11.5, 1, 6),
        Block.box(4.5, -7, 10, 11.5, 1, 10.5),
        Block.box(4.5, 1, 6.5, 11.5, 2, 9.5),
        Block.box(5, 1, 6, 11, 2, 6.5),
        Block.box(5, 1, 9.5, 11, 2, 10),
        Block.box(6.5, 1.5, 6.5, 9.5, 6, 9.5)
    ).optimize();

}
