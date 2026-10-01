package dev.italiansdelight.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

/**
 * White variant of Minecraft's short bubble-pop animation.
 *
 * The movement and lifetime intentionally mirror vanilla BubblePopParticle;
 * only the sprite set differs.
 */
public final class WhiteBubblePopParticle extends SingleQuadParticle {

    private final SpriteSet sprites;

    private WhiteBubblePopParticle(
        ClientLevel level,
        double x,
        double y,
        double z,
        double xa,
        double ya,
        double za,
        SpriteSet sprites
    ) {
        super(
            level,
            x,
            y,
            z,
            xa,
            ya,
            za,
            sprites.first()
        );

        this.sprites = sprites;
        this.lifetime = 4;
        this.gravity = 0.008F;
        this.xd = xa;
        this.yd = ya;
        this.zd = za;

        setSpriteFromAge(
            sprites
        );
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            remove();
            return;
        }

        this.yd -= this.gravity;
        move(
            this.xd,
            this.yd,
            this.zd
        );

        setSpriteFromAge(
            this.sprites
        );
    }

    @Override
    public Layer getLayer() {
        return Layer.OPAQUE;
    }

    public static final class Provider
        implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet sprites;

        public Provider(
            SpriteSet sprites
        ) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(
            SimpleParticleType type,
            ClientLevel level,
            double x,
            double y,
            double z,
            double xa,
            double ya,
            double za,
            RandomSource random
        ) {
            return new WhiteBubblePopParticle(
                level,
                x,
                y,
                z,
                xa,
                ya,
                za,
                sprites
            );
        }
    }
}
