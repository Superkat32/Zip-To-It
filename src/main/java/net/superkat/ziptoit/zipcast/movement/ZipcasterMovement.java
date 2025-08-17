package net.superkat.ziptoit.zipcast.movement;

import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.superkat.ziptoit.duck.ZipcasterPlayer;
import net.superkat.ziptoit.particle.ZipcastImpactEffect;
import net.superkat.ziptoit.particle.ZipcastImpactSplatterEffect;
import net.superkat.ziptoit.particle.ZipcastLandParticleEffect;
import net.superkat.ziptoit.particle.ZipcastZoomParticleEffect;
import net.superkat.ziptoit.zipcast.ZipcastManager;
import net.superkat.ziptoit.zipcast.color.ZipcastColor;
import net.superkat.ziptoit.zipcast.line.ZipcastLine;
import net.superkat.ziptoit.zipcast.util.ZipcastClientHelper;
import org.joml.Vector3f;

public class ZipcasterMovement {
    public static void tickZipcasterPlayer(PlayerEntity player) {
        if (!(player instanceof ZipcasterPlayer zipcasterPlayer)) return;
//        if(!player.getWorld().getTickManager().shouldTick()) return;
        zipcasterPlayer.increaseLastZipcastActivateTicks();

        if(zipcasterPlayer.isZipcasting()) {
            zipcasterPlayer.increaseZipcastTicks();
        }

        if(zipcasterPlayer.isStickingToWall()) {
            zipcasterPlayer.increaseWallTicks();
        }

        boolean onGround = player.isOnGround();
        if(zipcasterPlayer.slowFallForZipcast() && onGround) {
            zipcasterPlayer.setSlowFallForZipcast(false);
        }

        if(zipcasterPlayer.showZipcastDeathMessage() && onGround) {
            zipcasterPlayer.setShowZipcastDeathMessage(false);
        }
    }

    public static void tickZipcasterPlayerForNoClip(PlayerEntity player) {
        if (!(player instanceof ZipcasterPlayer zipcasterPlayer)) return;
        if(zipcasterPlayer.noClipForZipcast()) {
            player.noClip = true;
        }
    }

    public static void tickZipcasterPlayerAfterMovementApplied(PlayerEntity player) {
        if (!(player instanceof ZipcasterPlayer zipcasterPlayer)) return;

        if(player.getWorld().isClient && zipcasterPlayer.isZipcasting() && zipcasterPlayer.getZipcastLine() != null) {
            spawnZoomParticles(player);
            ZipcastLine zipcastLine = zipcasterPlayer.getZipcastLine();
            zipcastLine.tickPoints();

            ZipcastTarget zipcastTarget = zipcasterPlayer.zipcastTarget();

            if(zipcasterPlayer.zipcastTicks() == zipcastTarget.startTicks()) {
                Vec3d pos = zipcastTarget.pos();
                Direction direction = zipcastTarget.raycastSide();
                int color = zipcastTarget.color().color();
                Vector3f color3f = ColorHelper.toVector(color);
                player.getWorld().addParticleClient(new ZipcastLandParticleEffect(4, 0.5f, 4, 4, direction, color3f), true, true, pos.getX(), pos.getY(), pos.getZ(), 0, 0, 0);
            }
        }
    }

