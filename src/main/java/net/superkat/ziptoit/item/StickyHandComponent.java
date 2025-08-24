package net.superkat.ziptoit.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.ColorHelper;
import net.superkat.ziptoit.zipcast.color.StickyHandColors;
import net.superkat.ziptoit.zipcast.color.ZipcastColor;

import java.util.function.Consumer;

public record StickyHandComponent(int zipRange, float zipSpeed, int maxZipUses, ZipcastColor zipcastColor) implements TooltipAppender {
    public static final StickyHandComponent DEFAULT = new StickyHandComponent(48, 2.25f, -1, StickyHandColors.YELLOW);
    public static final int INFINITE_ZIP_USES = -1;

    public static final Codec<StickyHandComponent> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codecs.POSITIVE_INT.optionalFieldOf("zip_range", 48).forGetter(StickyHandComponent::zipRange),
                    Codecs.POSITIVE_FLOAT.optionalFieldOf("zip_speed", 2.25f).forGetter(StickyHandComponent::zipSpeed),
                    Codec.INT.optionalFieldOf("max_zips", INFINITE_ZIP_USES).forGetter(StickyHandComponent::maxZipUses),
                    ZipcastColor.CODEC.optionalFieldOf("zipcast_color", StickyHandColors.YELLOW).forGetter(StickyHandComponent::zipcastColor)
            ).apply(instance, StickyHandComponent::new)
    );

    public static final PacketCodec<RegistryByteBuf, StickyHandComponent> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, StickyHandComponent::zipRange,
            PacketCodecs.FLOAT, StickyHandComponent::zipSpeed,
            PacketCodecs.INTEGER, StickyHandComponent::maxZipUses,
            ZipcastColor.PACKET_CODEC, StickyHandComponent::zipcastColor,
            StickyHandComponent::new
    );

    @Override
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type, ComponentsAccess components) {
        if(this.zipRange <= 0) return;

        textConsumer.accept(Text.translatable("item.ziptoit.zippy_hand.ziprange")
                .append(ScreenTexts.SPACE)
                .append(String.valueOf(this.zipRange))
                .formatted(Formatting.GRAY));

        if(this.zipSpeed != 2.25f) {
            textConsumer.accept(Text.translatable("item.ziptoit.zippy_hand.zipspeed")
                    .append(ScreenTexts.SPACE)
                    .append(String.valueOf(this.zipSpeed))
                    .formatted(Formatting.GRAY));
        }

        if(this.maxZipUses != INFINITE_ZIP_USES) {
            int zipsUsed = components.getOrDefault(ZipToItItems.ZIPS_USED_COMPONENT_TYPE, ZipsUsedComponent.DEFAULT).zipsUsed();
            textConsumer.accept(Text.translatable("item.ziptoit.zippy_hand.zipuses")
                    .append(ScreenTexts.SPACE)
                    .append(String.valueOf(this.maxZipUses - zipsUsed))
                    .formatted(Formatting.GRAY));
        }

        if(StickyHandColors.zipcastColorIsDefault(this.zipcastColor)) return;
        appendColorTooltip(textConsumer, "main_color", this.zipcastColor.color());
        appendColorTooltip(textConsumer, "alt_color", this.zipcastColor.altColor());
        appendColorTooltip(textConsumer, "bright_color", this.zipcastColor.brightColor());
        appendColorTooltip(textConsumer, "preview_color", this.zipcastColor.previewColor());
    }

    private void appendColorTooltip(Consumer<Text> textConsumer, String translation, int color) {
        float red = ColorHelper.getRedFloat(color);
        float green = ColorHelper.getGreenFloat(color);
        float blue = ColorHelper.getBlueFloat(color);

        String translationReal = "item.ziptoit.zippy_hand." + translation;

        Text redText = Text.literal(red + "f").formatted(Formatting.RED, Formatting.ITALIC).append(", ");
        Text greenText = Text.literal(green + "f").formatted(Formatting.GREEN, Formatting.ITALIC).append(", ");
        Text blueText = Text.literal(blue + "f").formatted(Formatting.BLUE, Formatting.ITALIC);

        textConsumer.accept(Text.translatable(translationReal)
                .append(ScreenTexts.SPACE)
                .withColor(color)
                .append(redText).append(greenText).append(blueText));
    }
}
