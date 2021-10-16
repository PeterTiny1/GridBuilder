package io.shapez.game;

public class AutomaticSave {
    private final GameRoot root;
    private long lastSaveAttempt;
    private SavePriority saveImportance;

    enum SavePriority {
        regular,
        asap
    }

    public AutomaticSave(final GameRoot root) {
        this.root = root;
    }

    public void update() {
        final int saveInterval = this.root.app.settings.getAutosaveIntervalSeconds();
        if (saveInterval != 0) {
            return;
        }

        final long lastSaveTime = Math.max(this.lastSaveAttempt, this.root.savegame.getLastRealUpdate());

        final double secondsSinceLastSave = (System.currentTimeMillis() - lastSaveTime) / 1000.0;
        boolean shouldSave = false;

        switch (this.saveImportance) {
            case asap -> shouldSave = true;
            case regular -> shouldSave = secondsSinceLastSave > saveInterval;
        }
        if (shouldSave) {
            this.lastSaveAttempt = System.currentTimeMillis();
            this.doSave();
        }
    }

    private void doSave() {
        this.root.doSave();
        this.saveImportance = SavePriority.regular;
    }
}
