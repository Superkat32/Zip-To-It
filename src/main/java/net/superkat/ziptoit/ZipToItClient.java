package net.superkat.ziptoit;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.superkat.ziptoit.network.ZipToItClientNetworkHandler;
import net.superkat.ziptoit.particle.ZipToItParticles;
import net.superkat.ziptoit.particle.ZipcastZoomParticle;
import net.superkat.ziptoit.render.ZipToItEntityModelLayers;
import net.superkat.ziptoit.render.ZipToItRenderLayers;
import net.superkat.ziptoit.render.zipcast.ZipcastRenderer;
import net.superkat.ziptoit.render.zoom.ZipcastZoomFeatureRenderer;

public class ZipToItClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ZipToItRenderLayers.init();
        WorldRenderEvents.AFTER_TRANSLUCENT.register(ZipcastRenderer::render);

        ZipToItEntityModelLayers.init();
        ZipToItClientNetworkHandler.init();

        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
            if(!(entityRenderer instanceof PlayerEntityRenderer playerEntityRenderer)) return;

            registrationHelper.register(new ZipcastZoomFeatureRenderer(playerEntityRenderer, context.getEntityModels()));
        });

        ParticleFactoryRegistry.getInstance().register(ZipToItParticles.ZIPCAST_ZOOM, ZipcastZoomParticle.Factory::new);
    }
}
