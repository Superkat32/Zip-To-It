package net.superkat.ziptoit;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.superkat.ziptoit.render.ZipcastRenderer;

public class ZipToItClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        WorldRenderEvents.AFTER_TRANSLUCENT.register(ZipcastRenderer::render);
    }
}
