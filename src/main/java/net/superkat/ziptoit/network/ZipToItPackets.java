package net.superkat.ziptoit.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.superkat.ziptoit.network.packets.AllowZipcastingZipcastS2CPacket;
import net.superkat.ziptoit.network.packets.WallStickEndCommonPacket;
import net.superkat.ziptoit.network.packets.WallStickStartCommonPacket;
import net.superkat.ziptoit.network.packets.ZipcastCancelCommonPacket;
import net.superkat.ziptoit.network.packets.ZipcastEndCommonPacket;
import net.superkat.ziptoit.network.packets.ZipcastStartCommonPacket;

public class ZipToItPackets {

    public static void init() {
        PayloadTypeRegistry.playS2C().register(ZipcastStartCommonPacket.ID, ZipcastStartCommonPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(ZipcastStartCommonPacket.ID, ZipcastStartCommonPacket.CODEC);

        PayloadTypeRegistry.playS2C().register(WallStickStartCommonPacket.ID, WallStickStartCommonPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(WallStickStartCommonPacket.ID, WallStickStartCommonPacket.CODEC);

        PayloadTypeRegistry.playS2C().register(ZipcastEndCommonPacket.ID, ZipcastEndCommonPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(ZipcastEndCommonPacket.ID, ZipcastEndCommonPacket.CODEC);

        PayloadTypeRegistry.playS2C().register(WallStickEndCommonPacket.ID, WallStickEndCommonPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(WallStickEndCommonPacket.ID, WallStickEndCommonPacket.CODEC);

        PayloadTypeRegistry.playS2C().register(ZipcastCancelCommonPacket.ID, ZipcastCancelCommonPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(ZipcastCancelCommonPacket.ID, ZipcastCancelCommonPacket.CODEC);

        PayloadTypeRegistry.playS2C().register(AllowZipcastingZipcastS2CPacket.ID, AllowZipcastingZipcastS2CPacket.CODEC);
    }

}
