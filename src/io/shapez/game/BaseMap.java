package io.shapez.game;

import io.shapez.core.Layer;

import java.util.HashMap;
import java.util.Random;

public class BaseMap {
    public int seed;
    HashMap<String, Chunk> chunksById = new HashMap<>();

    public BaseMap(GameRoot root) {
        Random rng = new Random();
        seed = (int) Math.round(rng.nextDouble() * 100000);
    }

    public Chunk getChunk(int x, int y) {
        String code = x + "|" + y;
        chunksById.putIfAbsent(code, new Chunk(x, y));
        return chunksById.get(code);
    }

    public Chunk getChunkAtTile(int x, int y) {
        int chunkX = (int) Math.floor((double) x / (double) GlobalConfig.mapChunkSize);
        int chunkY = (int) Math.floor((double) y / (double) GlobalConfig.mapChunkSize);
        return getChunk(chunkX, chunkY);
    }

    public Entity getLayerContentXY(double x, double y, Layer layer) {
        Chunk chunk = this.getChunkAtTileOrNull(x, y);
        return chunk.getLayerContentFromWorldCoords(x, y, layer);
    }

    private Chunk getChunkAtTileOrNull(double tileX, double tileY) {
        int chunkX = (int) Math.floor(tileX / GlobalConfig.mapChunkSize);
        int chunkY = (int) Math.floor(tileY / GlobalConfig.mapChunkSize);
        return this.getChunk(chunkX, chunkY);
    }

    public Entity[] getLayerContentsMultipleXY(double x, double y) {
        Chunk chunk = this.getChunkAtTileOrNull(x, y);
        if (chunk == null) {
            return new Entity[]{};
        }
        return chunk.getLayersContentMultipleFromWorldCoords(x, y);
    }
}
