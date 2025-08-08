package net.superkat.ziptoit.zipcast;

import net.minecraft.entity.LivingEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public record ZipcastTarget(int playerId, Vec3d pos, Direction raycastSide, float speed, int startTicks, int lerpTicks, int buildupTicks) {

    public static final PacketCodec<RegistryByteBuf, ZipcastTarget> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, ZipcastTarget::playerId,
            Vec3d.PACKET_CODEC, ZipcastTarget::pos,
            Direction.PACKET_CODEC, ZipcastTarget::raycastSide,
            PacketCodecs.FLOAT, ZipcastTarget::speed,
            PacketCodecs.INTEGER, ZipcastTarget::startTicks,
            PacketCodecs.INTEGER, ZipcastTarget::lerpTicks,
            PacketCodecs.INTEGER, ZipcastTarget::buildupTicks,
            ZipcastTarget::new
    );

    public static ZipcastTarget ofRaycast(LivingEntity player, BlockHitResult raycast) {
        return ofPlayer(player, raycast.getPos(), raycast.getSide());
    }

    public static ZipcastTarget ofPlayer(LivingEntity player, Vec3d pos, Direction raycastSide) {
        float speed = 2.25f;
        double distanceToPos = player.getPos().distanceTo(pos);
        double velocitySquared = player.getVelocity().lengthSquared();

        int startTicks = (int) MathHelper.clamp(distanceToPos / 5, 5, 15);
        int minBuildupTicks = Math.max(startTicks, 12);
        int buildUpTicks = (int) MathHelper.clamp(startTicks + (distanceToPos / 10) + (velocitySquared * 5), minBuildupTicks, 22);
        int lerpTicks = MathHelper.clamp(buildUpTicks - 8, 4, 12);
        return new ZipcastTarget(player.getId(), pos, raycastSide, speed, startTicks, lerpTicks, buildUpTicks);
    }

}
