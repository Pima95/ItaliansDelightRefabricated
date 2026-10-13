package dev.italiansdelight.common.block.entity;

import dev.italiansdelight.common.pizza.PizzaComposition;
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
 * <p>No ticker, no hopper interface, and no new interaction yet. Rendering
 * still comes from the existing blockstate models.
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
        // Client synchronization and rendering will be implemented when
        // topping interactions are wired up.
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
