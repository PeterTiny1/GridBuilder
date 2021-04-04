package io.shapez.core;

import io.shapez.game.GameRoot;
import io.shapez.game.MapChunkView;
import io.shapez.game.MapView;
import io.shapez.game.systems.MapResourcesSystem;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class BufferMaintainer {
    private final GameRoot root;
    HashMap<String, HashMap<String, CacheEntry>> cache = new HashMap<>();
    int iterationIndex = 1;
    int lastIteration = 0;

    public BufferMaintainer(GameRoot root) {
        this.root = root;


    }

    public BufferedImage getForKey(String key, String subKey, byte w, byte h, int dpi, MapResourcesSystem mapResourcesSystem, MapChunkView chunk) {
        HashMap<String, CacheEntry> parent = this.cache.computeIfAbsent(key, k -> new HashMap<>());

        CacheEntry cacheHit = parent.get(subKey);
        if (cacheHit != null) {
            cacheHit.lastUse = this.iterationIndex;
            return cacheHit.canvas;
        }

        int effectiveWidth = w * dpi;
        int effectiveHeight = h * dpi;

        BufferedImage canvas = MapView.makeBuffer(effectiveWidth, effectiveHeight);
        Graphics2D context = (Graphics2D) canvas.getGraphics();
        mapResourcesSystem.generateChunkBackground(canvas, context, w, h, dpi, chunk);
        parent.put(subKey, new CacheEntry(canvas, context, this.iterationIndex));
        return canvas;
    }


    static class CacheEntry {
        private final Graphics2D context;
        public int lastUse;
        public BufferedImage canvas;

        public CacheEntry(BufferedImage canvas, Graphics2D context, int lastUse) {
            this.canvas = canvas;
            this.context = context;
            this.lastUse = lastUse;
        }
    }
}
