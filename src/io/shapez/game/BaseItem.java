package io.shapez.game;

import java.awt.*;

public abstract class BaseItem {

    void drawItemCenteredClipped(double x, double y, Graphics2D g2d) {
        int diameter = GlobalConfig.defaultItemDiameter;
//        if (parameters.visibleRect.containsCircle(x, y, diameter / 2)) { //TODO: implement this
        this.drawItemCenteredImpl(x, y, g2d, diameter);
    }

    protected abstract void drawItemCenteredImpl(double x, double y, Graphics2D g2d, int diameter);
}
