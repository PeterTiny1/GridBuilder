package io.shapez.game;

import io.shapez.core.Direction;
import io.shapez.core.Layer;
import io.shapez.core.Tile;
import io.shapez.game.components.StaticMapEntityComponent;

public class Entity {
    public int x, y;
    public Tile tile;
    public Direction direction;
    public EntityComponentStorage components = new EntityComponentStorage();
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

    public void addComponent(StaticMapEntityComponent staticMapEntityComponent) {
        
    }
}