package net.superkat.ziptoit.zipcast.color;

import net.minecraft.util.DyeColor;

import java.util.ArrayList;
import java.util.List;

public class StickyHandColors {
    // Java.awt.Color can't be used because of a specific issue on some Mac's. So, manual colors is the alternative
    // The Colors are commented out for easy viewing / editing within an IDE (should your IDE have a color picker)
    // You may need to import the Color class to see the color, but don't forgot to optimize imports afterwards!

    public static final List<DyeColor> RAINBOW_DYES = List.of(
            DyeColor.RED,
            DyeColor.ORANGE,
            DyeColor.YELLOW,
            DyeColor.LIME,
            DyeColor.BLUE,
            DyeColor.CYAN,
            DyeColor.LIGHT_BLUE,
            DyeColor.PINK,
            DyeColor.MAGENTA,
            DyeColor.PURPLE
    );

    private static final List<ZipcastColor> DEFAULT_COLORS = new ArrayList<>();

//    Color red = new Color(245, 25, 37);
//    Color altRed = new Color(252, 0, 113);
//    Color brightRed = new Color(255, 219, 219);
//    Color previewRed = new Color(255, 0, 0);
    public static final ZipcastColor RED = register(
            235, 0, 12,
            252, 0, 113,
            255, 219, 219,
            255, 0, 0
    );

//    Color orange = new Color(255, 177, 9);
//    Color altOrange = new Color(252, 113, 0);
//    Color brightOrange = new Color(255, 235, 209);
//    Color previewOrange = new Color(255, 152, 0);
    public static final ZipcastColor ORANGE = register(
            255, 177, 9,
            252, 113, 0,
            255, 235, 209,
            255, 152, 0
    );

//    Color yellow = new Color(250, 228, 84);
//    Color altYellow = new Color(253, 164, 63);
//    Color brightYellow = new Color(252, 247, 212);
//    Color previewYellow = new Color(255, 255, 0);
    public static final ZipcastColor YELLOW = register(
            250, 228, 84,
            253, 164, 63,
            252, 247, 212,
            255, 255, 0
    );

//    Color lime = new Color(129, 248, 43);
//    Color altLime = new Color(73, 248, 60);
//    Color brightLime = new Color(230, 255, 212);
//    Color previewLime = new Color(152, 255, 0);
    public static final ZipcastColor LIME = register(
            129, 248, 43,
            73, 248, 60,
            230, 255, 212,
            152, 255, 0
    );

//    Color green = new Color(43, 248, 70);
//    Color altGreen = new Color(60, 248, 104);
//    Color brightGreen = new Color(212, 255, 222);
//    Color previewGreen = new Color(0, 255, 0);
    public static final ZipcastColor GREEN = register(
            43, 248, 70,
            60, 248, 104,
            212, 255, 222,
            0, 255, 0
    );

//    Color blue = new Color(43, 132, 248);
//    Color altBlue = new Color(0, 105, 255);
//    Color brightBlue = new Color(217, 232, 252);
//    Color previewBlue = new Color(0, 0, 255);
    public static final ZipcastColor BLUE = register(
            43, 132, 248,
            0, 105, 255,
            217, 232, 252,
            0, 0, 255
    );

//    Color cyan = new Color(68, 255, 255);
//    Color altCyan = new Color(64, 255, 229);
//    Color brightCyan = new Color(217, 252, 251);
//    Color previewCyan = new Color(0, 255, 255);
    public static final ZipcastColor CYAN = register(
            68, 255, 255,
            64, 255, 229,
            217, 252, 251,
            0, 255, 255
    );

//    Color lightBlue = new Color(153, 246, 255);
//    Color altLightBlue = new Color(104, 216, 255);
//    Color brightLightBlue = new Color(217, 239, 252);
//    Color previewLightBlue = new Color(116, 250, 255);
    public static final ZipcastColor LIGHT_BLUE = register(
            153, 246, 255,
            104, 216, 255,
            217, 239, 252,
            116, 250, 255
    );

//    Color pink = new Color(255, 160, 255);
//    Color altPink = new Color(243, 122, 250);
//    Color brightPink = new Color(246, 229, 255);
//    Color previewPink = new Color(255, 116, 243);
    public static final ZipcastColor PINK = register(
            255, 160, 255,
            243, 122, 250,
            246, 229, 255,
            255, 116, 243
    );

//    Color magenta = new Color(255, 72, 231);
//    Color altMagenta = new Color(255, 34, 192);
//    Color brightMagenta = new Color(252, 190, 232);
//    Color previewMagenta = new Color(255, 62, 241);
    public static final ZipcastColor MAGENTA = register(
            255, 72, 231,
            255, 34, 192,
            252, 190, 232,
            255, 62, 241
    );

//    Color purple = new Color(149, 69, 255);
//    Color altPurple = new Color(115, 34, 255);
//    Color brightPurple = new Color(238, 223, 255);
//    Color previewPurple = new Color(152, 0, 255);
    public static final ZipcastColor PURPLE = register(
            149, 69, 255,
            115, 34, 255,
            238, 223, 255,
            152, 0, 255
    );

//    Color white = new Color(251, 235, 252);
//    Color altWhite = new Color(230, 219, 231);
//    Color brightWhite = new Color(253, 207, 255);
//    Color previewWhite = new Color(255, 255, 255);
    public static final ZipcastColor WHITE = register(
            251, 235, 252,
            230, 219, 231,
            253, 207, 255,
            255, 255, 255
    );

//    Color lightGray = new Color(234, 239, 239);
//    Color altLightGray = new Color(162, 167, 167);
//    Color brightLightGray = new Color(218, 255, 255);
//    Color previewLightGray = new Color(201, 201, 201);
    public static final ZipcastColor LIGHT_GRAY = register(
            234, 239, 239,
            162, 167, 167,
            218, 255, 255,
            201, 201, 201
    );

//    Color gray = new Color(168, 170, 172);
//    Color altGray = new Color(125, 127, 130);
//    Color brightGray = new Color(209, 235, 255);
//    Color previewGray = new Color(107, 107, 107);
    public static final ZipcastColor GRAY = register(
            234, 239, 239,
            162, 167, 167,
            218, 255, 255,
            201, 201, 201
    );

//    Color brown = new Color(97, 72, 59);
//    Color altBrown = new Color(101, 64, 37);
//    Color brightBrown = new Color(180, 144, 123);
//    Color previewBrown = new Color(54, 21, 1);
    public static final ZipcastColor BROWN = register(
            97, 72, 59,
            101, 64, 37,
            180, 144, 123,
            54, 21, 1
    );

//    Color black = new Color(33, 33, 41);
//    Color altBlack = new Color(13, 13, 17);
//    Color brightBlack = new Color(91, 91, 122);
//    Color previewBlack = new Color(0, 0, 0);
    public static final ZipcastColor BLACK = register(
            33, 33, 41,
            13, 13, 17,
            91, 91, 122,
            0, 0, 0
    );



