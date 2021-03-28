package io.shapez.game.components;

import io.shapez.core.Direction;
import io.shapez.game.BeltPath;
import io.shapez.game.Component;

public class BeltComponent extends Component {
    public Direction direction;
    public BeltPath assignedPath = null;
    private final double curvedBeltLength = (Math.PI / 4);

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

    public double getEffectiveLengthTiles() {
        return this.direction == Direction.Top ? 1.0 : curvedBeltLength;
    }
}
