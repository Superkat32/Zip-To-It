package net.superkat.ziptoit.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.superkat.ziptoit.duck.ZipcasterPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BipedEntityRenderer.class)
public class BipedEntityRendererMixin {

    @WrapOperation(method = "updateBipedRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isInSneakingPose()Z"))
    private static boolean ziptoit$renderPlayerAsSneakingWhenStickingToWall(LivingEntity instance, Operation<Boolean> original) {
        if(instance instanceof ZipcasterPlayer zipcasterPlayer) {
            if(zipcasterPlayer.isStickingToWall()) {
                return true;
            }
        }
        return original.call(instance);
    }

}
