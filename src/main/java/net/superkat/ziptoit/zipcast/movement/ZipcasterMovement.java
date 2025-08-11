package net.superkat.ziptoit.zipcast.movement;

import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.superkat.ziptoit.duck.ZipcasterPlayer;
import net.superkat.ziptoit.zipcast.ZipcastManager;
import net.superkat.ziptoit.zipcast.line.ZipcastLine;

public class ZipcasterMovement {
    public static void tickZipcasterPlayer(PlayerEntity player) {
        if (!(player instanceof ZipcasterPlayer zipcasterPlayer)) return;
        zipcasterPlayer.increaseLastZipcastActivateTicks();

        if(zipcasterPlayer.isZipcasting()) {
            zipcasterPlayer.increaseZipcastTicks();

            if(player.getWorld().isClient && zipcasterPlayer.getZipcastLine() != null) {
                ZipcastLine zipcastLine = zipcasterPlayer.getZipcastLine();
                zipcastLine.tickPoints();
            }
        }
        if(zipcasterPlayer.isStickingToWall()) {
            zipcasterPlayer.increaseWallTicks();
        }

        if(zipcasterPlayer.slowFallForZipcast()) {
            if(player.isOnGround()) {
                zipcasterPlayer.setSlowFallForZipcast(false);
            }
        }
    }

    public static void tickZipcasterPlayerForNoClip(PlayerEntity player) {
        if (!(player instanceof ZipcasterPlayer zipcasterPlayer)) return;
        if(zipcasterPlayer.noClipForZipcast()) {
            player.noClip = true;
        }
    }

    public static void travelZipcasting(PlayerEntity player, Vec3d movementInput) {
        if (!(player instanceof ZipcasterPlayer zipcasterPlayer)) return;

        ZipcastTarget zipcastTarget = zipcasterPlayer.zipcastTarget();
        Vec3d zipcastPos = zipcastTarget.pos();
        Vec3d currentPos = player.getPos().add(0, 0.5, 0);

        // Check to see if zipcast should be canceled
        if((zipcasterPlayer.zipcastTicks() >= 200 || player.isSneaking()) && player.isLogicalSideForUpdatingMovement()) {
            // Sneaking is technically supposed to be an emergency exit in case my code messes up,
            // but I think it allows for some fun uses elsewhere, so it's staying in
            player.playSound(SoundEvents.ITEM_TRIDENT_THUNDER.value(), 0.75f, 1f);
            ZipcastManager.cancelZipcast(player, false, true);
            return;
        }

        boolean end = false;
        int zipcastTicks = zipcasterPlayer.zipcastTicks();
        Vec3d currentVelocity = player.getVelocity();

        // Speed of the zipcast based on how long it's been used
        boolean lerpVelocity = false;
        boolean ignoreNewVelocity = false;
        float maxSpeed = zipcastTarget.speed();
        int startTicks = zipcastTarget.startTicks(); // ticks to shoot the zipcast - only uses player's current velocity
        int lerpTicks = zipcastTarget.lerpTicks(); // ticks to transition between pre-zipcast velocity and zipcast velocity
        int buildupTicks = zipcastTarget.buildupTicks(); // ticks building up to max speed

        if(zipcastTicks <= startTicks) {
            if(zipcastTicks == 1) {
                player.playSound(SoundEvents.ITEM_TRIDENT_THROW.value());
                player.playSound(SoundEvents.ITEM_TRIDENT_RETURN);
            }
            maxSpeed = 0; // shooting animation is still playing
            ignoreNewVelocity = true;
        } else if (zipcastTicks <= buildupTicks) {
            if(zipcastTicks == (startTicks + 1)) player.playSound(SoundEvents.ITEM_TRIDENT_RIPTIDE_3.value());
            // player is now building up speed
            maxSpeed *= (float) (zipcastTicks) / (buildupTicks);
            if(zipcastTicks <= lerpTicks) {
                lerpVelocity = true;
            }
        } else {
            // player is now at max speed
            zipcasterPlayer.setNoClipForZipcast(true);
        }

        // Get the new velocity to move the player towards the zipcast pos
        Vec3d difference = zipcastPos.subtract(currentPos);
        Vec3d normal = difference.normalize();
        Vec3d newVelocity = normal.multiply(maxSpeed);

        // Transition between the player's pre-zipcast velocity and the velocity caused by the zipcast
        if(lerpVelocity) {
            float lerpProgress = (float) (zipcastTicks - startTicks) / (lerpTicks - startTicks);
            newVelocity = MathHelper.lerp(lerpProgress, currentVelocity, newVelocity);
        }

        if(zipcastTicks >= startTicks) {
            // FIXME - If moving faster than default speed, this can break
            end = currentPos.add(newVelocity).distanceTo(zipcastPos) <= 3f;
        }

        if(player.isLogicalSideForUpdatingMovement()) {
            if (!ignoreNewVelocity) {
                player.setVelocity(newVelocity);
            }
            player.move(MovementType.SELF, player.getVelocity());
            player.tickBlockCollision(player.getLastRenderPos(), player.getPos());
        }

        if(end && player.getWorld().isClient) {
            player.playSound(SoundEvents.ITEM_MACE_SMASH_GROUND_HEAVY);
            ZipcastManager.tryStickingToWall(player, zipcastTarget);
            if(player.isLogicalSideForUpdatingMovement()) {
                player.setVelocity(Vec3d.ZERO);
            }
            ZipcastManager.endZipcast(player, true);
        }
    }

    public static void travelStickingToWall(PlayerEntity player, Vec3d movementInput) {
        if (!(player instanceof ZipcasterPlayer zipcasterPlayer)) return;

        boolean jumping = player.isJumping();
        boolean end = player.isSneaking() || zipcasterPlayer.wallTicks() >= 1200 || jumping;

        if(end) {
            ZipcastManager.endWallStick(player, jumping, true);
        } else {
            if(player.isLogicalSideForUpdatingMovement()) {
                player.setVelocity(Vec3d.ZERO);
                player.move(MovementType.SELF, player.getVelocity());
            }
        }

    }

}
