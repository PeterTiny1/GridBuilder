package io.shapez.game.profile;

import io.shapez.game.core.ReadWriteProxy;

public class ApplicationSettings extends ReadWriteProxy {
    public ApplicationSettings() {
        super("app_settings.bin");
    }

    public int getAutosaveIntervalSeconds() {
//        String id = this.getAllSettings().autosaveInterval; // TODO: make this work
//        for (int i = 0; i < movementSpeeds.; i++) { // TODO: also make this work
//
//        }
        return 1;
    }

    private Object getAllSettings() {
//        return this.currentData.settings; TODO: add ability to import settings
        return null;
    }
}
