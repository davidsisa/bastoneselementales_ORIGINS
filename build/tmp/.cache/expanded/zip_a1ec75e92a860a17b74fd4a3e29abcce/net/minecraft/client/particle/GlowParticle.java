package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GlowParticle extends TextureSheetParticle {
    static final RandomSource RANDOM = RandomSource.create();
    private final SpriteSet sprites;

    GlowParticle(
        ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSprites
    ) {
        super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
        this.friction = 0.96F;
        this.speedUpWhenYMotionIsBlocked = true;
        this.sprites = pSprites;
        this.quadSize *= 0.75F;
        this.hasPhysics = false;
        this.setSpriteFromAge(pSprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public int getLightColor(float pPartialTick) {
        float f = ((float)this.age + pPartialTick) / (float)this.lifetime;
        f = Mth.clamp(f, 0.0F, 1.0F);
        int i = super.getLightColor(pPartialTick);
        int j = i & 0xFF;
        int k = i >> 16 & 0xFF;
        j += (int)(f * 15.0F * 16.0F);
        if (j > 240) {
            j = 240;
        }

        return j | k << 16;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
    }

    @OnlyIn(Dist.CLIENT)
    public static class ElectricSparkProvider implements ParticleProvider<SimpleParticleType> {
        private final double SPEED_FACTOR = 0.25;
        private final SpriteSet sprite;

        public ElectricSparkProvider(SpriteSet pSprites) {
            this.sprite = pSprites;
        }

        public Particle createParticle(
            SimpleParticleType pType,
            ClientLevel pLevel,
            double pX,
            double pY,
            double pZ,
            double pXSpeed,
            double pYSpeed,
            double pZSpeed
        ) {
            GlowParticle glowparticle = new GlowParticle(pLevel, pX, pY, pZ, 0.0, 0.0, 0.0, this.sprite);
            glowparticle.setColor(1.0F, 0.9F, 1.0F);
            glowparticle.setParticleSpeed(pXSpeed * 0.25, pYSpeed * 0.25, pZSpeed * 0.25);
            int i = 2;
            int j = 4;
            glowparticle.setLifetime(pLevel.random.nextInt(2) + 2);
            return glowparticle;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class GlowSquidProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public GlowSquidProvider(SpriteSet pSprites) {
            this.sprite = pSprites;
        }

        public Particle createParticle(
            SimpleParticleType pType,
            ClientLevel pLevel,
            double pX,
            double pY,
            double pZ,
            double pXSpeed,
            double pYSpeed,
            double pZSpeed
        ) {
            GlowParticle glowparticle = new GlowParticle(
                pLevel,
                pX,
                pY,
                pZ,
                0.5 - GlowParticle.RANDOM.nextDouble(),
                pYSpeed,
                0.5 - GlowParticle.RANDOM.nextDouble(),
                this.sprite
            );
            if (pLevel.random.nextBoolean()) {
                glowparticle.setColor(0.6F, 1.0F, 0.8F);
            } else {
                glowparticle.setColor(0.08F, 0.4F, 0.4F);
            }

            glowparticle.yd *= 0.2F;
            if (pXSpeed == 0.0 && pZSpeed == 0.0) {
                glowparticle.xd *= 0.1F;
                glowparticle.zd *= 0.1F;
            }

            glowparticle.setLifetime((int)(8.0 / (pLevel.random.nextDouble() * 0.8 + 0.2)));
            return glowparticle;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class ScrapeProvider implements ParticleProvider<SimpleParticleType> {
        private final double SPEED_FACTOR = 0.01;
        private final SpriteSet sprite;

        public ScrapeProvider(SpriteSet pSprites) {
            this.sprite = pSprites;
        }

        public Particle createParticle(
            SimpleParticleType pType,
            ClientLevel pLevel,
            double pX,
            double pY,
            double pZ,
            double pXSpeed,
            double pYSpeed,
            double pZSpeed
        ) {
            GlowParticle glowparticle = new GlowParticle(pLevel, pX, pY, pZ, 0.0, 0.0, 0.0, this.sprite);
            if (pLevel.random.nextBoolean()) {
                glowparticle.setColor(0.29F, 0.58F, 0.51F);
            } else {
                glowparticle.setColor(0.43F, 0.77F, 0.62F);
            }

            glowparticle.setParticleSpeed(pXSpeed * 0.01, pYSpeed * 0.01, pZSpeed * 0.01);
            int i = 10;
            int j = 40;
            glowparticle.setLifetime(pLevel.random.nextInt(30) + 10);
            return glowparticle;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class WaxOffProvider implements ParticleProvider<SimpleParticleType> {
        private final double SPEED_FACTOR = 0.01;
        private final SpriteSet sprite;

        public WaxOffProvider(SpriteSet pSprites) {
            this.sprite = pSprites;
        }

        public Particle createParticle(
            SimpleParticleType pType,
            ClientLevel pLevel,
            double pX,
            double pY,
            double pZ,
            double pXSpeed,
            double pYSpeed,
            double pZSpeed
        ) {
            GlowParticle glowparticle = new GlowParticle(pLevel, pX, pY, pZ, 0.0, 0.0, 0.0, this.sprite);
            glowparticle.setColor(1.0F, 0.9F, 1.0F);
            glowparticle.setParticleSpeed(pXSpeed * 0.01 / 2.0, pYSpeed * 0.01, pZSpeed * 0.01 / 2.0);
            int i = 10;
            int j = 40;
            glowparticle.setLifetime(pLevel.random.nextInt(30) + 10);
            return glowparticle;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class WaxOnProvider implements ParticleProvider<SimpleParticleType> {
        private final double SPEED_FACTOR = 0.01;
        private final SpriteSet sprite;

        public WaxOnProvider(SpriteSet pSprites) {
            this.sprite = pSprites;
        }

        public Particle createParticle(
            SimpleParticleType pType,
            ClientLevel pLevel,
            double pX,
            double pY,
            double pZ,
            double pXSpeed,
            double pYSpeed,
            double pZSpeed
        ) {
            GlowParticle glowparticle = new GlowParticle(pLevel, pX, pY, pZ, 0.0, 0.0, 0.0, this.sprite);
            glowparticle.setColor(0.91F, 0.55F, 0.08F);
            glowparticle.setParticleSpeed(pXSpeed * 0.01 / 2.0, pYSpeed * 0.01, pZSpeed * 0.01 / 2.0);
            int i = 10;
            int j = 40;
            glowparticle.setLifetime(pLevel.random.nextInt(30) + 10);
            return glowparticle;
        }
    }
}