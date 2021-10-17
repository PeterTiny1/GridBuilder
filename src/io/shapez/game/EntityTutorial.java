package io.shapez.game;

import io.shapez.core.Tile;

public class EntityTutorial {

    public static final String[] descriptions = {
            "Used to transport items", //  How to use: descriptions[tileIndex]
            "Used to extract items",
            "Used to delete items",
            "Used to rotate shapes",
            "[DEBUG] Red lower layer tile"
    };

    public static String GetTitle(final Tile item) {
        if (item == Tile.None) return "";
        return item.toString();
    }

    public static String GetDescription(final Tile item) {
        if (item.ordinal() > EntityTutorial.descriptions.length || (item.ordinal() - 1) < 0) return "";

        return EntityTutorial.descriptions[item.ordinal() - 1];
    }

    public static String GetHotkey(final Tile item) {
        final String h = "Hotkey: " + Tile.valueOf(item.toString()).ordinal();
        if (h.contains("0")) return "";
        return h;
    }
}
