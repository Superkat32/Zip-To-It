package net.superkat.ziptoit.network.packets;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.superkat.ziptoit.ZipToIt;

public record WallStickS2CPacket(int playerId) implements CustomPayload {
    public static final Identifier WALL_STICK_ID = Identifier.of(ZipToIt.MOD_ID, "wall_stick");
    public static final CustomPayload.Id<WallStickS2CPacket> ID = new CustomPayload.Id<>(WALL_STICK_ID);
    public static final PacketCodec<RegistryByteBuf, WallStickS2CPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, WallStickS2CPacket::playerId,
            WallStickS2CPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
