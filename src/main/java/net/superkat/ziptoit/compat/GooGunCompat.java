package net.superkat.ziptoit.compat;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.World;
import net.superkat.ziptoit.api.ZipcasterEvents;
import net.superkat.ziptoit.duck.ZipcasterPlayer;
import toad.googun.block.ModBlocks;

public class GooGunCompat {

    public static void init() {
        if(!gooGunLoaded()) return;

        ZipcasterEvents.ALLOW_WALL_STICK.register((player, zipcastTarget, playerTeleportPos, wallBlockPos) -> {
            if(!(player instanceof ZipcasterPlayer zipcasterPlayer)) return true;
            World world = player.getWorld();

            // Push player backwards of the bouncy goo block based their speed
            if(world.getBlockState(wallBlockPos).isOf(ModBlocks.BOUNCY_GOO)) {
                float zipcastDelta = (float) zipcasterPlayer.zipcastTicks() / (zipcastTarget.startTicks() + zipcastTarget.buildupTicks());
                if(zipcastDelta > 1f) zipcastDelta = 1f;

                float speed = zipcastTarget.speed() * 1.25f * zipcastDelta;
                player.setVelocity(zipcastTarget.raycastSide().getDoubleVector().add(0, 0.5f, 0).multiply(speed));
                return false;
            }

            // Push player upwards based on their speed
            if (world.getBlockState(wallBlockPos).isOf(ModBlocks.SPEEDY_GOO)) {
                float zipcastDelta = (float) zipcasterPlayer.zipcastTicks() / (zipcastTarget.startTicks() + zipcastTarget.buildupTicks());
                if(zipcastDelta > 1f) zipcastDelta = 1f;

                float speed = zipcastTarget.speed() * 1.25f * zipcastDelta;
                player.setVelocity(0, speed, 0);
                return false;
            }

            // Don't do anything, allow player to catch onto the sticky goo themselves
//            if(world.getBlockState(wallBlockPos).isOf(ModBlocks.STICKY_GOO)) {
//                return false;
//            }

            return true;
        });
    }

    public static boolean gooGunLoaded() {
        return FabricLoader.getInstance().isModLoaded("googun");
    }

}
