package net.superkat.ziptoit.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.superkat.ziptoit.duck.ZipcasterPlayer;
import net.superkat.ziptoit.network.packets.WallStickCommonPacket;
import net.superkat.ziptoit.network.packets.ZipcastEndCommonPacket;
import net.superkat.ziptoit.network.packets.ZipcastStartCommonPacket;
import net.superkat.ziptoit.zipcast.ZipcastManager;
import net.superkat.ziptoit.zipcast.ZipcastTarget;

public class ZipToItServerNetworkHandler {

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(ZipcastStartCommonPacket.ID, ZipToItServerNetworkHandler::onZipcastStart);
        ServerPlayNetworking.registerGlobalReceiver(ZipcastEndCommonPacket.ID, ZipToItServerNetworkHandler::onZipcastEnd);
        ServerPlayNetworking.registerGlobalReceiver(WallStickCommonPacket.ID, ZipToItServerNetworkHandler::onWallStick);
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

    public static void onWallStick(WallStickCommonPacket payload, ServerPlayNetworking.Context context) {
        ServerWorld world = context.player().getWorld();
        int playerId = payload.playerId();
        Vec3d pos = payload.pos();

        Entity entity = world.getEntityById(playerId);
        if(!(entity instanceof PlayerEntity player) || !(player instanceof ZipcasterPlayer zipcasterPlayer)) return;

        ZipcastManager.stickToWall(player, pos, true);
    }

}
