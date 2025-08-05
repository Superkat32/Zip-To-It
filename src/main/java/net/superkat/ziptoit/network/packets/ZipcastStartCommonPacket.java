package net.superkat.ziptoit.network.packets;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.superkat.ziptoit.ZipToIt;
import net.superkat.ziptoit.zipcast.ZipcastTarget;

public record ZipcastStartCommonPacket(ZipcastTarget zipcastTarget) implements CustomPayload {
    public static final Identifier ZIPCAST_START_ID = Identifier.of(ZipToIt.MOD_ID, "zipcast_start");
    public static final CustomPayload.Id<ZipcastStartCommonPacket> ID = new CustomPayload.Id<>(ZIPCAST_START_ID);
    public static final PacketCodec<RegistryByteBuf, ZipcastStartCommonPacket> CODEC = PacketCodec.tuple(
            ZipcastTarget.PACKET_CODEC, ZipcastStartCommonPacket::zipcastTarget,
            ZipcastStartCommonPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
