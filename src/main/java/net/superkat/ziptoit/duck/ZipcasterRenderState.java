package net.superkat.ziptoit.duck;

import net.superkat.ziptoit.zipcast.movement.ZipcastTarget;

public interface ZipcasterRenderState {
    boolean isZipcasting();

    void setIsZipcasting(boolean isZipcasting);

    ZipcastTarget zipcastTarget();

    void setZipcastTarget(ZipcastTarget zipcastTarget);

    int zipcastTicks();

    void setZipcastTicks(int ticks);
}
