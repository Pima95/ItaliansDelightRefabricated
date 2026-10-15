package dev.italiansdelight.common.block.entity;

import dev.italiansdelight.common.pizza.PizzaComposition;
import dev.italiansdelight.common.pizza.PizzaBaking;
import dev.italiansdelight.common.block.PizzaDoughBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import dev.italiansdelight.common.registry.ModBlockEntities;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;

/**
 * Persistent pizza state in the world. Keeping the data here (rather than
 * one BlockState property per ingredient) supports arbitrary four-quarter
 * compositions without the combinatorial explosion of blockstate variants.
 *
 * <p>No ticker or hopper interface. The authoritative composition is saved
 * here. A compact division/facing shadow is synchronized as BlockState for
 * client-only visual hints; no ingredient data is duplicated in BlockState.
 */
public final class PizzaDoughBlockEntity extends BlockEntity {
    private PizzaComposition composition = PizzaComposition.empty();
    // A raw pizza may already be partly baked when placed back in the world.
    // Kept distinct from composition: cuts/toppings are independent of baking.
    private int bakeProgress;

    public PizzaDoughBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PIZZA_DOUGH, pos, state);
    }

    /** Immutable snapshot. Individual original ItemStacks are defensive copies. */
    public PizzaComposition composition() {
        return composition;
    }

    public int bakeProgress() {
        return bakeProgress;
    }

    /**
     * Restore oven progress when placing a partly baked RAW_PIZZA back on
     * the ground. Keep original progress when the block is later broken.
     * This is NOT a ticking oven: progress advances only in a cooking machine.
     */
    public void setBakeProgress(int value) {
        if (value < 0 || value > PizzaBaking.MAX_PROGRESS) {
            throw new IllegalArgumentException("Invalid placed pizza bake progress");
        }
        if (level != null && level.isClientSide()) {
            throw new IllegalStateException("Cannot change pizza progress on client");
        }
        if (this.bakeProgress == value) {
            return;
        }
        this.bakeProgress = value;
        setChanged();
        if (level != null) {
            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_CLIENTS);
        }
    }

    /**
     * Only the authoritative server should mutate real pizza state.
     * A change marks the chunk dirty so vanilla persists it at save/unload.
     */
    public void setComposition(PizzaComposition next) {
        Objects.requireNonNull(next, "next");
        if (level != null && level.isClientSide()) {
            throw new IllegalStateException("Cannot change pizza composition on client");
        }
        this.composition = next;
        setChanged();
        syncDivisionBlockState();
        // Dynamic topping models need the full BE data, not just the compact
        // cut/facing BlockState shadow. This packet is standard vanilla sync.
        if (level != null) {
            BlockState current = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, current, current, Block.UPDATE_CLIENTS);
        }
    }

    /**
     * Vanilla automatically broadcasts blockstate updates to nearby clients.
     * It is intentionally *not* a second storage for toppings or chronology.
     * Called after all successful cuts/joins and when a saved pizza is placed.
     */
    public void syncDivisionBlockState() {
        if (level == null || level.isClientSide()) {
            return;
        }
        BlockState current = level.getBlockState(worldPosition);
        if (!(current.getBlock() instanceof PizzaDoughBlock)) {
            return;
        }
        Direction direction = switch (composition.orientation()) {
            case "north" -> Direction.NORTH;
            case "south" -> Direction.SOUTH;
            case "east" -> Direction.EAST;
            case "west" -> Direction.WEST;
            default -> throw new IllegalStateException(
                    "Unsupported saved pizza orientation: " + composition.orientation());
        };
        BlockState updated = current
                .setValue(PizzaDoughBlock.DIVISION, composition.divisionState())
                .setValue(PizzaDoughBlock.DIVISION_FACING, direction);
        if (updated != current) {
            level.setBlock(worldPosition, updated, Block.UPDATE_ALL);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        composition = input.read("PizzaComposition", PizzaComposition.CODEC)
                .orElseGet(PizzaComposition::empty);
        // Old worlds have no BakeProgress field: default to zero.
        bakeProgress = Math.clamp(
                input.getIntOr("BakeProgress", 0), 0, PizzaBaking.MAX_PROGRESS);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store("PizzaComposition", PizzaComposition.CODEC, composition);
        if (bakeProgress > 0) {
            output.putInt("BakeProgress", bakeProgress);
        }
    }

    /**
     * Chunk watch/initial load: preserve the SAME codec as disk persistence,
     * including the precise remaining masks and the original ingredient
     * stacks. There is no separately mutable visual inventory.
     */
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    /** Current watchers receive updates after each successful interaction. */
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
