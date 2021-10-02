package io.shapez.game.systems;

import io.shapez.core.DrawParameters;
import io.shapez.game.*;
import io.shapez.game.themes.LightTheme;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class MapResourcesSystem extends GameSystem {
    public MapResourcesSystem(final GameRoot root) {
        super(root);
    }

    public void drawChunk(final DrawParameters parameters, final MapChunkView chunk) throws IOException {
        final BufferedImage basicChunkBackground = this.root.buffers.getForKey("mapresourcebg", chunk.renderKey, GlobalConfig.mapChunkSize, GlobalConfig.mapChunkSize, 1, this, chunk);
        drawSprite(parameters, basicChunkBackground, chunk.tileX * GlobalConfig.tileSize, chunk.tileY * GlobalConfig.tileSize, GlobalConfig.mapChunkWorldSize, GlobalConfig.mapChunkWorldSize);
        final Composite composite = parameters.context.getComposite();
        parameters.context.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f));
        if (this.root.app.settings.getAllSettings().lowQualityMapResources) {
            for (int i = 0; i < chunk.patches.size(); i++) {
                final MapChunk.Patch patch = chunk.patches.get(i);
                final double destX = chunk.x * GlobalConfig.mapChunkWorldSize + patch.pos.x * GlobalConfig.tileSize;
                final double destY = chunk.y * GlobalConfig.mapChunkWorldSize + patch.pos.y * GlobalConfig.tileSize;
                final double diameter = Math.min(80, 40 / parameters.zoomLevel);

                patch.item.drawItemCenteredClipped(destX, destY, parameters, diameter);
            }
        } else {
            final BaseItem[][] layer = chunk.lowerLayer;
            final Entity[][] layerEntities = chunk.contents;
            for (int x = 0; x < GlobalConfig.mapChunkSize; x++) {
                final BaseItem[] row = layer[x];
                final Entity[] rowEntities = layerEntities[x];
                final int worldX = (chunk.tileX + x) * GlobalConfig.tileSize;
                for (int y = 0; y < GlobalConfig.mapChunkSize; y++) {
                    final BaseItem lowerItem = row[y];
                    final Entity entity = rowEntities[y];
                    if (entity != null) {
                        continue;
                    }

                    if (lowerItem != null) {
                        final int worldY = (chunk.tileY + y) * GlobalConfig.tileSize;
                        final int destX = worldX + GlobalConfig.halfTileSize;
                        final int destY = worldY + GlobalConfig.halfTileSize;

                        lowerItem.drawItemCenteredClipped(destX, destY, parameters, GlobalConfig.defaultItemDiameter);
                    }
                }
            }
        }
        parameters.context.setComposite(composite);
    }

    private void drawSprite(final DrawParameters parameters, final BufferedImage sprite, final int x, final int y, final int w, final int h) {
        parameters.context.drawImage(sprite, x, y, w, h, null);
    }

    public void generateChunkBackground(final Graphics2D context, final int w, final int h, final MapChunkView chunk) {
        if (this.root.app.settings.getAllSettings().disableTileGrid) {
            context.setColor(LightTheme.Map.background);
            context.fillRect(0, 0, w, h);
        }

        context.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f));
        final BaseItem[][] layer = chunk.lowerLayer;
        for (int x = 0; x < GlobalConfig.mapChunkSize; x++) {
            final BaseItem[] row = layer[x];
            for (int y = 0; y < GlobalConfig.mapChunkSize; y++) {
                final BaseItem item = row[y];
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
