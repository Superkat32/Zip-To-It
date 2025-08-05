package net.superkat.ziptoit.zipcast;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.superkat.ziptoit.ZipToIt;
import net.superkat.ziptoit.duck.ZipcasterPlayer;
import net.superkat.ziptoit.item.StickyHandComponent;
import net.superkat.ziptoit.network.packets.WallStickCommonPacket;
import net.superkat.ziptoit.network.packets.ZipcastEndCommonPacket;
import net.superkat.ziptoit.network.packets.ZipcastStartCommonPacket;
import org.jetbrains.annotations.Nullable;

public class ZipcastManager {

    public static void startZipcast(LivingEntity player, ZipcastTarget zipcastTarget, boolean sendPackets) {
        if(!(player instanceof ZipcasterPlayer zipcasterPlayer)) return;

        zipcasterPlayer.ziptoit$startZipcast(zipcastTarget);

        if(!sendPackets) return;
        if(player.getWorld().isClient) {
            ZipcastClientHelper.sendC2SPacket(new ZipcastStartCommonPacket(zipcastTarget));
        } else {
            for (ServerPlayerEntity trackingPlayer : PlayerLookup.tracking(player)) {
                if (trackingPlayer == player) continue;
                ServerPlayNetworking.send(trackingPlayer, new ZipcastStartCommonPacket(zipcastTarget));
            }
        }
    }

    public static void endZipcast(LivingEntity player, boolean sendPackets) {
        if(!(player instanceof ZipcasterPlayer zipcasterPlayer)) return;

        Vec3d teleportPos = Vec3d.ZERO;
        if(zipcasterPlayer.zipcastTarget() != null) {
            teleportPos = zipcasterPlayer.zipcastTarget().pos();
        }

        if(teleportPos != Vec3d.ZERO) {
//            player.setPosition(teleportPos);
        }

        zipcasterPlayer.ziptoit$endZipcast();

        if(!sendPackets) return;
        if(player.getWorld().isClient) {
            ZipcastClientHelper.sendC2SPacket(new ZipcastEndCommonPacket(player.getId(), teleportPos, false));
        } else {

            for (ServerPlayerEntity trackingPlayer : PlayerLookup.tracking(player)) {
                if (trackingPlayer == player) continue;
                ServerPlayNetworking.send(trackingPlayer, new ZipcastEndCommonPacket(player.getId(), teleportPos, false));
            }
        }
    }

    public static void stickToWall(LivingEntity player, Vec3d pos, boolean sendPackets) {
        if(!(player instanceof ZipcasterPlayer zipcasterPlayer)) return;

        zipcasterPlayer.ziptoit$stickToWall(pos);
        if(player.getWorld().isClient) {
            player.setSneaking(true);
        }
        player.setNoGravity(true);
        player.setPosition(pos);

        if(!sendPackets) return;
        if(player.getWorld().isClient) {
            ZipcastClientHelper.sendC2SPacket(new WallStickCommonPacket(player.getId(), pos));
        } else {
            for (ServerPlayerEntity trackingPlayer : PlayerLookup.tracking(player)) {
                if (trackingPlayer == player) continue;
                ServerPlayNetworking.send(trackingPlayer, new WallStickCommonPacket(player.getId(), pos));
            }
        }
    }

    public static void cancelZipcast(LivingEntity player, boolean sendPackets) {

    }

    public static void tickZipcasterPlayer(PlayerEntity player) {
        if (!(player instanceof ZipcasterPlayer zipcasterPlayer)) return;

        if(zipcasterPlayer.noClipForZipcast()) {
            player.noClip = true;
        }

        if(zipcasterPlayer.slowFallForZipcast()) {
            if(player.isOnGround()) {
                zipcasterPlayer.setSlowFallForZipcast(false);
            }
        }
    }

