package io.shapez.core;

import java.util.HashMap;
import java.util.Map;

public enum Direction {
    Top(1),
    Right(2),
    Bottom(3),
    Left(4);

    private final int value;
    private static final Map<Integer, Direction> map = new HashMap<>();

    Direction(int value) {
        this.value = value;
    }

    static {
        for (Direction pageType : Direction.values()) {
            map.put(pageType.value, pageType);
        }
    }

    public static Direction valueOf(int pageType) {
        return map.get(pageType);
    }

    public int getValue() {
        return value;
    }
}