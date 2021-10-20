package io.shapez.platform;

import io.shapez.Application;

public abstract class AdProvider {
    private final Application app;

    public AdProvider(Application app) {
        this.app = app;
    }
}
