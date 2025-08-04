package net.superkat.ziptoit.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.superkat.ziptoit.duck.ZipcasterPlayer;
import net.superkat.ziptoit.network.packets.WallStickS2CPacket;
import net.superkat.ziptoit.network.packets.ZipcastStartCommonPacket;
import net.superkat.ziptoit.zipcast.ZipcastTarget;

public class ZipToItClientNetworkHandler {

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(ZipcastStartCommonPacket.ID, ZipToItClientNetworkHandler::onZipcastUse);
        ClientPlayNetworking.registerGlobalReceiver(WallStickS2CPacket.ID, ZipToItClientNetworkHandler::onWallStick);
    }

    public static void onZipcastUse(ZipcastStartCommonPacket payload, ClientPlayNetworking.Context context) {
        ClientWorld world = context.player().clientWorld;
        ZipcastTarget zipcastTarget = payload.zipcastTarget();
        int playerId = zipcastTarget.playerId();

        Entity entity = world.getEntityById(playerId);
        if(!(entity instanceof PlayerEntity player) || !(player instanceof ZipcasterPlayer zipcasterPlayer)) return;

        zipcasterPlayer.ziptoit$zipcastToPos(zipcastTarget);
    }

    public static void onWallStick(WallStickS2CPacket payload, ClientPlayNetworking.Context context) {
        ClientWorld world = context.player().clientWorld;
        int playerId = payload.playerId();

        Entity entity = world.getEntityById(playerId);
        if(!(entity instanceof PlayerEntity player) || !(player instanceof ZipcasterPlayer zipcasterPlayer)) return;

        zipcasterPlayer.setIsZipcasting(false);
        zipcasterPlayer.setIsStickingToWall(true);
    }

}
