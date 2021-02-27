package io.shapez;

import com.sun.jdi.connect.Connector;

import java.awt.*;
import java.util.Random;

public class Chunk {
    public int x, y;
    Rectangle drawn;
    public Color[][] lowerLayer;
    public Entity[][] contents;

    public Chunk(int x, int y) {
        this.x = x;
        this.y = y;

        lowerLayer = new Color[GlobalConfig.mapChunkSize][GlobalConfig.mapChunkSize];
        contents = new Entity[GlobalConfig.mapChunkSize][GlobalConfig.mapChunkSize];

        setSides();
        generateLowerLayer();
    }

    private void generateLowerLayer() {
        String code = x + "|" + y + "|" + GlobalConfig.map.seed;
        Random rng = new Random(generateHash(code));
        Vector chunkCenter = new Vector(x + 0.5, this.y + 0.5);
        double distanceToOriginInChunks = Math.round(chunkCenter.length());
        final double max = Math.max(0, Math.min(4, distanceToOriginInChunks / 8));
        double colorPatchChance = 0.9 - max;
        if (rng.nextDouble() < colorPatchChance) {
            double colorPatchSize = Math.max(2, Math.round(1 + max));
            internalGeneratePatch(rng, colorPatchSize,
                    rng.nextInt(4));
        }
    }

