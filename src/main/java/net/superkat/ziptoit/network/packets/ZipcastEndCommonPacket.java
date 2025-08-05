package net.superkat.ziptoit.network.packets;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.superkat.ziptoit.ZipToIt;

public record ZipcastEndCommonPacket(int playerId, Vec3d teleportPos, boolean cancelled) implements CustomPayload {
    public static final Identifier ZIPCAST_END_ID = Identifier.of(ZipToIt.MOD_ID, "zipcast_end");
    public static final CustomPayload.Id<ZipcastEndCommonPacket> ID = new CustomPayload.Id<>(ZIPCAST_END_ID);
    public static final PacketCodec<RegistryByteBuf, ZipcastEndCommonPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, ZipcastEndCommonPacket::playerId,
            Vec3d.PACKET_CODEC, ZipcastEndCommonPacket::teleportPos,
            PacketCodecs.BOOLEAN, ZipcastEndCommonPacket::cancelled,
            ZipcastEndCommonPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
