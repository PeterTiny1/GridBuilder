package io.shapez.game.savegame;

public class SavegameData {
    public long lastUpdate;

    public SavegameData(int currentVersion, Object o, SavegameStats savegameStats, long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
