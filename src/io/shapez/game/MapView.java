package io.shapez.game;

import io.shapez.core.DrawParameters;
import io.shapez.game.themes.LightTheme;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class MapView extends BaseMap {
    final byte backgroundCacheDPI = 2;
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

    public static BufferedImage makeBuffer(int w, int h) {
        assert w > 0 && h > 0;

        return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    }

    public void drawBackground(DrawParameters parameters) throws IOException {
        if (!this.root.app.settings.getAllSettings().disableTileGrid) {
            byte dpi = this.backgroundCacheDPI;
            parameters.context.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            parameters.context.scale(1.0 / dpi, 1.0 / dpi);

            parameters.context.setPaint(new TexturePaint(cachedBackgroundBuffer, new Rectangle2D.Float(0, 0, cachedBackgroundBuffer.getWidth(), cachedBackgroundBuffer.getHeight())));
            parameters.context.fillRect(parameters.visibleRect.x * dpi, parameters.visibleRect.y * dpi, parameters.visibleRect.width * dpi, parameters.visibleRect.height * dpi);
            parameters.context.scale(dpi, dpi);
        }

        this.drawVisibleChunks(parameters, MapChunkView.Methods.drawBackgroundLayer);
    }

    private void drawVisibleChunks(DrawParameters parameters, MapChunkView.Methods method) throws IOException {
        Rectangle cullRange = new Rectangle(parameters.visibleRect.x / GlobalConfig.tileSize, parameters.visibleRect.y / GlobalConfig.tileSize, parameters.visibleRect.width / GlobalConfig.tileSize, parameters.visibleRect.height / GlobalConfig.tileSize);
        int top = cullRange.y;
        int right = cullRange.x + cullRange.width;
        int bottom = cullRange.y + cullRange.height;
        int left = cullRange.x;

        int border = 0;
        int minY = top - border;
        int maxY = bottom + border;
        int minX = left - border;
        int maxX = right - border;

        int chunkStartsX = minX / GlobalConfig.mapChunkSize;
        int chunkStartsY = minY / GlobalConfig.mapChunkSize;

        int chunkEndX = maxX / GlobalConfig.mapChunkSize;
        int chunkEndY = maxY / GlobalConfig.mapChunkSize;

        for (int chunkX = chunkStartsX; chunkX <= chunkEndX; chunkX++) {
            for (int chunkY = chunkStartsY; chunkY <= chunkEndY; ++chunkY) {
                MapChunkView chunk = this.root.map.getChunk(chunkX, chunkY);
                switch (method) {
                    case drawBackgroundLayer -> chunk.drawBackgroundLayer(parameters);
                    case drawForegroundDynamicLayer -> chunk.drawForegroundDynamicLayer(parameters);
                    case drawForegroundStaticLayer -> chunk.drawForegroundStaticLayer(parameters);
                }
            }
        }
    }

    public void drawForeground(DrawParameters parameters) throws IOException {
        this.drawVisibleChunks(parameters, MapChunkView.Methods.drawForegroundDynamicLayer);
        this.drawVisibleChunks(parameters, MapChunkView.Methods.drawForegroundStaticLayer);
    }
}
