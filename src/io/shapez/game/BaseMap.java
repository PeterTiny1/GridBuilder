package io.shapez.game;

import io.shapez.core.Layer;

import java.util.HashMap;
import java.util.Random;

public class BaseMap {
    protected final GameRoot root;
    public int seed;
    final HashMap<String, MapChunkView> chunksById = new HashMap<>();

    public BaseMap(final GameRoot root) {
        this.root = root;
        final Random rng = new Random();
        seed = (int) Math.round(rng.nextDouble() * 100000);
    }

    public MapChunkView getChunk(final int x, final int y) {
        final String code = x + "|" + y;
        chunksById.computeIfAbsent(code, k -> new MapChunkView(root, x, y));
        return chunksById.get(code);
    }

    public MapChunk getChunkAtTile(final int x, final int y) {
        final int chunkX = (int) Math.floor((double) x / (double) GlobalConfig.mapChunkSize);
        final int chunkY = (int) Math.floor((double) y / (double) GlobalConfig.mapChunkSize);
        return getChunk(chunkX, chunkY);
    }

    public Entity getLayerContentXY(final double x, final double y, final Layer layer) {
        final MapChunk chunk = this.getChunkAtTileOrNull(x, y);
        return chunk.getLayerContentFromWorldCoords(x, y, layer);
    }

    private MapChunk getChunkAtTileOrNull(final double tileX, final double tileY) {
        final int chunkX = (int) Math.floor(tileX / GlobalConfig.mapChunkSize);
        final int chunkY = (int) Math.floor(tileY / GlobalConfig.mapChunkSize);
        return this.getChunk(chunkX, chunkY);
    }

    public Entity[] getLayerContentsMultipleXY(final double x, final double y) {
        final MapChunk chunk = this.getChunkAtTileOrNull(x, y);
        if (chunk == null) {
            return new Entity[]{};
        }
        return chunk.getLayersContentMultipleFromWorldCoords(x, y);
    }
}
