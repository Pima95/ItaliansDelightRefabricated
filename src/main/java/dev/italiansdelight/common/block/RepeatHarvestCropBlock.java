package dev.italiansdelight.common.block;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Minimal reusable crop logic for Rustic-inspired grapes and chili peppers.
 * Vanilla's 0..7 growth and bonemeal rules remain intact.
 * Mature produce can be collected without destroying the farmland plant.
 */
public final class RepeatHarvestCropBlock extends CropBlock {
    private final Supplier<Item> seeds;
    private final Supplier<Item> produce;
    private final Supplier<Item> rareProduce;

    public RepeatHarvestCropBlock(Properties properties,
                                  Supplier<Item> seeds, Supplier<Item> produce) {
        super(properties);
        this.seeds = seeds;
        this.produce = produce;
        this.rareProduce = null;
    }

    public RepeatHarvestCropBlock(Properties properties,
                                  Supplier<Item> seeds, Supplier<Item> produce,
                                  Supplier<Item> rareProduce) {
        super(properties);
        this.seeds = seeds;
        this.produce = produce;
        this.rareProduce = rareProduce;
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return seeds.get();
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level,
            BlockPos pos, Player player, BlockHitResult hit) {
        if (state.getValue(AGE) != MAX_AGE || player.isSpectator())
            return InteractionResult.PASS;
        if (level instanceof ServerLevel server) {
            // Verify the current block and reset BEFORE spawning a drop.
            BlockState current = server.getBlockState(pos);
            if (!current.is(this) || current.getValue(AGE) != MAX_AGE)
                return InteractionResult.PASS;
            server.setBlock(pos, current.setValue(AGE, 3), Block.UPDATE_CLIENTS);
            // Rustic's ghost pepper appears in one out of 42 ripe harvests.
            // The rare fruit replaces, rather than duplicates, a normal harvest.
            boolean rare = rareProduce != null && server.getRandom().nextInt(42) == 0;
            Item picked = rare ? rareProduce.get() : produce.get();
            Block.popResource(server, pos, new ItemStack(picked,
                rare ? 1 : 1 + server.getRandom().nextInt(3)));
            server.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES,
                SoundSource.BLOCKS, 0.8f, 0.95f);
        }
        return InteractionResult.SUCCESS;
    }
}
