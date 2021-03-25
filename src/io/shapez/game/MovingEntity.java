package io.shapez.game;

import io.shapez.core.Rotation;
import io.shapez.core.Tile;

import java.awt.*;

public class MovingEntity {
    public double x, y;
    public Tile tile;
    public Image texture;

    public MovingEntity(Tile type, Image texture, Rotation rotation, double x, double y) {
        this.x = x;
        this.y = y;
        this.tile = type;
        this.texture = texture;
    }
}
