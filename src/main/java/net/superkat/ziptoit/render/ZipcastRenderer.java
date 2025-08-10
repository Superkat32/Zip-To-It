package net.superkat.ziptoit.render;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import net.superkat.ziptoit.duck.ZipcasterPlayer;
import net.superkat.ziptoit.zipcast.ZipcastTarget;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class ZipcastRenderer {

    private static final int MAX_LIGHT = LightmapTextureManager.pack(15, 15);

    public static void render(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        for (AbstractClientPlayerEntity player : client.world.getPlayers()) {
            if(!(player instanceof ZipcasterPlayer zipcasterPlayer)) continue;

            if(!zipcasterPlayer.isZipcasting() || zipcasterPlayer.zipcastTarget() == null) continue;
            renderZipcaster(context, player);
        }

        RaycastRenderer.render(context);
    }

    public static void renderZipcaster(WorldRenderContext context, AbstractClientPlayerEntity player) {
        MinecraftClient client = MinecraftClient.getInstance();
        if(!(player instanceof ZipcasterPlayer zipcasterPlayer)) return;

        VertexConsumerProvider consumers = context.consumers();
        MatrixStack matrices = context.matrixStack();
        Camera camera = context.camera();
        float tickProgress = context.tickCounter().getTickProgress(true);
        ZipcastTarget zipcastTarget = zipcasterPlayer.zipcastTarget();

        List<ZipcastPoint> zipcastPoints = getZipcastPoints(player, 8, tickProgress);
        renderZipcastLine(matrices, consumers.getBuffer(ZipToItRenderLayers.ZIPCAST_LINE), camera, zipcastPoints, zipcastTarget, tickProgress, 45f);
        renderZipcastLine(matrices, consumers.getBuffer(ZipToItRenderLayers.ZIPCAST_LINE), camera, zipcastPoints, zipcastTarget, tickProgress, -45f);
//        renderDebugZipcastLine(matrices, consumers.getBuffer(ZipToItRenderLayers.RAYCAST_TARGET_BOX), camera, zipcastPoints, zipcastTarget, tickProgress);
        renderZipcastCube(matrices, consumers.getBuffer(ZipToItRenderLayers.RAYCAST_TARGET_BOX), camera, zipcastTarget, zipcastPoints.getLast().pos());
    }

    // This is a slightly modified version of my line renderer for Jet Lag(an unreleased for now:tm: mod)
    // https://github.com/Superkat32/Jet-Lag/blob/master/src/main/java/net/superkat/jetlag/rendering/ContrailRenderer.java#L232
    // It's basically just playing a game of connect the dots,
    // taking advantage of VertexFormat.DrawMode.TRIANGLE_STRIP's shared vertices
    public static void renderZipcastLine(MatrixStack matrices, VertexConsumer consumer, Camera camera, List<ZipcastPoint> points, ZipcastTarget zipcastTarget, float tickProgress, float rotation) {;
        for (int i = 0; i < points.size() - 1; i++) {
            boolean first = i == 0;
            boolean last = i == (points.size() - 1);
            ZipcastPoint zipcastPoint = points.get(i);
            ZipcastPoint targetZipcastPoint = points.get(i + 1);

            Vec3d pos = zipcastPoint.pos;
            Vec3d target = targetZipcastPoint.pos;
            int light = getLight(pos, 7);
            renderLineSegment(matrices, consumer, pos, target, 1f, first ? 1f : 0f, last ? 1f : 0f, 1f, light, 0.15f, zipcastPoint.offset, rotation);
        }
    }

    public static void renderZipcastCube(MatrixStack matrices, VertexConsumer consumer, Camera camera, ZipcastTarget zipcastTarget, Vec3d target) {
        matrices.push();
        matrices.translate(-camera.getPos().x, -camera.getPos().y, -camera.getPos().z);
        drawCube(matrices, consumer, target, 0.4f, 1f, 0f, 0f, 1f, MAX_LIGHT);
        matrices.pop();
    }

    public static void renderDebugZipcastLine(MatrixStack matrices, VertexConsumer consumer, Camera camera, List<ZipcastPoint> points, ZipcastTarget zipcastTarget, float tickProgress) {
        Vec3d cameraPos = camera.getPos();
        for (ZipcastPoint zipcastPoint : points) {
            matrices.push();
            Vec3d point = zipcastPoint.pos();

            matrices.translate(zipcastPoint.offset(), 0, 0);
            matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
            float greenPerPoint = 1f / points.size();
            float green = greenPerPoint * points.indexOf(zipcastPoint);
            RaycastRenderer.renderCube(matrices, consumer, point, 0.5f, 0f, green, 1f, 0.8f);
            matrices.pop();
        }
    }

    private static void drawCube(MatrixStack matrices, VertexConsumer consumer, Vec3d pos, float size, float red, float green, float blue, float alpha, int light) {
        float halfSize = size / 2f;
        VertexRendering.drawFilledBox(
                matrices, consumer,
                pos.getX() - halfSize, pos.getY() - halfSize, pos.getZ() - halfSize,
                pos.getX() + halfSize, pos.getY() + halfSize, pos.getZ() + halfSize,
                red, green, blue, alpha
        );
    }

    private static void renderLineSegment(MatrixStack matrixStack, VertexConsumer consumer, Vec3d origin, Vec3d target, float red, float green, float blue, float alpha, int light, float width, float xOffset, float rotation) {
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        Vec3d transformedMatrixPos = origin.subtract(camera.getPos());
        boolean firstPerson = MinecraftClient.getInstance().options.getPerspective().isFirstPerson();
        matrixStack.push();

        //offsets to the origin's pos
        matrixStack.translate(transformedMatrixPos.x, transformedMatrixPos.y, transformedMatrixPos.z);

        //calculates length
        float length = (float) origin.distanceTo(target);

        //rotates towards pos from origin
        Vec3d transformedPos = target.subtract(origin);
        transformedPos = transformedPos.normalize();
        float rightAngle = (float) Math.toRadians(90);
        float n = (float)Math.acos(transformedPos.y);
        float o = (float)Math.atan2(transformedPos.z, transformedPos.x);
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) Math.toDegrees(rightAngle - o))); //rotates left/right
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float) Math.toDegrees(rightAngle + n))); //rotates up/down

        matrixStack.translate(xOffset, 0, 0);
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation));

        drawTriangle(matrixStack.peek().getPositionMatrix(), consumer, width, -length, red, green, blue, alpha, light);

        matrixStack.pop();
    }

    private static void drawTriangle(Matrix4f matrix, VertexConsumer vertexConsumer, float width, float length, float red, float green, float blue, float alpha, int light) {
        vertexConsumer.vertex(matrix, width / 2f, 0, length)
                .color(red, green, blue, alpha).light(light);
        vertexConsumer.vertex(matrix, -width / 2f, 0, length)
                .color(red, green, blue, alpha).light(light);
    }

    // Should be "playerPos -> targetPos" in terms of list order
    public static List<ZipcastPoint> getZipcastPoints(AbstractClientPlayerEntity player, int subdivisions, float tickProgress) {
        if(!(player instanceof ZipcasterPlayer zipcasterPlayer)) return List.of();
        int zipcastTicks = zipcasterPlayer.zipcastTicks();
        ZipcastTarget zipcastTarget = zipcasterPlayer.zipcastTarget();

        int startTicks = zipcastTarget.startTicks();
        int shootTicks = startTicks - 2;
        int wobbleTicks = startTicks + 5;
        int lerpTicks = zipcastTarget.lerpTicks();
        int wobbleUntilLandTicks = lerpTicks + 8;
        int buildUpTicks = zipcastTarget.buildupTicks();

        Vec3d playerPos = player.getLerpedPos(tickProgress).add(0, 0.5, 0);
        Vec3d zipcastTargetPos = zipcastTarget.pos();
        Vec3d targetPos = zipcastTargetPos;
        Vec3d difference = targetPos.subtract(playerPos);
        boolean playerIsSelf = player.isMainPlayer();

        // Shoot animation (simply bringing back the targetPos some)
        if(zipcastTicks <= shootTicks) {
            float targetFallbackAmount = MathHelper.clamp((float) zipcastTicks / shootTicks, 0f, 1f);
            targetPos = playerPos.add(difference.multiply(targetFallbackAmount));
            difference = targetPos.subtract(playerPos);
        }

        // Wobble effects
        float tickMultiplier = 1f;
        float offsetMultiplier = 0f;
        if(zipcastTicks <= wobbleTicks) {
            // Huge wobble during shoot
            offsetMultiplier = (1f - ((float) zipcastTicks / wobbleTicks)) / 2f;
            tickMultiplier = 3;
        } else if (zipcastTicks >= wobbleUntilLandTicks) {
            // Small wobble during actual zipcast
            float distanceToTarget = (float) playerPos.distanceTo(zipcastTargetPos);
            float distanceOffset = 5f / MathHelper.clamp(distanceToTarget, 5f, 100f);
            offsetMultiplier = MathHelper.clamp(distanceOffset, 0f, 0.35f) / 3f;
            tickMultiplier = 2f;
        }

        if(!playerIsSelf) {
            // Exaggerate effect for other players
            offsetMultiplier *= 2f;
        }

        List<ZipcastPoint> points = new ArrayList<>();
        float subdivisionAmount = 1f / subdivisions;
        // Always ensure the point at the player is rendered correctly
        points.add(new ZipcastPoint(playerPos.add(difference.multiply(-0.05f)), 0));

        // Subdivide the line into many different points, then apply the wobble offset to each point
        // These points will then be rendered in one connected line, so they must be in order!
        for (int i = 0; i <= subdivisions; i++) {
            float subdivideAmount = subdivisionAmount * i;
            Vec3d vec3d = playerPos.add(difference.multiply(subdivideAmount));
            float offset = MathHelper.sin(zipcastTicks * tickMultiplier + i) * offsetMultiplier;
            points.add(new ZipcastPoint(vec3d, offset));
        }

        return points;
    }

    public static record ZipcastPoint(Vec3d pos, float offset) {}

    public static int getLight(Vec3d pos, int minLight) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientWorld world = client.world;
        BlockPos blockPos = BlockPos.ofFloored(pos);
        int blockLight = world.getLightLevel(LightType.BLOCK, blockPos);
        int skyLevel = world.getLightLevel(LightType.SKY, blockPos);
        return LightmapTextureManager.pack(Math.max(blockLight, minLight), skyLevel);
    }

}
