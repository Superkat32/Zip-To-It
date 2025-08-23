package net.superkat.ziptoit.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.superkat.ziptoit.duck.ZipcasterPlayer;
import net.superkat.ziptoit.network.packets.AllowZipcastingZipcastS2CPacket;
import net.superkat.ziptoit.network.packets.WallStickEndCommonPacket;
import net.superkat.ziptoit.network.packets.WallStickStartCommonPacket;
import net.superkat.ziptoit.network.packets.ZipcastCancelCommonPacket;
import net.superkat.ziptoit.network.packets.ZipcastEndCommonPacket;
import net.superkat.ziptoit.network.packets.ZipcastStartCommonPacket;
import net.superkat.ziptoit.zipcast.ZipcastManager;
import net.superkat.ziptoit.zipcast.movement.ZipcastTarget;

public class ZipToItClientNetworkHandler {

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(ZipcastStartCommonPacket.ID, ZipToItClientNetworkHandler::onZipcastStart);
        ClientPlayNetworking.registerGlobalReceiver(ZipcastEndCommonPacket.ID, ZipToItClientNetworkHandler::onZipcastEnd);
        ClientPlayNetworking.registerGlobalReceiver(WallStickStartCommonPacket.ID, ZipToItClientNetworkHandler::onWallStickStart);
        ClientPlayNetworking.registerGlobalReceiver(WallStickEndCommonPacket.ID, ZipToItClientNetworkHandler::onWallStickEnd);
        ClientPlayNetworking.registerGlobalReceiver(ZipcastCancelCommonPacket.ID, ZipToItClientNetworkHandler::onZipcastCancel);

        ClientPlayNetworking.registerGlobalReceiver(AllowZipcastingZipcastS2CPacket.ID, ZipToItClientNetworkHandler::onZipcastDuringZipcastGameruleChange);
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

    public static void onWallStickStart(WallStickStartCommonPacket payload, ClientPlayNetworking.Context context) {
        ClientWorld world = context.player().clientWorld;
        int playerId = payload.playerId();
        Vec3d pos = payload.pos();
        BlockPos wallPos = payload.wallPos();

        Entity entity = world.getEntityById(playerId);
        if(!(entity instanceof PlayerEntity player) || !(player instanceof ZipcasterPlayer zipcasterPlayer)) return;

        ZipcastManager.startWallStick(player, pos, wallPos, false);
    }

    public static void onWallStickEnd(WallStickEndCommonPacket payload, ClientPlayNetworking.Context context) {
        ClientWorld world = context.player().clientWorld;
        int playerId = payload.playerId();

        Entity entity = world.getEntityById(playerId);
        if(!(entity instanceof PlayerEntity player) || !(player instanceof ZipcasterPlayer zipcasterPlayer)) return;

        ZipcastManager.endWallStick(player, false, false);
    }

    public static void onZipcastCancel(ZipcastCancelCommonPacket payload, ClientPlayNetworking.Context context) {
        ClientWorld world = context.player().clientWorld;
        int playerId = payload.playerId();
        boolean hardCancel = payload.hardCancel();

        Entity entity = world.getEntityById(playerId);
        if(!(entity instanceof PlayerEntity player) || !(player instanceof ZipcasterPlayer zipcasterPlayer)) return;

        ZipcastManager.cancelZipcast(player, hardCancel, false);
    }

    public static void onZipcastDuringZipcastGameruleChange(AllowZipcastingZipcastS2CPacket payload, ClientPlayNetworking.Context context) {
        ClientPlayerEntity player = context.player();
        boolean allow = payload.allow();
        if(!(player instanceof ZipcasterPlayer zipcasterPlayer)) return;

        zipcasterPlayer.setAllowZipcastDuringZipcast(allow);
    }

}