    public static final ZipcastColor PRIDE = register(
            ZipcastColor.fromRainbow()
    );

    public static final ZipcastColor TRANS = register(
            153, 246, 255,
            255, 160, 255,
            251, 235, 252,
            116, 250, 255,
            true
    );

    public static void init() {
        // NO OP - init method may not be needed but just in case I guess
    }

    public static ZipcastColor register(
            int red, int green, int blue,
            int altRed, int altGreen, int altBlue,
            int brightRed, int brightGreen, int brightBlue,
            int previewRed, int previewGreen, int previewBlue
    ) {
        return register(
                red, green, blue,
                altRed, altGreen, altBlue,
                brightRed, brightGreen, brightBlue,
                previewRed, previewGreen, previewBlue,
                false
        );
    }

    public static ZipcastColor register(
            int red, int green, int blue,
            int altRed, int altGreen, int altBlue,
            int brightRed, int brightGreen, int brightBlue,
            int previewRed, int previewGreen, int previewBlue,
            boolean alternate
    ) {
        ZipcastColor color = ZipcastColor.fromRgbIntegers(
                red, green, blue,
                altRed, altGreen, altBlue,
                brightRed, brightGreen, brightBlue,
                previewRed, previewGreen, previewBlue, alternate
        );

        return register(color);
    }

    public static ZipcastColor register(ZipcastColor color) {
        DEFAULT_COLORS.add(color);
        return color;
    }

    public static boolean zipcastColorIsDefault(ZipcastColor color) {
        return DEFAULT_COLORS.contains(color);
    }

}