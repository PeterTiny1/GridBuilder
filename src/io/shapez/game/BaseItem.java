package io.shapez.game;

import io.shapez.core.DrawParameters;

import java.awt.*;

public abstract class BaseItem {
    public void drawItemCenteredClipped(double x, double y, DrawParameters parameters, double diameter) {
//        int diameter = GlobalConfig.defaultItemDiameter;
        if (parameters.visibleRect.contains(x, y)) {
            this.drawItemCenteredImpl(x, y, parameters, diameter);
        }
    }

    protected abstract void drawItemCenteredImpl(double x, double y, DrawParameters g2d, double diameter);

    public abstract Color getBackgroundColorAsResource();

    public void drawItemCenteredClipped(double x, double y, DrawParameters parameters) {
        if (parameters.visibleRect.contains(x, y)) {
            this.drawItemCenteredImpl(x, y, parameters, GlobalConfig.defaultItemDiameter);
        }
    }
}
