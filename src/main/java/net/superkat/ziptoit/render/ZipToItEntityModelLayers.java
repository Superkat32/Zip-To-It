package net.superkat.ziptoit.render;

import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import net.superkat.ziptoit.ZipToIt;
import net.superkat.ziptoit.render.zoom.ZipcastZoomModel;

public class ZipToItEntityModelLayers {

    public static final EntityModelLayer ZIPCAST_ZOOM = new EntityModelLayer(Identifier.of(ZipToIt.MOD_ID), "zipcast_zoom");

    public static void init() {
        EntityModelLayerRegistry.registerModelLayer(ZIPCAST_ZOOM, ZipcastZoomModel::getTexturedModelData);
    }

}
