package dev.italiansdelight.common.persistence;

import com.mojang.serialization.Codec;
import dev.italiansdelight.ItaliansDelight;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import java.util.Optional;

/** Registry-aware codec persistence using the 1.21.1 NBT API. */
public final class PortNbt {
    private PortNbt() {}

    public static <T> Optional<T> read(CompoundTag tag, HolderLookup.Provider registries, String key, Codec<T> codec) {
        Tag value = tag.get(key);
        if (value == null) return Optional.empty();
        return codec.parse(registries.createSerializationContext(NbtOps.INSTANCE), value)
                .resultOrPartial(message -> ItaliansDelight.LOGGER.warn("Cannot decode {}: {}", key, message));
    }

    public static <T> void write(CompoundTag tag, HolderLookup.Provider registries, String key, Codec<T> codec, T value) {
        codec.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), value)
                .resultOrPartial(message -> ItaliansDelight.LOGGER.error("Cannot encode {}: {}", key, message))
                .ifPresent(encoded -> tag.put(key, encoded));
    }
}
