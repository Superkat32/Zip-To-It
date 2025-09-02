package net.superkat.ziptoit.zipcast.death;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.FallLocation;
import net.superkat.ziptoit.ZipToIt;
import net.superkat.ziptoit.compat.BeASquidKid;
import net.superkat.ziptoit.duck.ZipcasterPlayer;

import java.util.ArrayList;
import java.util.List;

public class ZipcastFallLocations {

    public static final List<FallLocation> ZIPCAST_FALL_LOCATIONS = new ArrayList<>();
    public static final FallLocation GROUND = registerZipcast("ground");
    public static final FallLocation ZIPCASTIC = registerZipcast("zipcastic");
    public static final FallLocation UNDERCOVER = registerZipcast("undercover");
    public static final FallLocation SUN = registerZipcast("sun");
    public static final FallLocation SODA = registerZipcast("soda");
    public static final FallLocation SNIPED = registerZipcast("sniped");
    public static final FallLocation FLOUNDER = registerZipcast("flounder");
    public static final FallLocation FELL = registerZipcast("fell");
    public static final FallLocation OVER = registerZipcast("over");
    public static final FallLocation SPLAT = registerZipcast("splat");
    public static final FallLocation NOT_STOLEN_FROM_TERRARIA = registerZipcast("notstolenfromterraria");
    public static final FallLocation NOT_STOLEN_FROM_FREE_BIRD = registerZipcast("notstolenfromfreebird");
    public static final FallLocation FALL = registerZipcast("fall");
    public static final FallLocation SPLATTED = registerZipcast("splatted");
    public static final FallLocation MONKEY_CRAB = registerZipcast("monkey_crab");
    public static final FallLocation FACTS = registerZipcast("facts");

    private static FallLocation registerZipcast(String id) {
        FallLocation fallLocation = new FallLocation("zipcast." + id);
        ZIPCAST_FALL_LOCATIONS.add(fallLocation);
        return fallLocation;
    }

    public static FallLocation modifyFallLocationForZipcaster(LivingEntity player, FallLocation original) {
        if(!(player instanceof ZipcasterPlayer zipcasterPlayer)) return original;

        if(zipcasterPlayer.showZipcastDeathMessage() || zipcasterPlayer.ticksSinceZipcastEnd() <= 60) {
            FallLocation msg = rollMessage(player);
            if(msg == FACTS) { // make it rare
                msg = rollMessage(player);
            }

            if(ZipToIt.beADollLoaded() && BeASquidKid.playerIsDoll(player)) {
                 msg = BeASquidKid.rollDollMessage(player);
            }

            return msg;
        }

        return original;
    }

    private static FallLocation rollMessage(LivingEntity player) {
        int index = player.getRandom().nextInt(ZIPCAST_FALL_LOCATIONS.size());
        return ZIPCAST_FALL_LOCATIONS.get(index);
    }

}
