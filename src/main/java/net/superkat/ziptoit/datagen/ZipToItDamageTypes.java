package net.superkat.ziptoit.datagen;

import net.minecraft.entity.damage.DamageEffects;
import net.minecraft.entity.damage.DamageScaling;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DeathMessageType;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.superkat.ziptoit.ZipToIt;

public class ZipToItDamageTypes {

    public static final RegistryKey<DamageType> ZIPCASTER = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(ZipToIt.MOD_ID, "zipcaster"));

    public static void bootstrap(Registerable<DamageType> registerable) {
        registerable.register(ZIPCASTER, new DamageType(
                "zipcaster",
                DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER,
                0f,
                DamageEffects.HURT,
                DeathMessageType.FALL_VARIANTS
        ));
    }

}
