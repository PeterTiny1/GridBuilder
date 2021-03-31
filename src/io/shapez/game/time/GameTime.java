package io.shapez.game.time;

import java.util.Date;

public class GameTime {
    Date date = new Date();
    private double realtimeSeconds = 0;
    private final double realtimeAdjust = 0;

    public void updateRealtimeNow() {
        this.realtimeSeconds = date.getTime() / 1000.0 + this.realtimeAdjust;
    }

    public double systemNow() {
        return (this.realtimeSeconds - this.realtimeAdjust) * 1000.0;
    }
}
