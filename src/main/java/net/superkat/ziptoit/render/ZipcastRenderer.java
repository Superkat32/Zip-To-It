package net.superkat.ziptoit.render;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.superkat.ziptoit.duck.ZipcasterPlayer;
import net.superkat.ziptoit.zipcast.ZipcastTarget;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class ZipcastRenderer {

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

        VertexConsumerProvider consumer = context.consumers();
        MatrixStack matrices = context.matrixStack();
        Camera camera = context.camera();
        float tickProgress = context.tickCounter().getTickProgress(true);
        ZipcastTarget zipcastTarget = zipcasterPlayer.zipcastTarget();

        List<ZipcastPoint> zipcastPoints = getZipcastPoints(player, 8, tickProgress);
        renderZipcastLine(matrices, consumer.getBuffer(ZipToItRenderLayers.RAYCAST_LINE), camera, zipcastPoints, zipcastTarget, tickProgress);
    }

    // This is a slightly modified version of my line renderer for Jet Lag(an unreleased for now:tm: mod)
    // https://github.com/Superkat32/Jet-Lag/blob/master/src/main/java/net/superkat/jetlag/rendering/ContrailRenderer.java#L232
    // It's basically just playing a game of connect the dots,
    // taking advantage of VertexFormat.DrawMode.TRIANGLE_STRIP's shared vertices
    public static void renderZipcastLine(MatrixStack matrices, VertexConsumer consumer, Camera camera, List<ZipcastPoint> points, ZipcastTarget zipcastTarget, float tickProgress) {
        int light = LightmapTextureManager.pack(15, 15);
//        Vec3d cameraPos = camera.getPos();
//        boolean debug = true;

        for (int i = 0; i < points.size() - 1; i++) {
            boolean last = i == 0;
            ZipcastPoint zipcastPoint = points.get(i);
            ZipcastPoint targetZipcastPoint = points.get(i + (last ? 0 : 1));

            Vec3d pos = zipcastPoint.pos;
            Vec3d target = targetZipcastPoint.pos;

            renderLineSegment(matrices, consumer, pos, target, 1f, 0f, 0f, 1f, light, 0.2f, zipcastPoint.offset);
//            matrices.push();
//            Vec3d point = zipcastPoint.pos();
//
//            matrices.translate(zipcastPoint.offset(), 0, 0);
//            matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
//            if(debug) {
//                float greenPerPoint = 1f / points.size();
//                float green = greenPerPoint * points.indexOf(zipcastPoint);
//                RaycastRenderer.renderCube(matrices, consumer, point, 0.5f, 0f, green, 1f, 0.8f);
//            }
//
//            matrices.pop();
        }
    }

    private static void renderLineSegment(MatrixStack matrixStack, VertexConsumer consumer, Vec3d origin, Vec3d target, float red, float green, float blue, float alpha, int light, float width, float xOffsetAmount) {
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        Vec3d transformedMatrixPos = origin.subtract(camera.getPos());
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

        matrixStack.translate(xOffsetAmount, 0,0);

        drawTriangle(matrixStack.peek().getPositionMatrix(), consumer, width, -length, red, green, blue, alpha, light);

        matrixStack.pop();
    }

    private static void drawTriangle(Matrix4f matrix, VertexConsumer vertexConsumer, float width, float length, float red, float green, float blue, float alpha, int light) {
        vertexConsumer.vertex(matrix, width / 2f, 0, length)
                .color(red, green, blue, alpha);
        vertexConsumer.vertex(matrix, -width / 2f, 0, length)
                .color(red, green, blue, alpha);
    }

    // FIXME - I believe this goes from "target -> playerPos" instead of "playerPos -> target"
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
        Vec3d difference = playerPos.subtract(targetPos);
        boolean playerIsSelf = player.isMainPlayer();

        if(zipcastTicks <= shootTicks) {
            float targetFallbackAmount = 1f - MathHelper.clamp((float) zipcastTicks / shootTicks, 0f, 1f);
            targetPos = targetPos.add(difference.multiply(targetFallbackAmount));
            difference = playerPos.subtract(targetPos);
        }

        float tickMultiplier = 1f;
        float offsetMultiplier = 0f;
        if(zipcastTicks <= wobbleTicks) {
            offsetMultiplier = (1f - ((float) zipcastTicks / wobbleTicks)) / 2f;
            tickMultiplier = 3;
        } else if (zipcastTicks >= wobbleUntilLandTicks) {
            float distanceToTarget = (float) playerPos.distanceTo(zipcastTargetPos);
            float distanceOffset = 5f / MathHelper.clamp(distanceToTarget, 5f, 100f);
            offsetMultiplier = MathHelper.clamp(distanceOffset, 0f, 0.35f) / 3f;
            tickMultiplier = 2f;
        }

        if(!playerIsSelf) {
            offsetMultiplier *= 2f;
        }

        List<ZipcastPoint> points = new ArrayList<>();
        float subdivisionAmount = 1f / subdivisions;

        for (int i = 0; i <= subdivisions; i++) {
            float subdivideAmount = subdivisionAmount * i;
            Vec3d vec3d = targetPos.add(difference.multiply(subdivideAmount));
            float offset = MathHelper.sin(zipcastTicks * tickMultiplier + i) * offsetMultiplier;
            points.add(new ZipcastPoint(vec3d, offset));
        }

        return points;
    }

    public static record ZipcastPoint(Vec3d pos, float offset) {}

}