    public static void travelZipcasting(PlayerEntity player, Vec3d movementInput) {
        if (!(player instanceof ZipcasterPlayer zipcasterPlayer)) return;
//        if(player.getWorld().getTickManager().isFrozen()) return;

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

    public static void spawnZoomParticles(PlayerEntity player) {
        if (!player.getWorld().isClient || !(player instanceof ZipcasterPlayer zipcasterPlayer)) return;

        ZipcastTarget zipcastTarget = zipcasterPlayer.zipcastTarget();
        int zipcastTicks = zipcasterPlayer.zipcastTicks();
        if(zipcastTicks < zipcastTarget.startTicks() + 2) return;
        // Only spawn in the particles if the client player is within 64 blocks
        boolean force = ZipcastClientHelper.clientPlayerDistanceToPos(player.getPos()) <= 64;

        // Inner particles
        float innerForwardOffset = ZipcastClientHelper.mainPlayerFirstPerson(player) ? -0.5f : -0.25f;
        for (int i = 0; i < 3; i++) {
            zoomParticle(player, 0.025f, innerForwardOffset, 5f, force);
        }

        // Outer particles
        float speed = zipcastTarget.speed() + 0.1f;
        if(speed >= 0.5f) {
            int outerAmount = MathHelper.ceil(speed);
            if(outerAmount >= 10) outerAmount = 10;

            for (int i = 0; i < outerAmount; i++) {
                zoomParticle(player, 2f, 1f, 1f, force);
            }

        } else {
            int ticksPerAmount = MathHelper.ceil(1f / (speed == 0f ? 0.1f : speed));
            if(zipcastTicks % ticksPerAmount == 0) zoomParticle(player, 1f, 1f, 1f, force);
        }

    }

    private static void zoomParticle(PlayerEntity player, float maxOffset, float forwardOffset, float stretch, boolean force) {
        if (!player.getWorld().isClient || !(player instanceof ZipcasterPlayer zipcasterPlayer)) return;

        ZipcastTarget zipcastTarget = zipcasterPlayer.zipcastTarget();
        ZipcastColor zipcastColor = zipcastTarget.color();
        Vec3d playerVelocity = player.getVelocity();

        if (player instanceof OtherClientPlayerEntity) {
            // For some reason, other player's velocities aren't synced to the client,
            // so this strange workaround must be done instead.
            // Not perfect, but better than nothing, and probably better than constantly sending packets
            playerVelocity = player.getPos().subtract(player.getLastRenderPos());
            // give extra time to allow for previous position to update
            if(zipcasterPlayer.zipcastTicks() <= zipcastTarget.startTicks() + 5) return;
        }

        Random random = player.getWorld().random;

        // Particle's velocity, not player's velocity
        float velX = (float) (-playerVelocity.getX() / 2f);
        float velY = (float) (-playerVelocity.getY() / 2f);
        float velZ = (float) (-playerVelocity.getZ() / 2f);

        // Particle's positions, not player's
        Vec3d normalizedPlayerVelocity = playerVelocity.normalize();
        float forwardOffsetAmount = (float) (forwardOffset * playerVelocity.lengthSquared());
        float offsetX = (float) (normalizedPlayerVelocity.getX() * forwardOffsetAmount);
        float offsetY = (float) (normalizedPlayerVelocity.getY() * forwardOffsetAmount);
        float offsetZ = (float) (normalizedPlayerVelocity.getZ() * forwardOffsetAmount);

        float x = (float) (player.getX() + random.nextGaussian() * maxOffset) + offsetX;
        float y = (float) (player.getY() + random.nextGaussian() * maxOffset) + offsetY + 0.25f;
        float z = (float) (player.getZ() + random.nextGaussian() * maxOffset) + offsetZ;

        float yaw = (float) Math.atan2(playerVelocity.getZ(), -playerVelocity.getX());
        float pitch = (float) Math.asin(playerVelocity.getY() / playerVelocity.length());
        player.getWorld().addParticleClient(
                new ZipcastZoomParticleEffect(
                        zipcastColor, (float) Math.toDegrees(yaw), (float) Math.toDegrees(-pitch), stretch
                ), force, force, x, y, z, velX, velY, velZ
        );
    }

    public static void spawnImpactParticles(PlayerEntity player) {
        if (!player.getWorld().isClient || !(player instanceof ZipcasterPlayer zipcasterPlayer)) return;

        boolean force = ZipcastClientHelper.clientPlayerDistanceToPos(player.getPos()) <= 64;

        ZipcastTarget zipcastTarget = zipcasterPlayer.zipcastTarget();
        Direction direction = zipcastTarget.raycastSide();
        Vec3d target = zipcastTarget.pos();

        float offsetAmount = ZipcastClientHelper.mainPlayerFirstPerson(player) ? 3f : 1f;
        float offsetX = offsetAmount * direction.getOffsetX();
        float offsetY = offsetAmount * direction.getOffsetY();
        float offsetZ = offsetAmount * direction.getOffsetZ();

        float playerScale = player.getScale();
        float x = (float) (target.getX() + offsetX);
        float y = (float) (target.getY() + offsetY + (0.5f * playerScale));
        float z = (float) (target.getZ() + offsetZ);

        Vector3f color = ColorHelper.toVector(zipcastTarget.color().color());

        // explosion particle
        player.getWorld().addParticleClient(
                new ZipcastImpactEffect(
                        7, 3f * playerScale, color
                ), force, force, x, y, z, 0, 0, 0
        );

        // sparkle (splatter) particles
        for (int i = 0; i < 15; i++) {
            impactSplatterParticle(player, force);
        }
    }

    private static void impactSplatterParticle(PlayerEntity player, boolean force) {
        if (!player.getWorld().isClient || !(player instanceof ZipcasterPlayer zipcasterPlayer)) return;

        ZipcastTarget zipcastTarget = zipcasterPlayer.zipcastTarget();
        ZipcastColor zipcastColor = zipcastTarget.color();
        Direction direction = zipcastTarget.raycastSide();
        Vec3d target = zipcastTarget.pos();

        Random random = player.getWorld().random;
        float velX = (float) (direction.getOffsetX() * random.nextFloat() + random.nextGaussian());
        float velY = (float) (direction.getOffsetY() * random.nextFloat() + random.nextGaussian());
        float velZ = (float) (direction.getOffsetZ() * random.nextFloat() + random.nextGaussian());

        float offsetAmount = 0.5f;
        float offsetX = offsetAmount * direction.getOffsetX();
        float offsetY = offsetAmount * direction.getOffsetY();
        float offsetZ = offsetAmount * direction.getOffsetZ();

        float playerScale = player.getScale();
        float x = (float) (target.getX() + offsetX);
        float y = (float) (target.getY() + offsetY + (0.5f * playerScale));
        float z = (float) (target.getZ() + offsetZ);

        int age = random.nextBetween(15, 20);
        float scale = random.nextFloat() * random.nextFloat() / 1.5f;
        boolean altColor = random.nextInt(4) == 0;
        Vector3f color = ColorHelper.toVector(altColor ? zipcastColor.altColor() : zipcastColor.color());

        player.getWorld().addParticleClient(
                new ZipcastImpactSplatterEffect(age, scale, color), force, force, x, y, z, velX, velY, velZ
        );
    }

}
