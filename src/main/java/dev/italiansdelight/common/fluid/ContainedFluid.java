package dev.italiansdelight.common.fluid;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Real registered fluid for pipes, JEI and FluidVariant storage, but deliberately
 * not a placeable Fluid block. Compatible with Minecraft's vanilla Fluid API;
 * no Fabric dependency is needed here.
 *
 * Only olive oil and apple juice have flowing world-fluid implementations.
 */
public final class ContainedFluid extends Fluid {
    private final Supplier<Item> container;

    public ContainedFluid(Supplier<Item> container) {
        this.container = container;
    }

    @Override public Item getBucket() { return container.get(); }
    @Override protected boolean canBeReplacedWith(FluidState state, BlockGetter level,
            BlockPos pos, Fluid other, Direction direction) { return false; }
    @Override protected Vec3 getFlow(BlockGetter level, BlockPos pos, FluidState state) {
        return Vec3.ZERO;
    }
    @Override public int getTickDelay(LevelReader level) { return 0; }
    @Override protected float getExplosionResistance() { return 100.0f; }
    @Override public float getHeight(FluidState state, BlockGetter level, BlockPos pos) {
        return 0.0f;
    }
    @Override public float getOwnHeight(FluidState state) { return 0.0f; }
    @Override protected BlockState createLegacyBlock(FluidState state) {
        // No placement block, spread, source generation or world-fluid physics.
        return Blocks.AIR.defaultBlockState();
    }
    @Override public boolean isSource(FluidState state) { return true; }
    @Override public int getAmount(FluidState state) { return 8; }
    @Override public VoxelShape getShape(FluidState state, BlockGetter level, BlockPos pos) {
        return Shapes.empty();
    }
}
