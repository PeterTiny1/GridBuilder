package io.shapez.game.components;

import io.shapez.core.Direction;
import io.shapez.core.Vector;
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

    public Vector transformBeltToLocalSpace(double progress) {
        assert progress >= 0.0;
        switch (this.direction) {
            case Top:
                return new Vector(0, 0.5 - progress);
            case Right:
                double arcProgress = (progress / curvedBeltLength) * 0.5 * Math.PI;
                return new Vector(-0.5 - 0.5 * Math.cos(arcProgress), 0.5 - 0.5 * Math.sin(arcProgress));
            case Left:
                arcProgress = (progress / curvedBeltLength) * 0.5 * Math.PI;
                return new Vector(-0.5 + 0.5 * Math.cos(arcProgress), 0.5 - 0.5 * Math.sin(arcProgress));
            default:
                return new Vector();
        }
    }
}
