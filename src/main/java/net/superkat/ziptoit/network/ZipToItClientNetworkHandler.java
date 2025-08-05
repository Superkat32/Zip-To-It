package net.superkat.ziptoit.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.superkat.ziptoit.duck.ZipcasterPlayer;
import net.superkat.ziptoit.network.packets.WallStickCommonPacket;
import net.superkat.ziptoit.network.packets.ZipcastEndCommonPacket;
import net.superkat.ziptoit.network.packets.ZipcastStartCommonPacket;
import net.superkat.ziptoit.zipcast.ZipcastManager;
import net.superkat.ziptoit.zipcast.ZipcastTarget;

public class ZipToItClientNetworkHandler {

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(ZipcastStartCommonPacket.ID, ZipToItClientNetworkHandler::onZipcastStart);
        ClientPlayNetworking.registerGlobalReceiver(ZipcastEndCommonPacket.ID, ZipToItClientNetworkHandler::onZipcastEnd);
        ClientPlayNetworking.registerGlobalReceiver(WallStickCommonPacket.ID, ZipToItClientNetworkHandler::onWallStick);
    }

    public static void onZipcastStart(ZipcastStartCommonPacket payload, ClientPlayNetworking.Context context) {
        ClientWorld world = context.player().clientWorld;
        ZipcastTarget zipcastTarget = payload.zipcastTarget();
        int playerId = zipcastTarget.playerId();

        Entity entity = world.getEntityById(playerId);
        if(!(entity instanceof PlayerEntity player) || !(player instanceof ZipcasterPlayer zipcasterPlayer)) return;

        ZipcastManager.startZipcast(player, zipcastTarget, false);
    }

    public static void onZipcastEnd(ZipcastEndCommonPacket payload, ClientPlayNetworking.Context context) {
        ClientWorld world = context.player().clientWorld;
        int playerId = payload.playerId();

        Entity entity = world.getEntityById(playerId);
        if(!(entity instanceof PlayerEntity player) || !(player instanceof ZipcasterPlayer zipcasterPlayer)) return;

        ZipcastManager.endZipcast(player, false);
    }

    public static void onWallStick(WallStickCommonPacket payload, ClientPlayNetworking.Context context) {
        ClientWorld world = context.player().clientWorld;
        int playerId = payload.playerId();
        Vec3d pos = payload.pos();

        Entity entity = world.getEntityById(playerId);
        if(!(entity instanceof PlayerEntity player) || !(player instanceof ZipcasterPlayer zipcasterPlayer)) return;

        ZipcastManager.stickToWall(player, pos, false);
    }


}
