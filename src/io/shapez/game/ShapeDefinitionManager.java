package io.shapez.game;

import io.shapez.game.savegame.BasicSerializableObject;

import java.util.HashMap;

public class ShapeDefinitionManager extends BasicSerializableObject {
    final HashMap<String, ShapeDefinition> shapeKeyToDefinition = new HashMap<>();
    private final GameRoot root;

    public ShapeDefinitionManager(final GameRoot root) {
        this.root = root;
    }

    @Override
    protected String getId() {
        return "ShapeDefinitionManager";
    }

    public ShapeDefinition getShapeFromShortKey(String hash) {
        ShapeDefinition cached = this.shapeKeyToDefinition.get(hash);
        if (cached != null) {
            return cached;
        }
        this.shapeKeyToDefinition.put(hash, ShapeDefinition.fromShortKey(hash));
        return (this.shapeKeyToDefinition.get(hash));
    }

    public ShapeDefinition registerOrReturnHandle(ShapeDefinition definition) {
        String id = definition.getHash();
        if (this.shapeKeyToDefinition.containsKey(id)) {
            return this.shapeKeyToDefinition.get(id);
        }
        this.shapeKeyToDefinition.put(id, definition);
        return definition;
    }
}
