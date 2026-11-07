package dev.italiansdelight.common.block;

import com.mojang.serialization.MapCodec;
import dev.italiansdelight.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Golden oil in a physical vanilla-style cauldron; four levels of 250 mB each. */
public final class OliveOilCauldronBlock extends Block {
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 1, 4);
    private static final VoxelShape SHAPE =
        Blocks.CAULDRON.defaultBlockState().getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);

    public OliveOilCauldronBlock(Properties props) {
        super(props);
        registerDefaultState(stateDefinition.any().setValue(LEVEL, 4));
    }
    @Override protected MapCodec<? extends Block> codec() {
        return simpleCodec(OliveOilCauldronBlock::new);
    }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LEVEL);
    }
    @Override public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos,
                                         CollisionContext ctx) { return SHAPE; }
    @Override public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos,
                                                  CollisionContext ctx) { return SHAPE; }

    // Backward compatibility with filled cauldrons saved by earlier mod builds.
    @Override protected ItemStack getCloneItemStack(
            LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        return new ItemStack(Items.CAULDRON);
    }

    @Override protected InteractionResult useItemOn(ItemStack held, BlockState state, Level level,
            BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (player.isSpectator()) return InteractionResult.PASS;
        boolean inBottle = held.is(ModItems.OLIVE_OIL_BOTTLE);
        boolean inBucket = held.is(ModItems.OLIVE_OIL_BUCKET);
        boolean outBottle = held.is(Items.GLASS_BOTTLE);
        boolean outBucket = held.is(Items.BUCKET);
        if (!inBottle && !inBucket && !outBottle && !outBucket) return InteractionResult.PASS;
        int current = state.getValue(LEVEL);
        boolean deposit = inBottle || inBucket;
        int delta = inBucket || outBucket ? 4 : 1;
        if (deposit ? current + delta > 4 : current < delta) return InteractionResult.SUCCESS;
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        int next = current + (deposit ? delta : -delta);
        BlockState changed = next == 0 ? Blocks.CAULDRON.defaultBlockState() : state.setValue(LEVEL, next);
        if (!level.setBlockAndUpdate(pos, changed)) return InteractionResult.FAIL;

        ItemStack result = deposit
            ? new ItemStack(delta == 4 ? Items.BUCKET : Items.GLASS_BOTTLE)
            : new ItemStack(delta == 4 ? ModItems.OLIVE_OIL_BUCKET : ModItems.OLIVE_OIL_BOTTLE);
        player.setItemInHand(hand, ItemUtils.createFilledResult(held, player, result));
        level.playSound(null, pos, deposit
            ? (delta == 4 ? SoundEvents.BUCKET_EMPTY : SoundEvents.BOTTLE_EMPTY)
            : (delta == 4 ? SoundEvents.BUCKET_FILL : SoundEvents.BOTTLE_FILL),
            SoundSource.BLOCKS, 1f, 1f);
        return InteractionResult.SUCCESS;
    }
}
