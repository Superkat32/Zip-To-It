package net.superkat.ziptoit.zipcast;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Arm;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.superkat.ziptoit.api.ZipcasterEvents;
import net.superkat.ziptoit.duck.ZipcasterPlayer;
import net.superkat.ziptoit.item.StickyHandComponent;
import net.superkat.ziptoit.item.ZipToItItems;
import net.superkat.ziptoit.network.packets.WallStickEndCommonPacket;
import net.superkat.ziptoit.network.packets.WallStickStartCommonPacket;
import net.superkat.ziptoit.network.packets.ZipcastCancelCommonPacket;
import net.superkat.ziptoit.network.packets.ZipcastEndCommonPacket;
import net.superkat.ziptoit.network.packets.ZipcastStartCommonPacket;
import net.superkat.ziptoit.zipcast.movement.ZipcastTarget;
import net.superkat.ziptoit.zipcast.movement.ZipcasterMovement;
import net.superkat.ziptoit.zipcast.util.ZipcastClientHelper;
import org.jetbrains.annotations.Nullable;

public class ZipcastManager {

    public static void startZipcast(LivingEntity player, ZipcastTarget zipcastTarget, boolean sendPackets) {
        if(!(player instanceof ZipcasterPlayer zipcasterPlayer)) return;

        zipcasterPlayer.ziptoit$startZipcast(zipcastTarget);

        if(player instanceof ServerPlayerEntity serverPlayer) {
//            ItemStack stack = serverPlayer.getActiveItem();
//            if(stack.getItem() instanceof StickyHandItem stickyHandItem) {
//                stickyHandItem.damageStickyHand(player, stack);
//            }

            ZipcasterEvents.ZIPCAST_START.invoker().onZipcastStart(serverPlayer, zipcastTarget);
        }

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

        player.playSound(SoundEvents.ITEM_MACE_SMASH_GROUND_HEAVY);
        ZipcasterMovement.spawnImpactParticles((PlayerEntity) player);

        // Call event before endZipcast to keep zipcastTarget
        if(zipcasterPlayer.isZipcasting() && player instanceof ServerPlayerEntity playerEntity) {
            ZipcasterEvents.ZIPCAST_END.invoker().onZipcastEnd(playerEntity, zipcasterPlayer.zipcastTarget(), false);
        }

        zipcasterPlayer.ziptoit$endZipcast();

        if(!sendPackets) return;
        if(player.getWorld().isClient) {
            ZipcastClientHelper.sendC2SPacket(new ZipcastEndCommonPacket(player.getId()));
        } else {
            for (ServerPlayerEntity trackingPlayer : PlayerLookup.tracking(player)) {
                if (trackingPlayer == player) continue;
                ServerPlayNetworking.send(trackingPlayer, new ZipcastEndCommonPacket(player.getId()));
            }
        }
    }

    public static void startWallStick(LivingEntity player, Vec3d pos, BlockPos wallPos, boolean sendPackets) {
        if(!(player instanceof ZipcasterPlayer zipcasterPlayer)) return;

        zipcasterPlayer.ziptoit$stickToWall(pos, wallPos);
        if(player.getWorld().isClient) {
            player.setSneaking(true);
        }
        player.setNoGravity(true);
        player.setPosition(pos);

        if(player instanceof ServerPlayerEntity playerEntity) {
            ZipcasterEvents.WALL_STICK_START.invoker().onWallStickStart(playerEntity, pos, wallPos);
        }

        if(!sendPackets) return;
        if(player.getWorld().isClient) {
            ZipcastClientHelper.sendC2SPacket(new WallStickStartCommonPacket(player.getId(), pos, wallPos));
        } else {
            for (ServerPlayerEntity trackingPlayer : PlayerLookup.tracking(player)) {
                if (trackingPlayer == player) continue;
                ServerPlayNetworking.send(trackingPlayer, new WallStickStartCommonPacket(player.getId(), pos, wallPos));
            }
        }
    }

