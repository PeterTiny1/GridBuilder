package io.shapez.managers.providers;

import java.awt.*;
import java.util.Date;
import java.util.Random;

public class MiscProvider {
    public static final Date date = new Date();
    public static final String year = String.valueOf(date.getYear() + 1900);

    public static final String gameVersion = "1.1";

    public static final String[] splashes = {
            " - Java Edition!",
            " - Hardware Accelerated!",
            " - Faster than ever",
            " - 3/10 -IGN",
            " - Don't sue us!",
            " - In Development!",
            " - By PeterTiny1",
            " - Uses JSwing",
            " - Optimized!",
            " - Thanks tobspr",
            " - " + year,
            " - Made in Deutschland and the UK!",
            " - github.com/PeterTiny1/GridBuilder",
            " - Green!",
            " - Belts and belts and belts",
            " - " + gameVersion,
            " - When the!"
    };


    public static final byte OP_SAVE = 0;
    public static final byte OP_LOAD = 1;
    public static final byte OP_CLEAR = 2;

    public static final Dimension defDimensionBtn = new Dimension(70, 70);
    public static final Dimension defDimensionTxt = new Dimension(70, 30);
    public static final Dimension defWndDimension = new Dimension(500, 300);

    public static final String gameName = "GridBuilder";
    public static final String mainWndName = gameName + " - Java Edition";
    public static final String settingsWndName = gameName + " - Settings";
    public static final String moreWndName = gameName + " - More";


    public static String getRandomTitlebar() {
        Random rng = new Random();
        return splashes[rng.nextInt(splashes.length)];
    }

    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }

    public static int clampInt(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }

    public static byte clampByte(byte val, byte min, byte max) {
        return (byte) Math.max(min, Math.min(max, val));
    }
}
