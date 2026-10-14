package dev.italiansdelight.common.block.entity;

import dev.italiansdelight.common.pizza.PizzaComposition;
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

    public PizzaDoughBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PIZZA_DOUGH, pos, state);
    }

    /** Immutable snapshot. Individual original ItemStacks are defensive copies. */
    public PizzaComposition composition() {
        return composition;
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
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store("PizzaComposition", PizzaComposition.CODEC, composition);
    }
}
