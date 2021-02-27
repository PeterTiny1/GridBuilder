package io.shapez;

public class EntityTutorial {

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
                return "Used to delete items";
            default:
                return "";
        }
    }
}
