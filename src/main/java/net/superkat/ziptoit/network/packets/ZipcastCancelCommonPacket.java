package net.superkat.ziptoit.network.packets;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.superkat.ziptoit.ZipToIt;

public record ZipcastCancelCommonPacket(int playerId, boolean hardCancel) implements CustomPayload {
    public static final Identifier ZIPCAST_CANCEL_ID = Identifier.of(ZipToIt.MOD_ID, "zipcast_cancel");
    public static final CustomPayload.Id<ZipcastCancelCommonPacket> ID = new CustomPayload.Id<>(ZIPCAST_CANCEL_ID);
    public static final PacketCodec<RegistryByteBuf, ZipcastCancelCommonPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, ZipcastCancelCommonPacket::playerId,
            PacketCodecs.BOOLEAN, ZipcastCancelCommonPacket::hardCancel,
            ZipcastCancelCommonPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
