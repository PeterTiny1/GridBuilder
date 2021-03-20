package io.shapez.managers.providers;

import java.awt.*;
import java.util.Date;
import java.util.Random;

public class MiscProvider {
    public static final Date date = new Date();
    public static final String year = String.valueOf(date.getYear()+1900);

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
            " - Green!"
    };

    public static final Dimension defDimension = new Dimension(70, 70);
    public static final Dimension defWndDimension = new Dimension(500, 300);

    public static final String gameName = "GridBuilder";
    public static final String mainWndName = gameName + " - Java Edition";
    public static final String settingsWndName = gameName + " - Settings";
    public static final String moreWndName = gameName + " - More";


    public static String getRandomTitlebar(){
        Random rng = new Random();
        return splashes[rng.nextInt(splashes.length)];
    }
}
