package net.superkat.ziptoit.particle;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.superkat.ziptoit.ZipToIt;

public class ZipToItParticles {

    public static final ParticleType<ZipcastZoomParticleEffect> ZIPCAST_ZOOM = FabricParticleTypes.complex(ZipcastZoomParticleEffect.CODEC, ZipcastZoomParticleEffect.PACKET_CODEC);
    public static final ParticleType<ZipcastLandParticleEffect> ZIPCAST_LAND = FabricParticleTypes.complex(ZipcastLandParticleEffect.CODEC, ZipcastLandParticleEffect.PACKET_CODEC);
    public static final ParticleType<ZipcastImpactEffect> ZIPCAST_IMPACT = FabricParticleTypes.complex(ZipcastImpactEffect.CODEC, ZipcastImpactEffect.PACKET_CODEC);
    public static final ParticleType<ZipcastImpactSplatterEffect> ZIPCAST_IMPACT_SPLATTER = FabricParticleTypes.complex(ZipcastImpactSplatterEffect.CODEC, ZipcastImpactSplatterEffect.PACKET_CODEC);

    public static void init() {
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(ZipToIt.MOD_ID, "zipcast_zoom"), ZIPCAST_ZOOM);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(ZipToIt.MOD_ID, "zipcast_land"), ZIPCAST_LAND);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(ZipToIt.MOD_ID, "zipcast_impact"), ZIPCAST_IMPACT);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(ZipToIt.MOD_ID, "zipcast_impact_splatter"), ZIPCAST_IMPACT_SPLATTER);
    }

}
