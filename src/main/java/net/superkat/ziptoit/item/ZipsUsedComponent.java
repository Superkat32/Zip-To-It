package net.superkat.ziptoit.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.dynamic.Codecs;

public record ZipsUsedComponent(int zipsUsed) {
    public static final ZipsUsedComponent DEFAULT = new ZipsUsedComponent(0);

    public static final Codec<ZipsUsedComponent> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codecs.POSITIVE_INT.optionalFieldOf("zips_used", 0).forGetter(ZipsUsedComponent::zipsUsed)
            ).apply(instance, ZipsUsedComponent::new)
    );

    public static final PacketCodec<RegistryByteBuf, ZipsUsedComponent> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, ZipsUsedComponent::zipsUsed,
            ZipsUsedComponent::new
    );

}
