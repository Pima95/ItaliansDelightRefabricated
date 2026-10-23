package dev.italiansdelight.common.pizza;

import dev.italiansdelight.common.block.entity.PizzaDoughBlockEntity;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/**
 * One-shot migration/synchronization for pizza worlds saved before the
 * division/facing BlockState shadow existed.
 *
 * <p>Fabric warns that BE data may not yet be loaded during BLOCK_ENTITY_LOAD:
 * queue only the position and read the BE on the next end-of-level tick.
 * No per-pizza ticker and no ongoing work after the queue has drained.
 */
public final class PizzaDivisionSync {
    private static final Map<ServerLevel, Set<BlockPos>> PENDING = new WeakHashMap<>();

    private PizzaDivisionSync() {
    }

    public static void register() {
        ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.register((entity, level) -> {
            if (entity instanceof PizzaDoughBlockEntity) {
                PENDING.computeIfAbsent(level, unused -> new HashSet<>())
                        .add(entity.getBlockPos().immutable());
            }
        });

        ServerTickEvents.END_LEVEL_TICK.register(level -> {
            Set<BlockPos> pending = PENDING.remove(level);
            if (pending == null) {
                return;
            }
            for (BlockPos pos : pending) {
                if (level.getBlockEntity(pos) instanceof PizzaDoughBlockEntity pizza) {
                    pizza.syncDivisionBlockState();
                }
            }
        });
    }
}
