package io.shapez.game;

import io.shapez.game.savegame.BasicSerializableObject;

import java.util.HashMap;

public class ShapeDefinitionManager extends BasicSerializableObject {
    private final GameRoot root;
    HashMap<String, ShapeDefinition> shapeKeyToDefinition = new HashMap<>();

    public ShapeDefinitionManager(GameRoot root) {
        this.root = root;
    }

    @Override
    protected String getId() {
        return "ShapeDefinitionManager";
    }
}
