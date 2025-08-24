package net.superkat.ziptoit.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.superkat.ziptoit.zipcast.movement.ZipcastTarget;
import org.jetbrains.annotations.Nullable;

/**
 * The order of events are as follows:
 * <ul>
 *     <li>{@link ZipcasterEvents#ZIPCAST_START} - Player used Zippy Sticky Hand.</li>
 *     <li>{@link ZipcasterEvents#ALLOW_WALL_STICK} - Client-side event to check if a player can stick to a specific wall - allows you to cancel it.</li>
 *     <li>{@link ZipcasterEvents#WALL_STICK_START} - Player successfully stuck to a wall.</li>
 *     <li>{@link ZipcasterEvents#ZIPCAST_END} - Player either stuck to wall, hit the ground or ceiling, or cancelled their Zipcast.</li>
 *     <li>{@link ZipcasterEvents#WALL_STICK_END} - Player either jumped or sneaked off a wall, or zipped to another location.</li>
 * </ul> <br>
 * Note that Wall Stick Start gets called BEFORE Zipcast End!
 */
public class ZipcasterEvents {

    /**
     * Called when a player has just finished using their Zippy Sticky Hand item, and has started a Zipcast. <br><br>
     * Gives you access to the player and their ZipcastTarget.
     */
    public static final Event<ZipcastStart> ZIPCAST_START = EventFactory.createArrayBacked(ZipcastStart.class, callbacks -> (player, zipcastTarget) -> {
        for (ZipcastStart callback : callbacks) {
            callback.onZipcastStart(player, zipcastTarget);
        }
    });

    /**
     * Called <b>ON THE CLIENT</b> when a player attempts to stick to a wall.<br><br>
     * This is called AFTER the normal wall sticking check has been preformed and passed.
     */
    public static final Event<AllowWallStick> ALLOW_WALL_STICK = EventFactory.createArrayBacked(AllowWallStick.class, callbacks -> (player, zipcastTarget, playerTeleportPos, wallBlockPos) -> {
        for (AllowWallStick callback : callbacks) {
            if(!callback.allowWallStick(player, zipcastTarget, playerTeleportPos, wallBlockPos)) {
                return false;
            }
        }

        return true;
    });

    /**
     * Called when a player successfully sticks to a wall.<br><br>
     *
     * <b>CALLED BEFORE ZIPCAST END!</b>
     */
    public static final Event<WallStickStart> WALL_STICK_START = EventFactory.createArrayBacked(WallStickStart.class, callbacks -> (player,  playerTeleportPos, wallBlockPos) -> {
        for (WallStickStart callback : callbacks) {
            callback.onWallStickStart(player, playerTeleportPos, wallBlockPos);
        }
    });

    /**
     * Called when a player finishes or cancels their Zipcast.<br><br>
     * Players may not always stick to a wall right after this, as zipcasting to the floor or ceiling will disallow sticking to the wall.<br><br>
     * <b>CALLED AFTER WALL STICK START!</b>
     */
    public static final Event<ZipcastEnd> ZIPCAST_END = EventFactory.createArrayBacked(ZipcastEnd.class, callbacks -> (player, zipcastTarget, wasCancelled) -> {
        for (ZipcastEnd callback : callbacks) {
            callback.onZipcastEnd(player, zipcastTarget, wasCancelled);
        }
    });

    /**
     * Called after a player has stopped sticking to a wall, either because they jumped, sneaked, zipped to another location, or their wall stick timer ran out (1 minute).
     */
    public static final Event<WallStickEnd> WALL_STICK_END = EventFactory.createArrayBacked(WallStickEnd.class, callbacks -> (player, wallBlockPos, jumped) -> {
        for (WallStickEnd callback : callbacks) {
            callback.onWallStickEnd(player, wallBlockPos, jumped);
        }
    });

    @FunctionalInterface
    public interface ZipcastStart {
        /**
         * @param player The player who used the Zipcaster.
         * @param zipcastTarget The ZipcastTarget of the player, which contains the zip location, speed, and ZipcastColor object.
         */
        void onZipcastStart(ServerPlayerEntity player, ZipcastTarget zipcastTarget);
    }

    @FunctionalInterface
    public interface ZipcastEnd {
        /**
         * @param player The player who used the Zipcaster.
         * @param zipcastTarget The ZipcastTarget of the player, which contains the zip location, speed, and ZipcastColor object.
         * @param wasCancelled If the Zipcast was cancelled, most likely from the player sneaking while zipcasting.
         */
        void onZipcastEnd(ServerPlayerEntity player, @Nullable ZipcastTarget zipcastTarget, boolean wasCancelled);
    }

    @FunctionalInterface
    public interface AllowWallStick {
        /**
         * @param player The <b>CLIENT PLAYER</b> who used the Zipcaster.
         * @param zipcastTarget The ZipcastTarget of the player, which contains zip location, speed, and ZipcastColor object.
         * @param playerTeleportPos The position the player will be teleported to if they successfully stick to the wall. This is normally a little bit in front of the wall position, based on the player's scale.
         * @param wallBlockPos The BlockPos of the wall the player is trying to stick to.
         * @return If the player is allowed to stick to the wall.
         */
        boolean allowWallStick(PlayerEntity player, ZipcastTarget zipcastTarget, Vec3d playerTeleportPos, BlockPos wallBlockPos);
    }

    @FunctionalInterface
    public interface WallStickStart {
        /**
         * @param player The player who used the Zipcaster, and is now sticking to the wall.
         * @param playerTeleportPos The position the player will be teleported to if they successfully stick to the wall. This is normally a little bit in front of the wall position, based on the player's scale.
         * @param wallBlockPos The BlockPos of the wall the player is trying to stick to.
         */
        void onWallStickStart(ServerPlayerEntity player, Vec3d playerTeleportPos, BlockPos wallBlockPos);
    }

    @FunctionalInterface
    public interface WallStickEnd {
        /**
         * @param player The player who used the Zipcaster, and is now no longer sticking to the wall.
         * @param wallBlockPos The BlockPos of the wall the player was sticking to.
         * @param jumped If the player jumped - they gain extra jump height if they did jump.
         */
        void onWallStickEnd(ServerPlayerEntity player, BlockPos wallBlockPos, boolean jumped);
    }

}
