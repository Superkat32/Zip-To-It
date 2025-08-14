package net.superkat.ziptoit.network.packets;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.superkat.ziptoit.ZipToIt;

public record AllowZipcastingZipcastS2CPacket(boolean allow) implements CustomPayload {
    public static final Identifier ALLOW_ZIPCAST_DURING_ZIPCAST_ID = Identifier.of(ZipToIt.MOD_ID, "allow_zipcast_during_zipcast_payload");
    public static final CustomPayload.Id<AllowZipcastingZipcastS2CPacket> ID = new CustomPayload.Id<>(ALLOW_ZIPCAST_DURING_ZIPCAST_ID);
    public static final PacketCodec<RegistryByteBuf, AllowZipcastingZipcastS2CPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.BOOLEAN, AllowZipcastingZipcastS2CPacket::allow,
            AllowZipcastingZipcastS2CPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
