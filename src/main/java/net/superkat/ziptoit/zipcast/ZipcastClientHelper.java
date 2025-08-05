package net.superkat.ziptoit.zipcast;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.packet.CustomPayload;

public class ZipcastClientHelper {

    public static void sendC2SPacket(CustomPayload customPayload) {
        ClientPlayNetworking.send(customPayload);
    }

}
