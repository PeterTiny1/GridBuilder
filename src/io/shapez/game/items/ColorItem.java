package io.shapez.game.items;

import io.shapez.core.DrawParameters;
import io.shapez.game.BaseItem;
import io.shapez.game.Colors;

import java.awt.*;

public class ColorItem extends BaseItem {
    private final Colors color;

    public ColorItem(Colors color) {
        super();
        this.color = color;
    }

    static String getId() {
        return "color";
    }

    @Override
    protected void drawItemCenteredImpl(double x, double y, DrawParameters g2d, double diameter) {

    }

    @Override
    public Color getBackgroundColorAsResource() {
        return null;
    }
}
