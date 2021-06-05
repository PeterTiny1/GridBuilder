package io.shapez.game.time;

import io.shapez.game.GameRoot;

import java.util.Date;

public class GameTime {
    private final GameRoot root;
    final Date date = new Date();
    private double realtimeSeconds = 0;
    private final double realtimeAdjust = 0;

    public GameTime(GameRoot root) {
        this.root = root;
    }

    public void updateRealtimeNow() {
        this.realtimeSeconds = date.getTime() / 1000.0 + this.realtimeAdjust;
    }

    public double systemNow() {
        return (this.realtimeSeconds - this.realtimeAdjust) * 1000.0;
    }

    public double realtimeNow() {
        return this.realtimeSeconds;
    }
}
