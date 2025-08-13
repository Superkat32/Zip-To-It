package net.superkat.ziptoit.particle;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.Direction;
import org.joml.Vector3f;

public record ZipcastLandParticleEffect(int age, float scale, int fadeOutTicks, int scaleUpTicks, Direction direction, Vector3f color) implements ParticleEffect {
    public static final MapCodec<ZipcastLandParticleEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codecs.POSITIVE_INT.optionalFieldOf("age", 12).forGetter(particle -> particle.age),
                    Codecs.POSITIVE_FLOAT.optionalFieldOf("scale", 0.5f).forGetter(particle -> particle.scale),
                    Codecs.POSITIVE_INT.optionalFieldOf("fade_ticks", 4).forGetter(particle -> particle.age),
                    Codecs.POSITIVE_INT.optionalFieldOf("scale_ticks", 8).forGetter(particle -> particle.scaleUpTicks),
                    net.minecraft.util.math.Direction.CODEC.optionalFieldOf("direction", net.minecraft.util.math.Direction.DOWN).forGetter(particle -> particle.direction),
                    Codecs.VECTOR_3F.optionalFieldOf("color", new Vector3f(1f, 1f, 1f)).forGetter(particle -> particle.color)
            ).apply(instance, ZipcastLandParticleEffect::new)
    );

    public static final PacketCodec<RegistryByteBuf, ZipcastLandParticleEffect> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, ZipcastLandParticleEffect::age,
            PacketCodecs.FLOAT, ZipcastLandParticleEffect::scale,
            PacketCodecs.INTEGER, ZipcastLandParticleEffect::fadeOutTicks,
            PacketCodecs.INTEGER, ZipcastLandParticleEffect::scaleUpTicks,
            Direction.PACKET_CODEC, ZipcastLandParticleEffect::direction,
            PacketCodecs.VECTOR_3F, ZipcastLandParticleEffect::color,
            ZipcastLandParticleEffect::new
    );

    @Override
    public ParticleType<?> getType() {
        return ZipToItParticles.ZIPCAST_LAND;
    }
}
