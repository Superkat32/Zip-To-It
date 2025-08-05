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

}
