package net.superkat.ziptoit.network.packets;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.superkat.ziptoit.ZipToIt;

public record WallStickEndCommonPacket(int playerId, boolean jump) implements CustomPayload {
    public static final Identifier WALL_STICK_END_ID = Identifier.of(ZipToIt.MOD_ID, "wall_stick_end");
    public static final CustomPayload.Id<WallStickEndCommonPacket> ID = new CustomPayload.Id<>(WALL_STICK_END_ID);
    public static final PacketCodec<RegistryByteBuf, WallStickEndCommonPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, WallStickEndCommonPacket::playerId,
            PacketCodecs.BOOLEAN, WallStickEndCommonPacket::jump,
            WallStickEndCommonPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
