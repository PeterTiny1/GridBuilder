package io.shapez.game;

import io.shapez.core.Direction;
import io.shapez.core.Tile;

import java.awt.*;

public class Entity {
    public int x, y;
    public Tile tile;
    public Image texture;
    public Direction direction;
    public EntityComponentStorage components = new EntityComponentStorage();
    public Integer uid = 0;

    public Entity(Tile type, Image texture, Direction direction, int x, int y) {
        this.x = x;
        this.y = y;
        this.tile = type;
        this.texture = texture;
        this.direction = direction;
    }

    public Entity() {

    }

}