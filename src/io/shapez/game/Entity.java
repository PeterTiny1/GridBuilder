package io.shapez.game;

import io.shapez.core.Rotations;
import io.shapez.core.Tile;

import java.awt.*;

public class Entity {
    public int x, y;
    public Tile tile;
    public Image texture;
    public Rotations.cRotations rotation;

    public Entity(Tile type, Image texture, Rotations.cRotations rotation, int x, int y) {
        this.x = x;
        this.y = y;
        this.tile = type;
        this.texture = texture;
        this.rotation = rotation;
    }
}
