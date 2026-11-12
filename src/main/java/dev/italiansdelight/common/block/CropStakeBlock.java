package dev.italiansdelight.common.block;

import dev.italiansdelight.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * One physical stake shared by chili farming, Farmer's Delight tomato seeds
 * and horizontal rope anchoring. Extending IronBarsBlock makes FD ropes
 * connect to its sides without registering a second rope implementation.
 */
public final class CropStakeBlock extends IronBarsBlock {
    private static final VoxelShape POST = Block.box(6, 0, 6, 10, 16, 10);

    public CropStakeBlock(Properties properties) { super(properties); }

    @Override public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos,
            CollisionContext context) { return POST; }
    @Override public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos,
            CollisionContext context) { return POST; }

    @Override protected InteractionResult useItemOn(ItemStack held, BlockState state,
            Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        boolean chili = held.is(ModBlocks.CHILI_PEPPER_SEEDS);
        boolean tomatoes = held.is(vectorwing.farmersdelight.common.registry.ModItems.TOMATO_SEEDS.get());
        if (!chili && !tomatoes) return InteractionResult.PASS;
        if (player.isSpectator()) return InteractionResult.PASS;
        if (!level.getBlockState(pos.below()).is(Blocks.FARMLAND)) return InteractionResult.FAIL;
        if (level instanceof ServerLevel server) {
            BlockState plant = chili ? ModBlocks.CHILI_CROP.defaultBlockState()
                : ModBlocks.STAKED_TOMATOES.defaultBlockState();
            if (server.setBlock(pos, plant, Block.UPDATE_ALL)) {
                if (!player.getAbilities().instabuild) held.shrink(1);
                server.playSound(null, pos, SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 1, 1);
            }
        }
        return InteractionResult.SUCCESS;
    }
}
