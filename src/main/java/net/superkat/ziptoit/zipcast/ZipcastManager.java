package net.superkat.ziptoit.zipcast;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.superkat.ziptoit.ZipToIt;
import net.superkat.ziptoit.duck.ZipcasterPlayer;
import net.superkat.ziptoit.item.StickyHandComponent;
import org.jetbrains.annotations.Nullable;

public class ZipcastManager {

    public static void tickZipcastingPlayer(PlayerEntity player) {
        if (!(player instanceof ZipcasterPlayer zipcasterPlayer)) return;

        Vec3d zipcastPos = zipcasterPlayer.zipcastPos();
        Vec3d currentPos = player.getPos();
        if(zipcasterPlayer.zipcastTicks() >= 120) {
            zipcasterPlayer.ziptoit$endZipcastEarly();
            return;
        }

        float maxSpeed = 5f * (zipcasterPlayer.zipcastTicks() >= 20 ? 1f : (float) zipcasterPlayer.zipcastTicks() / 20);
        Vec3d currentVelocity = player.getVelocity();
        Vec3d difference = zipcastPos.subtract(currentPos);
        Vec3d normal = difference.normalize();

        float distance = (float) difference.length();
        if(distance <= 1 * maxSpeed) {
            zipcasterPlayer.ziptoit$endZipcastEarly();
            player.setVelocity(player.getVelocity().multiply(0.25));
            return;
        }

        float velX = (float) normal.getX() * maxSpeed;
        float velY = (float) normal.getY() * maxSpeed;
        float velZ = (float) normal.getZ() * maxSpeed;

        player.setVelocity(velX, velY, velZ);
        player.velocityDirty = true;
    }

    @Nullable
    public static BlockHitResult raycastStickyHand(LivingEntity entity, ItemStack stickyHand, float tickProgress) {
        StickyHandComponent component = stickyHand.get(ZipToIt.STICKY_HAND_COMPONENT_TYPE);

        int zipRange = 48;
        if(component != null) zipRange = component.zipRange();

        HitResult raycast = entity.raycast(zipRange, tickProgress, false);
        if(raycast.getType() == HitResult.Type.BLOCK) {
            return (BlockHitResult) raycast;
        }
        return null;
    }

    public static boolean playerIsAimingZipcaster(PlayerEntity player) {
        return player.isUsingItem() && player.getActiveItem().isOf(ZipToIt.STICKY_HAND_ITEM);
    }

    public static boolean shouldDisableCollisionForPlayer(PlayerEntity player) {
        if (!(player instanceof ZipcasterPlayer zipcasterPlayer)) return false;
        if(!zipcasterPlayer.isZipcasting()) return false;

        return zipcasterPlayer.zipcastTicks() >= 15;
    }

}
