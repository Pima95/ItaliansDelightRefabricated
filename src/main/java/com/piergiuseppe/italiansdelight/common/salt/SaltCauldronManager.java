package com.piergiuseppe.italiansdelight.common.salt;

import java.util.Iterator;
import java.util.Map;

import com.piergiuseppe.italiansdelight.common.registry.ModBlocks;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * Handles passive salt production in full vanilla water cauldrons.
 */
public final class SaltCauldronManager {

    // Numero di tick realmente validi richiesti; notte, maltempo e ostacoli
    // mettono in pausa il conteggio e non contribuiscono al totale.
    public static final int REQUIRED_USEFUL_TICKS = 12_000;

    private SaltCauldronManager() {
    }

    // Registra i due ingressi del sistema: tick della dimensione e scansione
    // dei chunk caricati per recuperare calderoni già esistenti.
    public static void register() {
        ServerTickEvents.END_LEVEL_TICK.register(
            SaltCauldronManager::tickLevel
        );

        // Also discovers full cauldrons that already existed before the mod
        // started tracking them. Sections whose palettes cannot contain a full
        // water cauldron are skipped without scanning their 4096 block states.
        ServerChunkEvents.CHUNK_LOAD.register(
            SaltCauldronManager::onChunkLoad
        );
    }

    /**
     * Called by the Level mixin after a vanilla cauldron state has actually
     * changed.
     */
    public static void onCauldronStateChanged(
        ServerLevel level,
        BlockPos pos,
        BlockState state
    ) {
        SaltCauldronProgressData data =
            SaltCauldronProgressData.get(level);

        long packedPos = pos.asLong();

        if (isFullWaterCauldron(state)) {
            if (
                data.progressByPosition()
                    .putIfAbsent(packedPos, 0) == null
            ) {
                data.setDirty();
            }

            return;
        }

        if (
            data.progressByPosition()
                .remove(packedPos) != null
        ) {
            data.setDirty();
        }
    }

    public static boolean isFullWaterCauldron(
        BlockState state
    ) {
        return state.is(Blocks.WATER_CAULDRON)
            && state.getValue(
                LayeredCauldronBlock.LEVEL
            ) == LayeredCauldronBlock.MAX_FILL_LEVEL;
    }

    /**
     * Cerca calderoni pieni già presenti nel chunk. Prima controlla la palette
     * di ogni section, evitando di scandire 4096 blocchi quando è impossibile
     * che la section contenga un water cauldron pieno.
     */
    private static void onChunkLoad(
        ServerLevel level,
        LevelChunk chunk,
        boolean generated
    ) {
        SaltCauldronProgressData data =
            SaltCauldronProgressData.get(level);

        LevelChunkSection[] sections =
            chunk.getSections();

        ChunkPos chunkPos =
            chunk.getPos();

        boolean changed = false;

        for (
            int sectionIndex = 0;
            sectionIndex < sections.length;
            sectionIndex++
        ) {
            LevelChunkSection section =
                sections[sectionIndex];

            if (
                !section.maybeHas(
                    SaltCauldronManager::isFullWaterCauldron
                )
            ) {
                continue;
            }

            int sectionY =
                chunk.getSectionYFromSectionIndex(
                    sectionIndex
                );

            int baseY =
                SectionPos.sectionToBlockCoord(
                    sectionY
                );

            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    for (int x = 0; x < 16; x++) {
                        BlockState state =
                            section.getBlockState(
                                x,
                                y,
                                z
                            );

                        if (!isFullWaterCauldron(state)) {
                            continue;
                        }

                        long packedPos =
                            BlockPos.asLong(
                                chunkPos.getMinBlockX() + x,
                                baseY + y,
                                chunkPos.getMinBlockZ() + z
                            );

                        if (
                            data.progressByPosition()
                                .putIfAbsent(
                                    packedPos,
                                    0
                                ) == null
                        ) {
                            changed = true;
                        }
                    }
                }
            }
        }

        if (changed) {
            data.setDirty();
        }
    }

    /**
     * Aggiorna tutti i calderoni monitorati della dimensione. Non forza mai il
     * caricamento di chunk: quelli scaricati restano semplicemente in pausa.
     */
    private static void tickLevel(
        ServerLevel level
    ) {
        SaltCauldronProgressData data =
            SaltCauldronProgressData.get(level);

        Iterator<Map.Entry<Long, Integer>> iterator =
            data.progressByPosition()
                .entrySet()
                .iterator();

        boolean changed = false;

        while (iterator.hasNext()) {
            Map.Entry<Long, Integer> entry =
                iterator.next();

            BlockPos pos =
                BlockPos.of(entry.getKey());

            // Unloaded chunks must neither progress nor be force-loaded.
            if (!level.hasChunkAt(pos)) {
                continue;
            }

            BlockState state =
                level.getBlockState(pos);

            // Water removed, level lowered or cauldron replaced/broken:
            // progress is lost completely.
            if (!isFullWaterCauldron(state)) {
                iterator.remove();
                changed = true;
                continue;
            }

            // Invalid environmental conditions pause, rather than reset,
            // the process.
            if (!canEvaporate(level, pos)) {
                continue;
            }

            int nextProgress =
                entry.getValue() + 1;

            if (
                nextProgress
                    >= REQUIRED_USEFUL_TICKS
            ) {
                iterator.remove();

                level.setBlockAndUpdate(
                    pos,
                    ModBlocks.SALT_CAULDRON
                        .defaultBlockState()
                );

                changed = true;
                continue;
            }

            entry.setValue(nextProgress);
            changed = true;
        }

        if (changed) {
            data.setDirty();
        }
    }

    // Tutte le condizioni ambientali devono essere vere nello stesso tick.
    private static boolean canEvaporate(
        ServerLevel level,
        BlockPos pos
    ) {
        if (!level.isBrightOutside()) {
            return false;
        }

        if (
            level.isRaining()
            || level.isThundering()
        ) {
            return false;
        }

        return isCompletelyOpenAbove(
            level,
            pos
        );
    }

    /**
     * WORLD_SURFACE reports one block above the highest non-air block in the
     * column. Therefore it equals cauldronY + 1 only when every block above the
     * cauldron is air, including cases such as open trapdoors, glass and leaves.
     */
    private static boolean isCompletelyOpenAbove(
        ServerLevel level,
        BlockPos pos
    ) {
        int surfaceY =
            level.getHeight(
                Heightmap.Types.WORLD_SURFACE,
                pos.getX(),
                pos.getZ()
            );

        return surfaceY == pos.getY() + 1;
    }
}
