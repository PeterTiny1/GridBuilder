package io.shapez.game.components;

import io.shapez.core.Direction;
import io.shapez.game.BeltPath;

public class BeltComponent {
    public Direction direction;
    BeltPath assignedPath = null;

    public BeltComponent(Direction direction) {
        super();
        this.direction = direction;
    }
}
