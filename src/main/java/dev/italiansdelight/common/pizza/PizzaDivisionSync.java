package dev.italiansdelight.common.pizza;

import dev.italiansdelight.common.block.entity.PizzaDoughBlockEntity;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

/** Reconcile the division shadow once after a pizza block entity is loaded. */
public final class PizzaDivisionSync {
    private static final Map<ServerLevel, Set<BlockPos>> PENDING = new WeakHashMap<>();
    private PizzaDivisionSync() {}

    public static void register() {
        NeoForge.EVENT_BUS.addListener(PizzaDivisionSync::afterTick);
    }

    public static void queue(ServerLevel level, BlockPos pos) {
        PENDING.computeIfAbsent(level, unused -> new HashSet<>()).add(pos.immutable());
    }

    private static void afterTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        Set<BlockPos> pending = PENDING.remove(level);
        if (pending == null) return;
        for (BlockPos pos : pending) {
            if (level.hasChunkAt(pos) && level.getBlockEntity(pos) instanceof PizzaDoughBlockEntity pizza)
                pizza.syncDivisionBlockState();
        }
    }
}
