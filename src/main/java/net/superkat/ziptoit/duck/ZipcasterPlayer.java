package net.superkat.ziptoit.duck;

import net.minecraft.util.math.Vec3d;
import net.superkat.ziptoit.item.StickyHandItem;
import net.superkat.ziptoit.zipcast.line.ZipcastLine;
import net.superkat.ziptoit.zipcast.movement.ZipcastTarget;

public interface ZipcasterPlayer {

    void ziptoit$startZipcast(ZipcastTarget zipcastTarget);

    void ziptoit$endZipcast();

    void ziptoit$stickToWall(Vec3d wallPos);

    void ziptoit$endWallStick();

    /**
     * Zipcasting, sorta-survival friendly canceling (gives slow falling)
     */
    void ziptoit$softCancelZipcast();

    /**
     * Full reset of zipcasting related fields
     */
    void ziptoit$hardCancelZipcast();


    boolean isZipcasting();

    void setIsZipcasting(boolean zipcasting);

    boolean isStickingToWall();

    void setIsStickingToWall(boolean isStickingToWall);

    ZipcastTarget zipcastTarget();

    void setZipcastTarget(ZipcastTarget zipcastTarget);

    ZipcastLine getZipcastLine();

    void setZipcastLine(ZipcastLine zipcastLine);

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

    int ticksSinceZipcastActivate();

    void setTicksSinceZipcastActivate(int ticksSinceLastZipcast);

    default void increaseLastZipcastActivateTicks() {
        this.setTicksSinceZipcastActivate(this.ticksSinceZipcastActivate() + 1);
    }

    default boolean shouldPlayZipcastActiveSound() {
        return this.ticksSinceZipcastActivate() >= StickyHandItem.TICKS_UNTIL_ZIPCAST_ACTIVE_SOUND;
    }

    boolean showZipcastDeathMessage();

    void setShowZipcastDeathMessage(boolean show);

    boolean allowZipcastDuringZipcast();

    void setAllowZipcastDuringZipcast(boolean allow);

}
