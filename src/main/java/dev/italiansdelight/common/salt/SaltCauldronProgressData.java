package dev.italiansdelight.common.salt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

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

    private static final String DATA_NAME = "italiansdelight_salt_cauldron_progress";
    private static final SavedData.Factory<SaltCauldronProgressData> FACTORY =
        new SavedData.Factory<>(SaltCauldronProgressData::new,
            SaltCauldronProgressData::load, null);

    private static SaltCauldronProgressData load(CompoundTag tag, HolderLookup.Provider registries) {
        return CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), tag)
            .getOrThrow();
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        var encoded = CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), this)
            .getOrThrow();
        if (!(encoded instanceof CompoundTag compound)) {
            throw new IllegalStateException("Salt cauldron progress must encode as a compound");
        }
        tag.merge(compound);
        return tag;
    }

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
            .computeIfAbsent(FACTORY, DATA_NAME);
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
