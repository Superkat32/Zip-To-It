package net.superkat.ziptoit.mixin.death;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.FallLocation;
import net.superkat.ziptoit.duck.ZipcasterPlayer;
import net.superkat.ziptoit.zipcast.death.ZipcastFallLocations;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FallLocation.class)
public class FallLocationMixin {

    @ModifyReturnValue(method = "fromEntity", at = @At("RETURN"))
    private static FallLocation ziptoit$modifyFallLocationDeathMessageForZipcaster(FallLocation original, LivingEntity entity) {
        if(entity instanceof ZipcasterPlayer) {
            // I'm sure this is fine yeah?
            return ZipcastFallLocations.modifyFallLocationForZipcaster(entity, original);
        }
        return original;
    }

}
