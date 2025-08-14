package net.superkat.ziptoit.render.zipcast;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import net.superkat.ziptoit.duck.ZipcasterPlayer;
import net.superkat.ziptoit.render.ZipToItRenderLayers;
import net.superkat.ziptoit.zipcast.color.ZipcastColor;
import net.superkat.ziptoit.zipcast.line.ZipcastLine;
import net.superkat.ziptoit.zipcast.movement.ZipcastTarget;
import org.joml.Matrix4f;

import java.util.List;

public class ZipcastRenderer {

    public static final int MAX_LIGHT = LightmapTextureManager.pack(15, 15);

    public static void render(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        for (AbstractClientPlayerEntity player : client.world.getPlayers()) {
            if(!(player instanceof ZipcasterPlayer zipcasterPlayer)) continue;

            if(!zipcasterPlayer.isZipcasting() || zipcasterPlayer.zipcastTarget() == null || zipcasterPlayer.getZipcastLine() == null || zipcasterPlayer.getZipcastLine().points.isEmpty()) continue;
            renderZipcaster(context, player);
        }

        RaycastRenderer.render(context);
    }

    public static void renderZipcaster(WorldRenderContext context, AbstractClientPlayerEntity player) {
        if(!(player instanceof ZipcasterPlayer zipcasterPlayer)) return;

        VertexConsumerProvider consumers = context.consumers();
        MatrixStack matrices = context.matrixStack();
        Camera camera = context.camera();
        float tickProgress = context.tickCounter().getTickProgress(true);
        ZipcastTarget zipcastTarget = zipcasterPlayer.zipcastTarget();
        ZipcastLine zipcastLine = zipcasterPlayer.getZipcastLine();
        List<ZipcastLine.ZipcastPoint> zipcastPoints = zipcastLine.points;

        ZipcastLine.ZipcastPoint lastPoint = zipcastPoints.getLast();
        // Use second to last point's light instead of last points,
        // because it may sometimes be fully dark because it's inside the block
        ZipcastLine.ZipcastPoint secondLastPoint = zipcastPoints.get(zipcastPoints.size() - 2);

        renderZipcastLine(matrices, consumers.getBuffer(ZipToItRenderLayers.ZIPCAST_LINE), zipcastPoints, 45f, tickProgress);
        renderZipcastLine(matrices, consumers.getBuffer(ZipToItRenderLayers.ZIPCAST_LINE), zipcastPoints, -45f, tickProgress);
        renderZipcastCube(matrices, consumers.getBuffer(ZipToItRenderLayers.ZIPCAST_HAND_BOX), camera, zipcastTarget, lastPoint.lerpPos(tickProgress), secondLastPoint.getLight());
//        renderDebugZipcastLine(matrices, consumers.getBuffer(ZipToItRenderLayers.RAYCAST_TARGET_BOX), camera, zipcastPoints, zipcastTarget, tickProgress);
    }

    public static void renderZipcastLine(MatrixStack matrices, VertexConsumer consumer, List<ZipcastLine.ZipcastPoint> points, float rotation, float tickProgress) {;
        for (int i = 0; i < points.size() - 1; i++) {
            ZipcastLine.ZipcastPoint zipcastPoint = points.get(i);
            ZipcastLine.ZipcastPoint targetZipcastPoint = points.get(i + 1);

            Vec3d pos = zipcastPoint.lerpPos(tickProgress);
            Vec3d target = targetZipcastPoint.lerpPos(tickProgress);
            int color = zipcastPoint.lerpColor(tickProgress);
            int light = zipcastPoint.getLight();
            renderLineSegment(matrices, consumer, pos, target, color, light, 0.15f, zipcastPoint.lerpOffset(tickProgress), rotation);
        }
    }

    public static void renderZipcastCube(MatrixStack matrices, VertexConsumer consumer, Camera camera, ZipcastTarget zipcastTarget, Vec3d target, int light) {
        matrices.push();
        matrices.translate(-camera.getPos().x, -camera.getPos().y, -camera.getPos().z);
        ZipcastColor zipcastColor = zipcastTarget.color();
        int mainColor = zipcastColor.color();
//        int light = getLight(target, 3);
        drawCube(matrices, consumer, target, 0.4f, mainColor, light, 1f);
        matrices.pop();
    }

//    public static void renderDebugZipcastLine(MatrixStack matrices, VertexConsumer consumer, Camera camera, List<ZipcastLine.ZipcastPoint> points, ZipcastTarget zipcastTarget, float tickProgress) {
//        Vec3d cameraPos = camera.getPos();
//        for (ZipcastPoint zipcastPoint : points) {
//            matrices.push();
//            Vec3d point = zipcastPoint.pos();
//
//            matrices.translate(zipcastPoint.offset(), 0, 0);
//            matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
//            float greenPerPoint = 1f / points.size();
//            float green = greenPerPoint * points.indexOf(zipcastPoint);
////            RaycastRenderer.renderCube(matrices, consumer, point, 0.5f, 0f, green, 1f, 0.8f);
//            matrices.pop();
//        }
//    }

