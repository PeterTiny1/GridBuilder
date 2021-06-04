package io.shapez.game;

import io.shapez.game.savegame.BasicSerializableObject;

import java.util.ArrayList;

public class ShapeDefinition extends BasicSerializableObject {
    ArrayList<ShapeLayer> layers = new ArrayList<>();

    @Override
    protected String getId() {
        return "ShapeDefinition";
    }

    ShapeDefinition() {
    }

    private class ShapeLayer {
        ShapeLayerItem[] layerItems = new ShapeLayerItem[4];
    }

    private class ShapeLayerItem {
        SubShape subShape;
        Colors color;
    }

    private enum SubShape {
        rect,
        circle,
        star,
        windmill
    }
}
