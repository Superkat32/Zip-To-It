package net.superkat.ziptoit.mixin.render;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.superkat.ziptoit.duck.ZipcasterPlayer;
import net.superkat.ziptoit.duck.ZipcasterRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {

    @Inject(method = "updateRenderState(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;F)V", at = @At("TAIL"))
    public void ziptoit$updateZipcasterRenderState(AbstractClientPlayerEntity abstractClientPlayerEntity, PlayerEntityRenderState playerRenderState, float f, CallbackInfo ci) {
        if(!(abstractClientPlayerEntity instanceof ZipcasterPlayer zipcasterPlayer)) return;
        if(!(playerRenderState instanceof ZipcasterRenderState zipcasterRenderState)) return;

        zipcasterRenderState.setIsZipcasting(zipcasterPlayer.isZipcasting());
        zipcasterRenderState.setZipcastTarget(zipcasterPlayer.zipcastTarget());
        zipcasterRenderState.setZipcastTicks(zipcasterPlayer.zipcastTicks());
    }

}
