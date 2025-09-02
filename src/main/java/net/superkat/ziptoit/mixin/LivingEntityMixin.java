package net.superkat.ziptoit.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public class LivingEntityMixin extends EntityMixin {

    @ModifyReturnValue(method = "getEffectiveGravity", at = @At(value = "RETURN"))
    public double ziptoit$modifyEffectiveGravityForPostZipcast(double original) {
        return original;
    }

    @ModifyReturnValue(method = "computeFallDamage", at = @At("RETURN"))
    public int ziptoit$modifyFallDamageForZipcaster(int original) {
        return original;
    }

//    @WrapOperation(method = "handleFallDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;serverDamage(Lnet/minecraft/entity/damage/DamageSource;F)V"))
//    public void ziptoit$modifyFallDamageSourceForZipcasterDamageSource(LivingEntity instance, DamageSource damageSource, float v, Operation<Void> original) {
//        if(instance instanceof ZipcasterPlayer zipcasterPlayer && (zipcasterPlayer.isZipcasting() || zipcasterPlayer.slowFallForZipcast())) {
//            DamageSource zipcasterDamageSource = new DamageSource(
//                    instance.getWorld().getRegistryManager()
//                            .getOrThrow(RegistryKeys.DAMAGE_TYPE)
//                            .getEntry(ZipToItDamageTypes.ZIPCASTER.getValue()).get()
//            );
//            original.call(instance, zipcasterDamageSource, v);
//            return;
//        }
//
//        original.call(instance, damageSource, v);
//    }

}
