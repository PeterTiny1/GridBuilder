package io.shapez.game;

import io.shapez.game.savegame.Savegame;
import io.shapez.game.time.GameTime;

public class GameCore {
    private final Application application;
    private GameRoot root;

    public GameCore(Application application) {
        this.application = application;
    }

    public void tick(long deltaMs) {
        root.time.updateRealtimeNow();

        root.camera.update(deltaMs);

        root.automaticSave.update();
    }

    public void initializeRoot(Savegame savegame) {
        this.root = new GameRoot(this.application);
        this.root.savegame = savegame;

        root.dynamicTickrate = new DynamicTickrate(root);

        root.camera = new Camera(root, application);
        root.map = new MapView(root);
        root.time = new GameTime();
        root.automaticSave = new AutomaticSave(root);
    }
}
