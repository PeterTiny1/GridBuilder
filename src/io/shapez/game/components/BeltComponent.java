package io.shapez.game.components;

import io.shapez.core.Direction;
import io.shapez.game.BeltPath;
import io.shapez.game.Component;

public class BeltComponent implements Component {
    public Direction direction;
    BeltPath assignedPath = null;

    public BeltComponent(Direction direction) {
        super();
        this.direction = direction;
    }

    public BeltComponent() {

    }

    @Override
    public String getId() {
        return "Belt";
    }
}
