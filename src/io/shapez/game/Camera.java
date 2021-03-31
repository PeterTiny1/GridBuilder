package io.shapez.game;

import io.shapez.core.Vector;

import static io.shapez.core.Vector.mixVector;

public class Camera {
    private final Application application;
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
    private Vector desiredPan = new Vector(0, 0);
    private Vector currentPan = new Vector(0, 0);
    private double zoomLevel;

    public Camera(GameRoot root, Application application) {
        this.root = root;
        this.application = application;
        zoomLevel = this.findInitialZoom();
    }

    private double findInitialZoom() {
        int desiredWorldSpaceWidth = 15 * GlobalConfig.tileSize;
        int zoomLevelX = this.application.getWidth() / desiredWorldSpaceWidth;
        int zoomLevelY = this.application.getHeight() / desiredWorldSpaceWidth;

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
        double baseStrength = velocityStrength * this.application.platformWrapper.getTouchPanStrength();

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
}
