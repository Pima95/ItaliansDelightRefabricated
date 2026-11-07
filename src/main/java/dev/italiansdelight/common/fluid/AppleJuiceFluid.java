package dev.italiansdelight.common.fluid;

import java.util.Optional;
import dev.italiansdelight.common.registry.ModBlocks;
import dev.italiansdelight.common.registry.ModFluids;
import dev.italiansdelight.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.FlowingFluid;

/** Real apple juice source/flowing fluid; only manual buckets produce sources. */
public abstract class AppleJuiceFluid extends FlowingFluid {
    @Override public Fluid getFlowing() { return ModFluids.APPLE_JUICE_FLOWING; }
    @Override public Fluid getSource() { return ModFluids.APPLE_JUICE_STILL; }
    @Override public boolean isSame(Fluid fluid) {
        return fluid == ModFluids.APPLE_JUICE_STILL || fluid == ModFluids.APPLE_JUICE_FLOWING;
    }
    @Override public Item getBucket() { return ModItems.APPLE_JUICE_BUCKET; }
    @Override protected BlockState createLegacyBlock(FluidState state) {
        return ModBlocks.APPLE_JUICE_FLUID_BLOCK.defaultBlockState()
            .setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
    }
    @Override protected boolean canConvertToSource(ServerLevel world) { return false; }
    @Override protected void beforeDestroyingBlock(LevelAccessor world, BlockPos pos, BlockState state) {
        Block.dropResources(state, world, pos, world.getBlockEntity(pos));
    }
    @Override protected int getSlopeFindDistance(LevelReader world) { return 2; }
    @Override public int getDropOff(LevelReader world) { return 2; }
    @Override public int getTickDelay(LevelReader world) { return 20; }
    @Override public boolean canBeReplacedWith(FluidState state, BlockGetter world, BlockPos pos,
                                                Fluid other, Direction direction) {
        return direction == Direction.DOWN && !isSame(other);
    }
    @Override protected float getExplosionResistance() { return 100.0F; }
    @Override public Optional<SoundEvent> getPickupSound() {
        return Optional.of(SoundEvents.BUCKET_FILL);
    }

    public static final class Flowing extends AppleJuiceFluid {
        @Override protected void createFluidStateDefinition(
                StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }
        @Override public int getAmount(FluidState state) { return state.getValue(LEVEL); }
        @Override public boolean isSource(FluidState state) { return false; }
    }

    public static final class Source extends AppleJuiceFluid {
        @Override public int getAmount(FluidState state) { return 8; }
        @Override public boolean isSource(FluidState state) { return true; }
    }
}