    private void internalGeneratePatch(Random rng, double patchSize, int color) {
        int border = (int) (patchSize / 2) + 3;
        int patchX = (int) Math.floor(rng.nextDouble() * (GlobalConfig.mapChunkSize - border - 1 - border) + border);
        int patchY = (int) Math.floor(rng.nextDouble() * (GlobalConfig.mapChunkSize - border - 1 - border) + border);

        for (var i = 0; i < patchSize; i++) {
            double circleRadius = Math.min(1 + i, patchSize);
            double circleRadiusSquare = circleRadius * circleRadius;
            double circleOffsetRadius = (patchSize - i) / 2 + 2;

            double circleScaleX = rng.nextDouble() * 0.2 + 0.9;
            double circleScaleY = rng.nextDouble() * 0.2 + 0.9;

            int circleX = patchX + (int) Math.floor(rng.nextDouble() * (circleOffsetRadius * 2) - circleOffsetRadius);
            int circleY = patchY + (int) Math.floor(rng.nextDouble() * (circleOffsetRadius * 2) - circleOffsetRadius);
            for (var dx = -circleRadius * circleScaleX - 2; dx <= circleRadius * circleScaleX + 2; ++dx) {
                for (var dy = -circleRadius * circleScaleY - 2; dy <= circleRadius * circleScaleY + 2; ++dy) {
                    int x = (int) Math.round(circleX + dx);
                    int y = (int) Math.round(circleY + dy);
                    if (x >= 0 && x < GlobalConfig.mapChunkSize && y >= 0 && y <= GlobalConfig.mapChunkSize) {
                        double originalDx = dx / circleScaleX;
                        double originalDy = dy / circleScaleY;
                        if (originalDx * originalDx + originalDy * originalDy <= circleRadiusSquare) {
                            lowerLayer[x][y] = colorshapeTypeFromByte(color);
                        }
                    }
                }
            }
        }
    }
    private Color colorshapeTypeFromByte(int b){
        // temporary
        if(b == 0){
            return Color.RED;
        } else if(b == 1){
            return Color.GREEN;
        }else if(b == 2){
            return Color.BLUE;
        }else if(b == 3){
            return Color.LIGHT_GRAY; // uncolored shape patch!!!
        }else {
            throw new IllegalArgumentException("Color index is not valid");
        }
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

    private void setSides() {
        if(!SettingsManager.drawChunkEdges)return;
        for (int x = 0; x < GlobalConfig.mapChunkSize; x++) {
            if (lowerLayer[x][0] == null) {
                lowerLayer[x][0] = Color.GRAY;
            }
        }
        for (int y = 0; y < GlobalConfig.mapChunkSize; y++) {
            if (lowerLayer[0][y] == null) {
                lowerLayer[0][y] = Color.GRAY;
            }
        }
    }

    public void drawChunk(Graphics g, int offsetX, int offsetY, int gridOffsetX, int gridOffsetY, int scale, boolean containsEntity) {
        if (scale > GlobalConfig.zoomedScale) {
            for (int i = 0; i < GlobalConfig.mapChunkSize; i++) {
                for (int j = 0; j < GlobalConfig.mapChunkSize; j++) {
                    drawn = new Rectangle((x * GlobalConfig.mapChunkSize + gridOffsetX + i) * scale + offsetX, (y * GlobalConfig.mapChunkSize + gridOffsetY + j) * scale + offsetY, scale, scale);
                    if (lowerLayer[i][j] != null) {
                        g.setColor(lowerLayer[i][j]);
                        g.fillRect(drawn.x, drawn.y, drawn.width, drawn.height);
                    }
                    if (contents[i][j] != null) {
                        g.drawImage(contents[i][j].texture, drawn.x, drawn.y, drawn.width, drawn.height, null);
                    }
                }
            }
            g.setColor(Color.gray);
            // Draw grid
            for (int i = 0; i < GlobalConfig.mapChunkSize; i++)
            {
                int movX = (x * GlobalConfig.mapChunkSize + gridOffsetX + i) * scale + offsetX;
                int constY = (y * GlobalConfig.mapChunkSize + gridOffsetY) * scale + offsetY;
                g.drawLine(movX, constY, movX, constY + GlobalConfig.mapChunkSize * scale);
            }
            for (int j = 0; j < GlobalConfig.mapChunkSize; j++)
            {
                int movY = (y * GlobalConfig.mapChunkSize + gridOffsetY + j) * scale + offsetY;
                int constX = (x * GlobalConfig.mapChunkSize + gridOffsetX) * scale + offsetX;
                g.drawLine(constX, movY, constX + GlobalConfig.mapChunkSize * scale, movY);
            }
        } else {
            drawn = new Rectangle((x * GlobalConfig.mapChunkSize + gridOffsetX) * scale + offsetX, (y * GlobalConfig.mapChunkSize + gridOffsetY) * scale + offsetY, GlobalConfig.mapChunkSize * scale, GlobalConfig.mapChunkSize * scale);
            if (!containsEntity) {
                g.setColor(Color.GRAY);
                g.fillRect(drawn.x, drawn.y, drawn.width, drawn.height);
                int movX = (x * GlobalConfig.mapChunkSize + gridOffsetX) * scale + offsetX;
                int constY = (y * GlobalConfig.mapChunkSize + gridOffsetY) * scale + offsetY;
                g.setColor(Color.BLACK);
                g.drawLine(movX, constY, movX, constY + GlobalConfig.mapChunkSize * scale);
                
                /*movX = (x * GlobalConfig.mapChunkSize + gridOffsetX + 16) * scale + offsetX;
                constY = (y * GlobalConfig.mapChunkSize + gridOffsetY) * scale + offsetY;
                g.drawLine(movX, constY, movX, constY + GlobalConfig.mapChunkSize * scale);*/

                int movY = (y * GlobalConfig.mapChunkSize + gridOffsetY) * scale + offsetY;
                int constX = (x * GlobalConfig.mapChunkSize + gridOffsetX) * scale + offsetX;
                g.drawLine(constX, movY, constX + GlobalConfig.mapChunkSize * scale, movY);

               /* movY = (y * GlobalConfig.mapChunkSize + gridOffsetY + 16) * scale + offsetY;
                constX = (x * GlobalConfig.mapChunkSize + gridOffsetX) * scale + offsetX;
                g.drawLine(constX, movY, constX + GlobalConfig.mapChunkSize * scale, movY);*/
            }
        }
    }
}
