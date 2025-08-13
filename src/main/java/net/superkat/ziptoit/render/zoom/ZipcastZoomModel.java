package net.superkat.ziptoit.render.zoom;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;

public class ZipcastZoomModel extends EntityModel<PlayerEntityRenderState> {
//    private final ModelPart[] parts = new ModelPart[2];
//    public ZipcastZoomModel(ModelPart root) {
//        super(root);
//
//        for (int i = 0; i < 2; i++) {
//            this.parts[i] = root.getChild(getPartName(i));
//        }
//    }

    private final ModelPart main;
    private final ModelPart edges;
    private final ModelPart north;
    private final ModelPart east;
    private final ModelPart south;
    private final ModelPart west;
    public ZipcastZoomModel(ModelPart root) {
        super(root);

        this.main = root.getChild("main");
        this.edges = this.main.getChild("edges");
        this.north = this.edges.getChild("north");
        this.east = this.edges.getChild("east");
        this.south = this.edges.getChild("south");
        this.west = this.edges.getChild("west");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData main = modelPartData.addChild("main", ModelPartBuilder.create().uv(0, 25).cuboid(-4.0F, -16.0F, -4.0F, 8.0F, 0.0F, 8.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 24.0F, 0.0F));

        ModelPartData edges = main.addChild("edges", ModelPartBuilder.create(), ModelTransform.origin(0.0F, -14.0F, -3.0F));

        ModelPartData north = edges.addChild("north", ModelPartBuilder.create().uv(30, 10).cuboid(-5.0F, 0.0F, 0.0F, 10.0F, 15.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.0F, -1.0F, -0.2618F, 0.0F, 0.0F));

        ModelPartData east = edges.addChild("east", ModelPartBuilder.create().uv(30, 0).cuboid(0.0F, 0.0F, -5.0F, 0.0F, 15.0F, 10.0F, new Dilation(0.0F)), ModelTransform.of(-4.0F, -2.0F, 3.0F, 0.0F, 0.0F, 0.2618F));

        ModelPartData south = edges.addChild("south", ModelPartBuilder.create().uv(20, 10).cuboid(-5.0F, 0.0F, 0.0F, 10.0F, 15.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.0F, 7.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData west = edges.addChild("west", ModelPartBuilder.create().uv(20, 0).cuboid(0.0F, 0.0F, -5.0F, 0.0F, 15.0F, 10.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, -2.0F, 3.0F, 0.0F, 0.0F, -0.2618F));
        return TexturedModelData.of(modelData, 64, 64);
    }

//    private static String getPartName(int index) {
//        return "box" + index;
//    }

//    public static TexturedModelData getTexturedModelData() {
//        ModelData modelData = new ModelData();
//        ModelPartData modelPartData = modelData.getRoot();
//
//        for (int i = 0; i < 2; i++) {
//            float f = -3.2F + 9.6F * (i + 1);
//            float g = 0.75F * (i + 1);
//            modelPartData.addChild(
//                    getPartName(i),
//                    ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -16.0F + f, -8.0F, 16.0F, 32.0F, 16.0F),
//                    ModelTransform.NONE.withScale(g)
//            );
//        }
//
//        return TexturedModelData.of(modelData, 64, 64);
//    }


    public void setAngles(PlayerEntityRenderState playerEntityRenderState) {
        super.setAngles(playerEntityRenderState);

//        for (int i = 0; i < this.parts.length; i++) {
//            float f = playerEntityRenderState.age * -(10 + (i + 1));
//            this.parts[i].yaw = MathHelper.wrapDegrees(f) * (float) (Math.PI / 180.0);
//        }
    }
}
