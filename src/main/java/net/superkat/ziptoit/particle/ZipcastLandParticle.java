package net.superkat.ziptoit.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Direction;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ZipcastLandParticle extends SpriteBillboardParticle {
    private final SpriteProvider spriteProvider;
    private final Direction direction;
    private int fadeOutTicks;
    private int scaleUpTicks;

    public ZipcastLandParticle(ClientWorld clientWorld, ZipcastLandParticleEffect params, double x, double y, double z, double velX, double velY, double velZ, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, velX, velY, velX);
        this.spriteProvider = spriteProvider;
        this.maxAge = params.age();
        this.scale = params.scale();
        this.fadeOutTicks = params.fadeOutTicks();
        this.scaleUpTicks = params.scaleUpTicks();
        this.direction = params.direction();
        Vector3f color = params.color();
        this.setColor(color.x, color.y, color.z);
        this.alpha = 1f;

        float offsetAmount = 0.025f;
        float xOffset = offsetAmount * this.direction.getOffsetX();
        float yOffset = offsetAmount * this.direction.getOffsetY();
        float zOffset = offsetAmount * this.direction.getOffsetZ();
        this.setPos(x + xOffset, y + yOffset, z + zOffset);
        this.repositionFromBoundingBox();

        this.velocityX = velX;
        this.velocityY = velY;
        this.velocityZ = velZ;

        this.setSpriteForAge(this.spriteProvider);
    }

    @Override
    public void tick() {
        int nonFadeOutTicks = this.maxAge - this.fadeOutTicks;
        if(this.age >= nonFadeOutTicks) {
            this.alpha = 1f - (float) (this.age - nonFadeOutTicks) / this.fadeOutTicks;
        }

        int nonScaleUpTicks = this.maxAge - this.scaleUpTicks;
        if(this.age >= nonScaleUpTicks) {
            this.scale += 0.05f;
        }

        if(this.alpha <= 0) this.markDead();

        this.setSpriteForAge(this.spriteProvider);
        super.tick();
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float tickProgress) {
        Quaternionf quaternionf = new Quaternionf();

//        quaternionf.rotateXYZ(this.direction.getOffsetX(), this.direction.getOffsetY(), 0);
        if(direction == Direction.DOWN || direction == Direction.UP) {
            quaternionf.rotateX((float) Math.toRadians(90f));
        } else if(direction == Direction.EAST || direction == Direction.WEST) {
            quaternionf.rotateY((float) Math.toRadians(90f));
        }
        this.render(vertexConsumer, camera, quaternionf, tickProgress);
        this.render(vertexConsumer, camera, quaternionf.rotateY((float) Math.toRadians(180f)), tickProgress);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Factory implements ParticleFactory<ZipcastLandParticleEffect> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(ZipcastLandParticleEffect params, ClientWorld world, double x, double y, double z, double velX, double velY, double velZ) {
            return new ZipcastLandParticle(world, params, x, y, z, velX, velY, velZ, spriteProvider);
        }
    }
}
