package io.shapez.game;

import io.shapez.Application;
import io.shapez.game.hud.GameHUD;
import io.shapez.game.modes.RegularGameMode;
import io.shapez.game.savegame.Savegame;
import io.shapez.game.time.GameTime;

import java.io.IOException;

public class GameCore {
    private final Application application;
    public GameRoot root;

    public GameCore(Application application) {
        this.application = application;
    }

    public void tick(long deltaMs) {
        root.time.updateRealtimeNow();

        root.camera.update(deltaMs);

        root.automaticSave.update();
    }

    public void initializeRoot(Savegame savegame) throws IOException {
        this.root = new GameRoot(this.application);
        this.root.savegame = savegame;

        root.dynamicTickrate = new DynamicTickrate(root);

        root.gameMode = new RegularGameMode(root);

        root.camera = new Camera(root, application);
        root.map = new MapView(root);
        root.logic = new GameLogic(root);
        root.hud = new GameHUD(root);
        root.time = new GameTime(root);
        root.automaticSave = new AutomaticSave(root);
        root.soundProxy = new SoundProxy(root);

        root.entityMgr = new EntityManager(root);
        root.systemMgr = new GameSystemManager(root);
        root.shapeDefinitionMgr = new ShapeDefinitionManager(root);
        root.hubGoals = new HubGoals(root);

        this.root.hud.initialize();
    }
}
