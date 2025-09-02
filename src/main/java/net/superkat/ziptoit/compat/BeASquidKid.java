package net.superkat.ziptoit.compat;

import io.github.afamiliarquiet.be_a_doll.BeAMaid;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.FallLocation;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class BeASquidKid {
    public static final List<FallLocation> DOLL_FALL_LOCATIONS = new ArrayList<>();
    // wow these are hard to come up with on the spot
    public static final FallLocation NEEDLE = registerDollZipcaster("needle");
    public static final FallLocation SPLATTED = registerDollZipcaster("splatted");
    public static final FallLocation UNRAVELED = registerDollZipcaster("unraveled");
    public static final FallLocation BLANKET = registerDollZipcaster("blanket");

    public static boolean playerIsDoll(LivingEntity player) {
        return player instanceof PlayerEntity beWaryOfSquidKid && BeAMaid.isDoll(beWaryOfSquidKid);
    }

    public static FallLocation rollDollMessage(LivingEntity player) {
        int index = player.getRandom().nextInt(DOLL_FALL_LOCATIONS.size());
        return DOLL_FALL_LOCATIONS.get(index);
    }

    private static FallLocation registerDollZipcaster(String id) {
        FallLocation fallLocation = new FallLocation("zipcast_doll." + id);
        DOLL_FALL_LOCATIONS.add(fallLocation);
        return fallLocation;
    }

}
