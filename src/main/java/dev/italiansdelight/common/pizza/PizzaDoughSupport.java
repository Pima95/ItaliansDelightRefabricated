package dev.italiansdelight.common.pizza;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;

/**
 * Accepted surfaces for placing pizza dough.
 *
 * Only vanilla full-collision blocks are allowed by default.
 * Unusual surfaces require explicit approval: a full top support face alone
 * is NOT enough (chests, short blocks, hollow blocks, etc.).
 */
public final class PizzaDoughSupport {
    private PizzaDoughSupport() {
    }

    public static boolean canSupport(BlockState support, BlockGetter level, BlockPos pos) {
        // Slabs: bottom is too low; top and double reach normal block height.
        if (support.getBlock() instanceof SlabBlock) {
            SlabType type = support.getValue(SlabBlock.TYPE);
            return type == SlabType.TOP || type == SlabType.DOUBLE;
        }

        // Upside-down stairs have a flat, complete upper surface.
        if (support.getBlock() instanceof StairBlock) {
            return support.getValue(StairBlock.HALF) == Half.TOP;
        }

        // Only closed trapdoors attached in the upper half.
        if (support.getBlock() instanceof TrapDoorBlock) {
            return !support.getValue(TrapDoorBlock.OPEN)
                    && support.getValue(TrapDoorBlock.HALF) == Half.TOP;
        }

        // Anvils are the only explicitly approved special non-cubic support.
        if (support.getBlock() instanceof AnvilBlock) {
            return true;
        }

        // Explicit rejections; subclasses cover all vanilla color/level variants.
        if (support.is(Blocks.SOUL_SAND)
                || support.is(Blocks.DIRT_PATH)
                || support.is(Blocks.FARMLAND)
                || support.is(Blocks.SNOW)
                || support.is(Blocks.ENCHANTING_TABLE)
                || support.is(Blocks.COMPOSTER)
                || support.is(Blocks.CHEST)
                || support.is(Blocks.TRAPPED_CHEST)
                || support.is(Blocks.ENDER_CHEST)
                || support.getBlock() instanceof CarpetBlock
                || support.getBlock() instanceof BedBlock
                || support.getBlock() instanceof AbstractCauldronBlock) {
            return false;
        }

        // Conservative default: solid, full-height, full-width collision cube.
        // Do not implicitly approve unreviewed partial/hollow surfaces.
        return support.isCollisionShapeFullBlock(level, pos);
    }

}
