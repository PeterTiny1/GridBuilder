package io.shapez.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class BaseMap {
    public int seed;
    HashMap<String, Chunk> chunksById = new HashMap<>();

    public BaseMap() {
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

    public void addChunks(ArrayList<Chunk> chunks) {
        for (Chunk chunk : chunks) {
            int x = chunk.x;
            int y = chunk.y;

            String code = x + "|" + y;

            chunksById.putIfAbsent(code, new Chunk(x, y));
        }
    }
}
