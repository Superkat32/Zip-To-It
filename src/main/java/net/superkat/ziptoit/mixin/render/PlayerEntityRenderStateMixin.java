package net.superkat.ziptoit.mixin.render;

import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.superkat.ziptoit.duck.ZipcasterRenderState;
import net.superkat.ziptoit.zipcast.movement.ZipcastTarget;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerEntityRenderState.class)
public class PlayerEntityRenderStateMixin implements ZipcasterRenderState {
    public boolean zipcasting = false;
    public ZipcastTarget zipcastTarget = null;
    public int zipcastTicks = 0;

    @Override
    public boolean isZipcasting() {
        return this.zipcasting;
    }

    @Override
    public void setIsZipcasting(boolean isZipcasting) {
        this.zipcasting = isZipcasting;
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
    public void setZipcastTicks(int ticks) {
        this.zipcastTicks = ticks;
    }
}
