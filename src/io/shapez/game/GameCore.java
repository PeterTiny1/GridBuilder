package io.shapez.game;

import io.shapez.Application;
import io.shapez.core.BufferMaintainer;
import io.shapez.core.DrawParameters;
import io.shapez.game.hud.GameHUD;
import io.shapez.game.modes.RegularGameMode;
import io.shapez.game.savegame.Savegame;
import io.shapez.game.time.GameTime;

import java.awt.*;
import java.io.IOException;

public class GameCore {
    private static final String ORIGINAL_SPRITE_SCALE = "0.75";
    private final Application app;
    public GameRoot root;
    private double overlayAlpha = 0;

    public GameCore(Application app) {
        this.app = app;
    }

    public void tick(long deltaMs) {
        root.time.updateRealtimeNow();

        root.camera.update(deltaMs);

        root.automaticSave.update();
    }

    public void initializeRoot(Savegame savegame) throws IOException {
        this.root = new GameRoot(this.app);
        this.root.savegame = savegame;

        root.dynamicTickrate = new DynamicTickrate(root);

        root.gameMode = new RegularGameMode(root);

        root.camera = new Camera(root, app);
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
        root.buffers = new BufferMaintainer(root);

        this.root.hud.initialize();
    }

    public void draw(Graphics2D context) {
        GameSystemManager systemMgr = root.systemMgr;

        root.dynamicTickrate.onFrameRendered();
        
        if (!this.shouldRender()) {
            root.hud.update();
            return;
        }

        double zoomLevel = root.camera.zoomLevel;
        boolean lowQuality = root.app.settings.getAllSettings().lowQualityTextures;
        double effectiveZoomLevel = (zoomLevel / GlobalConfig.assetsDpi) * getDeviceDPI() * GlobalConfig.assetsSharpness;
        String desiredAtlasScale = "0.25";

        if (effectiveZoomLevel > 0.5 && !lowQuality) {
            desiredAtlasScale = ORIGINAL_SPRITE_SCALE;
        } else if (effectiveZoomLevel > 0.35 && !lowQuality) {
            desiredAtlasScale = "0.5";
        }

        DrawParameters params = new DrawParameters(context, root.camera.getVisibleRect(), desiredAtlasScale, zoomLevel, root);

        root.camera.transform(context);

        root.hud.update();

        double desiredOverlayAlpha = this.root.camera.getIsMapOverlayActive() ? 1 : 0;

        if (this.root.entityMgr.entities.size() > 5000 || this.root.dynamicTickrate.averageFps < 50) {
            this.overlayAlpha = desiredOverlayAlpha;
        }

        if (this.overlayAlpha < 0.99) {
            root.map.drawBackground(params);
        }
    }

    private double getDeviceDPI() {
        return 1;
    }

    private boolean shouldRender() {
        if (this.root.hud.shouldPauseRendering()) {
            return false;
        }

        return this.app.isRenderable();
    }
}
