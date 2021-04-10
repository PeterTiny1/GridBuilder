package io.shapez.game.items;

import io.shapez.core.DrawParameters;
import io.shapez.core.ItemType;
import io.shapez.game.BaseItem;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class BooleanItem extends BaseItem {
    public boolean value;
    @Override
    protected void drawItemCenteredImpl(double x, double y, DrawParameters parameters, double diameter) throws IOException {
        BufferedImage sprite;
        if (this.value) {
            sprite = ImageIO.read(this.getClass().getResource("/sprites/wires/boolean_true.png"));
        } else {
            sprite = ImageIO.read(this.getClass().getResource("/sprites/wires/boolean_false.png"));
        }
        drawCachedCentered(sprite, parameters, x, y, diameter);
    }

    private void drawCachedCentered(BufferedImage sprite, DrawParameters parameters, double x, double y, double size) {
        this.drawCached(sprite, parameters, x, y, size);
    }

    private void drawCached(BufferedImage sprite, DrawParameters parameters, double x, double y, double size) {
//        Rectangle visibleRect = parameters.visibleRect;
//
//        String scale = parameters.desiredAtlasScale; //TODO: maybe implement this
        parameters.context.drawImage(sprite, (int) x, (int) y, (int) size / 2, (int) size / 2, null);
    }

    @Override
    public Color getBackgroundColorAsResource() {
        return null;
    }

    @Override
    public ItemType getItemType() {
        return ItemType.bool;
    }
}
