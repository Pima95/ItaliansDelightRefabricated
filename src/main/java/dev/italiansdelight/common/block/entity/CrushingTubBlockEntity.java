package dev.italiansdelight.common.block.entity;

import dev.italiansdelight.common.block.CrushingTubRecipes;
import dev.italiansdelight.common.block.CrushingTubBlock;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import dev.italiansdelight.common.registry.ModBlockEntities;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Rustic Crushing Tub: one ingredient stack (64), one fluid (8000 mB),
 * one recipe processed per landing. All mutations are server-side.
 */
public final class CrushingTubBlockEntity extends BlockEntity {
    public static final int CAPACITY = 8_000;
    public static final int MAX_ITEMS = 64;
    private ItemStack input = ItemStack.EMPTY;
    private String fluidId = "";
    private int amount;

    // Transient per-tub, per-entity landing arms; never written to save data.
    // A jump must first lift the feet above the 9/16-high rim (10/16 min).
    // Sampling this server-side avoids the vanilla fallOn 0.2-block offset
    // that mistakes our real 1/16-high inner floor for the block below.
    private final Map<UUID, Long> airborneInside = new HashMap<>();
    private static final double RIM_CLEARANCE_Y = 10.0 / 16.0;
    private static final double BASIN_FLOOR_Y = 1.0 / 16.0;

    public CrushingTubBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CRUSHING_TUB, pos, state);
    }

    public ItemStack getInput() { return input.copy(); }
    public int getAmount() { return amount; }
    public String getFluidId() { return fluidId; }
    public boolean isEmpty() { return amount == 0; }

    /** Insert as many ingredients as will fit, without overwriting a different input. */
    public int insert(ItemStack held) {
        if (held.isEmpty() || CrushingTubRecipes.find(held) == null) return 0;
        if (!input.isEmpty() && !ItemStack.isSameItemSameComponents(input, held)) return 0;
        int n = Math.min(held.getCount(), MAX_ITEMS - input.getCount());
        if (n <= 0) return 0;
        if (input.isEmpty()) { input = held.copy(); input.setCount(n); }
        else input.grow(n);
        changedAndSync();
        return n;
    }

    public ItemStack takeInputs() {
        if (input.isEmpty()) return ItemStack.EMPTY;
        ItemStack result = input;
        input = ItemStack.EMPTY;
        changedAndSync();
        return result;
    }

    /** Atomically process exactly ONE item, respecting fluid identity and capacity. */
    public ItemStack stomp() {
        CrushingTubRecipes.Recipe recipe = CrushingTubRecipes.find(input);
        if (recipe == null || recipe.amount() > CAPACITY - amount ||
            (amount > 0 && !fluidId.equals(recipe.fluidId()))) return null;
        if (amount == 0) fluidId = recipe.fluidId();
        amount += recipe.amount();
        input.shrink(1);
        if (input.isEmpty()) input = ItemStack.EMPTY;
        changedAndSync();
        return recipe.byproduct().copy();
    }


    /**
     * Only active tubs scan entities. A real airborne -> grounded movement
     * within the center produces one stomp; walking into the hollow or
     * merely standing in it cannot process items.
     */
    public static void serverTick(ServerLevel level, BlockPos pos, BlockState state,
                                  CrushingTubBlockEntity tub) {
        if (state.getValue(CrushingTubBlock.WATERLOGGED) || tub.input.isEmpty()) {
            tub.airborneInside.clear();
            return;
        }
        long now = level.getGameTime();
        if (now % 20 == 0)
            tub.airborneInside.entrySet().removeIf(e -> now - e.getValue() > 40);

        // Track airborne players/mobs even at their jump apex, but limit X/Z
        // to this basin. Adjacent blocks and other tubs never share state.
        AABB area = new AABB(pos.getX() + 1.0 / 16.0, pos.getY(),
                pos.getZ() + 1.0 / 16.0, pos.getX() + 15.0 / 16.0,
                pos.getY() + 2.25, pos.getZ() + 15.0 / 16.0);
        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, area)) {
            double x = entity.getX() - pos.getX();
            double z = entity.getZ() - pos.getZ();
            if (x <= 2.0 / 16.0 || x >= 14.0 / 16.0 ||
                z <= 2.0 / 16.0 || z >= 14.0 / 16.0)
                continue;
            double feetY = entity.getY() - pos.getY();
            if (!entity.onGround() && feetY > RIM_CLEARANCE_Y && feetY < 2.25) {
                tub.airborneInside.put(entity.getUUID(), now);
            } else if (entity.onGround() &&
                       Math.abs(feetY - BASIN_FLOOR_Y) <= 1.0 / 16.0) {
                Long armedAt = tub.airborneInside.remove(entity.getUUID());
                if (armedAt != null && now - armedAt <= 20) {
                    CrushingTubBlock.finishStomp(level, pos, tub);
                    if (tub.input.isEmpty()) break;
                }
            }
        }
    }

    public boolean canExtract(int volume) { return volume > 0 && volume <= amount; }

    public boolean extract(int volume) {
        if (!canExtract(volume)) return false;
        amount -= volume;
        if (amount == 0) fluidId = "";
        changedAndSync();
        return true;
    }

    public boolean discardFluid() {
        if (amount == 0) return false;
        amount = 0;
        fluidId = "";
        changedAndSync();
        return true;
    }

    private void changedAndSync() {
        setChanged();
        if (level != null && !level.isClientSide()) {
            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_CLIENTS);
        }
    }

    @Override protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (!input.isEmpty()) output.store("Input", ItemStack.CODEC, input);
        output.putString("FluidId", fluidId);
        output.putInt("Amount", amount);
    }
    @Override protected void loadAdditional(ValueInput inputData) {
        super.loadAdditional(inputData);
        input = inputData.read("Input", ItemStack.CODEC).orElse(ItemStack.EMPTY);
        if (input.getCount() > MAX_ITEMS) input.setCount(MAX_ITEMS);
        amount = Math.clamp(inputData.getIntOr("Amount", 0), 0, CAPACITY);
        fluidId = inputData.getStringOr("FluidId", "");
        if (amount == 0 || fluidId.isBlank()) { amount = 0; fluidId = ""; }
    }

    @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }
    @Override public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
