package net.superkat.ziptoit.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import org.joml.Vector3f;

public class ZipcastImpactSplatterParticle extends SpriteBillboardParticle {
    private final SpriteProvider spriteProvider;
    public ZipcastImpactSplatterParticle(ClientWorld clientWorld, ZipcastImpactSplatterEffect params, double x, double y, double z, double velX, double velY, double velZ, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, velX, velY, velX);
        this.spriteProvider = spriteProvider;

        this.maxAge = params.age();
        this.scale = params.scale();
        Vector3f color = params.color();
        this.setColor(color.x, color.y, color.z);

        this.velocityX = velX;
        this.velocityY = velY;
        this.velocityZ = velZ;

        this.velocityMultiplier = 0.95f;
        this.setSpriteForAge(this.spriteProvider);
    }

    @Override
    public void tick() {
        this.setSpriteForAge(this.spriteProvider);
        super.tick();
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Factory implements ParticleFactory<ZipcastImpactSplatterEffect> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(ZipcastImpactSplatterEffect params, ClientWorld world, double x, double y, double z, double velX, double velY, double velZ) {
            return new ZipcastImpactSplatterParticle(world, params, x, y, z, velX, velY, velZ, spriteProvider);
        }
    }
}
