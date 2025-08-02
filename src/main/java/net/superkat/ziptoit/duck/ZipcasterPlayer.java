package net.superkat.ziptoit.duck;

import net.minecraft.util.math.Vec3d;

public interface ZipcasterPlayer {

    void ziptoit$zipcastToPos(Vec3d pos, float speed, float speedIncreasePerTick);

    void ziptoit$endZipcastEarly();

    boolean isZipcasting();

    boolean isStickingToWall();

    Vec3d zipcastPos();

    float maxZipcastSpeed();

    float currentZipcastSpeed();

    int zipcastTicks();

    default void ziptoit$zipcastToPos(Vec3d pos) {
        this.ziptoit$zipcastToPos(pos, 1f, 0.05f);
    }


}
