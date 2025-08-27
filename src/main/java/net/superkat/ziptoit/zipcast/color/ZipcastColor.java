package net.superkat.ziptoit.zipcast.color;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Colors;
import net.minecraft.util.DyeColor;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.ColorHelper;

public record ZipcastColor(int color, int altColor, int brightColor, int previewColor, boolean alternate, boolean rainbow) {

    public static final Codec<ZipcastColor> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
                Codecs.RGB.fieldOf("color").forGetter(ZipcastColor::color),
                Codecs.RGB.fieldOf("altColor").forGetter(ZipcastColor::altColor),
                Codecs.RGB.fieldOf("brightColor").forGetter(ZipcastColor::brightColor),
                Codecs.RGB.fieldOf("previewColor").forGetter(ZipcastColor::previewColor),
                Codec.BOOL.optionalFieldOf("alternate", false).forGetter(ZipcastColor::alternate),
                Codec.BOOL.optionalFieldOf("rainbow", false).forGetter(ZipcastColor::rainbow)
        ).apply(instance, ZipcastColor::new)
    );

    public static final PacketCodec<RegistryByteBuf, ZipcastColor> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, ZipcastColor::color,
            PacketCodecs.INTEGER, ZipcastColor::altColor,
            PacketCodecs.INTEGER, ZipcastColor::brightColor,
            PacketCodecs.INTEGER, ZipcastColor::previewColor,
            PacketCodecs.BOOLEAN, ZipcastColor::alternate,
            PacketCodecs.BOOLEAN, ZipcastColor::rainbow,
            ZipcastColor::new
    );

    public static ZipcastColor fromFloats(
            float red, float green, float blue,
            float altRed, float altGreen, float altBlue,
            float brightRed, float brightGreen, float brightBlue,
            float previewRed, float previewGreen, float previewBlue,
            boolean alternate
    ) {
        int color = ColorHelper.fromFloats(1f, red, green, blue);
        int altColor = ColorHelper.fromFloats(1f, altRed, altGreen, altBlue);
        int brightColor = ColorHelper.fromFloats(1f, brightRed, brightGreen, brightBlue);
        int previewColor = ColorHelper.fromFloats(1f, previewRed, previewGreen, previewBlue);
        return new ZipcastColor(color, altColor, brightColor, previewColor, alternate, false);
    }

    public static ZipcastColor fromFloats(
            float red, float green, float blue,
            float altRed, float altGreen, float altBlue,
            float brightRed, float brightGreen, float brightBlue,
            float previewRed, float previewGreen, float previewBlue
    ) {
        return fromFloats(
                red, green, blue,
                altRed, altGreen, altBlue,
                brightRed, brightGreen, brightBlue,
                previewRed, previewGreen, previewBlue,
                false
        );
    }

    public static ZipcastColor fromRgbIntegers(
            int red, int green, int blue,
            int altRed, int altGreen, int altBlue,
            int brightRed, int brightGreen, int brightBlue,
            int previewRed, int previewGreen, int previewBlue,
            boolean alternate
    ) {
        return ZipcastColor.fromFloats(
                red / 255f, green / 255f, blue / 255f,
                altRed / 255f, altGreen / 255f, altBlue / 255f,
                brightRed / 255f, brightGreen / 255f, brightBlue / 255f,
                previewRed / 255f, previewGreen / 255f, previewBlue / 255f,
                alternate
        );
    }

    public static ZipcastColor fromRgbIntegers(
            int red, int green, int blue,
            int altRed, int altGreen, int altBlue,
            int brightRed, int brightGreen, int brightBlue,
            int previewRed, int previewGreen, int previewBlue
    ) {
        return ZipcastColor.fromRgbIntegers(
                red, green, blue,
                altRed, altGreen, altBlue,
                brightRed, brightGreen, brightBlue,
                previewRed, previewGreen, previewBlue,
                false
        );
    }

    public static ZipcastColor fromRainbow() {
        int white = Colors.WHITE;
        return new ZipcastColor(DyeColor.PURPLE.getEntityColor(), DyeColor.PINK.getEntityColor(), white, DyeColor.RED.getEntityColor(), false, true);
    }

}
