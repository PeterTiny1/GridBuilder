package io.shapez;

import java.awt.*;

public class Entity {
    public int x, y;
    public Tile type;
    public Image texture;
    public Rotations.cRotations rotation;

    public Entity(Tile type, Image texture, Rotations.cRotations rotation, int x, int y) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.texture = texture;
        this.rotation = rotation;
    }
}
