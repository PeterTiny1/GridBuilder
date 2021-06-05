package io.shapez.game;

import java.util.ArrayList;
import java.util.Comparator;

public class DynamicTickrate {
    private final GameRoot root;
    double averageTickDuration = 0;
    int accumulatedFps = 0;
    long accumulatedFpsLastUpdate = 0;
    final int averageFps = 60;
    private int currentTickRate;
    private double deltaMs;
    public double deltaSeconds;
    final long fpsAccumulationTime = 1000;
    private long currentTickStart;
    private ArrayList<Integer> capturedTicks = new ArrayList<>();

    public DynamicTickrate(final GameRoot root) {
        this.root = root;

        this.setTickRate(this.root.app.settings.getDesiredFps());
    }

    private void setTickRate(final int rate) {
        this.currentTickRate = rate;
        this.deltaMs = 1000.0 / this.currentTickRate;
        this.deltaSeconds = 1.0 / this.currentTickRate;
    }

    public void onFrameRendered() {
        ++this.accumulatedFps;
        final long now = root.app.date.getTime();
        final long timeDuration = now - this.accumulatedFpsLastUpdate;
        if (timeDuration > fpsAccumulationTime) {
            final long averageFps = (this.accumulatedFps / fpsAccumulationTime) * 1000;
            this.accumulatedFps = 0;
            this.accumulatedFpsLastUpdate = now;
        }
    }

    public void beginTick() {
        this.currentTickStart = root.app.date.getTime();
        if (this.capturedTicks.size() > currentTickRate * 2) {
            this.capturedTicks.sort(Comparator.comparingInt(o -> o));
            final ArrayList<Integer> ticks = (ArrayList<Integer>) capturedTicks.subList(10, this.capturedTicks.size() - 11);
            double average = 0;
            for (final Integer tick : ticks) {
                average += tick;
            }
            average /= ticks.size();
            this.averageTickDuration = average;
            this.capturedTicks = new ArrayList<>();
        }
    }
}
