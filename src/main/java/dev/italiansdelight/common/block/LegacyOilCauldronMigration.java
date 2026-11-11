package dev.italiansdelight.common.block;

import dev.italiansdelight.common.block.entity.FluidCauldronBlockEntity;
import dev.italiansdelight.common.registry.ModBlocks;
import java.util.ArrayDeque;
import java.util.IdentityHashMap;
import java.util.Map;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Migrate existing 1.1.0 oil cauldrons without resetting their stored levels.
 *
 * Earlier builds used italiansdelight:olive_oil_cauldron, which rendered a
 * fixed top layer and lacks a block entity. The current generic cauldron has
 * dynamic, animated surfaces, so old placed blocks need an in-world upgrade.
 *
 * Scan only loaded chunks whose palette contains the legacy block. Defer the
 * actual change until END_LEVEL_TICK to avoid changing a chunk during load.
 * No forced loads, no world-wide scans, no overwriting vanilla cauldrons.
 */
public final class LegacyOilCauldronMigration {
    private static final Map<ServerLevel, ArrayDeque<BlockPos>> PENDING =
        new IdentityHashMap<>();

    private LegacyOilCauldronMigration() {}

    public static void register() {
        ServerChunkEvents.CHUNK_LOAD.register(LegacyOilCauldronMigration::onChunkLoad);
        ServerTickEvents.END_LEVEL_TICK.register(LegacyOilCauldronMigration::onLevelTick);
    }

    private static void onChunkLoad(ServerLevel level, LevelChunk chunk, boolean generated) {
        LevelChunkSection[] sections = chunk.getSections();
        ChunkPos chunkPos = chunk.getPos();
        for (int i = 0; i < sections.length; i++) {
            LevelChunkSection section = sections[i];
            if (!section.maybeHas(s -> s.is(ModBlocks.OLIVE_OIL_CAULDRON)))
                continue;

            int baseY = SectionPos.sectionToBlockCoord(chunk.getSectionYFromSectionIndex(i));
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    for (int x = 0; x < 16; x++) {
                        BlockState state = section.getBlockState(x, y, z);
                        if (!state.is(ModBlocks.OLIVE_OIL_CAULDRON)) continue;
                        PENDING.computeIfAbsent(level, ignored -> new ArrayDeque<>())
                            .addLast(new BlockPos(
                                chunkPos.getMinBlockX() + x,
                                baseY + y,
                                chunkPos.getMinBlockZ() + z));
                    }
                }
            }
        }
    }

    private static void onLevelTick(ServerLevel level) {
        ArrayDeque<BlockPos> queue = PENDING.remove(level);
        if (queue == null) return;
        while (!queue.isEmpty()) {
            BlockPos pos = queue.removeFirst();
            if (!level.hasChunkAt(pos)) continue;
            BlockState state = level.getBlockState(pos);
            if (!state.is(ModBlocks.OLIVE_OIL_CAULDRON)) continue;
            int levelCount = state.getValue(OliveOilCauldronBlock.LEVEL);
            BlockState replacement = ModBlocks.FLUID_CAULDRON.defaultBlockState()
                .setValue(FluidCauldronBlock.LEVEL, levelCount);
            if (!level.setBlockAndUpdate(pos, replacement)) continue;
            if (!(level.getBlockEntity(pos) instanceof FluidCauldronBlockEntity tank)
                || !tank.insert("italiansdelight:olive_oil", levelCount * 250)) {
                // Preserve the old saved contents if the new block entity fails.
                level.setBlockAndUpdate(pos, state);
            }
        }
    }
}
