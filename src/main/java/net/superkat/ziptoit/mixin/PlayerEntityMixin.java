package net.superkat.ziptoit.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.superkat.ziptoit.duck.ZipcasterPlayer;
import net.superkat.ziptoit.zipcast.ZipcastManager;
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
    public Vec3d zipcastPos = null;
    @Unique
    public float maxZipcastSpeed = 0f;
    @Unique
    public float speedIncreasePerTick = 0f;
    @Unique
    public float currentZipcastSpeed = 0f;
    @Unique
    public boolean stickingToWall = false;
    @Unique
    public int zipcastTicks = 0;

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isSpectator()Z"))
//    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;hasVehicle()Z"))
    public void ziptoit$disableCollisionWhileZipcasting(CallbackInfo ci) {
        if(this.isZipcasting()) {
            PlayerEntity self = (PlayerEntity) (Object) this;
            if(ZipcastManager.shouldDisableCollisionForPlayer(self)) {
                this.noClip = true;
            }

            zipcastTicks++;
            ZipcastManager.tickZipcastingPlayer(self);
//            if(this.zipcastTicks() >= 60) {
//                this.ziptoit$endZipcastEarly();
//            }
        }
    }

    @Override
    public void ziptoit$zipcastToPos(Vec3d pos, float speed, float speedIncreasePerTick) {
        this.zipcastPos = pos;
        this.maxZipcastSpeed = speed;
        this.speedIncreasePerTick = speedIncreasePerTick;
        this.zipcasting = true;
        this.stickingToWall = false;
        this.zipcastTicks = 0;
    }

    @Override
    public void ziptoit$endZipcastEarly() {
        this.zipcasting = false;
        this.zipcastPos = null;
        this.maxZipcastSpeed = 0;
        this.speedIncreasePerTick = 0;
        this.zipcastTicks = 0;
    }

    @Override
    public boolean isZipcasting() {
        return this.zipcasting;
    }

    @Override
    public boolean isStickingToWall() {
        return this.stickingToWall;
    }

    @Override
    public Vec3d zipcastPos() {
        return this.zipcastPos;
    }

    @Override
    public float maxZipcastSpeed() {
        return this.maxZipcastSpeed;
    }

    @Override
    public float currentZipcastSpeed() {
        return this.currentZipcastSpeed;
    }

    @Override
    public int zipcastTicks() {
        return this.zipcastTicks;
    }
}