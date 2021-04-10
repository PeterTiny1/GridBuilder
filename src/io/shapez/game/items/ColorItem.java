package io.shapez.game.items;

import io.shapez.core.DrawParameters;
import io.shapez.core.ItemType;
import io.shapez.game.BaseItem;
import io.shapez.game.Colors;

import java.awt.*;

public class ColorItem extends BaseItem {
    public final Colors color;

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
        return switch (color) {
            case red -> Color.RED;
            case green -> Color.GREEN;
            case blue -> Color.BLUE;
            case cyan -> Color.CYAN;
            case uncolored -> Color.GRAY;
            case white -> Color.WHITE;
            case yellow -> Color.YELLOW;
            case purple -> Color.MAGENTA;
        };
    }

    @Override
    public ItemType getItemType() {
        return ItemType.color;
    }
}
