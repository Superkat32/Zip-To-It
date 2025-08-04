package net.superkat.ziptoit.duck;

import net.minecraft.util.math.Vec3d;
import net.superkat.ziptoit.zipcast.ZipcastTarget;

public interface ZipcasterPlayer {

    void ziptoit$zipcastToPos(ZipcastTarget zipcastTarget);

    void ziptoit$cancelZipcast();


    boolean isZipcasting();

    void setIsZipcasting(boolean zipcasting);

    boolean isStickingToWall();

    void setIsStickingToWall(boolean isStickingToWall);

    ZipcastTarget zipcastTarget();

    void setZipcastTarget(ZipcastTarget zipcastTarget);
//
//    Vec3d zipcastPos();
//
//    void setZipcastPos(Vec3d zipcastPos);
//
//    float maxZipcastSpeed();
//
//    float currentZipcastSpeed();

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

    Vec3d lastZipcastVelocity();

    void setLastZipcastVelocity(Vec3d velocity);

}
