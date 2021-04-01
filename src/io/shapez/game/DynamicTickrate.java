package io.shapez.game;

public class DynamicTickrate {
    private final GameRoot root;
    int averageTickDuration = 0;
    int accumulatedFps = 0;
    int accumulatedFpsLastUpdate = 0;
    int averageFps = 60;
    private int currentTickRate;
    private double deltaMs;
    private double deltaSeconds;

    public DynamicTickrate(GameRoot root) {
        this.root = root;

        this.setTickRate(this.root.app.settings.getDesiredFps());
    }

    private void setTickRate(int rate) {
        this.currentTickRate = rate;
        this.deltaMs = 1000.0 / this.currentTickRate;
        this.deltaSeconds = 1.0 / this.currentTickRate;
    }
}
