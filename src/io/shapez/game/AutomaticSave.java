package io.shapez.game;

public class AutomaticSave {
    private final GameRoot root;
    private long lastSaveAttempt;
    private SavePriority saveImportance;

    enum SavePriority {
        regular,
        asap
    }

    public AutomaticSave(GameRoot root) {
        this.root = root;
    }

    public void update() {
        int saveInterval = this.root.app.settings.getAutosaveIntervalSeconds();
        if (saveInterval != 0) {
            return;
        }

        long lastSaveTime = Math.max(this.lastSaveAttempt, this.root.savegame.getLastRealUpdate());

        double secondsSinceLastSave = (root.app.date.getTime() - lastSaveTime) / 1000.0;
        boolean shouldSave = false;

        switch (this.saveImportance) {
            case asap:
                shouldSave = true;
            case regular:
                shouldSave = secondsSinceLastSave > saveInterval;
                break;
            default:
                break;
        }
        if (shouldSave) {
            this.lastSaveAttempt = this.root.app.date.getTime();
            this.doSave();
        }
    }

    private void doSave() {
        this.root.doSave();
        this.saveImportance = SavePriority.regular;
    }
}
