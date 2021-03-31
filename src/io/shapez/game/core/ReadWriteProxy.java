package io.shapez.game.core;

public class ReadWriteProxy {
    private final String filename;
    public Object currentData = null;

    public ReadWriteProxy(String filename) {
        this.filename = filename;

    }
}
