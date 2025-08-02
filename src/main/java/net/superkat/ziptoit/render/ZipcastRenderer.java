package net.superkat.ziptoit.render;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;

public class ZipcastRenderer {

    public static void render(WorldRenderContext context) {
        RaycastRenderer.render(context);
    }

}
