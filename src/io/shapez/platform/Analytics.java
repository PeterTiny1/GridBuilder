package io.shapez.platform;

import io.shapez.Application;

public abstract class Analytics {
    private final Application app;

    Analytics(Application app) {
        this.app = app;
    } 
}
