package io.shapez.game;

import io.shapez.core.DrawParameters;
import io.shapez.core.ItemType;

import java.awt.*;
import java.io.IOException;

public abstract class BaseItem {
    public final boolean renderFloatingItems = true;
//    public BooleanItem value;

    public void drawItemCenteredClipped(double x, double y, DrawParameters parameters, double diameter) throws IOException {
//        int diameter = GlobalConfig.defaultItemDiameter;
        if (parameters.visibleRect.contains(x, y)) {
            this.drawItemCenteredImpl(x, y, parameters, diameter);
        }
    }

    protected abstract void drawItemCenteredImpl(double x, double y, DrawParameters parameters, double diameter) throws IOException;

    public abstract Color getBackgroundColorAsResource();

    public void drawItemCenteredClipped(double x, double y, DrawParameters parameters) throws IOException {
        if (parameters.visibleRect.contains(x, y)) {
            this.drawItemCenteredImpl(x, y, parameters, GlobalConfig.defaultItemDiameter);
        }
    }

    public abstract ItemType getItemType();
}
