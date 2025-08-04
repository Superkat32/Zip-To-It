package net.superkat.ziptoit.zipcast;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public record ZipcastTarget(int playerId, Vec3d target, Direction raycastSide, float speed) {

    public static final PacketCodec<RegistryByteBuf, ZipcastTarget> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, ZipcastTarget::playerId,
            Vec3d.PACKET_CODEC, ZipcastTarget::target,
            Direction.PACKET_CODEC, ZipcastTarget::raycastSide,
            PacketCodecs.FLOAT, ZipcastTarget::speed,
            ZipcastTarget::new
    );

    public ZipcastTarget(int playerId, BlockHitResult raycast) {
        this(playerId, raycast.getPos(), raycast.getSide());
    }

    public ZipcastTarget(int playerId, Vec3d pos, Direction raycastSide) {
        this(playerId, pos, raycastSide, 5f);
    }

    public void write(RegistryByteBuf buf) {
        PACKET_CODEC.encode(buf, this);
    }

}
