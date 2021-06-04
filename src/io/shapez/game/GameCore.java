package io.shapez.game;

import io.shapez.Application;
import io.shapez.core.BufferMaintainer;
import io.shapez.core.DrawParameters;
import io.shapez.core.Layer;
import io.shapez.core.Vector;
import io.shapez.game.buildings.MetaHubBuilding;
import io.shapez.game.components.StaticMapEntityComponent;
import io.shapez.game.hud.GameHUD;
import io.shapez.game.modes.RegularGameMode;
import io.shapez.game.savegame.Savegame;
import io.shapez.game.time.GameTime;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.Random;

public class GameCore {
    private static final String ORIGINAL_SPRITE_SCALE = "0.75";
    private final Application app;
    public GameRoot root;
    private double overlayAlpha = 0;
    private boolean duringLogicUpdate;

    public GameCore(final Application app) {
        this.app = app;
    }

    public void tick(final long deltaMs) {
        root.time.updateRealtimeNow();

        root.camera.update(deltaMs);

        root.automaticSave.update();
    }

    public void initializeRoot(final Savegame savegame) throws IOException {
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

    public void draw(final Graphics2D context) throws IOException {
        final GameSystemManager systemMgr = root.systemMgr;

        root.dynamicTickrate.onFrameRendered();

        if (!this.shouldRender()) {
            root.hud.update();
            return;
        }

        final double zoomLevel = root.camera.zoomLevel;
        final boolean lowQuality = root.app.settings.getAllSettings().lowQualityTextures;
        final double effectiveZoomLevel = (zoomLevel / GlobalConfig.assetsDpi) * getDeviceDPI() * GlobalConfig.assetsSharpness;
        String desiredAtlasScale = "0.25";

        if (effectiveZoomLevel > 0.5 && !lowQuality) {
            desiredAtlasScale = GameCore.ORIGINAL_SPRITE_SCALE;
        } else if (effectiveZoomLevel > 0.35 && !lowQuality) {
            desiredAtlasScale = "0.5";
        }

        final DrawParameters params = new DrawParameters(context, root.camera.getVisibleRect(), desiredAtlasScale, zoomLevel, root);

        final AffineTransform oldTransform = context.getTransform();

        root.camera.transform(context);

        root.hud.update();

        final double desiredOverlayAlpha = this.root.camera.getIsMapOverlayActive() ? 1 : 0;

        if (this.root.entityMgr.entities.size() > 5000 || this.root.dynamicTickrate.averageFps < 50) {
            this.overlayAlpha = desiredOverlayAlpha;
        }

        if (this.overlayAlpha < 0.99) {
            root.map.drawBackground(params);
            systemMgr.belt.drawBeltItems(params);
            root.map.drawForeground(params);
        }

        context.setTransform(oldTransform);
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

    public void initNewGame() {
        this.root.gameIsFresh = true;
        this.root.map.seed = new Random().nextInt(10000);
        final Entity hub = new Entity(); // new MetaHubBuilding(root, new Vector(-2, -2), 0, 0, 0, "default");
        hub.layer = Layer.Regular;
        hub.components.StaticMapEntity = new StaticMapEntityComponent(new Vector(-2, -2), 0, 0, new Vector(4, 4), BuildingCodes.getCodeFromBuildingData(new MetaHubBuilding(), BuildingCodes.defaultBuildingVariant, 0));
        this.root.map.placeStaticEntity(hub);
        this.root.entityMgr.registerEntity(hub);
    }

    public void updateLogic() {
        final GameRoot root = this.root;

        root.dynamicTickrate.beginTick();

        this.duringLogicUpdate = true;

        root.entityMgr.update();

        root.systemMgr.update();
    }
}
