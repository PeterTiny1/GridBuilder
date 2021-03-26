package io.shapez.game.components;

import io.shapez.core.Direction;
import io.shapez.core.Vector;
import io.shapez.game.Component;

public class StaticMapEntityComponent implements Component {
    private final Vector origin;
    private final int rotation;
    private final int code;
    private final int originalRotation;

    public StaticMapEntityComponent(Vector origin,/* Vector tileSize,*/ int rotation, int originalRotation, int code) {
        super();
        this.origin = origin;
        this.rotation = rotation;
        this.code = code;
        this.originalRotation = originalRotation;
    }

    public Vector localTileToWorld(Vector localTile) {
        Vector result = localTile.rotateFastMultipleOf90(this.rotation);
        result.x += this.origin.x;
        result.y += this.origin.y;
        return result;
    }

    public Direction localDirectionToWorld(Direction direction) {
        return Vector.transformDirectionFromMultipleOf90(direction, this.rotation);
    }

    public Direction worldDirectionToLocal(Direction direction) {
        return Vector.transformDirectionFromMultipleOf90(direction, 360 - this.rotation);
    }

    public Vector worldToLocalTile(Vector worldTile) {
        Vector localUnrotated = worldTile.sub(this.origin);
        return this.unapplyRotationToVector(localUnrotated);
    }

    private Vector unapplyRotationToVector(Vector vector) {
        return vector.rotateFastMultipleOf90(360 - this.rotation);
    }


    @Override
    public String getId() {
        return "StaticMapEntity";
    }
}
