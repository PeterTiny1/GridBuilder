package io.shapez.core;

import java.util.HashMap;
import java.util.Map;

public enum Tile {
    None(1),
    Belt(2),
    Miner(3),
    Trash(4),
    Rotator(5),
    DEBUG_LowerLayer(99); // DEBUG: a red-colored lower layer tile

    private static final Map<Integer, Tile> map = new HashMap<>();

    static {
        for (Tile pageType : Tile.values()) {
            map.put(pageType.value, pageType);
        }
    }

    private final int value;

    Tile(int value) {
        this.value = value;
    }

    public static Tile valueOf(int pageType) {
        return map.get(pageType);
    }

    public int getValue() {
        return value;
    }
}

