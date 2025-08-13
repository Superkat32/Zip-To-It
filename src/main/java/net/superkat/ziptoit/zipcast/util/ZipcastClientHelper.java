package net.superkat.ziptoit.zipcast.util;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.Vec3d;

public class ZipcastClientHelper {

    public static boolean mainPlayerFirstPerson(PlayerEntity player) {
        return player.isMainPlayer() && MinecraftClient.getInstance().options.getPerspective().isFirstPerson();
    }

    public static double clientPlayerDistanceToPos(Vec3d pos) {
        return MinecraftClient.getInstance().player.getPos().distanceTo(pos);
    }

    public static void sendC2SPacket(CustomPayload customPayload) {
        ClientPlayNetworking.send(customPayload);
    }

}
