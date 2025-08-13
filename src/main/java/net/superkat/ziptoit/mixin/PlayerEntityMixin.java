package net.superkat.ziptoit.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.superkat.ziptoit.duck.ZipcasterPlayer;
import net.superkat.ziptoit.item.StickyHandItem;
import net.superkat.ziptoit.zipcast.ZipcastManager;
import net.superkat.ziptoit.zipcast.line.ZipcastLine;
import net.superkat.ziptoit.zipcast.movement.ZipcastTarget;
import net.superkat.ziptoit.zipcast.movement.ZipcasterMovement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin extends LivingEntityMixin implements ZipcasterPlayer {
    @Unique
    public boolean zipcasting = false;
    @Unique
    public boolean stickingToWall = false;
    @Unique
    public ZipcastTarget zipcastTarget;
    @Unique
    public ZipcastLine zipcastLine;
    @Unique
    public boolean noClipForZipcast = false;
    @Unique
    public boolean slowFallForZipcast = false;
    @Unique
    public int zipcastTicks = 0;
    @Unique
    public int wallTicks = 0;

    @Unique
    public int ticksSinceLastZipcast = StickyHandItem.TICKS_UNTIL_ZIPCAST_ACTIVE_SOUND;

    // The main tick method where tick related numbers are increased, and stuff like slow falling is applied
    @Inject(method = "tick", at = @At(value = "HEAD"))
    public void ziptoit$tickZipcastPlayer(CallbackInfo ci) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        ZipcasterMovement.tickZipcasterPlayer(self);
    }

    // The first tick method can't change noClip because it'll be changed right back to default,
    // but this tick method gets called twice per tick, which isn't okay for the normal tickZipcasterPlayer method
    // So, the mixins are split up
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isSpectator()Z"))
    public void ziptoit$tickZipcastPlayerForNoClip(CallbackInfo ci) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        ZipcasterMovement.tickZipcasterPlayerForNoClip(self);
    }

    // Called at the tail instead of head to allow for the player movement to update, preventing issues with
    // the zipcaster line lerping being one tick behind of the player movement lerping
    @Inject(method = "tick", at = @At("TAIL"))
    public void ziptoit$tickZipcastPlayerForPostMovementRender(CallbackInfo ci) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        ZipcasterMovement.tickZipcasterPlayerAfterMovementApplied(self);
    }

    @WrapOperation(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;travel(Lnet/minecraft/util/math/Vec3d;)V"))
    public void ziptoit$modifyMovementWhileZipcastingAndStickingToWall(PlayerEntity instance, Vec3d movementInput, Operation<Void> original) {
        boolean ignoreZipcastControls = ZipcastManager.playerShouldIgnoreZipcastControls(instance);
        if(ignoreZipcastControls) {
            // This mixin can get called in spots when I don't want it to,
            // specifically when the player has a vehicle or is creative flying,
            // so this allows those to remain normal while also providing a good spot for mod compatibility
            original.call(instance, movementInput);
            return;
        }

        if(this.isZipcasting()) {
            ZipcasterMovement.travelZipcasting(instance, movementInput);
        } else if(this.isStickingToWall()) {
            ZipcasterMovement.travelStickingToWall(instance, movementInput);
        } else {
            original.call(instance, movementInput);
        }
    }

    @ModifyReturnValue(method = "getExpectedPose", at = @At("RETURN"))
    public EntityPose ziptoit$modifyPoseAndHitboxForZipcasting(EntityPose original) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        if(this.isZipcasting()) {
            if(ZipcastManager.activateSwimmingPoseForZipcaster(self)) {
                return EntityPose.SWIMMING;
            }
        } else if (this.isStickingToWall()) {
            return EntityPose.CROUCHING;
        }
        return original;
    }

    @Override
    public double ziptoit$modifyEffectiveGravityForPostZipcast(double original) {
        if(this.slowFallForZipcast()) {
            return ZipcastManager.postZipcastSlowfallAmountForPlayer((PlayerEntity) (Object) this, original);
        }
        return original;
    }

    @Override
    public void ziptoit$startZipcast(ZipcastTarget zipcastTarget) {
        this.zipcastTarget = zipcastTarget;
        this.zipcastLine = new ZipcastLine((PlayerEntity) (Object) this, 8);
        this.zipcasting = true;
        this.stickingToWall = false;
        this.zipcastTicks = 0;
        this.wallTicks = 0;
        this.slowFallForZipcast = false;

        ZipcastManager.endWallStick((PlayerEntity) (Object) this, false, true);
    }

    @Override
    public void ziptoit$endZipcast() {
        this.zipcasting = false;
        this.zipcastTarget = null;
        this.zipcastLine = null;
        this.noClipForZipcast = false;
    }

    @Override
    public void ziptoit$stickToWall(Vec3d wallPos) {
        this.stickingToWall = true;
        this.wallTicks = 0;
        this.slowFallForZipcast = true;
    }

    @Override
    public void ziptoit$endWallStick() {
        this.stickingToWall = false;
    }

    @Override
    public void ziptoit$softCancelZipcast() {
        this.zipcastTarget = null;
        this.zipcasting = false;
        this.stickingToWall = false;
        this.noClipForZipcast = false;
        this.zipcastTicks = 0;
        this.wallTicks = 0;

        this.slowFallForZipcast = true;
    }

    @Override
    public void ziptoit$hardCancelZipcast() {
        this.ziptoit$softCancelZipcast();
        this.slowFallForZipcast = false;
    }

    @Override
    public boolean isZipcasting() {
        return this.zipcasting;
    }

    @Override
    public void setIsZipcasting(boolean zipcasting) {
        this.zipcasting = zipcasting;
    }

    @Override
    public boolean isStickingToWall() {
        return this.stickingToWall;
    }

    @Override
    public void setIsStickingToWall(boolean isStickingToWall) {
        this.stickingToWall = isStickingToWall;
    }

    @Override
    public ZipcastTarget zipcastTarget() {
        return this.zipcastTarget;
    }

    @Override
    public void setZipcastTarget(ZipcastTarget zipcastTarget) {
        this.zipcastTarget = zipcastTarget;
    }

    @Override
    public ZipcastLine getZipcastLine() {
        return this.zipcastLine;
    }

    @Override
    public void setZipcastLine(ZipcastLine zipcastLine) {
        this.zipcastLine = zipcastLine;
    }

    @Override
    public boolean noClipForZipcast() {
        return this.noClipForZipcast;
    }

    @Override
    public void setNoClipForZipcast(boolean noClipForZipcast) {
        this.noClipForZipcast = noClipForZipcast;
    }

    @Override
    public boolean slowFallForZipcast() {
        return this.slowFallForZipcast;
    }

    @Override
    public void setSlowFallForZipcast(boolean slowFallForZipcast) {
        this.slowFallForZipcast = slowFallForZipcast;
    }

    @Override
    public int zipcastTicks() {
        return this.zipcastTicks;
    }

    @Override
    public void setZipcastTicks(int zipcastTicks) {
        this.zipcastTicks = zipcastTicks;
    }

    @Override
    public int wallTicks() {
        return this.wallTicks;
    }

    @Override
    public void setWallTicks(int wallTicks) {
        this.wallTicks = wallTicks;
    }



    @Override
    public int ticksSinceZipcastActivate() {
        return this.ticksSinceLastZipcast;
    }

    @Override
    public void setTicksSinceZipcastActivate(int ticksSinceLastZipcast) {
        this.ticksSinceLastZipcast = ticksSinceLastZipcast;
    }
}