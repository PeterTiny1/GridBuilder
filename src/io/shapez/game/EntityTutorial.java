package io.shapez.game;

import io.shapez.core.Tile;

public class EntityTutorial {

    public static String GetTitle(Tile item){
        if(item == Tile.None)return "";

        return item.toString();
    }
    public static String GetDescription(Tile item){
        String description;
        switch(item){
            case Belt:
                description = "Used to transport items";
                break;
            case Miner:
                description = "Used to extract items";
                break;
            case Trash:
                description = "Used to delete items";
                break;
            default:
                description = "";
                break;
        }
        return description;
    }

    public static String GetHotkey(Tile item){
        String h = "Hotkey: " + Tile.valueOf(item.toString()).ordinal();
        if(h.contains("0"))return "";
        return h;
    }
}
