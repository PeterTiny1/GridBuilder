package io.shapez.game.items;

import io.shapez.core.DrawParameters;
import io.shapez.core.ItemType;
import io.shapez.game.BaseItem;
import io.shapez.game.ShapeDefinition;
import io.shapez.game.themes.LightTheme;

import java.awt.*;
import java.io.IOException;

public class ShapeItem extends BaseItem {
    public ShapeDefinition definition;

    @Override
    protected void drawItemCenteredImpl(final double x, final double y, final DrawParameters parameters, final double diameter) throws IOException {
        this.definition.drawCentered(x, y, parameters, diameter);
    }

    @Override
    public Color getBackgroundColorAsResource() {
        return LightTheme.Map.Resources.shape;
    }

    @Override
    public ItemType getItemType() {
        return ItemType.shape;
    }
}
