package io.shapez.game.savegame;

import java.util.HashMap;

public abstract class BaseDataType {
    public abstract HashMap<Object, Object> serialize(Object value);
}