    public static void renderLineSegment(MatrixStack matrixStack, VertexConsumer consumer, Vec3d origin, Vec3d target, int color, int light, float width, float xOffset, float rotation) {
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        Vec3d transformedMatrixPos = origin.subtract(camera.getPos());
        matrixStack.push();

        matrixStack.translate(transformedMatrixPos.x, transformedMatrixPos.y, transformedMatrixPos.z);

        float length = (float) origin.distanceTo(target);
        // Rotates towards pos from origin
        Vec3d transformedPos = target.subtract(origin);
        transformedPos = transformedPos.normalize();
        float rightAngle = (float) Math.toRadians(90);
        float n = (float)Math.acos(transformedPos.y);
        float o = (float)Math.atan2(transformedPos.z, transformedPos.x);
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) Math.toDegrees(rightAngle - o))); // rotates left/right
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float) Math.toDegrees(rightAngle + n))); // rotates up/down

        matrixStack.translate(xOffset, 0, 0); // Move horizontally
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation)); // Rotate roll in place

        drawTriangle(matrixStack.peek().getPositionMatrix(), consumer, width, -length, color, light);

        matrixStack.pop();
    }

    private static void drawTriangle(Matrix4f matrix, VertexConsumer vertexConsumer, float width, float length, int color, int light) {
        vertexConsumer.vertex(matrix, width / 2f, 0, length)
                .color(color).light(light);
        vertexConsumer.vertex(matrix, -width / 2f, 0, length)
                .color(color).light(light);
    }

    public static void drawCube(MatrixStack matrices, VertexConsumer consumer, Vec3d pos, float size, int color, int light, float alpha) {
        float halfSize = size / 2f;
        float red = ColorHelper.getRedFloat(color);
        float green = ColorHelper.getGreenFloat(color);
        float blue = ColorHelper.getBlueFloat(color);
        drawFilledBox(
                matrices, consumer,
                (float) (pos.getX() - halfSize), (float) (pos.getY() - halfSize), (float) (pos.getZ() - halfSize),
                (float) (pos.getX() + halfSize), (float) (pos.getY() + halfSize), (float) (pos.getZ() + halfSize),
                red, green, blue, alpha, light
        );
    }

    public static void drawFilledBox(
            MatrixStack matrices,
            VertexConsumer vertexConsumers,
            float minX,
            float minY,
            float minZ,
            float maxX,
            float maxY,
            float maxZ,
            float red,
            float green,
            float blue,
            float alpha,
            int light
    ) {
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        vertexConsumers.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, alpha).light(light);
        vertexConsumers.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, alpha).light(light);
        vertexConsumers.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, alpha).light(light);
        vertexConsumers.vertex(matrix4f, minX, minY, maxZ).color(red, green, blue, alpha).light(light);
        vertexConsumers.vertex(matrix4f, minX, maxY, minZ).color(red, green, blue, alpha).light(light);
        vertexConsumers.vertex(matrix4f, minX, maxY, maxZ).color(red, green, blue, alpha).light(light);
        vertexConsumers.vertex(matrix4f, minX, maxY, maxZ).color(red, green, blue, alpha).light(light);
        vertexConsumers.vertex(matrix4f, minX, minY, maxZ).color(red, green, blue, alpha).light(light);
        vertexConsumers.vertex(matrix4f, maxX, maxY, maxZ).color(red, green, blue, alpha).light(light);
        vertexConsumers.vertex(matrix4f, maxX, minY, maxZ).color(red, green, blue, alpha).light(light);
        vertexConsumers.vertex(matrix4f, maxX, minY, maxZ).color(red, green, blue, alpha).light(light);
        vertexConsumers.vertex(matrix4f, maxX, minY, minZ).color(red, green, blue, alpha).light(light);
        vertexConsumers.vertex(matrix4f, maxX, maxY, maxZ).color(red, green, blue, alpha).light(light);
        vertexConsumers.vertex(matrix4f, maxX, maxY, minZ).color(red, green, blue, alpha).light(light);
        vertexConsumers.vertex(matrix4f, maxX, maxY, minZ).color(red, green, blue, alpha).light(light);
        vertexConsumers.vertex(matrix4f, maxX, minY, minZ).color(red, green, blue, alpha).light(light);
        vertexConsumers.vertex(matrix4f, minX, maxY, minZ).color(red, green, blue, alpha).light(light);
        vertexConsumers.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, alpha).light(light);
        vertexConsumers.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, alpha).light(light);
        vertexConsumers.vertex(matrix4f, maxX, minY, minZ).color(red, green, blue, alpha).light(light);
        vertexConsumers.vertex(matrix4f, minX, minY, maxZ).color(red, green, blue, alpha).light(light);
        vertexConsumers.vertex(matrix4f, maxX, minY, maxZ).color(red, green, blue, alpha).light(light);
        vertexConsumers.vertex(matrix4f, maxX, minY, maxZ).color(red, green, blue, alpha).light(light);
        vertexConsumers.vertex(matrix4f, minX, maxY, minZ).color(red, green, blue, alpha).light(light);
        vertexConsumers.vertex(matrix4f, minX, maxY, minZ).color(red, green, blue, alpha).light(light);
        vertexConsumers.vertex(matrix4f, minX, maxY, maxZ).color(red, green, blue, alpha).light(light);
        vertexConsumers.vertex(matrix4f, maxX, maxY, minZ).color(red, green, blue, alpha).light(light);
        vertexConsumers.vertex(matrix4f, maxX, maxY, maxZ).color(red, green, blue, alpha).light(light);
        vertexConsumers.vertex(matrix4f, maxX, maxY, maxZ).color(red, green, blue, alpha).light(light);
        vertexConsumers.vertex(matrix4f, maxX, maxY, maxZ).color(red, green, blue, alpha).light(light);
    }

    public static int getLight(Vec3d pos, int minLight) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientWorld world = client.world;
        BlockPos blockPos = BlockPos.ofFloored(pos);
        int blockLight = world.getLightLevel(LightType.BLOCK, blockPos);
        int skyLevel = world.getLightLevel(LightType.SKY, blockPos);
        return LightmapTextureManager.pack(Math.max(blockLight, minLight), skyLevel);
    }

}
