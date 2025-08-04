package net.superkat.ziptoit.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.superkat.ziptoit.network.packets.WallStickS2CPacket;
import net.superkat.ziptoit.network.packets.ZipcastStartCommonPacket;

public class ZipToItPackets {

    public static void init() {
        PayloadTypeRegistry.playS2C().register(ZipcastStartCommonPacket.ID, ZipcastStartCommonPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(ZipcastStartCommonPacket.ID, ZipcastStartCommonPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(WallStickS2CPacket.ID, WallStickS2CPacket.CODEC);
    }

}
