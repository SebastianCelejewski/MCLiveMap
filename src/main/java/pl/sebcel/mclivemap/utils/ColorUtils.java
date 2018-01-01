package pl.sebcel.mclivemap.utils;

import java.awt.Color;

public class ColorUtils {

    public static Color getColor(String colorCode) {
        if (colorCode == null) {
            return null;
        }

        if (colorCode.trim().length() == 0) {
            return null;
        }

        if (colorCode.trim().length() != 6) {
            throw new IllegalArgumentException("Color code should have 6 characters representing red, green, and blue in hexadecimal format");
        }

        String redStr = colorCode.substring(0, 2);
        String greenStr = colorCode.substring(2, 4);
        String blueStr = colorCode.substring(4, 6);

        return new Color(hex2int(redStr), hex2int(greenStr), hex2int(blueStr));
    }

    private static int hex2int(String hexString) {
        return Integer.parseInt(hexString, 16);
    }
}