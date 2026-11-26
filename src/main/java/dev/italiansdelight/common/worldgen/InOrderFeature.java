package dev.italiansdelight.common.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

/** Local replacement for the newer FDR ordered feature, preserving the
 * individual placements and predicates of the vine surroundings. */
public final class InOrderFeature extends Feature<InOrderFeature.Config> {
    public record Config(List<Holder<PlacedFeature>> features) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(PlacedFeature.CODEC.listOf().fieldOf("features").forGetter(Config::features))
                .apply(instance, Config::new));
    }

    public InOrderFeature() { super(Config.CODEC); }

    @Override public boolean place(FeaturePlaceContext<Config> context) {
        boolean placed = false;
        for (Holder<PlacedFeature> feature : context.config().features()) {
            placed |= feature.value().place(context.level(), context.chunkGenerator(),
                context.random(), context.origin());
        }
        return placed;
    }
}
