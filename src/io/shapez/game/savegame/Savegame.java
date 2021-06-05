package io.shapez.game.savegame;

import io.shapez.Application;
import io.shapez.game.core.ExplainedResult;
import io.shapez.game.core.ReadWriteProxy;

import java.util.Date;

public class Savegame extends ReadWriteProxy {
    private final String internalId;
    private final String metaDataRef;
    final SavegameData currentData = this.getDefaultData();
    final Date date = new Date();

    public SavegameData getDefaultData() {
        assert date != null;
        return new SavegameData(Savegame.getCurrentVersion(), null, new SavegameStats(), date.getTime());
    }

    static int getCurrentVersion() {
        return 1007;
    }

    public Savegame(final Application app, final String internalId, final String metaDataRef) {
        super(app, "savegame-" + internalId + ".bin");
        this.internalId = internalId;
        this.metaDataRef = metaDataRef;
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
    public void writeAsync() {
        super.writeAsync();
    }

    @Override
    protected ExplainedResult verify(final Object data) {
        return null;
    }
}
