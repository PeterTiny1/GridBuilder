package io.shapez.core;

import io.shapez.game.GameRoot;
import io.shapez.game.MapChunkView;
import io.shapez.game.MapView;
import io.shapez.game.systems.MapResourcesSystem;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class BufferMaintainer {
    private final GameRoot root;
    private final double bufferGcDurationSeconds = 0.5;
    final HashMap<String, HashMap<String, CacheEntry>> cache = new HashMap<>();
    int iterationIndex = 1;
    double lastIteration = 0;

    public BufferMaintainer(final GameRoot root) {
        this.root = root;


    }

    public BufferedImage getForKey(final String key, final String subKey, final byte w, final byte h, final int dpi, final MapResourcesSystem mapResourcesSystem, final MapChunkView chunk) {
        final HashMap<String, CacheEntry> parent = this.cache.computeIfAbsent(key, k -> new HashMap<>());

        final CacheEntry cacheHit = parent.get(subKey);
        if (cacheHit != null) {
            cacheHit.lastUse = this.iterationIndex;
            return cacheHit.canvas;
        }

        final int effectiveWidth = w * dpi;
        final int effectiveHeight = h * dpi;

        final BufferedImage canvas = MapView.makeBuffer(effectiveWidth, effectiveHeight);
        final Graphics2D context = (Graphics2D) canvas.getGraphics();
        mapResourcesSystem.generateChunkBackground(canvas, context, w, h, dpi, chunk);
        parent.put(subKey, new CacheEntry(canvas, context, this.iterationIndex));
        return canvas;
    }

    public void update() {
        final double now = this.root.time.realtimeNow();
        if (now - lastIteration > bufferGcDurationSeconds) {
            this.lastIteration = now;
            this.garbageCollect();
        }
    }

    private void garbageCollect() {
        final AtomicInteger totalKeys = new AtomicInteger();
        final AtomicInteger deletedKeys = new AtomicInteger();
        final int minIteration = this.iterationIndex;
        this.cache.forEach((key, subCache) -> {
            final ArrayList<String> unusedSubKeys = new ArrayList<>();

            subCache.forEach((subKey, cacheEntry) -> {
                if (cacheEntry.lastUse < minIteration) {
                    unusedSubKeys.add(subKey);
                    cacheEntry.canvas = null;
                    deletedKeys.getAndIncrement();
                } else {
                    totalKeys.getAndIncrement();
                }
            });

            for (final String unusedSubKey : unusedSubKeys) {
                subCache.remove(unusedSubKey);
            }
        });
        ++this.iterationIndex;
    }


    static class CacheEntry {
        private final Graphics2D context;
        public int lastUse;
        public BufferedImage canvas;

        public CacheEntry(final BufferedImage canvas, final Graphics2D context, final int lastUse) {
            this.canvas = canvas;
            this.context = context;
            this.lastUse = lastUse;
        }
    }
}
