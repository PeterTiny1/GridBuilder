package io.shapez.game;

import io.shapez.core.Layer;
import io.shapez.core.Vector;

public class MetaBuilding {
    private String id;

    public MetaBuilding(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public Layer getLayer() {
        return Layer.Regular;
    }

    public Vector getDimensions(Layer variant) {
        return new Vector(1, 1);
    }
}
