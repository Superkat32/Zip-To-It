package net.superkat.ziptoit.duck;

import net.minecraft.util.math.Vec3d;
import net.superkat.ziptoit.zipcast.ZipcastTarget;

public interface ZipcasterPlayer {

    void ziptoit$startZipcast(ZipcastTarget zipcastTarget);

    void ziptoit$endZipcast();

    void ziptoit$stickToWall(Vec3d wallPos);

    void ziptoit$endWallStick();

    void ziptoit$cancelZipcast();


    boolean isZipcasting();

    void setIsZipcasting(boolean zipcasting);

    boolean isStickingToWall();

    void setIsStickingToWall(boolean isStickingToWall);

    ZipcastTarget zipcastTarget();

    void setZipcastTarget(ZipcastTarget zipcastTarget);

    boolean noClipForZipcast();

    void setNoClipForZipcast(boolean noClipForZipcast);

    boolean slowFallForZipcast();

    void setSlowFallForZipcast(boolean slowFallForZipcast);

    int zipcastTicks();

    void setZipcastTicks(int zipcastTicks);

    default void increaseZipcastTicks() {
        this.setZipcastTicks(this.zipcastTicks() + 1);
    }

    int wallTicks();

    void setWallTicks(int wallTicks);

    default void increaseWallTicks() {
        this.setWallTicks(this.wallTicks() + 1);
    }

}
