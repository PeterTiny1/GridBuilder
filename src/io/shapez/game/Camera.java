package io.shapez.game;

import io.shapez.Application;
import io.shapez.core.Vector;
import io.shapez.game.platform.PlatformWrapperInterface;
import io.shapez.managers.providers.MiscProvider;

import java.awt.*;
import java.awt.geom.AffineTransform;

import static io.shapez.core.Vector.mixVector;

public class Camera {
    private final Application app;
    private final double velocityStrength = 0.4;
    private final double velocityFade = 0.98;
    private final byte ticksBeforeErasingVelocity = 10;
    private final boolean currentlyMoving = false;
    private final boolean currentlyPinching = false;
    private final byte velocityMax = 20;
    private final GameRoot root;
    private long cameraUpdateTimeBucket;
    private Vector touchPostMoveVelocity = new Vector(0, 0);
    private Vector desiredCenter;
    private Object lastMovingPositionLastTick;
    private int numTicksStandingStill;
    private Vector lastMovingPosition;
    private Vector center = new Vector(0, 0);
    private final Vector desiredPan = new Vector(0, 0);
    private Vector currentPan = new Vector(0, 0);
    public double zoomLevel;
    private final Vector currentShake = new Vector(0, 0);
    private double desiredZoom;

    public Camera(GameRoot root, Application application) {
        this.root = root;
        this.app = application;
        zoomLevel = this.findInitialZoom();
    }

    private double findInitialZoom() {
        int desiredWorldSpaceWidth = 15 * GlobalConfig.tileSize;
        int zoomLevelX = this.app.getWidth() / desiredWorldSpaceWidth;
        int zoomLevelY = this.app.getHeight() / desiredWorldSpaceWidth;

        return Math.min(zoomLevelX, zoomLevelY);
    }

    public void update(long dt) {
        dt = Math.min(dt, 33);
        this.cameraUpdateTimeBucket += dt;

        int updatesPerFrame = 4;
        double physicsStepSizeMs = 1000.0 / (60.0 * updatesPerFrame);

        double now = this.root.time.systemNow() - 3 * physicsStepSizeMs;
        while (this.cameraUpdateTimeBucket > physicsStepSizeMs) {
            now += physicsStepSizeMs;
            this.cameraUpdateTimeBucket -= physicsStepSizeMs;
            this.internalUpdatePanning(now, physicsStepSizeMs);
        }
    }

    private void internalUpdatePanning(double now, double dt) {
        double baseStrength = velocityStrength * this.app.platformWrapper.getTouchPanStrength();

        this.touchPostMoveVelocity = this.touchPostMoveVelocity.multiplyScalar(velocityFade);

        if (this.currentlyMoving && this.desiredCenter == null) {
            if (this.lastMovingPositionLastTick != null/* && this.lastMovingPositionLastTick.equalsEpsilon*/) {
                this.numTicksStandingStill++;
            } else {
                this.numTicksStandingStill = 0;
            }
            this.lastMovingPositionLastTick = this.lastMovingPosition.copy();

            if (numTicksStandingStill >= ticksBeforeErasingVelocity) {
                this.touchPostMoveVelocity.y = 0;
                this.touchPostMoveVelocity.x = 0;
            }
        }

        if (!this.currentlyMoving && !this.currentlyPinching) {
            double len = this.touchPostMoveVelocity.length();
            if (len >= velocityMax) {
                this.touchPostMoveVelocity.x = (this.touchPostMoveVelocity.x * velocityMax) / len;
                this.touchPostMoveVelocity.y = (this.touchPostMoveVelocity.y * velocityMax) / len;
            }

            this.center = this.center.add(this.touchPostMoveVelocity.multiplyScalar(baseStrength));

            this.currentPan = mixVector(this.currentPan, this.desiredPan, 0.06);
            this.center = this.center.add(this.currentPan.multiplyScalar((0.5 * dt) / this.zoomLevel));
        }
    }

    public Rectangle getVisibleRect() {
        return new Rectangle((int) Math.floor(this.getViewportLeft()), (int) Math.floor(this.getViewportTop()), (int) Math.ceil(this.getViewportRight() - this.getViewportLeft()), (int) Math.ceil(this.getViewportBottom() - getViewportTop()));
    }

    private double getViewportRight() {
        return this.center.x + this.getViewportWidth() / 2 + (this.currentShake.x * 10) / this.zoomLevel;
    }

    private double getViewportBottom() {
        return this.center.y + this.getViewportHeight() / 2 + (this.currentShake.x * 10) / this.zoomLevel;
    }

    private double getViewportLeft() {
        return this.center.x - this.getViewportWidth() / 2 + (this.currentShake.x * 10) / this.zoomLevel;
    }

    private double getViewportWidth() {
        return this.root.app.getWidth() / this.zoomLevel;
    }

    private double getViewportTop() {
        return this.center.y + this.getViewportHeight() / 2 + (this.currentShake.x * 10) / this.zoomLevel;
    }

    private double getViewportHeight() {
        return this.root.app.getHeight() / this.zoomLevel;
    }

    public void transform(Graphics2D context) {
        this.clampZoomLevel();
        double zoom = this.zoomLevel;

        context.setTransform(new AffineTransform(zoom, 0, 0, zoom, -zoom * this.getViewportLeft(), -zoom * this.getViewportTop()));
    }

    private void clampZoomLevel() {
        PlatformWrapperInterface wrapper = this.root.app.platformWrapper;
        this.zoomLevel = MiscProvider.clamp(this.zoomLevel, wrapper.getMinimumZoom(app), wrapper.getMaximumZoom(app));

        this.desiredZoom = MiscProvider.clamp(this.desiredZoom, wrapper.getMinimumZoom(app), wrapper.getMaximumZoom(app));
    }

    public boolean getIsMapOverlayActive() {
        return this.zoomLevel < GlobalConfig.mapChunkOverviewMinZoom;
    }

    public Vector screenToWorld(Vector screen) {
        Vector centerSpace = screen.subScalars(this.root.app.getWidth() / 2, this.root.app.getHeight() / 2);
        return centerSpace.divideScalar(this.zoomLevel).add(this.center);
    }
}
