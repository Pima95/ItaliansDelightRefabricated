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

/** Water-like in-world movement (via minecraft:water fluid tag), but NEVER creates new sources. */
public abstract class OliveOilFluid extends FlowingFluid {
    @Override public Fluid getFlowing() { return ModFluids.OLIVE_OIL_FLOWING; }
    @Override public Fluid getSource() { return ModFluids.OLIVE_OIL_STILL; }
    @Override public boolean isSame(Fluid fluid) {
        return fluid == ModFluids.OLIVE_OIL_STILL || fluid == ModFluids.OLIVE_OIL_FLOWING;
    }
    @Override public Item getBucket() { return ModItems.OLIVE_OIL_BUCKET; }
    @Override protected BlockState createLegacyBlock(FluidState state) {
        return ModBlocks.OLIVE_OIL_FLUID_BLOCK.defaultBlockState()
            .setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
    }
    // Do not consult waterSourceConversion: unlike vanilla water, two sources never generate a third.
    @Override protected boolean canConvertToSource(ServerLevel world) { return false; }
    @Override protected void beforeDestroyingBlock(LevelAccessor world, BlockPos pos, BlockState state) {
        Block.dropResources(state, world, pos, world.getBlockEntity(pos));
    }
    @Override protected int getSlopeFindDistance(LevelReader world) { return 4; }
    @Override public int getDropOff(LevelReader world) { return 1; }
    @Override public int getTickDelay(LevelReader world) { return 5; }
    @Override public boolean canBeReplacedWith(FluidState state, BlockGetter world, BlockPos pos,
                                                Fluid other, Direction direction) {
        return direction == Direction.DOWN && !isSame(other);
    }
    @Override protected float getExplosionResistance() { return 100.0F; }
    @Override public Optional<SoundEvent> getPickupSound() {
        return Optional.of(SoundEvents.BUCKET_FILL);
    }

    public static final class Flowing extends OliveOilFluid {
        @Override protected void createFluidStateDefinition(
                StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }
        @Override public int getAmount(FluidState state) { return state.getValue(LEVEL); }
        @Override public boolean isSource(FluidState state) { return false; }
    }

    public static final class Source extends OliveOilFluid {
        @Override public int getAmount(FluidState state) { return 8; }
        @Override public boolean isSource(FluidState state) { return true; }
    }
}
