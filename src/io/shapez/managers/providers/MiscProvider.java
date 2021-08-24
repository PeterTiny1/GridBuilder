package io.shapez.managers.providers;

import java.awt.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class MiscProvider {
    public static final String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));

    public static final String gameVersion = "1.1";

    public static final String[] splashes = {
            " - Java Edition!",
            " - Hardware Accelerated!",
            " - Faster than ever",
            " - Don't sue us!",
            " - In Development!",
            " - By PeterTiny1",
            " - Uses JSwing",
            " - Optimized!",
            " - Thanks tobspr",
            " - " + MiscProvider.year,
            " - Made in Deutschland and the UK!",
            " - github.com/PeterTiny1/GridBuilder",
            " - Green!",
            " - Belts and belts and belts",
            " - " + MiscProvider.gameVersion,
            " - When the!"
    };


    public static final byte OP_SAVE = 0;
    public static final byte OP_LOAD = 1;
    public static final byte OP_CLEAR = 2;

    public static final Dimension defDimensionBtn = new Dimension(70, 70);
    public static final Dimension defDimensionTxt = new Dimension(70, 30);
    public static final Dimension defWndDimension = new Dimension(500, 300);

    public static final String gameName = "GridBuilder";
    public static final String mainWndName = MiscProvider.gameName + " - Java Edition";
    public static final String settingsWndName = MiscProvider.gameName + " - Settings";
    public static final String moreWndName = MiscProvider.gameName + " - More";


    public static String getRandomTitlebar() {
        final Random rng = new Random();
        return MiscProvider.splashes[rng.nextInt(MiscProvider.splashes.length)];
    }

    public static double clamp(final double val, final double min, final double max) {
        return Math.max(min, Math.min(max, val));
    }

    public static int clampInt(final int val, final int min, final int max) {
        return Math.max(min, Math.min(max, val));
    }

    public static byte clampByte(final byte val, final byte min, final byte max) {
        return (byte) Math.max(min, Math.min(max, val));
    }
}
