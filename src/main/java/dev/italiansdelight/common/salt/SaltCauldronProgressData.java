package dev.italiansdelight.common.salt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.italiansdelight.common.registry.ModRegistries;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

/**
 * Persistent per-dimension progress for salt evaporation in vanilla water
 * cauldrons.
 */
public final class SaltCauldronProgressData extends SavedData {

    // Compact form used only to serialize the runtime map.
    // BlockPos is stored as a long to reduce storage overhead.
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

    // Codec for the whole SavedData object: converts the map into a list of entries.
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

    // Runtime map: cauldron position -> accumulated useful ticks.
    private final Map<Long, Integer> progressByPosition =
        new HashMap<>();

    public SaltCauldronProgressData() {
    }

    // Constructor used by the codec while loading the world.
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

    // Returns the current dimension SavedData, creating it if necessary.
    public static SaltCauldronProgressData get(
        ServerLevel level
    ) {
        return level.getDataStorage()
            .computeIfAbsent(TYPE);
    }

    Map<Long, Integer> progressByPosition() {
        return progressByPosition;
    }

    // Converts the runtime map into the serializable form expected by the CODEC.
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
