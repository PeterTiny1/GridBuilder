package io.shapez.core;

import java.util.HashMap;
import java.util.Map;

public enum Direction {
    Top(0),
    Right(1),
    Bottom(2),
    Left(3);

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