package io.shapez.util;

public class DebugUtil {

    public static void printTime(String type1, String type2, long t1, long t2) {
        long time = (t2 - t1) / 1000000;
        String _f = String.format("%s: %s%s", type1, time, type2);
        if (time > 400) _f = _f.concat(" [>400ms!!!!] ");
        System.out.println(_f);

    }
}
