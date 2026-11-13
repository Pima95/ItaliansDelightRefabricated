package dev.italiansdelight.common.block;

import dev.italiansdelight.common.registry.ModBlocks;
import dev.italiansdelight.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import vectorwing.farmersdelight.common.block.TomatoBlock;

/**
 * A pepper crop with Farmer's Delight tomato growth, bonemeal, survival and
 * picking. Only the fruit item, optional rope block and preserved 0..7 age
 * differ from tomatoes. There is no decorative stake integration.
 *
 * Keep AGE_7 and the old registry IDs: pre-1.2.0 worlds can still load their
 * existing peppers and stages rather than losing age 4..7 on migration.
 */
public class ChiliCropBlock extends TomatoBlock {
    public static final IntegerProperty CHILI_AGE = BlockStateProperties.AGE_7;

    public ChiliCropBlock(Properties properties) {
        super(properties);
    }

    /** For a rope segment: FDR HangingTomatoBlock's dummy parent constructor. */
    protected ChiliCropBlock(Properties properties, boolean hanging) {
        super(properties, hanging);
    }

    @Override
    public IntegerProperty getAgeProperty() {
        return CHILI_AGE;
    }

    @Override
    public int getMaxAge() {
        return 7;
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return ModBlocks.CHILI_PEPPER_SEEDS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        // Same as FDR TomatoBlock's root, with preserved eight-age property.
        builder.add(CHILI_AGE, ROPELOGGED);
    }

    /** Rich soil farmland is equivalent to vanilla farmland for root survival. */
    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (StakeSoilTilling.isFarmland(level.getBlockState(pos.below()))) {
            return hasGoodCropConditions(level, pos);
        }
        return super.canSurvive(state, level, pos);
    }

    @Override
    public BlockState getClimbingState(BlockState above) {
        // FDR's canClimbBlock respects its configured rope tag. Keep the
        // original rope object and replace only the climbing CROP.
        return canClimbBlock(above)
            ? ModBlocks.CHILI_ON_ROPE.defaultBlockState()
            : null;
    }

    @Override
    public void climbRopeAbove(ServerLevel level, BlockPos pos) {
        if (!level.hasChunksAt(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) return;

        // FDR's default counter uses 'is(this)' and cannot count a root and
        // a separate hanging subclass together. Count both concrete IDs.
        int height = 1;
        BlockPos below = pos.below();
        while (height < 3) {
            BlockState next = level.getBlockState(below);
            if (!next.is(ModBlocks.CHILI_CROP) && !next.is(ModBlocks.CHILI_ON_ROPE)) break;
            height++;
            below = below.below();
        }
        if (height >= 3) return;
        BlockState next = getClimbingState(level.getBlockState(pos.above()));
        if (next != null) level.setBlockAndUpdate(pos.above(), next);
    }

    /** Let bone meal travel into chili rope segments as it does for tomatoes. */
    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos,
            BlockState state, BonemealSource source) {
        if (super.isValidBonemealTarget(level, pos, state, source)) return true;
        if (!isMaxAge(state)) return true;
        for (int height = 1; height < 3; height++) {
            BlockState above = level.getBlockState(pos.above(height));
            if (canClimbBlock(above)) return true;
            if (above.is(ModBlocks.CHILI_ON_ROPE)) {
                if (!isMaxAge(above)) return true;
            } else return false;
        }
        return false;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random,
            BlockPos pos, BlockState state, BonemealSource source) {
        if (!isMaxAge(state)) {
            super.performBonemeal(level, random, pos, state, source);
            return;
        }
        BlockState above = level.getBlockState(pos.above());
        if (canClimbBlock(above)) {
            climbRopeAbove(level, pos);
        } else if (above.is(ModBlocks.CHILI_ON_ROPE)) {
            ((ChiliCropBlock) above.getBlock()).performBonemeal(
                level, random, pos.above(), above, source);
        }
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level,
            BlockPos pos, Player player, BlockHitResult hit) {
        if (!isMaxAge(state) || player.isSpectator())
            return super.useWithoutItem(state, level, pos, player, hit);
        if (level instanceof ServerLevel server) {
            BlockState current = server.getBlockState(pos);
            if (!current.is(this) || !isMaxAge(current)) return InteractionResult.PASS;
            boolean rare = server.getRandom().nextInt(42) == 0;
            int amount = rare ? 1 : 1 + server.getRandom().nextInt(2);
            Block.popResource(server, pos,
                new ItemStack(rare ? ModItems.GHOST_PEPPER : ModItems.CHILI_PEPPER, amount));
            server.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES,
                SoundSource.BLOCKS, 0.8F, 0.9F + server.getRandom().nextFloat() * 0.2F);
            server.setBlock(pos, current.setValue(CHILI_AGE, 0), Block.UPDATE_CLIENTS);
        }
        return InteractionResult.SUCCESS;
    }
}
