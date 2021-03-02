package io.shapez.game;

import io.shapez.core.Rotation;
import io.shapez.core.Tile;

import java.awt.*;

public class Entity {
    public int x, y;
    public Tile tile;
    public Image texture;
    public Rotation rotation;

    public Entity(Tile type, Image texture, Rotation rotation, int x, int y) {
        this.x = x;
        this.y = y;
        this.tile = type;
        this.texture = texture;
        this.rotation = rotation;
    }
}
