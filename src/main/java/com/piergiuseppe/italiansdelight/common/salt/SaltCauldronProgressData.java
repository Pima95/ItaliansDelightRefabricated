package com.piergiuseppe.italiansdelight.common.salt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.piergiuseppe.italiansdelight.common.registry.ModRegistries;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

/**
 * Persistent per-dimension progress for salt evaporation in vanilla water
 * cauldrons.
 */
public final class SaltCauldronProgressData extends SavedData {

    // Forma compatta usata solo per serializzare la mappa runtime.
    // BlockPos viene salvato come long per ridurre lo spazio occupato.
    private record ProgressEntry(long pos, int progress) {

        private static final Codec<ProgressEntry> CODEC =
            RecordCodecBuilder.create(instance ->
                instance.group(
                    Codec.LONG.fieldOf("pos")
                        .forGetter(ProgressEntry::pos),
                    Codec.INT.fieldOf("progress")
                        .forGetter(ProgressEntry::progress)
                ).apply(
                    instance,
                    ProgressEntry::new
                )
            );
    }

    // Codec dell'intero SavedData: converte la mappa in una lista di entry.
    private static final Codec<SaltCauldronProgressData> CODEC =
        RecordCodecBuilder.create(instance ->
            instance.group(
                ProgressEntry.CODEC
                    .listOf()
                    .fieldOf("entries")
                    .forGetter(
                        SaltCauldronProgressData::serializedEntries
                    )
            ).apply(
                instance,
                SaltCauldronProgressData::new
            )
        );

    @SuppressWarnings("DataFlowIssue")
    public static final SavedDataType<SaltCauldronProgressData> TYPE =
        new SavedDataType<>(
            ModRegistries.id("salt_cauldron_progress"),
            SaltCauldronProgressData::new,
            CODEC,
            null
        );

    // Mappa runtime: posizione del calderone -> tick utili accumulati.
    private final Map<Long, Integer> progressByPosition =
        new HashMap<>();

    public SaltCauldronProgressData() {
    }

    // Costruttore usato dal codec durante il caricamento del mondo.
    private SaltCauldronProgressData(
        List<ProgressEntry> entries
    ) {
        for (ProgressEntry entry : entries) {
            progressByPosition.put(
                entry.pos(),
                Math.max(0, entry.progress())
            );
        }
    }

    // Restituisce il SavedData della dimensione corrente, creandolo se manca.
    public static SaltCauldronProgressData get(
        ServerLevel level
    ) {
        return level.getDataStorage()
            .computeIfAbsent(TYPE);
    }

    Map<Long, Integer> progressByPosition() {
        return progressByPosition;
    }

    // Converte la mappa runtime nella forma serializzabile prevista dal CODEC.
    private List<ProgressEntry> serializedEntries() {
        return progressByPosition
            .entrySet()
            .stream()
            .map(entry ->
                new ProgressEntry(
                    entry.getKey(),
                    entry.getValue()
                )
            )
            .toList();
    }
}
