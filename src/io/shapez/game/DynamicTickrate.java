package io.shapez.game;

public class DynamicTickrate {
    private final GameRoot root;
    int averageTickDuration = 0;
    int accumulatedFps = 0;
    long accumulatedFpsLastUpdate = 0;
    int averageFps = 60;
    private int currentTickRate;
    private double deltaMs;
    private double deltaSeconds;
    final long fpsAccumulationTime = 1000;

    public DynamicTickrate(GameRoot root) {
        this.root = root;

        this.setTickRate(this.root.app.settings.getDesiredFps());
    }

    private void setTickRate(int rate) {
        this.currentTickRate = rate;
        this.deltaMs = 1000.0 / this.currentTickRate;
        this.deltaSeconds = 1.0 / this.currentTickRate;
    }

    public void onFrameRendered() {
        ++this.accumulatedFps;
        long now = root.app.date.getTime();
        long timeDuration = now - this.accumulatedFpsLastUpdate;
        if (timeDuration > fpsAccumulationTime) {
            long averageFps = (this.accumulatedFps / fpsAccumulationTime) * 1000;
            this.accumulatedFps = 0;
            this.accumulatedFpsLastUpdate = now;
        }
    }
}
