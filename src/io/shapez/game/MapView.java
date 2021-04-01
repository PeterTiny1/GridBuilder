package io.shapez.game;

import io.shapez.game.themes.LightTheme;

import java.awt.*;
import java.awt.image.BufferedImage;

public class MapView extends BaseMap {
    byte backgroundCacheDPI = 2;
    BufferedImage cachedBackgroundBuffer = null;

    public MapView(GameRoot root) {
        super(root);

        this.internalInitializeCachedBackgroundCanvases();
    }

    private void internalInitializeCachedBackgroundCanvases() {
        byte dims = GlobalConfig.tileSize;
        byte dpi = this.backgroundCacheDPI;
        BufferedImage canvas = makeBuffer(dims * dpi, dims * dpi);

        Graphics context = canvas.getGraphics();
        context.setColor(LightTheme.Map.background);
        context.fillRect(0, 0, dims, dims);

        int borderWidth = LightTheme.Map.gridLineWidth;
        context.setColor(LightTheme.Map.grid);
        context.fillRect(0, 0, dims, borderWidth);
        context.fillRect(0, borderWidth, borderWidth, dims);

        context.fillRect(dims - borderWidth, borderWidth, borderWidth, dims - 2 * borderWidth);
        context.fillRect(borderWidth, dims - borderWidth, dims, borderWidth);

        this.cachedBackgroundBuffer = canvas;
    }

    private BufferedImage makeBuffer(int w, int h) {
        assert w > 0 && h > 0;

        return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    }
}
