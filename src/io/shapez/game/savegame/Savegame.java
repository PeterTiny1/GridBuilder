package io.shapez.game.savegame;

import io.shapez.Application;
import io.shapez.game.core.ExplainedResult;
import io.shapez.game.core.ReadWriteProxy;

import java.util.Date;

public class Savegame extends ReadWriteProxy {
    final Date date = new Date();
    final SavegameData currentData = this.getDefaultData();
    private final String internalId;
    private final String metaDataRef;

    public Savegame(final Application app, final String internalId, final String metaDataRef) {
        super(app, "savegame-" + internalId + ".bin");
        this.internalId = internalId;
        this.metaDataRef = metaDataRef;
    }

    static int getCurrentVersion() {
        return 1007;
    }

    public SavegameData getDefaultData() {
        assert date != null;
        return new SavegameData(Savegame.getCurrentVersion(), null, new SavegameStats(), date.getTime());
    }

    public long getLastRealUpdate() {
        return this.currentData.lastUpdate;
    }

    public boolean isSavable() {
        return true;
    }

    public void writeSavegameAndMetadata() {
        this.writeAsync();
    }

    @Override
    protected ExplainedResult verify(final Object data) {
        return null;
    }
}
