package net.superkat.ziptoit.particle;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.dynamic.Codecs;
import org.joml.Vector3f;

public record ZipcastImpactEffect(int age, float scale, Vector3f color) implements ParticleEffect {

    public static final MapCodec<ZipcastImpactEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codecs.POSITIVE_INT.optionalFieldOf("age", 10).forGetter(particle -> particle.age),
                    Codecs.POSITIVE_FLOAT.optionalFieldOf("scale", 0.1f).forGetter(particle -> particle.scale),
                    Codecs.VECTOR_3F.optionalFieldOf("color", new Vector3f(1f, 1f, 1f)).forGetter(particle -> particle.color)
            ).apply(instance, ZipcastImpactEffect::new)
    );

    public static final PacketCodec<RegistryByteBuf, ZipcastImpactEffect> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, ZipcastImpactEffect::age,
            PacketCodecs.FLOAT, ZipcastImpactEffect::scale,
            PacketCodecs.VECTOR_3F, ZipcastImpactEffect::color,
            ZipcastImpactEffect::new
    );

    @Override
    public ParticleType<?> getType() {
        return ZipToItParticles.ZIPCAST_IMPACT;
    }
}
