package io.shapez.game;

import io.shapez.core.Tile;
import io.shapez.core.Rotations;

import java.awt.*;

public class Entity {
    public int x, y;
    public Tile type;
    public Image texture;
    public Rotations rotation;

    public Entity(Tile type, Image texture, Rotations rotation, int x, int y) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.texture = texture;
        this.rotation = rotation;
    }
}
