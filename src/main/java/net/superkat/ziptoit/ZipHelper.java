package net.superkat.ziptoit;

import net.minecraft.entity.player.PlayerEntity;

public class ZipHelper {

    public static boolean playerIsAimingZipcaster(PlayerEntity player) {
        return player.isUsingItem() && player.getActiveItem().isOf(ZipToIt.STICKY_HAND_ITEM);
    }

}
