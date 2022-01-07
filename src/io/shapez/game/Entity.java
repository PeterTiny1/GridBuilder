package io.shapez.game;

import io.shapez.core.Direction;
import io.shapez.core.Layer;
import io.shapez.core.Tile;

public class Entity {
    public final EntityComponentStorage components = new EntityComponentStorage();
    public int x, y;
    public Tile tile;
    public Direction direction;
    public Integer uid = 0;
    public Layer layer;

    public Entity(final Tile type, final Direction direction, final int x, final int y) {
        this.x = x;
        this.y = y;
        this.tile = type;
        this.direction = direction;
    }

    public Entity() {

    }
}