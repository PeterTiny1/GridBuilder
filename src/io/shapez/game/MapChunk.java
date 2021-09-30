package io.shapez.game;

import io.shapez.Application;
import io.shapez.core.Layer;
import io.shapez.core.Vector;
import io.shapez.game.items.ColorItem;
import io.shapez.managers.SettingsManager;
import io.shapez.util.TileUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MapChunk {
    public final int tileX;
    public final int tileY;
    public final GameRoot root;
    public final int x, y;
    public final ArrayList<Patch> patches = new ArrayList<>();
    public final Rectangle tileSpaceRectangle;
    Rectangle drawn;
    public final BaseItem[][] lowerLayer = new BaseItem[GlobalConfig.mapChunkSize][GlobalConfig.mapChunkSize];
    public final Entity[][] contents = new Entity[GlobalConfig.mapChunkSize][GlobalConfig.mapChunkSize];
    private final Entity[][] wireContents = new Entity[GlobalConfig.mapChunkSize][GlobalConfig.mapChunkSize];
    private final ArrayList<Entity> containedEntities = new ArrayList<>();
    private final HashMap<Layer, ArrayList<Entity>> containedEntitiesByLayer = new HashMap<>() {
        {
            put(Layer.Regular, new ArrayList<>());
            put(Layer.Wires, new ArrayList<>());
        }

    };


    public MapChunk(final GameRoot root, final int x, final int y) {
        this.root = root;
        this.x = x;
        this.y = y;
        this.tileX = x * GlobalConfig.mapChunkSize;
        this.tileY = y * GlobalConfig.mapChunkSize;
        tileSpaceRectangle = new Rectangle(this.tileX, this.tileY, GlobalConfig.mapChunkSize, GlobalConfig.mapChunkSize);

        generateLowerLayer();
    }

    public void generateLowerLayer() {
        final String code = x + "|" + y + "|" + root.map.seed;
        final Random rng = new Random(generateHash(code));
        final Vector chunkCenter = new Vector(x + 0.5, this.y + 0.5);
        final double distanceToOriginInChunks = Math.round(chunkCenter.length());
        final double max = Math.max(0, Math.min(4, distanceToOriginInChunks / 8));
        double colorPatchChance = 0.9 - max;
        if (colorPatchChance < 0.2) colorPatchChance = 0.2;
        if (rng.nextDouble() < colorPatchChance) {
            final double colorPatchSize = Math.max(2, Math.round(1 + max));
            internalGenerateShapePatch(rng, colorPatchSize,
                    rng.nextInt(4));
        }
    }

    private void internalGenerateShapePatch(final Random rng, final double colorPatchSize, final int distanceToOriginInChunks) {
        final ArrayList<Colors> availableColors = new ArrayList<>() {{
            add(Colors.red);
            add(Colors.green);
        }};
        if (distanceToOriginInChunks > 2) {
            availableColors.add(Colors.blue);
        }
        this.internalGeneratePatch(rng, colorPatchSize, new ColorItem(availableColors.get(rng.nextInt(availableColors.size()))));
    }

    private void internalGeneratePatch(final Random rng, final double patchSize, final BaseItem item) {
        final int border = (int) (patchSize / 2) + 3;
        final int patchX = (int) Math.floor(rng.nextDouble() * (GlobalConfig.mapChunkSize - border - 1 - border) + border);
        final int patchY = (int) Math.floor(rng.nextDouble() * (GlobalConfig.mapChunkSize - border - 1 - border) + border);

        final Vector avgPos = new Vector(0, 0);
        int patchesDrawn = 0;

        var i = 0;
        while (i < patchSize) {
            final double circleRadius = Math.min(1 + i, patchSize);
            final double circleRadiusSquare = circleRadius * circleRadius;
            final double circleOffsetRadius = (patchSize - i) / 2 + 2;

            final double circleScaleX = rng.nextDouble() * 0.2 + 0.9;
            final double circleScaleY = rng.nextDouble() * 0.2 + 0.9;

            final int circleX = patchX + (int) Math.floor(rng.nextDouble() * (circleOffsetRadius * 2) - circleOffsetRadius);
            final int circleY = patchY + (int) Math.floor(rng.nextDouble() * (circleOffsetRadius * 2) - circleOffsetRadius);
            var dx = -circleRadius * circleScaleX - 2;
            while (dx <= circleRadius * circleScaleX + 2) {
                var dy = -circleRadius * circleScaleY - 2;
                while (dy <= circleRadius * circleScaleY + 2) {
                    final int x = (int) Math.round(circleX + dx);
                    final int y = (int) Math.round(circleY + dy);
                    if (x >= 0 && x <= GlobalConfig.mapChunkSize && y >= 0 && y <= GlobalConfig.mapChunkSize && (x < lowerLayer.length) && (y < lowerLayer.length)) {
                        final double originalDx = dx / circleScaleX;
                        final double originalDy = dy / circleScaleY;
                        if (originalDx * originalDx + originalDy * originalDy <= circleRadiusSquare) {
                            lowerLayer[x][y] = item;
                            ++patchesDrawn;
                            avgPos.x += x;
                            avgPos.y += y;
                        }
                    }
                    ++dy;
                }
                ++dx;
            }
            i++;
        }
        this.patches.add(new Patch(avgPos.divideScalar(patchesDrawn), item, patchSize));
    }

    private long generateHash(final String str) {
        var hash = 0;
        if (str.length() == 0) return hash;
        for (int i = 0; i < str.length(); i++) {
            final int charCode = str.toCharArray()[i];
            hash = (hash << 7) - hash + charCode;
            hash &= hash;
        }
        return hash;
    }

    public void drawChunk(final Graphics2D g, final int offsetX, final int offsetY, final int gridOffsetX, final int gridOffsetY, final int scale, boolean containsEntity) {
        final int _x = x * GlobalConfig.mapChunkSize + gridOffsetX;
        final int _y = y * GlobalConfig.mapChunkSize + gridOffsetY; // Micro-optimization: avoid computing inside loops!
        final int _s = GlobalConfig.mapChunkSize * scale;
        final int constX = (_x) * scale + offsetX;
        final int constY = (_y) * scale + offsetY;
        // Do NOT touch this. I have done this with a very good reason.
        int entCount = 0;
        for (final Entity[] content : contents) {
            int _k = 0;
            while (_k < contents.length) {
                if (content[_k] != null) entCount++;
                _k++;
            }
        }
        if (!containsEntity && entCount != 0) {
            containsEntity = true;
            Application.usedChunks.remove(this);
            Application.usedChunks.add(this);
        }
        if (containsEntity && entCount == 0) {
            containsEntity = false;
            Application.usedChunks.remove(this);
        }

        if (scale > GlobalConfig.zoomedScale) {
            int i = 0;
            while (i < GlobalConfig.mapChunkSize) {
                int j = 0;
                while (j < GlobalConfig.mapChunkSize) {
                    drawn = new Rectangle((x * GlobalConfig.mapChunkSize + gridOffsetX + i) * scale + offsetX, (y * GlobalConfig.mapChunkSize + gridOffsetY + j) * scale + offsetY, scale, scale);
                    if (lowerLayer[i][j] != null) {
                        g.setColor(lowerLayer[i][j].getBackgroundColorAsResource());
                        g.fillRect(drawn.x, drawn.y, drawn.width, drawn.height);
                    }
                    if (contents[i][j] != null) {
                        double rotation = 0;
                        if (contents[i][j].direction != null) {
                            rotation = switch (contents[i][j].direction) {
                                case Top -> Math.toRadians(0);
                                case Right -> Math.toRadians(90);
                                case Bottom -> Math.toRadians(180);
                                case Left -> Math.toRadians(270);
                            };
                        }
                        g.translate(drawn.x, drawn.y);
                        g.rotate(rotation, drawn.width >> 1, drawn.height >> 1);
                        g.drawImage(TileUtil.getTileTexture(contents[i][j].tile), 0, 0, drawn.width, drawn.height, null);
                        g.rotate(-rotation, drawn.width >> 1, drawn.height >> 1);
                        g.translate(-drawn.x, -drawn.y);
                    }
                    // Up: x, y-1
                    // Right: x+1, y
                    // Down: x, y+1
                    // Left: x-1, y
                    j++;
                }
                i++;
            }
            // Draw grid
            i = 0;
            g.setColor(Color.gray);

            while (i < GlobalConfig.mapChunkSize) {
                final int movX = (_x + i) * scale + offsetX;
                final int movY = (_y + i) * scale + offsetY;

//                g.drawLine(movX, constY, movX, constY + _s);
//                g.drawLine(constX, movY, constX + _s, movY);

                if (SettingsManager.drawChunkEdges) {
                    g.setColor(Color.RED);
                    int lowCount = 0;

                    for (final BaseItem[] item : lowerLayer) {
                        int _k = 0;
                        while (_k < lowerLayer.length) {
                            if (item[_k] != null) lowCount++;
                            _k++;
                        }
                    }
                    g.drawString("Chunk " + x + "/" + y, constX, constY);
                    g.drawString("Entities " + entCount, constX, constY + 15);
                    g.drawString("LowLayer " + (lowCount - 31), constX, constY + 30);
                    g.drawString("Used " + String.valueOf(Application.usedChunks.contains(this)).toUpperCase(), constX, constY + 45);
                    g.drawString("Bad " + String.valueOf((!Application.usedChunks.contains(this) && entCount != 0) || Application.usedChunks.contains(this) && entCount == 0).toUpperCase(), constX, constY + 60);
                    g.setColor(Color.gray);
                }
                i++;
            }

        } else {
            final int movY = (y * GlobalConfig.mapChunkSize + gridOffsetY) * scale + offsetY;
            drawn = new Rectangle((x * GlobalConfig.mapChunkSize + gridOffsetX) * scale + offsetX, movY, GlobalConfig.mapChunkSize * scale, GlobalConfig.mapChunkSize * scale);
            if (!containsEntity) {
                g.setColor(Color.gray);
                g.fillRect(drawn.x, drawn.y, drawn.width, drawn.height);
                final int movX = (_x) * scale + offsetX;

                g.setColor(Color.black);
                g.drawLine(movX, constY, movX, constY + _s);

                g.drawLine(constX, movY, constX + _s, movY);
            }
        }
    }

    public Entity getLayerContentFromWorldCoords(final double worldX, final double worldY, final Layer layer) {
        final int localX = (int) (worldX - this.tileX);
        final int localY = (int) (worldY - this.tileY);
        if (layer == Layer.Regular) {
            return this.contents[localX][localY];
        } else {
            return this.wireContents[localX][localY];
        }
    }

    public Entity[] getLayersContentMultipleFromWorldCoords(final double worldX, final double worldY) {
        final int localX = (int) (worldX - this.tileX);
        final int localY = (int) (worldY - this.tileY);
        final Entity regularContent = this.contents[localX][localY];
        final Entity wireContent = this.wireContents[localX][localY];

        final ArrayList<Entity> result = new ArrayList<>();
        if (regularContent != null) {
            result.add(regularContent);
        }
        if (wireContent != null) {
            result.add(wireContent);
        }
        return (Entity[]) result.toArray();
    }

    public void setLayerContentFromWorldCoords(final int tileX, final int tileY, final Entity contents, final Layer layer) {
        final int localX = tileX - this.tileX;
        final int localY = tileY - this.tileY;
        assert localX >= 0;
        assert localY >= 0;
        assert localX < GlobalConfig.mapChunkSize;
        assert localY < GlobalConfig.mapChunkSize;

        final Entity oldContents;
        if (layer == Layer.Regular) {
            oldContents = this.contents[localX][localY];
        } else {
            oldContents = this.wireContents[localX][localY];
        }

        assert contents != null || oldContents == null;

        if (layer == Layer.Regular) {
            this.contents[localX][localY] = contents;
        } else {
            this.wireContents[localX][localY] = contents;
        }

        if (contents != null) {
            if (!this.containedEntities.contains(contents)) {
                this.containedEntities.add(contents);
            }

            if (!this.containedEntitiesByLayer.get(layer).contains(contents)) {
                this.containedEntitiesByLayer.get(layer).add(contents);
            }
        }
    }

    public static class Patch {
        public final Vector pos;
        public final BaseItem item;
        private final double size;

        public Patch(final Vector pos, final BaseItem item, final double size) {
            this.pos = pos;
            this.item = item;
            this.size = size;
        }
    }
}
