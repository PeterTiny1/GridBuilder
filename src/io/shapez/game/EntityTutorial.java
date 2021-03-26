package io.shapez.game;

import io.shapez.core.Tile;

import java.util.Random;

public class EntityTutorial {

    protected static Random rng = new Random();

    public static String[] descriptions = {
            "Used to transport items", //  How to use: descriptions[tileIndex]
            "Used to extract items",
            "Used to delete items",
            "Used to rotate shapes",
            "[DEBUG] Red lower layer tile"
    };

    public static String GetTitle(Tile item){
        if(item == Tile.None)return "";
        return item.toString();
    }
    public static String GetDescription(Tile item){
        if(item.ordinal() > descriptions.length || (item.ordinal()-1) < 0)return "";

        return descriptions[item.ordinal()-1];
    }

    public static String GetHotkey(Tile item){
        String h = "Hotkey: " + Tile.valueOf(item.toString()).ordinal();
        if(h.contains("0"))return "";
        return h;
    }
}
