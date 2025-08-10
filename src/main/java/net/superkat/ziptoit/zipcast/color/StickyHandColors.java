package net.superkat.ziptoit.zipcast.color;

public class StickyHandColors {
    // Java.awt.Color can't be used because of a specific issue on some Mac's. So, this is the alternative
    // The colors are commented out for easy viewing / editing within an IDE (should your IDE have a color picker)
    // You may need to import the Color class to see the color, so don't forgot to optimize imports afterwards!


//    Color red = new Color(245, 25, 37);
//    Color altRed = new Color(252, 0, 113);
//    Color brightRed = new Color(255, 219, 219);
//    Color previewRed = new Color(255, 0, 0);
    public static final ZipcastColor RED = ZipcastColor.fromRgbIntegers(
            235, 0, 12,
            252, 0, 113,
            255, 219, 219,
            255, 0, 0
    );

//    Color yellow = new Color(250, 228, 84);
//    Color altYellow = new Color(253, 164, 63);
//    Color brightYellow = new Color(252, 247, 212);
//    Color previewYellow = new Color(255, 255, 0);
    public static final ZipcastColor YELLOW = ZipcastColor.fromRgbIntegers(
            250, 228, 84,
            253, 164, 63,
            252, 247, 212,
            255, 255, 0
    );

    public static void init() {
        // NO OP - init method may not be needed but just in case I guess
    }

    public static boolean zipcastColorIsDefault(ZipcastColor color) {
        return (color == RED || color == YELLOW);
    }

}