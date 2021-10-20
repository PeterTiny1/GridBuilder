package io.shapez.platform;

import io.shapez.Application;

public class ShapezGameAnalytics implements GameAnalyticsInterface {
    public ShapezGameAnalytics(Application app) {

    }

    @Override
    public void handleLevelCompleted(int level) {
        this.sendGameEvent("level_complete", "" + level);
    }

    private void sendGameEvent(String category, String value) {
//        this.sendToApi("/v1/game-event", )
    }
}
