package io.shapez.managers;

import java.io.File;

public class SystemPathManager {

    public static final File rootFile = new File(System.getenv("APPDATA") + "/.jgridbuilder");

    public static final File settingsFile = new File(rootFile.getPath() + "/settings.bin");
    public static final File saveFile = new File(rootFile.getPath() + "/level.bin");


}
