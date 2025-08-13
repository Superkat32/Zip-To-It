package net.superkat.ziptoit.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.superkat.ziptoit.zipcast.color.ZipcastColor;

public record ZipcastZoomParticleEffect(ZipcastColor zipcastColor, float yaw, float pitch, float stretch) implements ParticleEffect {
    public static final ZipcastColor DEFAULT_COLOR = ZipcastColor.fromFloats(1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f);
    public static final MapCodec<ZipcastZoomParticleEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    ZipcastColor.CODEC.optionalFieldOf("zipcast_color", DEFAULT_COLOR).forGetter(particle -> particle.zipcastColor),
                    Codec.FLOAT.fieldOf("yaw").forGetter(particle -> particle.yaw),
                    Codec.FLOAT.fieldOf("pitch").forGetter(particle -> particle.pitch),
                    Codec.FLOAT.optionalFieldOf("stretch", 1f).forGetter(particle -> particle.stretch)
            ).apply(instance, ZipcastZoomParticleEffect::new)
    );

    public static final PacketCodec<RegistryByteBuf, ZipcastZoomParticleEffect> PACKET_CODEC = PacketCodec.tuple(
            ZipcastColor.PACKET_CODEC, particle -> particle.zipcastColor,
            PacketCodecs.FLOAT, particle -> particle.yaw,
            PacketCodecs.FLOAT, particle -> particle.pitch,
            PacketCodecs.FLOAT, particle -> particle.stretch,
            ZipcastZoomParticleEffect::new
    );

    @Override
    public ParticleType<?> getType() {
        return ZipToItParticles.ZIPCAST_ZOOM;
    }
}
