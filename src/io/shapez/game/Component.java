package io.shapez.game;

import io.shapez.game.savegame.BasicSerializableObject;

public abstract class Component extends BasicSerializableObject {
    public abstract String getId();
}
