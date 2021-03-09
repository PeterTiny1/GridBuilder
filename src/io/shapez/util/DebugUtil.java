package io.shapez.util;

public class DebugUtil {

    public static void printTime(String type1, long t1, long t2){
        String _f =  String.format("%s: %s", type1, t2 - t1);
        System.out.println(_f);
    }
    public static void printTime(String type1, String type2, long t1, long t2){
        String _f =  String.format("%s: %s%s", type1, ((t2 - t1) / 1000000), type2);
        System.out.println(_f);
    }
}
