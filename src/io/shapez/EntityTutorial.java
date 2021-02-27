package io.shapez;

import java.util.Random;

public class EntityTutorial {

    public static Random random = new Random();

    public static String GetTitle(Items item){
        if(item == Items.None)return "";

        return item.toString();
    }
    public static String GetDescription(Items item){
        switch(item){
            case Belt:
                return "Used to transport items";
            case Miner:
                return "Used to extract items";
            case Trash:
                if(random.nextInt(11) == 10)
                    return "This is KPOP";
                return "Used to delete items";
            default:
                return "";
        }
    }
}
