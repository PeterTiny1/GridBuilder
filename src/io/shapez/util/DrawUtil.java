package io.shapez.util;

import io.shapez.core.DrawParameters;

import java.awt.*;
import java.awt.image.BufferedImage;

public class DrawUtil {
    public static void drawCentered(Graphics2D context, int x, int y, double size) {
        draw(context, x - size / 2, y - size / 2, size, size);
    }

    static private void draw(Graphics2D context, double x, double y, double width, double height) {
    }

    public static void drawCachedCentered(BufferedImage image, DrawParameters parameters, double x, double y, double size) {
        drawCached(image, parameters, x - size / 2, y - size / 2, size, size, true);
    }

    private static void drawCached(BufferedImage image, DrawParameters parameters, double x, double y, double width, double height, boolean clipping) {
//        Rectangle visibleRect = parameters.visibleRect;
//
//        String scale = parameters.desiredAtlasScale;
        parameters.context.drawImage(
                image,
                (int) x,
                (int) y,
                (int) width,
                (int) height,
                null
        );
    }
}
