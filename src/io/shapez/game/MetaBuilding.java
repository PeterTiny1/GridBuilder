package io.shapez.game;

import io.shapez.core.Layer;
import io.shapez.core.Vector;

public class MetaBuilding {
    private final String id;

    public MetaBuilding(final String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public Layer getLayer() {
        return Layer.Regular;
    }

    public Vector getDimensions(final String variant) {
        return new Vector(1, 1);
    }

    public short[] computeOptimalDirectionAndRotationVariantAtTile(final Vector tile, final short rotation, final String variant, final Layer layer) {
        if (!this.getIsRotatable(variant)) {
            return new short[]{0, 0};
        }
        return new short[]{rotation, 0};
    }

    private boolean getIsRotatable(final String variant) {
        return true;
    }

    public void updateVariants(final Entity entity, final short rotationVariant, final String variant) {
    }
}
