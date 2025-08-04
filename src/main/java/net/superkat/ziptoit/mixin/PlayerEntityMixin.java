package net.superkat.ziptoit.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.superkat.ziptoit.duck.ZipcasterPlayer;
import net.superkat.ziptoit.zipcast.ZipcastManager;
import net.superkat.ziptoit.zipcast.ZipcastTarget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin extends EntityMixin implements ZipcasterPlayer {
    @Unique
    public boolean zipcasting = false;
    @Unique
    public boolean stickingToWall = false;
    @Unique
    public ZipcastTarget zipcastTarget;
    @Unique
    public float currentZipcastSpeed = 0f;
    @Unique
    public int zipcastTicks = 0;
    @Unique
    public int wallTicks = 0;
    @Unique
    public Vec3d lastZipcastVelocity = Vec3d.ZERO;

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isSpectator()Z"))
    public void ziptoit$tickZipcastPlayer(CallbackInfo ci) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        ZipcastManager.tickZipcasterPlayer(self);
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
            ZipcastManager.travelZipcasting(instance, movementInput);
        } else if(this.isStickingToWall()) {
            ZipcastManager.travelStickingToWall(instance, movementInput);
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
    public void ziptoit$zipcastToPos(ZipcastTarget zipcastTarget) {
        this.zipcastTarget = zipcastTarget;
        this.zipcasting = true;
        this.stickingToWall = false;
        this.zipcastTicks = 0;
        this.wallTicks = 0;

        ZipcastManager.endWallStick((PlayerEntity) (Object) this, false);
    }

    @Override
    public void ziptoit$cancelZipcast() {
        this.zipcastTarget = null;
        this.zipcasting = false;
        this.stickingToWall = false;
        this.zipcastTicks = 0;
        this.wallTicks = 0;
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
    public Vec3d lastZipcastVelocity() {
        return this.lastZipcastVelocity;
    }

    @Override
    public void setLastZipcastVelocity(Vec3d velocity) {
        this.lastZipcastVelocity = velocity;
    }
}