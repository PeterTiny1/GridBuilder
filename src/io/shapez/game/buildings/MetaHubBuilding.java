package io.shapez.game.buildings;

import io.shapez.core.Vector;
import io.shapez.game.GameRoot;
import io.shapez.game.MetaBuilding;

public class MetaHubBuilding extends MetaBuilding {
    public MetaHubBuilding(final GameRoot root, final Vector origin, final int rotation, final int originalRotation, final int rotationVariant, final String variant) {
        super("hub");

    }

    public MetaHubBuilding() {
        super("hub");
    }
}
