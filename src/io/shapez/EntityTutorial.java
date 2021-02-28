package io.shapez;

import java.util.Arrays;
import java.util.Random;

public class EntityTutorial {

    public static String GetTitle(Items item){
        if(item == Items.None)return "";

        return item.toString();
    }
    public static String GetDescription(Items item){
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

    public static String GetHotkey(Items item){
        String h = "Hotkey: " + Items.valueOf(item.toString()).ordinal();
        if(h.contains("0"))return "";
        return h;
    }
}
