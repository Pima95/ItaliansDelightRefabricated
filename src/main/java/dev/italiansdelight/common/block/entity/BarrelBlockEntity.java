package dev.italiansdelight.common.block.entity;

import dev.italiansdelight.common.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/** Persistent single-fluid storage; capacity matches Rustic's 16,000 mB barrel. */
public final class BarrelBlockEntity extends BlockEntity {
    public static final int CAPACITY = 16_000;
    public static final int BUCKET_AMOUNT = 1_000;
    public static final int BOTTLE_AMOUNT = 250;
    private int oliveOilAmount;

    public BarrelBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BARREL, pos, state);
    }

    public int getOliveOilAmount() {
        return oliveOilAmount;
    }

    public boolean insertOliveOil(int amount) {
        if (amount <= 0 || amount > CAPACITY - oliveOilAmount) return false;
        oliveOilAmount += amount;
        setChanged();
        return true;
    }

    public boolean extractOliveOil(int amount) {
        if (amount <= 0 || oliveOilAmount < amount) return false;
        oliveOilAmount -= amount;
        setChanged();
        return true;
    }

    /** Keep contents on the dropped barrel item, not just across world reloads. */
    public void preserveContents(ItemStack stack, HolderLookup.Provider registries) {
        if (oliveOilAmount <= 0) return;
        TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, registries);
        output.putInt("OliveOilAmount", oliveOilAmount);
        BlockItem.setBlockEntityData(stack, getType(), output);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("OliveOilAmount", oliveOilAmount);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        oliveOilAmount = Math.max(0, Math.min(CAPACITY, input.getIntOr("OliveOilAmount", 0)));
    }
}
