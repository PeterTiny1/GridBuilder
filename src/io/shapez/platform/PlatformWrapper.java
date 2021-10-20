package io.shapez.platform;

import io.shapez.Application;

public abstract class PlatformWrapper {
    public int getTouchPanStrength() {
        return 1;
    }

    public double getMinimumZoom(Application app) {
        return 0.1 * this.getScreenScale(app);
    }

    protected double getScreenScale(Application app) {
        return Math.min(app.getWidth(), app.getHeight()) / 1024.0;
    }

    public double getMaximumZoom(Application app) {
        return 3.5 * this.getScreenScale(app);
    }
}
