package net.superkat.ziptoit.network;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.superkat.ziptoit.duck.ZipcasterPlayer;
import net.superkat.ziptoit.network.packets.ZipcastStartCommonPacket;
import net.superkat.ziptoit.zipcast.ZipcastTarget;

public class ZipToItServerNetworkHandler {

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(ZipcastStartCommonPacket.ID, ZipToItServerNetworkHandler::onZipcastStart);
    }

    public static void onZipcastStart(ZipcastStartCommonPacket payload, ServerPlayNetworking.Context context) {
        ServerWorld world = context.player().getWorld();
        ZipcastTarget zipcastTarget = payload.zipcastTarget();
        int playerId = zipcastTarget.playerId();

        Entity entity = world.getEntityById(playerId);
        if(!(entity instanceof PlayerEntity player) || !(player instanceof ZipcasterPlayer zipcasterPlayer)) return;

        zipcasterPlayer.ziptoit$zipcastToPos(zipcastTarget);
        for (ServerPlayerEntity trackingPlayer : PlayerLookup.tracking(entity)) {
            if (trackingPlayer == entity) continue;
            ServerPlayNetworking.send(trackingPlayer, new ZipcastStartCommonPacket(zipcastTarget));
        }
    }

}