    public static void endWallStick(LivingEntity player, boolean jump, boolean sendPackets) {
        if(!(player instanceof ZipcasterPlayer zipcasterPlayer)) return;

        // get wall stick pos before making it null
        BlockPos wallPos = zipcasterPlayer.getWallStickPos();
        boolean wasStickingToWall = zipcasterPlayer.isStickingToWall();
        zipcasterPlayer.ziptoit$endWallStick();

        if(wasStickingToWall && player instanceof ServerPlayerEntity playerEntity) {
            ZipcasterEvents.WALL_STICK_END.invoker().onWallStickEnd(playerEntity, wallPos, jump);
        }

        if (jump && player.isLogicalSideForUpdatingMovement()) {
            player.setVelocity(0, 0.7f, 0);
        }
        if(player.getWorld().isClient) {
            player.setSneaking(false);
        }
        player.setNoGravity(false);

        if(!sendPackets) return;
        if(player.getWorld().isClient) {
            ZipcastClientHelper.sendC2SPacket(new WallStickEndCommonPacket(player.getId(), jump));
        } else {
            for (ServerPlayerEntity trackingPlayer : PlayerLookup.tracking(player)) {
                if (trackingPlayer == player) continue;
                ServerPlayNetworking.send(trackingPlayer, new WallStickEndCommonPacket(player.getId(),  jump));
            }
        }
    }

    public static void cancelZipcast(LivingEntity player, boolean hardCancel, boolean sendPackets) {
        if (!(player instanceof ZipcasterPlayer zipcasterPlayer)) return;

        player.playSound(SoundEvents.ITEM_TRIDENT_THUNDER.value(), 0.75f, 1f);
        zipcasterPlayer.ziptoit$softCancelZipcast();
        if(hardCancel) zipcasterPlayer.ziptoit$hardCancelZipcast();

        if(player instanceof ServerPlayerEntity playerEntity) {
            ZipcasterEvents.ZIPCAST_END.invoker().onZipcastEnd(playerEntity, zipcasterPlayer.zipcastTarget(), true);
        }

        if(!sendPackets) return;
        if(player.getWorld().isClient) {
            ZipcastClientHelper.sendC2SPacket(new ZipcastCancelCommonPacket(player.getId(), hardCancel));
        } else {
            for (ServerPlayerEntity trackingPlayer : PlayerLookup.tracking(player)) {
                if (trackingPlayer == player) continue;
                ServerPlayNetworking.send(trackingPlayer, new ZipcastCancelCommonPacket(player.getId(),  hardCancel));
            }
        }
    }

    // Assumed client side
    public static void tryStickingToWall(PlayerEntity player, ZipcastTarget zipcastTarget) {
        if (!(player instanceof ZipcasterPlayer zipcasterPlayer)) return;
        if(!targetIsWallStickable(player, zipcastTarget)) {
            if(zipcastTarget.raycastSide() != Direction.UP) return;

            // Not perfect, but should help a bunch
            if(!player.getWorld().getBlockState(player.getBlockPos()).isAir()) {
                player.setPosition(player.getPos().add(0, 0.5, 0));
            }
            return;
        }

        Vec3d newPosition = zipcastTarget.pos().offset(zipcastTarget.raycastSide(), 0.5 * player.getScale());

        if(!ZipcasterEvents.ALLOW_WALL_STICK.invoker().allowWallStick(player, zipcastTarget, newPosition, zipcastTarget.blockPos())) return;

        startWallStick(player, newPosition, zipcastTarget.blockPos(), true);
        player.setVelocity(Vec3d.ZERO);
    }

    @Nullable
    public static BlockHitResult raycastStickyHand(LivingEntity entity, ItemStack stickyHand, float tickProgress) {
        StickyHandComponent component = stickyHand.get(ZipToItItems.STICKY_HAND_COMPONENT_TYPE);

        int zipRange = 48;
        if(component != null) zipRange = component.zipRange();

        HitResult raycast = entity.raycast(zipRange, tickProgress, false);
        if(raycast.getType() == HitResult.Type.BLOCK) {
            if(!entity.getWorld().getWorldBorder().contains(raycast.getPos())) return null;
            return (BlockHitResult) raycast;
        }
        return null;
    }

    public static Arm getArmHoldingStickyHand(PlayerEntity player) {
        return player.getMainHandStack().getItem().getDefaultStack().isIn(ZipToItItems.STICKY_HANDS) ? player.getMainArm() : player.getMainArm().getOpposite();
    }

    public static boolean playerIsAimingZipcaster(PlayerEntity player) {
        return player.isUsingItem() && player.getActiveItem().isIn(ZipToItItems.STICKY_HANDS);
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
//        if(player.getWorld().getTickManager().isFrozen()) return false;

        return zipcasterPlayer.zipcastTicks() >= 3;
//        return false;
    }

    public static double postZipcastSlowfallAmountForPlayer(PlayerEntity player, double original) {
        return Math.min(original, 0.0625);
//        return 0.025;
    }

    public static int getFallDamageForZipcastPlayer(PlayerEntity player, int original) {
        if (!(player instanceof ZipcasterPlayer zipcasterPlayer)) return original;
        if(zipcasterPlayer.isZipcasting()) return 0;
        return original - 12;
    }

}
