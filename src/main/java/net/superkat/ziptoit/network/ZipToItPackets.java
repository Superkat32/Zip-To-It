package net.superkat.ziptoit.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.superkat.ziptoit.network.packets.WallStickCommonPacket;
import net.superkat.ziptoit.network.packets.ZipcastEndCommonPacket;
import net.superkat.ziptoit.network.packets.ZipcastStartCommonPacket;

public class ZipToItPackets {

    public static void init() {
        PayloadTypeRegistry.playS2C().register(ZipcastStartCommonPacket.ID, ZipcastStartCommonPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(ZipcastStartCommonPacket.ID, ZipcastStartCommonPacket.CODEC);

        PayloadTypeRegistry.playS2C().register(WallStickCommonPacket.ID, WallStickCommonPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(WallStickCommonPacket.ID, WallStickCommonPacket.CODEC);

        PayloadTypeRegistry.playS2C().register(ZipcastEndCommonPacket.ID, ZipcastEndCommonPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(ZipcastEndCommonPacket.ID, ZipcastEndCommonPacket.CODEC);
    }

}
