package io.shapez.game;

import io.shapez.Application;
import io.shapez.core.Layer;
import io.shapez.core.Vector;
import io.shapez.game.items.ColorItem;
import io.shapez.managers.SettingsManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MapChunk {
    public final int tileX;
    public final int tileY;
    public GameRoot root;
    public final int x, y;
    public ArrayList<Patch> patches = new ArrayList<>();
    public Rectangle tileSpaceRectangle;
    Rectangle drawn;
    public BaseItem[][] lowerLayer;
    public Entity[][] contents;
    public MovingEntity[][] movingContents;
    private Entity[][] wireContents;


    public MapChunk(GameRoot root, int x, int y) {
        this.root = root;
        this.x = x;
        this.y = y;
        this.tileX = x * GlobalConfig.mapChunkSize;
        this.tileY = y * GlobalConfig.mapChunkSize;
        tileSpaceRectangle = new Rectangle(this.tileX, this.tileY, GlobalConfig.mapChunkSize, GlobalConfig.mapChunkSize);

        lowerLayer = new BaseItem[GlobalConfig.mapChunkSize][GlobalConfig.mapChunkSize];
        contents = new Entity[GlobalConfig.mapChunkSize][GlobalConfig.mapChunkSize];
        movingContents = new MovingEntity[GlobalConfig.mapChunkSize][GlobalConfig.mapChunkSize];

        generateLowerLayer();
    }

    public void generateLowerLayer() {
        String code = x + "|" + y + "|" + root.map.seed;
        Random rng = new Random(generateHash(code));
        Vector chunkCenter = new Vector(x + 0.5, this.y + 0.5);
        double distanceToOriginInChunks = Math.round(chunkCenter.length());
        final double max = Math.max(0, Math.min(4, distanceToOriginInChunks / 8));
        double colorPatchChance = 0.9 - max;
        if (colorPatchChance < 0.2) colorPatchChance = 0.2;
        if (rng.nextDouble() < colorPatchChance) {
            double colorPatchSize = Math.max(2, Math.round(1 + max));
            internalGenerateShapePatch(rng, colorPatchSize,
                    rng.nextInt(4));
        }
    }

    private void internalGenerateShapePatch(Random rng, double colorPatchSize, int distanceToOriginInChunks) {
        ArrayList<Colors> availableColors = new ArrayList<>() {{
            add(Colors.red);
            add(Colors.green);
        }};
        if (distanceToOriginInChunks > 2) {
            availableColors.add(Colors.blue);
        }
        this.internalGeneratePatch(rng, colorPatchSize, new ColorItem(availableColors.get(rng.nextInt(availableColors.size()))));
    }

    private void internalGeneratePatch(Random rng, double patchSize, BaseItem item) {
        int border = (int) (patchSize / 2) + 3;
        int patchX = (int) Math.floor(rng.nextDouble() * (GlobalConfig.mapChunkSize - border - 1 - border) + border);
        int patchY = (int) Math.floor(rng.nextDouble() * (GlobalConfig.mapChunkSize - border - 1 - border) + border);

        Vector avgPos = new Vector(0, 0);
        int patchesDrawn = 0;

        var i = 0;
        while (i < patchSize) {
            double circleRadius = Math.min(1 + i, patchSize);
            double circleRadiusSquare = circleRadius * circleRadius;
            double circleOffsetRadius = (patchSize - i) / 2 + 2;

            double circleScaleX = rng.nextDouble() * 0.2 + 0.9;
            double circleScaleY = rng.nextDouble() * 0.2 + 0.9;

            int circleX = patchX + (int) Math.floor(rng.nextDouble() * (circleOffsetRadius * 2) - circleOffsetRadius);
            int circleY = patchY + (int) Math.floor(rng.nextDouble() * (circleOffsetRadius * 2) - circleOffsetRadius);
            var dx = -circleRadius * circleScaleX - 2;
            while (dx <= circleRadius * circleScaleX + 2) {
                var dy = -circleRadius * circleScaleY - 2;
                while (dy <= circleRadius * circleScaleY + 2) {
                    int x = (int) Math.round(circleX + dx);
                    int y = (int) Math.round(circleY + dy);
                    if (x >= 0 && x <= GlobalConfig.mapChunkSize && y >= 0 && y <= GlobalConfig.mapChunkSize && (x < lowerLayer.length) && (y < lowerLayer.length)) {
                        double originalDx = dx / circleScaleX;
                        double originalDy = dy / circleScaleY;
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

    private long generateHash(String str) {
        var hash = 0;
        if (str.length() == 0) return hash;
        for (int i = 0; i < str.length(); i++) {
            int charCode = str.toCharArray()[i];
            hash = (hash << 7) - hash + charCode;
            hash &= hash;
        }
        return hash;
    }

//    private void setSides() {
//        if (!SettingsManager.drawChunkEdges) return;
//        for (int x = 0; x < GlobalConfig.mapChunkSize; x++) {
//            if (lowerLayer[x][0] == null) {
//                lowerLayer[x][0] = Color.GRAY;
//            }
//        }
//        for (int y = 0; y < GlobalConfig.mapChunkSize; y++) {
//            if (lowerLayer[0][y] == null) {
//                lowerLayer[0][y] = Color.GRAY;
//            }
//        }
//    }

    public void drawChunk(Graphics2D g, int offsetX, int offsetY, int gridOffsetX, int gridOffsetY, int scale, boolean containsEntity) {
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        int _x = x * GlobalConfig.mapChunkSize + gridOffsetX;
        int _y = y * GlobalConfig.mapChunkSize + gridOffsetY; // Micro-optimization: avoid computing inside loops!
        int _s = GlobalConfig.mapChunkSize * scale;
        int constX = (_x) * scale + offsetX;
        int constY = (_y) * scale + offsetY;
        // Do NOT touch this. I have done this with a very good reason.
        int entCount = 0, mEntCount = 0;

        /*  [IMPORTANT] DO NOT touch or modify  this code */
        for (MovingEntity[] ignored : movingContents) {
            int __k = 0;
            while (__k < movingContents.length) {
                if (movingContents[__k] != null) mEntCount++;
                __k++;
            }
        }
        for (Entity[] content : contents) {
            int _k = 0;
            while (_k < contents.length) {
                if (content[_k] != null) entCount++;
                _k++;
            }
        }
        if (!containsEntity && entCount != 0 || mEntCount != 0) {
            containsEntity = true;
            Application.usedChunks.remove(this);
            Application.usedChunks.add(this);
        }
        if (containsEntity && entCount == 0 || mEntCount == 0) {
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
                        g.drawImage(contents[i][j].texture, drawn.x, drawn.y, drawn.width, drawn.height, null);
                    }
                    // Up: x, y-1
                    // Right: x+1, y
                    // Down: x, y+1
                    // Left: x-1, y
                    assert movingContents[i] != null;
                    if (movingContents[i][j] != null) {
                        g.drawImage(movingContents[i][j].texture/*Not yet*/, drawn.x + drawn.width / 2, drawn.y + drawn.height / 2, drawn.width / 2, drawn.height / 2, null);
                    }
                    j++;
                }
                i++;
            }
            // Draw grid
            i = 0;
            g.setColor(Color.gray);

            while (i < GlobalConfig.mapChunkSize) {
                int movX = (_x + i) * scale + offsetX;
                int movY = (_y + i) * scale + offsetY;

                g.drawLine(movX, constY, movX, constY + _s);
                g.drawLine(constX, movY, constX + _s, movY);

                if (SettingsManager.drawChunkEdges) {
                    g.setColor(Color.RED);
                    int lowCount = 0;

                    for (BaseItem[] item : lowerLayer) {
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
            drawn = new Rectangle((x * GlobalConfig.mapChunkSize + gridOffsetX) * scale + offsetX, (y * GlobalConfig.mapChunkSize + gridOffsetY) * scale + offsetY, GlobalConfig.mapChunkSize * scale, GlobalConfig.mapChunkSize * scale);
            if (!containsEntity) {
                g.setColor(Color.gray);
                g.fillRect(drawn.x, drawn.y, drawn.width, drawn.height);
                int movX = (_x) * scale + offsetX;
                int movY = (y * GlobalConfig.mapChunkSize + gridOffsetY) * scale + offsetY;

                g.setColor(Color.black);
                g.drawLine(movX, constY, movX, constY + _s);

                g.drawLine(constX, movY, constX + _s, movY);
            }
        }
    }

    public Entity getLayerContentFromWorldCoords(double worldX, double worldY, Layer layer) {
        int localX = (int) (worldX - this.tileX);
        int localY = (int) (worldY - this.tileY);
        if (layer == Layer.Regular) {
            return this.contents[localX][localY];
        } else {
            return this.wireContents[localX][localY];
        }
    }

    public Entity[] getLayersContentMultipleFromWorldCoords(double worldX, double worldY) {
        int localX = (int) (worldX - this.tileX);
        int localY = (int) (worldY - this.tileY);
        Entity regularContent = this.contents[localX][localY];
        Entity wireContent = this.wireContents[localX][localY];

        ArrayList<Entity> result = new ArrayList<>();
        if (regularContent != null) {
            result.add(regularContent);
        }
        if (wireContent != null) {
            result.add(wireContent);
        }
        return (Entity[]) result.toArray();
    }

    public class Patch {
        public final Vector pos;
        public final BaseItem item;
        private final double size;

        public Patch(Vector pos, BaseItem item, double size) {
            this.pos = pos;
            this.item = item;
            this.size = size;
        }
    }
}
