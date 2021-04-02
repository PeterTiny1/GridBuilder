package io.shapez.game.core;

import io.shapez.Application;
import io.shapez.game.savegame.SavegameData;

public abstract class ReadWriteProxy {
    private final String filename;
    public SavegameData currentData = null;

    public ReadWriteProxy(Application app, String filename) {
        this.filename = filename;

    }

    public void writeAsync() {
        ExplainedResult verifyResult = this.internalVerifyEntry(this.currentData);

        if (verifyResult == null) {
            return;
        }
        int _i;
        this.debouncedWrite();
    }

    protected void debouncedWrite() {
        // TODO: 01/04/2021 Implement writing using this class
    }

    private ExplainedResult internalVerifyEntry(Object data) {
        /*if (data.version != this.getCurrentVersion) {
         *   return ExplainedResult.bad(); //TODO: implement this*/

        ExplainedResult verifyStructureError = this.internalVerifyBasicStructure(data);
        if (!verifyStructureError.isGood()) {
            return verifyStructureError;
        }

        return this.verify(data);
    }

    protected abstract ExplainedResult verify(Object data);

    private ExplainedResult internalVerifyBasicStructure(Object data) {
        if (data == null) {
            return ExplainedResult.bad("Data is empty");
        }
//        if (data.version < 0) {
//
//        } TODO: implement this
        return ExplainedResult.good();
    }

    public abstract SavegameData getDefaultData();
}
