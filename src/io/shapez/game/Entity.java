package io.shapez.game;

import io.shapez.core.Rotations;

import java.awt.*;
import java.lang.reflect.Type;

public class Entity {
    public int x, y;
    public Type type;
    public Image texture;
    public Rotations rotation;

    public Entity(Type type, Image texture, Rotations rotation, int x, int y) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.texture = texture;
        this.rotation = rotation;
    }
}