    public static void travelZipcasting(PlayerEntity player, Vec3d movementInput) {
        if (!(player instanceof ZipcasterPlayer zipcasterPlayer)) return;

        zipcasterPlayer.increaseZipcastTicks();
        ZipcastTarget zipcastTarget = zipcasterPlayer.zipcastTarget();
        Vec3d zipcastPos = zipcastTarget.pos();
        Vec3d currentPos = player.getPos().add(0, 0.5, 0);

        // Check to see if zipcast should be canceled
        if(zipcasterPlayer.zipcastTicks() >= 200 || player.isSneaking()) {
            // Sneaking is technically supposed to be an emergency exit in case my code messes up,
            // but I think it allows for some fun uses elsewhere, so it's staying in
            zipcasterPlayer.ziptoit$cancelZipcast();
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
            maxSpeed = 0; // shooting animation is still playing
            ignoreNewVelocity = true;
        } else if (zipcastTicks <= buildupTicks) {
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
            end = currentPos.add(newVelocity).distanceTo(zipcastPos) <= 3f;

//            end = player.getBoundingBox().offset(newVelocity).contains(zipcastPos);

//            if(end) {
//                player.setPosition(zipcastPos);
//            }
//            end = currentPos.distanceTo(zipcastPos) <= 3f;
//            if(player.horizontalCollision || player.verticalCollision) {
//                end = true;
//                newVelocity = Vec3d.ZERO;
//            }

//            Vec3d vec3d = player.getPos();
//            BlockHitResult blockHitResult = player.getWorld()
//                .getCollisionsIncludingWorldBorder(
//                        new RaycastContext(player.getPos(), player.getPos().add(newVelocity), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, player)
//                );
//            Vec3d vec3d2 = blockHitResult.getPos();
////            player.setVelocity(newVelocity);
////            player.setPosition(vec3d2);
////            player.tickBlockCollision(vec3d, vec3d2);
//
//            if(blockHitResult.getType() == HitResult.Type.BLOCK) {
////                Vec3d velo = player.getVelocity();
////                Vec3d signum = new Vec3d(Math.signum(velo.x), Math.signum(velo.y), Math.signum(velo.z));
////                Vec3d vec3d3 = signum.multiply(1f);
////                player.setPosition(player.getPos().subtract(vec3d3));
//                player.setPosition(zipcastPos);
//                if(player.isLogicalSideForUpdatingMovement()) {
//                    player.setVelocity(Vec3d.ZERO);
//                }
//                end = true;
//            }
        }

        if(player.isLogicalSideForUpdatingMovement()) {
            if (!ignoreNewVelocity) {
                player.setVelocity(newVelocity);
            }
            player.move(MovementType.SELF, player.getVelocity());
            player.tickBlockCollision(player.getLastRenderPos(), player.getPos());

        }

        if(end && player.getWorld().isClient) {
            tryStickingToWall(player, zipcastTarget);
            if(player.isLogicalSideForUpdatingMovement()) {
                player.setVelocity(Vec3d.ZERO);
            }
            endZipcast(player, true);
        }
    }

    // Assumed client side
    public static void tryStickingToWall(PlayerEntity player, ZipcastTarget zipcastTarget) {
        if (!(player instanceof ZipcasterPlayer zipcasterPlayer)) return;
        if(!targetIsWallStickable(player, zipcastTarget)) return;
        Vec3d newPosition = zipcastTarget.pos().offset(zipcastTarget.raycastSide(), 0.5 * player.getScale());
//        player.tickBlockCollision(player.getLastRenderPos(), player.getPos());
//        Vec3d velocity = player.getVelocity().normalize();
//        Vec3d signum = new Vec3d(Math.signum(velocity.x), Math.signum(velocity.y), Math.signum(velocity.z));
//        Vec3d distanced = signum.multiply(0.5f);
//        Vec3d newPosition = zipcastTarget.pos().subtract(distanced);
//        player.tickBlockCollision(player.getPos(), newPosition);
        stickToWall(player, newPosition, true);
        player.setVelocity(Vec3d.ZERO);
    }

    public static void travelStickingToWall(PlayerEntity player, Vec3d movementInput) {
        if (!(player instanceof ZipcasterPlayer zipcasterPlayer)) return;
        zipcasterPlayer.increaseWallTicks();

        boolean jumping = player.isJumping();
        boolean end = player.isSneaking() || zipcasterPlayer.wallTicks() >= 1200 || jumping;

        if(end) {
            endWallStick(player, jumping);
        } else {
            if(player.isLogicalSideForUpdatingMovement()) {
                player.setVelocity(Vec3d.ZERO);
                player.move(MovementType.SELF, player.getVelocity());
            }
        }

    }

    public static void endWallStick(PlayerEntity player, boolean jump) {
        if (!(player instanceof ZipcasterPlayer zipcasterPlayer)) return;

        if (jump && player.isLogicalSideForUpdatingMovement()) {
            player.setVelocity(0, 0.7f, 0);
        }

        zipcasterPlayer.setIsStickingToWall(false);
        player.setSneaking(false);
        player.setNoGravity(false);
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

    public static boolean targetIsWallStickable(PlayerEntity player, ZipcastTarget zipcastTarget) {
        Direction direction = zipcastTarget.raycastSide();
        return direction != Direction.UP && direction != Direction.DOWN;
    }

    public static boolean playerShouldIgnoreZipcastControls(PlayerEntity player) {
        return player.hasVehicle() || player.getAbilities().flying;
    }

    public static boolean activateSwimmingPoseForZipcaster(PlayerEntity player) {
        if (!(player instanceof ZipcasterPlayer zipcasterPlayer)) return false;
        if(!zipcasterPlayer.isZipcasting()) return false;

        return zipcasterPlayer.zipcastTicks() >= 3;
//        return false;
    }

    public static double postZipcastSlowfallAmountForPlayer(PlayerEntity player, double original) {
        return Math.min(original, 0.0625);
    }

}
