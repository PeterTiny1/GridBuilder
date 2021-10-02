package io.shapez.game;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public enum Colors {
    red,
    green,
    blue,
    yellow,
    purple,
    cyan,
    white,
    uncolored;

    public static final HashMap<Colors, Character> colorToShortcode = new HashMap<>() {{
        put(Colors.red, 'r');
        put(Colors.green, 'g');
        put(Colors.blue, 'b');
        put(Colors.yellow, 'y');
        put(Colors.purple, 'p');
        put(Colors.cyan, 'c');
        put(Colors.white, 'w');
        put(Colors.uncolored, 'u');
    }};

    public static final HashMap<Character, Colors> shortcodeToColor = new HashMap<>() {{
        for (Map.Entry<Colors, Character> pair : colorToShortcode.entrySet()) {
            put(pair.getValue(), pair.getKey());
        }
    }};

    public static final HashMap<Colors, Color> colorsToCode = new HashMap<>() {{
        put(Colors.red, new Color(0xff666a));
        put(Colors.green, new Color(0x78ff66));
        put(Colors.blue, new Color(0x66a7ff));
        put(Colors.yellow, new Color(0xfcf52a));
        put(Colors.purple, new Color(0xdd66ff));
        put(Colors.cyan, new Color(0x00fcff));
        put(Colors.white, new Color(0xffffff));
        put(Colors.uncolored, new Color(0xaaaaaa));
    }};
}
