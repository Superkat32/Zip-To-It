package net.superkat.ziptoit.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.superkat.ziptoit.ZipToIt;

public class ZipToItRenderLayers {

    public static final RenderPipeline RAYCAST_LINE_PIPELINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET)
                    .withLocation(Identifier.of(ZipToIt.MOD_ID, "pipeline/zipcast_raycast_line"))
//                    .withVertexShader("core/rendertype_leash")
//                    .withFragmentShader("core/rendertype_leash")
//                    .withSampler("Sampler2")
                    .withCull(false)
                    .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.TRIANGLE_STRIP) // TRIANGLE STRIP VERY IMPORTANT!
                    .build()
    );

    public static final RenderLayer.MultiPhase RAYCAST_LINE = RenderLayer.of(
            "zipcast_raycast_line",
            1536,
            RAYCAST_LINE_PIPELINE,
            RenderLayer.MultiPhaseParameters.builder().texture(RenderPhase.NO_TEXTURE).build(false)
    );

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

    // Don't know if this is needed but just in case
    public static void init() {
        // NO OP
    }
}
