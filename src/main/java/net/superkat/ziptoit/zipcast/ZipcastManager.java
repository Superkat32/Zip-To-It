package net.superkat.ziptoit.zipcast;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.superkat.ziptoit.ZipToIt;
import net.superkat.ziptoit.duck.ZipcasterPlayer;
import net.superkat.ziptoit.item.StickyHandComponent;
import net.superkat.ziptoit.network.packets.WallStickS2CPacket;
import org.jetbrains.annotations.Nullable;

public class ZipcastManager {

    public static void tickZipcasterPlayer(PlayerEntity player) {
        if (!(player instanceof ZipcasterPlayer zipcasterPlayer)) return;

        if(zipcasterPlayer.isZipcasting()) {
            player.noClip = true;
        }

//        if(zipcasterPlayer.isZipcasting()) {
//            zipcasterPlayer.setZipcastTicks(zipcasterPlayer.zipcastTicks() + 1);
//            tickZipcasting(player);
//        } else if (zipcasterPlayer.isStickingToWall()) {
//            zipcasterPlayer.setWallTicks(zipcasterPlayer.wallTicks() + 1);
//            tickStickingToWall(player);
//        }

    }

//    public static void tickZipcasting(PlayerEntity player) {
//        if (!(player instanceof ZipcasterPlayer zipcasterPlayer)) return;
//
//        Vec3d zipcastPos = zipcasterPlayer.zipcastPos();
//        Vec3d currentPos = player.getPos();
//        if(zipcasterPlayer.zipcastTicks() >= 200) {
//            zipcasterPlayer.ziptoit$endZipcastEarly();
//            return;
//        }
//
//        float maxSpeed = 5f;
//        int startingTicks = 10;
//        int buildupTicks = 40;
//        int zipcastTicks = zipcasterPlayer.zipcastTicks();
//        if(zipcastTicks <= startingTicks) {
//            maxSpeed = 0.25f;
//        } else if (zipcastTicks <= buildupTicks) {
//            maxSpeed *= (float) zipcastTicks / buildupTicks;
//        }
//
//        Vec3d currentVelocity = player.getVelocity();
//        Vec3d difference = zipcastPos.subtract(currentPos);
//        Vec3d normal = difference.normalize();
//
//        float distance = (float) difference.length();
//        if(distance <= 0.5 * currentVelocity.length()) {
//            player.setVelocity(0, 0,0);
//            tryStickingToWall(player);
//            return;
//        }
//
//        float velX = (float) normal.getX() * maxSpeed;
//        float velY = (float) normal.getY() * maxSpeed;
//        float velZ = (float) normal.getZ() * maxSpeed;
//
//        player.setVelocity(velX, velY, velZ);
//        player.velocityDirty = true;
//    }

//    public static void tickStickingToWall(PlayerEntity player) {
//        if (!(player instanceof ZipcasterPlayer zipcasterPlayer)) return;
//
//        player.setSneaking(true);
//        if(zipcasterPlayer.wallTicks() >= 80) {
//            zipcasterPlayer.setIsStickingToWall(false);
//            player.setSneaking(false);
//            player.setNoGravity(false);
//        }
//    }

    public static void travelZipcasting(PlayerEntity player, Vec3d movementInput) {
        if (!(player instanceof ZipcasterPlayer zipcasterPlayer)) return;

        zipcasterPlayer.increaseZipcastTicks();
        ZipcastTarget zipcastTarget = zipcasterPlayer.zipcastTarget();
        Vec3d zipcastPos = zipcastTarget.target();
        Vec3d currentPos = player.getPos().add(0, 0.5, 0);
        if(zipcasterPlayer.zipcastTicks() >= 200 || player.isSneaking()) {
            zipcasterPlayer.ziptoit$cancelZipcast();
            return;
        }

        float maxSpeed = 5f;
        int startingTicks = 10;
        int buildupTicks = 40;
        int zipcastTicks = zipcasterPlayer.zipcastTicks();
        if(zipcastTicks <= startingTicks) {
            maxSpeed = 0.25f;
        } else if (zipcastTicks <= buildupTicks) {
            maxSpeed *= (float) zipcastTicks / buildupTicks;
        }

        boolean end = false;
        Vec3d currentVelocity = player.getVelocity();
        Vec3d difference = zipcastPos.subtract(currentPos);
        Vec3d normal = difference.normalize();
        Vec3d newVelocity = normal.multiply(maxSpeed);

        if(zipcastTicks >= startingTicks) {
//            if(player.horizontalCollision || player.verticalCollision) {
//                end = true;
//                newVelocity = Vec3d.ZERO;
//            }

            Vec3d vec3d = player.getPos();
            BlockHitResult blockHitResult = player.getWorld()
                .getCollisionsIncludingWorldBorder(
                        new RaycastContext(player.getPos(), player.getPos().add(currentVelocity), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, player)
                );
            Vec3d vec3d2 = blockHitResult.getPos();
            player.setVelocity(newVelocity);
            player.setPosition(vec3d2);
            player.tickBlockCollision(vec3d, vec3d2);

            if(blockHitResult.getType() == HitResult.Type.BLOCK) {
                Vec3d velo = player.getVelocity();
                Vec3d signum = new Vec3d(Math.signum(velo.x), Math.signum(velo.y), Math.signum(velo.z));
                Vec3d vec3d3 = signum.multiply(0.5f);
                player.setPosition(player.getPos().subtract(vec3d3));
                player.setVelocity(Vec3d.ZERO);
                end = true;
            }

        }

        if(end) {
            tryStickingToWall(player);
        }
    }

    public static void tryStickingToWall(PlayerEntity player) {
        if (!(player instanceof ZipcasterPlayer zipcasterPlayer)) return;

        zipcasterPlayer.setIsZipcasting(false);
        ZipcastTarget zipcastTarget = zipcasterPlayer.zipcastTarget();
        zipcasterPlayer.setZipcastTarget(null);
        if(!targetIsWallStickable(player, zipcastTarget)) return;

        player.setVelocity(Vec3d.ZERO);
        zipcasterPlayer.setIsStickingToWall(true);
        zipcasterPlayer.setWallTicks(0);
        player.setNoGravity(true);
        player.setSneaking(true);
        player.noClip = false;

        if(player.getWorld().isClient) return;
        ServerPlayNetworking.send((ServerPlayerEntity) player, new WallStickS2CPacket(player.getId()));
    }

    public static void travelStickingToWall(PlayerEntity player, Vec3d movementInput) {
        if (!(player instanceof ZipcasterPlayer zipcasterPlayer)) return;
        if(!player.isLogicalSideForUpdatingMovement()) return;
        zipcasterPlayer.increaseWallTicks();

        player.setVelocity(Vec3d.ZERO);
        boolean jumping = player.isJumping();
        boolean end = player.isSneaking() || zipcasterPlayer.wallTicks() >= 1200 || jumping;

        if(end) {
            endWallStick(player, jumping);
        }

        player.move(MovementType.SELF, player.getVelocity());
    }

    public static void endWallStick(PlayerEntity player, boolean jump) {
        if (!(player instanceof ZipcasterPlayer zipcasterPlayer)) return;
        if(!player.isLogicalSideForUpdatingMovement()) return;

        if(jump) {
            player.setVelocity(0, 0.8f, 0);
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

}
