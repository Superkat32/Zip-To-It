package net.superkat.ziptoit.render.zoom;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.superkat.ziptoit.ZipToIt;
import net.superkat.ziptoit.render.ZipToItEntityModelLayers;

public class ZipcastZoomFeatureRenderer extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {
    public static final Identifier TEXTURE = Identifier.of(ZipToIt.MOD_ID, "textures/entity/zipcast_zoom.png");
    private final ZipcastZoomModel model;
    public ZipcastZoomFeatureRenderer(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context, LoadedEntityModels entityModels) {
        super(context);
        this.model = new ZipcastZoomModel(entityModels.getModelPart(ZipToItEntityModelLayers.ZIPCAST_ZOOM));
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, PlayerEntityRenderState state, float limbAngle, float limbDistance) {
//        if(!(state instanceof ZipcasterRenderState zipcasterRenderState)) return;
//        if(!zipcasterRenderState.isZipcasting()) return;
//
//        ZipcastTarget zipcastTarget = zipcasterRenderState.zipcastTarget();
//        int zipcastTicks = zipcasterRenderState.zipcastTicks();
//        int zoomTicks = zipcastTarget.startTicks();
//        int fadeInZoomTicks = 8;
//
//        if(zipcastTicks <= zoomTicks) return;
//
//        int mainColor = zipcastTarget.color().color();
//        float fadeInAmount = 1f;
//        if(zipcastTicks <= (zoomTicks + fadeInZoomTicks)) {
//            fadeInAmount = (float) (zipcastTicks - zoomTicks) / (fadeInZoomTicks);
//        }
//        float scale = 1.5f * fadeInAmount;
//        float yOffset = -0.85f * fadeInAmount;
//
//        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(this.model.getLayer(TEXTURE));
//        matrices.push();
//        matrices.scale(scale, scale, scale);
//        matrices.translate(0, yOffset, 0.05); // move towards front of player
//        this.model.setAngles(state);
//        this.model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, mainColor);
//        matrices.pop();
    }
}
