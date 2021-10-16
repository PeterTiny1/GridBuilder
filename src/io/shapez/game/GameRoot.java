package io.shapez.game;

import io.shapez.Application;
import io.shapez.core.BufferMaintainer;
import io.shapez.core.Layer;
import io.shapez.game.hud.GameHUD;
import io.shapez.game.savegame.Savegame;
import io.shapez.game.time.GameTime;


public class GameRoot {
    public final Application app;
    public Savegame savegame = null;
    public GameTime time = null;
    public Camera camera;
    public AutomaticSave automaticSave;
    public DynamicTickrate dynamicTickrate = null;
    public MapView map = null;
    public GameLogic logic = null;
    public GameHUD hud = null;
    public SoundProxy soundProxy = null;
    public EntityManager entityMgr = null;
    public GameSystemManager systemMgr = null;
    public ShapeDefinitionManager shapeDefinitionMgr = null;
    public GameMode gameMode = null;
    public HubGoals hubGoals = null;
    public BufferMaintainer buffers = null;
    public Layer currentLayer;
    public boolean gameIsFresh;
    public boolean logicInitialized = false;
    public boolean requireRedraw;
    public ProductionAnalytics productionAnalytics;
    public boolean gameInitialised = false;

    public GameRoot(final Application app) {
        this.app = app;
    }

    public void doSave() {
        if (this.savegame == null || !this.savegame.isSavable()) {
            return;
        }

        this.savegame.writeSavegameAndMetadata();

    }

    public void gameFrameStarted() {
        buffers.update();
    }
}
