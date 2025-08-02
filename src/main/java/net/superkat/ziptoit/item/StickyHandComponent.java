package net.superkat.ziptoit.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.dynamic.Codecs;

import java.util.function.Consumer;

public record StickyHandComponent(int zipRange) implements TooltipAppender {

    public static final Codec<StickyHandComponent> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codecs.POSITIVE_INT.optionalFieldOf("zip_range", 0).forGetter(StickyHandComponent::zipRange)
            ).apply(instance, StickyHandComponent::new)
    );

    public static final PacketCodec<ByteBuf, StickyHandComponent> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER,
            StickyHandComponent::zipRange,
            StickyHandComponent::new
    );

    @Override
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type, ComponentsAccess components) {
        if(this.zipRange <= 0) return;

        textConsumer.accept(Text.translatable("item.ziptoit.stickyhand.ziprange")
                .append(ScreenTexts.SPACE)
                .append(String.valueOf(this.zipRange))
                .formatted(Formatting.GRAY));
    }
}
