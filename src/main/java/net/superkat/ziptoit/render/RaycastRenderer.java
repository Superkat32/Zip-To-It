package net.superkat.ziptoit.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.superkat.ziptoit.ZipHelper;
import net.superkat.ziptoit.ZipToIt;
import net.superkat.ziptoit.item.StickyHandComponent;
import org.jetbrains.annotations.Nullable;

public class RaycastRenderer {

    public static final RenderPipeline TARGET_BOX_PIPELINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET)
                    .withLocation(Identifier.of(ZipToIt.MOD_ID, "pipeline/zipcast_target_box"))
                    .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.TRIANGLE_STRIP)
                    .build()
    );

    public static final RenderLayer.MultiPhase TARGET_BOX = RenderLayer.of(
            "zipcast_target_box",
            1536,
            false,
            true,
            TARGET_BOX_PIPELINE,
            RenderLayer.MultiPhaseParameters.builder().layering(RenderPhase.VIEW_OFFSET_Z_LAYERING).build(false)
    );

    public static void render(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if(player == null) return;
        if(!ZipHelper.playerIsAimingZipcaster(player)) return;

        float tickProgress = context.tickCounter().getTickProgress(true);
        ItemStack stickyHand = player.getActiveItem();
        BlockHitResult raycast = raycastStickyHand(player, stickyHand, tickProgress);
        if(raycast == null) return;

        renderRaycast(context, raycast, stickyHand);
    }

    public static void renderRaycast(WorldRenderContext context, BlockHitResult raycast, ItemStack stickyHand) {
        MinecraftClient client = MinecraftClient.getInstance();
        VertexConsumerProvider consumer = context.consumers();
        MatrixStack matrices = context.matrixStack();
        Vec3d cameraPos = context.camera().getPos();
        Vec3d playerPos = client.player.getEyePos();
        Vec3d raycastPos = raycast.getPos();

        matrices.push();
        matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        renderLine(matrices, consumer.getBuffer(TARGET_BOX), playerPos, raycastPos, 1f, 1f, 1f, 1f, 1f);
        renderCube(matrices, consumer.getBuffer(TARGET_BOX), raycastPos, 1f, 1f, 0f, 0f, 1f);

        matrices.pop();
    }

    @Nullable
    public static BlockHitResult raycastStickyHand(ClientPlayerEntity player, ItemStack stickyHand, float tickProgress) {
        StickyHandComponent component = stickyHand.get(ZipToIt.STICKY_HAND_COMPONENT_TYPE);

        int zipRange = 48;
        if(component != null) zipRange = component.zipRange();

        HitResult raycast = player.raycast(zipRange, tickProgress, false);
        if(raycast.getType() == HitResult.Type.BLOCK) {
            return (BlockHitResult) raycast;
        }
        return null;
    }

    public static void renderLine(MatrixStack matrixStack, VertexConsumer consumer, Vec3d origin, Vec3d target, float width, float red, float green, float blue, float alpha) {
        float halfWidth = width / 2f;
//        renderCube(matrixStack, consumer, origin, halfWidth, 0f, 1f, 0f, alpha);
//        renderCube(matrixStack, consumer, target, halfWidth, 0f, 0f, 1f, alpha);
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

}
