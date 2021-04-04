package io.shapez.game;

import io.shapez.core.DrawParameters;

import java.awt.*;

public abstract class BaseItem {
    public void drawItemCenteredClipped(double x, double y, DrawParameters parameters, double diameter) {
//        int diameter = GlobalConfig.defaultItemDiameter;
//        if (parameters.visibleRect.containsCircle(x, y, diameter / 2)) { //TODO: implement this
        this.drawItemCenteredImpl(x, y, parameters.context, diameter);
    }

    protected abstract void drawItemCenteredImpl(double x, double y, Graphics2D g2d, double diameter);

    public abstract Color getBackgroundColorAsResource();
}
