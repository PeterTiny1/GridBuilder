package io.shapez.game;

import io.shapez.Application;
import io.shapez.game.core.ExplainedResult;
import io.shapez.game.core.ReadWriteProxy;
import io.shapez.game.savegame.SavegameData;

public class RestrictionManager extends ReadWriteProxy {
    public RestrictionManager(Application app) {
        super(app, "restriction-flags.bin");

        this.currentData = this.getDefaultData();
    }

    public SavegameData getDefaultData() {
        return new SavegameData(this.getCurrentVersion(), false);
    }

    private int getCurrentVersion() {
        return 1;
    }

    @Override
    protected ExplainedResult verify(Object data) {
        return ExplainedResult.good();
    }

    public boolean getHasExtendedUpgrades() {
        return !this.isLimitedVersion() || this.currentData.savegameV1119Imported;
    }

    private boolean isLimitedVersion() {
        return false;
    }

    public boolean getIsStandaloneMarketingActive() {
        return this.isLimitedVersion();
    }
}
