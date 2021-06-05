package io.shapez.core;

public class DpiManager {

    public static double smoothenDpi(final double dpi) {
        if (dpi < 0.05) {
            return 0.05;
        } else if (dpi < 0.2) {
            return Math.floor((Math.round(dpi / 0.04) * 0.04) * 100.0) / 100.0;
        } else if (dpi < 1) {
            return Math.floor((Math.round(dpi / 0.1) * 0.1) * 10.0) / 10.0;
        } else if (dpi < 4) {
            return Math.floor((Math.round(dpi / 0.5) * 0.5) * 10.0) / 10.0;
        } else {
            return 4;
        }
    }
}
