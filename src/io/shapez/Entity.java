package io.shapez;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Entity {
    public int x, y;
    public Items type;
    public Image texture;
    public Rotation rotation;

    public Entity(Items type, Image texture, Rotation rotation, int x, int y) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.texture = texture;
        this.rotation = rotation;
    }
}
