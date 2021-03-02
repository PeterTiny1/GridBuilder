package io.shapez.core;

import java.util.HashMap;
import java.util.Map;

public enum Rotation {
    Up(1),
    Right(2),
    Down(3),
    Left(4);

    private final int value;
    private static final Map<Integer, Rotation> map = new HashMap<>();

    Rotation(int value) {
        this.value = value;
    }

    static {
        for (Rotation pageType : Rotation.values()) {
            map.put(pageType.value, pageType);
        }
    }

    public int getValue() {
        return value;
    }
}