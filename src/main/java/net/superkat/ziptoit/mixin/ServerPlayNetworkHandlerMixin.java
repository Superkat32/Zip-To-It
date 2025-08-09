package net.superkat.ziptoit.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.superkat.ziptoit.duck.ZipcasterPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {

    // Solution from hama's Dream Burst Spirit Vector mod - If you like this mod, you'll struggle to stop playing DBSV
    // https://github.com/HamaIndustries/spirit-vector/blob/1.21.1/src/main/java/symbolics/division/spirit_vector/mixin/ServerPlayNetworkHandlerMixin.java

    // hama you're probably gonna see this, so hi :)

    @ModifyExpressionValue(method = "onPlayerMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;isInTeleportationState()Z"))
    public boolean lieToServerThatWeAreActuallySimplyTeleportingAndAreDefinitelyNotPreformingAnyShenanigansToPreventRubberbanding(boolean actuallyTeleporting) {
        boolean fakeTeleporting = false;
        ServerPlayNetworkHandler self = (ServerPlayNetworkHandler) (Object) this;
        if(self.player instanceof ZipcasterPlayer zipcasterPlayer) {
            fakeTeleporting = zipcasterPlayer.isZipcasting() || zipcasterPlayer.isStickingToWall();
        }

        return actuallyTeleporting || fakeTeleporting;
    }

}
