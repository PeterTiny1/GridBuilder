package io.shapez.core;

import io.shapez.game.GameRoot;

import java.awt.*;

public class DrawParameters {
    public final Graphics2D context;
    public final Rectangle visibleRect;
    public final String desiredAtlasScale;
    public final double zoomLevel;
    private final GameRoot root;

    public DrawParameters(Graphics2D context, Rectangle visibleRect, String desiredAtlasScale, double zoomLevel, GameRoot root) {
        this.context = context;
        this.visibleRect = visibleRect;
        this.desiredAtlasScale = desiredAtlasScale;
        this.zoomLevel = zoomLevel;
        this.root = root;
    }
}
