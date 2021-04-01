package io.shapez.game.savegame;

public class SavegameData {
    public final boolean savegameV1119Imported;
    public long lastUpdate;

    public SavegameData(int currentVersion, Object o, SavegameStats savegameStats, long lastUpdate) {
        this.lastUpdate = lastUpdate;
        savegameV1119Imported = true;
    }

    public SavegameData(int currentVersion, boolean savegameV1119Imported) {
        this.savegameV1119Imported = savegameV1119Imported;
    }
}
