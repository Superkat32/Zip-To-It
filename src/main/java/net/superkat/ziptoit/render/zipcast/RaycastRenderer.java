package net.superkat.ziptoit.render.zipcast;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.superkat.ziptoit.item.StickyHandComponent;
import net.superkat.ziptoit.item.ZipToItItems;
import net.superkat.ziptoit.render.ZipToItRenderLayers;
import net.superkat.ziptoit.zipcast.ZipcastManager;
import net.superkat.ziptoit.zipcast.color.ZipcastColor;

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
        ZipcastColor zipcastColor = stickyHand.getComponents().getOrDefault(ZipToItItems.STICKY_HAND_COMPONENT_TYPE, StickyHandComponent.DEFAULT).zipcastColor();
        Vec3d cameraPos = context.camera().getPos();
        Vec3d playerPos = player.getLerpedPos(tickProgress);
        Vec3d raycastPos = raycast.getPos();

        int previewColor = zipcastColor.previewColor();

        // Ever so slightly cursed but its ModFest its fine
        matrices.push();
        boolean firstPerson = client.options.getPerspective().isFirstPerson() && player == MinecraftClient.getInstance().player;
        float offsetAmount = firstPerson ? 1 : 0.05f;
        float xOffset = getArmHoldingStickyHand(player) == Arm.RIGHT ? offsetAmount : -offsetAmount;
        float yOffset = firstPerson ? 0.5f : 1f;
        renderSimpleLine(matrices, consumer.getBuffer(ZipToItRenderLayers.RAYCAST_LINE), playerPos.add(0, yOffset, 0), raycastPos, previewColor, 0.1f, xOffset);
        matrices.pop();

        matrices.push();
        matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        ZipcastRenderer.drawCube(matrices, consumer.getBuffer(ZipToItRenderLayers.RAYCAST_TARGET_BOX), raycastPos, 0.75f, previewColor, ZipcastRenderer.MAX_LIGHT, 0.7f);
//        renderCube(matrices, consumer.getBuffer(ZipToItRenderLayers.RAYCAST_TARGET_BOX), raycastPos, 0.75f, previewColor, 0.7f);
        matrices.pop();
    }

    // This is a slightly modified version of my line renderer for Jet Lag(an unreleased for now:tm: mod)
    // https://github.com/Superkat32/Jet-Lag/blob/master/src/main/java/net/superkat/jetlag/rendering/ContrailRenderer.java#L232
    public static void renderSimpleLine(MatrixStack matrixStack, VertexConsumer consumer, Vec3d origin, Vec3d target, int color, float width, float xOffset) {
        // Normally, there'd be more than 2 points in this list, but because there's only two, this was my best workaround
        float rotation = ((MinecraftClient.getInstance().player.getItemUseTime() + 10) % 100f) / 100f * 360f;
        ZipcastRenderer.renderLineSegment(matrixStack, consumer, origin, target, color, ZipcastRenderer.MAX_LIGHT, width, 0, rotation);
        ZipcastRenderer.renderLineSegment(matrixStack, consumer, target, origin, color, ZipcastRenderer.MAX_LIGHT, width, xOffset, rotation);
    }

    private static Arm getArmHoldingStickyHand(PlayerEntity player) {
        return player.getMainHandStack().getItem().getDefaultStack().isIn(ZipToItItems.STICKY_HANDS) ? player.getMainArm() : player.getMainArm().getOpposite();
    }

}
