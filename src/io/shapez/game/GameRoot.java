package io.shapez.game;

import io.shapez.game.savegame.Savegame;
import io.shapez.game.time.GameTime;

public class GameRoot {
    public final Application app;
    public Savegame savegame = null;
    public GameTime time = null;
    public Camera camera;
    public AutomaticSave automaticSave;

    public GameRoot(Application app) {
        this.app = app;
    }
}
