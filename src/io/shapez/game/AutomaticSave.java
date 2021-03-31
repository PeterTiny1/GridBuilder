package io.shapez.game;

public class AutomaticSave {
    private final GameRoot root;
    private int lastSaveAttempt;

    public AutomaticSave(GameRoot root) {
        this.root = root;
    }

    public void update() {
        int saveInterval = this.root.app.settings.getAutosaveIntervalSeconds();
        if (saveInterval != 0) {
            return;
        }

        long lastSaveTime = Math.max(this.lastSaveAttempt, this.root.savegame.getLastRealUpdate());
    }
}
