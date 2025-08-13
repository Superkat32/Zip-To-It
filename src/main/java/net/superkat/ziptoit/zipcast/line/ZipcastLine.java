package net.superkat.ziptoit.zipcast.line;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.superkat.ziptoit.duck.ZipcasterPlayer;
import net.superkat.ziptoit.render.zipcast.ZipcastRenderer;
import net.superkat.ziptoit.zipcast.color.ZipcastColor;
import net.superkat.ziptoit.zipcast.movement.ZipcastTarget;

import java.util.ArrayList;
import java.util.List;

// Basically just a ZipcasterPlayerRenderState, updated every tick instead of setup every render frame
// *I think RenderStates are setup every render frame*
public class ZipcastLine {
    public final PlayerEntity player;
    public final List<ZipcastPoint> points = new ArrayList<>();
    public final int subdivisions;

    public ZipcastLine(PlayerEntity player, int subdivisions) {
        this.player = player;
        this.subdivisions = subdivisions;
    }

    public void tickPoints() {
        if(!(player instanceof ZipcasterPlayer zipcasterPlayer)) return;

        int zipcastTicks = zipcasterPlayer.zipcastTicks();
        ZipcastTarget zipcastTarget = zipcasterPlayer.zipcastTarget();
        ZipcastColor zipcastColor = zipcastTarget.color();

        int startTicks = zipcastTarget.startTicks();
        int shootTicks = startTicks - 2;
        int wobbleTicks = startTicks + 5;
        int lerpTicks = zipcastTarget.lerpTicks();
        int wobbleUntilLandTicks = lerpTicks + 8;
        int buildUpTicks = zipcastTarget.buildupTicks();

        int mainColor = zipcastColor.color();
        int altColor = zipcastColor.altColor();
        int brightColor = zipcastColor.brightColor();
        boolean brighten = false;
        float brightenLerp = 0f;
        int minLight = 3;

        boolean playerIsSelf = player.isMainPlayer();
        boolean firstPerson = playerIsSelf && (MinecraftClient.getInstance().options.getPerspective().isFirstPerson());

        // Get player position, but also modify it for any adjustments for easier viewing experiences (e.g. in first person)
        Vec3d handOffset = player.getHandPosOffset(player.getActiveItem().getItem()).multiply(firstPerson ? 0.5f : 1);
        Vec3d playerPos = player.getPos().add(0, firstPerson ? 0 : 0.5, 0).subtract(handOffset);
        Vec3d zipcastTargetPos = zipcastTarget.pos();
        Vec3d targetPos = zipcastTargetPos;
        Vec3d difference = targetPos.subtract(playerPos);

        // Shoot animation (simply bringing back the targetPos some)
        if(zipcastTicks <= shootTicks) {
            float targetFallbackAmount = MathHelper.clamp((float) zipcastTicks / shootTicks, 0f, 1f);
            targetPos = playerPos.add(difference.multiply(targetFallbackAmount));
            difference = targetPos.subtract(playerPos);
        }

        // Wobble effects
        float tickMultiplier = 1f;
        float offsetMultiplier = 0f;
        if(zipcastTicks <= wobbleTicks) {
            // Huge wobble during shoot
            offsetMultiplier = (1f - ((float) zipcastTicks / wobbleTicks)) / 2f;
            tickMultiplier = 3;
            brighten = true;
            brightenLerp = (1f - (float) zipcastTicks / wobbleTicks);
            minLight = (int) (((15 - minLight) * brightenLerp) + minLight);
        } else if (zipcastTicks >= wobbleUntilLandTicks) {
            // Small wobble during actual zipcast
            float distanceToTarget = (float) playerPos.distanceTo(zipcastTargetPos);
            float distanceOffset = 5f / MathHelper.clamp(distanceToTarget, 5f, 100f);
            offsetMultiplier = MathHelper.clamp(distanceOffset, 0f, 0.35f) / 3f;
            tickMultiplier = 2f;
        }

        if(!playerIsSelf) {
            // Exaggerate effect for other players
            offsetMultiplier *= 2f;
        }

        boolean createPoints = this.points.isEmpty();

        float subdivisionAmount = 1f / subdivisions;
        // Always ensure the point at the player is rendered correctly
        Vec3d firstPoint = playerPos.add(difference.multiply(-0.05f));
        int firstLight = ZipcastRenderer.getLight(firstPoint, minLight);
        if(createPoints) {
            this.points.add(new ZipcastPoint(firstPoint, 0, altColor, firstLight));
        }
        this.points.getFirst().update(firstPoint, 0, altColor, firstLight);

        // Subdivide the line into many different points, then apply the wobble offset to each point
        // These points will then be rendered in one connected line, so they must be in order!
        int lerpPoints = subdivisions - (subdivisions / 2);
        float colorLerpAmount = 1f / lerpPoints;
        for (int i = 0; i <= subdivisions; i++) {
            float subdivideAmount = subdivisionAmount * i;
            Vec3d vec3d = playerPos.add(difference.multiply(subdivideAmount));
            float offset = MathHelper.sin(zipcastTicks * tickMultiplier + i) * offsetMultiplier;

            float colorLerp = 1f;
            if(i <= lerpPoints) {
                colorLerp = i * colorLerpAmount;
            }
            int color = ColorHelper.lerp(colorLerp, altColor, mainColor);

            if(brighten) {
                color = ColorHelper.lerp(brightenLerp, color, brightColor);
            }

            int light = ZipcastRenderer.getLight(vec3d, minLight);
            if(createPoints) {
                this.points.add(new ZipcastPoint(vec3d, offset, color, light));
            }
            this.points.get(i + 1).update(vec3d, offset, color, light);
        }

    }

    public static final class ZipcastPoint {
        private Vec3d pos;
        private Vec3d prevPos;
        private float offset;
        private float prevOffset;
        private int color;
        private int prevColor;
        private int light;

        public ZipcastPoint(Vec3d pos, Vec3d prevPos, float offset, float prevOffset, int color, int prevColor, int light) {
            this.pos = pos;
            this.prevPos = prevPos;
            this.offset = offset;
            this.prevOffset = prevOffset;
            this.color = color;
            this.prevColor = prevColor;
        }

        public ZipcastPoint(Vec3d pos, float offset, int color, int light) {
            this(pos, pos, offset, offset, color, color, light);
        }

        public void update(Vec3d pos, float offset, int color, int light) {
            this.prevPos = this.pos;
            this.prevOffset = this.offset;
            this.prevColor = this.color;
            this.pos = pos;
            this.offset = offset;
            this.color = color;
            this.light = light;
        }

        public void updatePosFromLerpedPlayerPos(Vec3d pos) {
//            this.prevPos = this.pos;
            this.pos = pos;
            this.prevPos = pos;
        }

        public Vec3d lerpPos(float tickProgress) {
            return prevPos.lerp(pos, tickProgress);
        }

        public float lerpOffset(float tickProgress) {
            return MathHelper.lerp(tickProgress, prevOffset, offset);
        }

        public int lerpColor(float tickProgress) {
            return ColorHelper.lerp(tickProgress, prevColor, color);
        }

        public int getLight() {
            return this.light;
        }
    }

}
