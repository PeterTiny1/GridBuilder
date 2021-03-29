package io.shapez.game;

import io.shapez.core.Layer;
import io.shapez.core.Vector;

public class MetaBuilding {
    private final String id;

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

    public short[] computeOptimalDirectionAndRotationVariantAtTile(Vector tile, short rotation, String variant, Layer layer) {
        if (!this.getIsRotatable(variant)) {
            return new short[]{0, 0};
        }
        return new short[]{rotation, 0};
    }

    private boolean getIsRotatable(String variant) {
        return true;
    }

    public void updateVariants(Entity entity, short rotationVariant, String variant) {
    }
}
