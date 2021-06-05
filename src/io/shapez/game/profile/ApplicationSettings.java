package io.shapez.game.profile;

import io.shapez.Application;
import io.shapez.game.core.ExplainedResult;
import io.shapez.game.core.ReadWriteProxy;
import io.shapez.game.savegame.SavegameData;

import static java.lang.Integer.parseInt;

public class ApplicationSettings extends ReadWriteProxy {
    public ApplicationSettings(final Application app) {
        super(app, "app_settings.bin");
    }

    public int getAutosaveIntervalSeconds() {
        final AutosaveInterval id = this.getAllSettings().autosaveInterval;
        if (id != null) {
            return id.getSeconds();
        }
        return 120;
    }

    public SettingsStorage getAllSettings() {
//        return this.currentData.settings; //TODO: add ability to import settings
        return new SettingsStorage();
    }

    @Override
    protected void debouncedWrite() {

    }

    @Override
    protected ExplainedResult verify(final Object data) {
        return null;
    }

    @Override
    public SavegameData getDefaultData() {
        return null;
    }

    public int getDesiredFps() {
        return parseInt(this.getAllSettings().refreshRate);
    }

    public enum AutosaveInterval {
        one_minute(60),
        two_minutes(120),
        five_minutes(5 * 60),
        ten_minutes(10 * 60),
        twenty_minutes(20 * 60);

        protected final int seconds;

        AutosaveInterval(final int seconds) {
            this.seconds = seconds;
        }

        int getSeconds() {
            return seconds;
        }
    }
}
