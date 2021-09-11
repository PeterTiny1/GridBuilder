package io.shapez.game;

import io.shapez.core.DrawParameters;
import io.shapez.game.components.StaticMapEntityComponent;
import io.shapez.game.themes.LightTheme;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class MapView extends BaseMap {
    BufferedImage cachedBackgroundBuffer = null;

    public MapView(final GameRoot root) {
        super(root);

        this.internalInitializeCachedBackgroundCanvases();
    }

    private void internalInitializeCachedBackgroundCanvases() {
        final byte dims = GlobalConfig.tileSize;
        final byte dpi = 1;
        final BufferedImage canvas = MapView.makeBuffer(dims * dpi, dims * dpi);

        final Graphics context = canvas.getGraphics();
        context.setColor(LightTheme.Map.background);
        context.fillRect(0, 0, dims, dims);

        final int borderWidth = LightTheme.Map.gridLineWidth;
        context.setColor(LightTheme.Map.grid);
        context.fillRect(0, 0, dims, borderWidth);
        context.fillRect(0, borderWidth, borderWidth, dims);

        context.fillRect(dims - borderWidth, borderWidth, borderWidth, dims - 2 * borderWidth);
        context.fillRect(borderWidth, dims - borderWidth, dims, borderWidth);

        this.cachedBackgroundBuffer = canvas;
    }

    public static BufferedImage makeBuffer(final int w, final int h) {
        assert w > 0 && h > 0;

        return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    }

    public void drawBackground(final DrawParameters parameters) throws IOException {
        if (!this.root.app.settings.getAllSettings().disableTileGrid) {
            final byte dpi = 1;
            parameters.context.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            parameters.context.scale(1.0 / dpi, 1.0 / dpi);
            parameters.context.setPaint(new TexturePaint(cachedBackgroundBuffer, new Rectangle2D.Float(0, 0, cachedBackgroundBuffer.getWidth(), cachedBackgroundBuffer.getHeight())));
            parameters.context.fillRect(parameters.visibleRect.x * dpi, parameters.visibleRect.y * dpi, parameters.visibleRect.width * dpi, parameters.visibleRect.height * dpi);
            parameters.context.scale(dpi, dpi);
        }

        this.drawVisibleChunks(parameters, MapChunkView.Methods.drawBackgroundLayer);
    }

    private void drawVisibleChunks(final DrawParameters parameters, final MapChunkView.Methods method) throws IOException {
        final Rectangle cullRange = new Rectangle(parameters.visibleRect.x / GlobalConfig.tileSize, parameters.visibleRect.y / GlobalConfig.tileSize, parameters.visibleRect.width / GlobalConfig.tileSize, parameters.visibleRect.height / GlobalConfig.tileSize);
        final int top = cullRange.y;
        final int right = cullRange.x + cullRange.width;
        final int bottom = cullRange.y + cullRange.height;
        final int left = cullRange.x;

        final int border = 0;
        final int minY = top - border;
        final int maxY = bottom + border;
        final int minX = left - border;
        final int maxX = right - border;

        final int chunkStartsX = minX / GlobalConfig.mapChunkSize;
        final int chunkStartsY = minY / GlobalConfig.mapChunkSize;

        final int chunkEndX = maxX / GlobalConfig.mapChunkSize;
        final int chunkEndY = maxY / GlobalConfig.mapChunkSize;

        for (int chunkX = chunkStartsX; chunkX <= chunkEndX; chunkX++) {
            for (int chunkY = chunkStartsY; chunkY <= chunkEndY; ++chunkY) {
                final MapChunkView chunk = this.root.map.getChunk(chunkX, chunkY);
                switch (method) {
                    case drawBackgroundLayer -> chunk.drawBackgroundLayer(parameters);
                    case drawForegroundDynamicLayer -> chunk.drawForegroundDynamicLayer(parameters);
                    case drawForegroundStaticLayer -> chunk.drawForegroundStaticLayer(parameters);
                }
            }
        }
    }

    public void drawForeground(final DrawParameters parameters) throws IOException {
        this.drawVisibleChunks(parameters, MapChunkView.Methods.drawForegroundDynamicLayer);
        this.drawVisibleChunks(parameters, MapChunkView.Methods.drawForegroundStaticLayer);
    }

    public void placeStaticEntity(final Entity entity) {
        final StaticMapEntityComponent staticComp = entity.components.StaticMapEntity;
        final Rectangle rect = staticComp.getTileSpaceBounds();
        for (int dx = 0; dx < rect.width; ++dx) {
            for (int dy = 0; dy < rect.height; ++dy) {
                final int x = rect.x + dx;
                final int y = rect.y + dy;
                this.getOrCreateChunkAtTile(x, y).setLayerContentFromWorldCoords(x, y, entity, entity.layer);
            }
        }
    }

    private MapChunkView getOrCreateChunkAtTile(final int tileX, final int tileY) {
        final int chunkX = (int) Math.floor((double) tileX / (double) GlobalConfig.mapChunkSize);
        final int chunkY = (int) Math.floor((double) tileY / (double) GlobalConfig.mapChunkSize);
        return this.getChunk(chunkX, chunkY);
    }
}
