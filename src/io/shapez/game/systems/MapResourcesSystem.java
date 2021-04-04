package io.shapez.game.systems;

import io.shapez.core.DrawParameters;
import io.shapez.game.*;
import io.shapez.game.themes.LightTheme;

import java.awt.*;
import java.awt.image.BufferedImage;

public class MapResourcesSystem extends GameSystem {
    public MapResourcesSystem(GameRoot root) {
        super(root);
    }

    public void drawChunk(DrawParameters parameters, MapChunkView chunk) {
        BufferedImage basicChunkBackground = this.root.buffers.getForKey("mapresourcebg", chunk.renderKey, GlobalConfig.mapChunkSize, GlobalConfig.mapChunkSize, 1, this, chunk);
        drawSpriteClipped(parameters, basicChunkBackground, chunk.tileX * GlobalConfig.tileSize, chunk.tileY * GlobalConfig.tileSize, GlobalConfig.mapChunkWorldSize, GlobalConfig.mapChunkWorldSize, GlobalConfig.mapChunkSize, GlobalConfig.mapChunkSize);
        parameters.context.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f));

        if (this.root.app.settings.getAllSettings().lowQualityMapResources) {
            for (int i = 0; i < chunk.patches.size(); i++) {
                MapChunk.Patch patch = chunk.patches.get(i);
                double destX = chunk.x * GlobalConfig.mapChunkWorldSize + patch.pos.x * GlobalConfig.tileSize;
                double destY = chunk.y * GlobalConfig.mapChunkWorldSize + patch.pos.y * GlobalConfig.tileSize;
                double diameter = Math.min(80, 40 / parameters.zoomLevel);

                patch.item.drawItemCenteredClipped(destX, destY, parameters, diameter);
            }
        } else {
            BaseItem[][] layer = chunk.lowerLayer;
            Entity[][] layerEntities = chunk.contents;
            for (int x = 0; x < GlobalConfig.mapChunkSize; x++) {
                BaseItem[] row = layer[x];
                Entity[] rowEntities = layerEntities[x];
                int worldX = (chunk.tileX + x) * GlobalConfig.tileSize;
                for (int y = 0; y < GlobalConfig.mapChunkSize; y++) {
                    BaseItem lowerItem = row[y];
                    Entity entity = rowEntities[y];
                    if (entity != null) {
                        continue;
                    }

                    if (lowerItem != null) {
                        int worldY = (chunk.tileY + y) * GlobalConfig.tileSize;

                        int destX = worldX + GlobalConfig.halfTileSize;
                        int destY = worldY + GlobalConfig.halfTileSize;

                        lowerItem.drawItemCenteredClipped(destX, destY, parameters, GlobalConfig.defaultItemDiameter);
                    }
                }
            }
        }
        parameters.context.setComposite(AlphaComposite.Clear);
    }

    private void drawSpriteClipped(DrawParameters parameters, BufferedImage sprite, int x, int y, int w, int h, byte originalW, byte originalH) {
        Rectangle rect = new Rectangle(x, y, w, h);
        Rectangle intersection = rect.intersection(parameters.visibleRect);
        parameters.context.drawImage(sprite, ((intersection.x - x) / Math.max(w, 1)) * originalW, ((intersection.y - y) / Math.max(h, 1)) * originalH, (originalW * intersection.width) / Math.max(w, 1), (originalH * intersection.height) / Math.max(h, 1), intersection.x, intersection.y, intersection.width, intersection.height, null);
    }

    public void generateChunkBackground(BufferedImage canvas, Graphics2D context, int w, int h, int dpi, MapChunkView chunk) {
        if (this.root.app.settings.getAllSettings().disableTileGrid) {
            context.setColor(LightTheme.Map.background);
            context.fillRect(0, 0, w, h);
        } else {
            context.clearRect(0, 0, w, h);
        }
        context.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f));
        BaseItem[][] layer = chunk.lowerLayer;
        for (int x = 0; x < GlobalConfig.mapChunkSize; x++) {
            BaseItem[] row = layer[x];
            for (int y = 0; y < GlobalConfig.mapChunkSize; y++) {
                BaseItem item = row[y];
                if (item != null) {
                    context.setColor(item.getBackgroundColorAsResource());
                    context.fillRect(x, y, 1, 1);
                }
            }
        }

        if (this.root.app.settings.getAllSettings().displayChunkBorders) {
            context.setColor(LightTheme.Map.chunkBorders);
            context.fillRect(0, 0, w, 1);
            context.fillRect(0, 1, 1, h);
        }
    }
}
