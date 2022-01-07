package io.shapez.game.components;

import io.shapez.core.Direction;
import io.shapez.core.Vector;
import io.shapez.game.Component;

public class BeltUnderlaysComponent extends Component {
    public BeltUnderlayTile[] underlays;

    @Override
    public String getId() {
        return "BeltUnderlays";
    }

    public enum ClippedBeltUnderlayType {
        full,
        bottomOnly,
        topOnly,
        none
    }

    public static class BeltUnderlayTile {
        public Vector pos;
        public Direction direction;
        public ClippedBeltUnderlayType cachedType;
    }
}
