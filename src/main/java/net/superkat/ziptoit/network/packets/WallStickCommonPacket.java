package net.superkat.ziptoit.network.packets;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.superkat.ziptoit.ZipToIt;

public record WallStickCommonPacket(int playerId, Vec3d pos) implements CustomPayload {
    public static final Identifier WALL_STICK_ID = Identifier.of(ZipToIt.MOD_ID, "wall_stick");
    public static final CustomPayload.Id<WallStickCommonPacket> ID = new CustomPayload.Id<>(WALL_STICK_ID);
    public static final PacketCodec<RegistryByteBuf, WallStickCommonPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, WallStickCommonPacket::playerId,
            Vec3d.PACKET_CODEC, WallStickCommonPacket::pos,
            WallStickCommonPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
