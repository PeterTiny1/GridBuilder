package io.shapez.managers.providers;

import java.io.File;

import static io.shapez.managers.providers.MiscProvider.gameName;

public class SystemPathProvider {

    public static final File rootFile = new File(System.getenv("APPDATA") + "/." + gameName.toLowerCase());

    public static final File settingsFile = new File(rootFile.getPath() + "/settings.bin");
    public static final File saveFile = new File(rootFile.getPath() + "/level.bin");


}
