package io.shapez;

import java.util.HashMap;
import java.util.Map;

public enum Tile {
    None(1),
    Belt(2),
    Miner(3),
    Trash(4);

    private final int value;
    private static final Map<Integer, Tile> map = new HashMap<>();

    Tile(int value) {
        this.value = value;
    }

    static {
        for (Tile pageType : Tile.values()) {
            map.put(pageType.value, pageType);
        }
    }

    public static Tile valueOf(int pageType) {
        return map.get(pageType);
    }

    public int getValue() {
        return value;
    }
}

