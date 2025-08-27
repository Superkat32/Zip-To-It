package net.superkat.ziptoit.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.superkat.ziptoit.zipcast.color.StickyHandColors;
import net.superkat.ziptoit.zipcast.color.ZipcastColor;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ZipcastZoomParticle extends SpriteBillboardParticle {
    private static final float I_TURN_DEGREES_INTO_U_TURN_RADIANS = (float) Math.toRadians(180f);

    private final SpriteProvider spriteProvider;
    private float yaw;
    private float pitch;
    private float stretch;

    public ZipcastZoomParticle(ClientWorld clientWorld, ZipcastZoomParticleEffect params, double x, double y, double z, double velX, double velY, double velZ, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, velX, velY, velZ);
        this.spriteProvider = spriteProvider;

        this.yaw = params.yaw();
        this.pitch = params.pitch();
        this.stretch = params.stretch();

        this.velocityX = velX;
        this.velocityY = velY;
        this.velocityZ = velZ;

        this.maxAge = 15;
        this.scale = 0.15f + this.random.nextFloat() / 2f;

        ZipcastColor zipcastColor = params.zipcastColor();

        int color;
        if(zipcastColor.rainbow()) {
            int maxColors = StickyHandColors.RAINBOW_DYES.size();
            int index = this.random.nextInt(maxColors);
            color = StickyHandColors.RAINBOW_DYES.get(index).getEntityColor();
        } else {
            color = this.random.nextFloat() <= 0.25f ? zipcastColor.altColor() : zipcastColor.color();
        }

        this.setColorFromInt(color);

        this.setSprite(this.spriteProvider);
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float tickProgress) {
        Quaternionf quaternionf = new Quaternionf();
        if (this.angle != 0.0F) {
            quaternionf.rotateZ(MathHelper.lerp(tickProgress, this.lastAngle, this.angle));
        }

        quaternionf.rotateY((float) Math.toRadians(this.yaw));
        quaternionf.rotateZ((float) Math.toRadians(this.pitch));

        this.render(vertexConsumer, camera, quaternionf, tickProgress);
        // this technically isn't perfect but I started getting frustrated trying to figure out why
        // probably something to do with my texture not being centered vertically
        quaternionf.rotateY(I_TURN_DEGREES_INTO_U_TURN_RADIANS);
        quaternionf.rotateZ(I_TURN_DEGREES_INTO_U_TURN_RADIANS);
        this.render(vertexConsumer, camera, quaternionf, tickProgress);
    }

    @Override
    protected void render(VertexConsumer vertexConsumer, Quaternionf quaternionf, float x, float y, float z, float tickProgress) {
        float scale = this.getSize(tickProgress);
        float minU = this.getMinU();
        float maxU = this.getMaxU();
        float minV = this.getMinV();
        float maxV = this.getMaxV();
        int light = this.getBrightness(tickProgress);
        this.renderVertex(vertexConsumer, quaternionf, x, y, z, 1.0F + this.stretch, -1.0F, scale, maxU, maxV, light);
        this.renderVertex(vertexConsumer, quaternionf, x, y, z, 1.0F + this.stretch, 1.0F, scale, maxU, minV, light);
        this.renderVertex(vertexConsumer, quaternionf, x, y, z, -1.0F, 1.0F, scale, minU, minV, light);
        this.renderVertex(vertexConsumer, quaternionf, x, y, z, -1.0F, -1.0F, scale, minU, maxV, light);
    }

    private void renderVertex(
            VertexConsumer vertexConsumer, Quaternionf quaternionf, float x, float y, float z, float offsetX, float offsetY, float size, float u, float v, int light
    ) {
        Vector3f vector3f = new Vector3f(offsetX, offsetY, 0.0F).rotate(quaternionf).mul(size).add(x, y, z);
        vertexConsumer.vertex(vector3f.x(), vector3f.y(), vector3f.z()).texture(u, v).color(this.red, this.green, this.blue, this.alpha).light(light);
    }

    @Override
    public void tick() {
        int fadeOutTicks = 5;
        int nonFadeOutTicks = this.maxAge - fadeOutTicks;
        if(this.age >= nonFadeOutTicks) {
            this.alpha = 1f - (float) (this.age - nonFadeOutTicks) / fadeOutTicks;
        }
        if(this.alpha <= 0f) this.markDead();
        super.tick();
    }

    private void setColorFromInt(int color) {
        float red = ColorHelper.getRedFloat(color);
        float green = ColorHelper.getGreenFloat(color);
        float blue = ColorHelper.getBlueFloat(color);
        float alpha = ColorHelper.getAlphaFloat(color);
        this.setColor(red, green, blue);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Factory implements ParticleFactory<ZipcastZoomParticleEffect> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(ZipcastZoomParticleEffect params, ClientWorld world, double x, double y, double z, double velX, double velY, double velZ) {
            return new ZipcastZoomParticle(world, params, x, y, z, velX, velY, velZ, spriteProvider);
        }
    }


}
