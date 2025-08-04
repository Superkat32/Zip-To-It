package net.superkat.ziptoit.render;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.superkat.ziptoit.ZipToIt;
import net.superkat.ziptoit.zipcast.ZipcastManager;
import org.joml.Matrix4f;

public class RaycastRenderer {

    public static void render(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if(player == null) return;
        if(!ZipcastManager.playerIsAimingZipcaster(player)) return;

        float tickProgress = context.tickCounter().getTickProgress(true);
        ItemStack stickyHand = player.getActiveItem();
        BlockHitResult raycast = ZipcastManager.raycastStickyHand(player, stickyHand, tickProgress);
        if(raycast == null) return;

        renderRaycast(context, raycast, stickyHand, tickProgress);
    }

    public static void renderRaycast(WorldRenderContext context, BlockHitResult raycast, ItemStack stickyHand, float tickProgress) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        VertexConsumerProvider consumer = context.consumers();
        MatrixStack matrices = context.matrixStack();
        Vec3d cameraPos = context.camera().getPos();
        Vec3d playerPos = player.getLerpedPos(tickProgress);
        Vec3d raycastPos = raycast.getPos();

        // Ever so slightly cursed but its ModFest its fine
        matrices.push();
        boolean firstPerson = client.options.getPerspective().isFirstPerson() && player == MinecraftClient.getInstance().player;
        float offsetAmount = firstPerson ? 1 : 0.05f;
        float xOffset = getArmHoldingStickyHand(player) == Arm.RIGHT ? offsetAmount : -offsetAmount;
        renderSimpleLine(matrices, consumer.getBuffer(ZipToItRenderLayers.RAYCAST_LINE), playerPos.add(0, 0.5, 0), raycastPos, 0.1f, xOffset, 1f, 0f, 0f, 1f);
        matrices.pop();

        matrices.push();
        matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        renderCube(matrices, consumer.getBuffer(ZipToItRenderLayers.TARGET_BOX), raycastPos, 0.75f, 1f, 0f, 0f, 0.8f);
        matrices.pop();
    }

    public static void renderSimpleLine(MatrixStack matrixStack, VertexConsumer consumer, Vec3d origin, Vec3d target, float width, float xOffsetAmount, float red, float green, float blue, float alpha) {
        int light = LightmapTextureManager.pack(15, 15);
        renderLineSegment(matrixStack, consumer, origin, target, red, green, blue, alpha, light, width, 0);
        renderLineSegment(matrixStack, consumer, target, origin, red, green, blue, alpha, light, width, xOffsetAmount);
    }

    // Target & origin may be swapped? Don't know don't care it just works now ¯\_(ツ)_/¯ (that's gonna hurt me later isn't it)
    private static void renderLineSegment(MatrixStack matrixStack, VertexConsumer consumer, Vec3d origin, Vec3d target, float red, float green, float blue, float alpha, int light, float width, float xOffsetAmount) {
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        Vec3d transformedMatrixPos = origin.subtract(camera.getPos());
        matrixStack.push();

        //offsets to the origin's pos
        matrixStack.translate(transformedMatrixPos.x, transformedMatrixPos.y, transformedMatrixPos.z);

        //calculates length
        float length = (float) origin.distanceTo(target);

        //rotates towards target from origin
        Vec3d transformedPos = target.subtract(origin);
        transformedPos = transformedPos.normalize();
        float rightAngle = (float) Math.toRadians(90);
        float n = (float)Math.acos(transformedPos.y);
        float o = (float)Math.atan2(transformedPos.z, transformedPos.x);
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) Math.toDegrees(rightAngle - o))); //rotates left/right
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float) Math.toDegrees(rightAngle + n))); //rotates up/down

        matrixStack.translate(xOffsetAmount, 0,0);

        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((System.currentTimeMillis() % 5000) / 5000f * 360f));
        drawTriangle(matrixStack.peek().getPositionMatrix(), consumer, width, -length, red, green, blue, alpha, light);

        matrixStack.pop();
    }

    private static void drawTriangle(Matrix4f matrix, VertexConsumer vertexConsumer, float width, float length, float red, float green, float blue, float alpha, int light) {
        vertexConsumer.vertex(matrix, width / 2f, 0, length)
                .color(red, green, blue, alpha);
        vertexConsumer.vertex(matrix, -width / 2f, 0, length)
                .color(red, green, blue, alpha);
    }

    public static void renderCube(MatrixStack matrices, VertexConsumer consumer, Vec3d pos, float size, float red, float green, float blue, float alpha) {
        float halfSize = size / 2f;
        VertexRendering.drawFilledBox(
                matrices, consumer,
                pos.getX() - halfSize, pos.getY() - halfSize, pos.getZ() - halfSize,
                pos.getX() + halfSize, pos.getY() + halfSize, pos.getZ() + halfSize,
                red, green, blue, alpha
        );
    }

    private static Arm getArmHoldingStickyHand(PlayerEntity player) {
        return player.getMainHandStack().getItem().getDefaultStack().isOf(ZipToIt.STICKY_HAND_ITEM) ? player.getMainArm() : player.getMainArm().getOpposite();
    }

}
