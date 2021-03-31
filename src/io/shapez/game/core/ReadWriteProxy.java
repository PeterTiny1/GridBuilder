package io.shapez.game.core;

public abstract class ReadWriteProxy {
    private final String filename;
    public Object currentData = null;

    public ReadWriteProxy(String filename) {
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

    protected abstract void debouncedWrite();

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
}
