package net.superkat.ziptoit.particle;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.superkat.ziptoit.ZipToIt;

public class ZipToItParticles {

    // Surely forcing this to spawn won't cause any performance issues, right? (. ❛ ᴗ ❛.)
    public static final ParticleType<ZipcastZoomParticleEffect> ZIPCAST_ZOOM = FabricParticleTypes.complex(ZipcastZoomParticleEffect.CODEC, ZipcastZoomParticleEffect.PACKET_CODEC);

    public static void init() {
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(ZipToIt.MOD_ID, "zipcast_zoom"), ZIPCAST_ZOOM);
    }

}
