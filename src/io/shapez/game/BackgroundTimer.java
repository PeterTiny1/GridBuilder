package io.shapez.game;

import io.shapez.Application;

import java.util.Date;
import java.util.TimerTask;

public class BackgroundTimer extends TimerTask {
    private long time;
    private final Application application;
    Date date = new Date();
    private final GameCore core;

    public BackgroundTimer(Application application) {
        this.application = application;
        core = new GameCore(application);
        this.time = date.getTime();
    }

    @Override
    public void run() {

    }

    private void onBackgroundTick(long dt) {
        onRender(dt);
    }

    private void onRender(long dt) {
        this.core.tick(dt);
    }
}
