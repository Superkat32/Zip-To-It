package net.superkat.ziptoit.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.superkat.ziptoit.duck.ZipcasterPlayer;
import net.superkat.ziptoit.network.packets.WallStickEndCommonPacket;
import net.superkat.ziptoit.network.packets.WallStickStartCommonPacket;
import net.superkat.ziptoit.network.packets.ZipcastCancelCommonPacket;
import net.superkat.ziptoit.network.packets.ZipcastEndCommonPacket;
import net.superkat.ziptoit.network.packets.ZipcastStartCommonPacket;
import net.superkat.ziptoit.zipcast.ZipcastManager;
import net.superkat.ziptoit.zipcast.movement.ZipcastTarget;

public class ZipToItServerNetworkHandler {

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(ZipcastStartCommonPacket.ID, ZipToItServerNetworkHandler::onZipcastStart);
        ServerPlayNetworking.registerGlobalReceiver(ZipcastEndCommonPacket.ID, ZipToItServerNetworkHandler::onZipcastEnd);
        ServerPlayNetworking.registerGlobalReceiver(WallStickStartCommonPacket.ID, ZipToItServerNetworkHandler::onWallStickStart);
        ServerPlayNetworking.registerGlobalReceiver(WallStickEndCommonPacket.ID, ZipToItServerNetworkHandler::onWallStickEnd);
        ServerPlayNetworking.registerGlobalReceiver(ZipcastCancelCommonPacket.ID, ZipToItServerNetworkHandler::onZipcastCancel);
    }

    public static void onZipcastStart(ZipcastStartCommonPacket payload, ServerPlayNetworking.Context context) {
        ServerWorld world = context.player().getWorld();
        ZipcastTarget zipcastTarget = payload.zipcastTarget();
        int playerId = zipcastTarget.playerId();

        Entity entity = world.getEntityById(playerId);
        if(!(entity instanceof PlayerEntity player) || !(player instanceof ZipcasterPlayer zipcasterPlayer)) return;

        ZipcastManager.startZipcast(player, zipcastTarget, true);
    }

    public static void onZipcastEnd(ZipcastEndCommonPacket payload, ServerPlayNetworking.Context context) {
        ServerWorld world = context.player().getWorld();
        int playerId = payload.playerId();

        Entity entity = world.getEntityById(playerId);
        if(!(entity instanceof PlayerEntity player) || !(player instanceof ZipcasterPlayer zipcasterPlayer)) return;

        ZipcastManager.endZipcast(player, true);
    }

    public static void onWallStickStart(WallStickStartCommonPacket payload, ServerPlayNetworking.Context context) {
        ServerWorld world = context.player().getWorld();
        int playerId = payload.playerId();
        Vec3d pos = payload.pos();
        BlockPos wallPos = payload.wallPos();

        Entity entity = world.getEntityById(playerId);
        if(!(entity instanceof PlayerEntity player) || !(player instanceof ZipcasterPlayer zipcasterPlayer)) return;

        ZipcastManager.startWallStick(player, pos, wallPos, true);
    }

    public static void onWallStickEnd(WallStickEndCommonPacket payload, ServerPlayNetworking.Context context) {
        ServerWorld world = context.player().getWorld();
        int playerId = payload.playerId();
        boolean jump = payload.jump();

        Entity entity = world.getEntityById(playerId);
        if(!(entity instanceof PlayerEntity player) || !(player instanceof ZipcasterPlayer zipcasterPlayer)) return;

        ZipcastManager.endWallStick(player, jump, true);
    }

    public static void onZipcastCancel(ZipcastCancelCommonPacket payload, ServerPlayNetworking.Context context) {
        ServerWorld world = context.player().getWorld();
        int playerId = payload.playerId();
        boolean hardCancel = payload.hardCancel();

        Entity entity = world.getEntityById(playerId);
        if(!(entity instanceof PlayerEntity player) || !(player instanceof ZipcasterPlayer zipcasterPlayer)) return;

        ZipcastManager.cancelZipcast(player, hardCancel, true);
    }

}
