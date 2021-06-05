package io.shapez.game.items;

import io.shapez.core.DrawParameters;
import io.shapez.core.ItemType;
import io.shapez.game.BaseItem;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class BooleanItem extends BaseItem {
    public final boolean value;

    public BooleanItem(final boolean value) {
        super();
        this.value = value;
    }

    public static boolean isTruthyItem(final BaseItem item) {
        if (item == null) {
            return false;
        }

        if (item.getItemType() == ItemType.bool) {
            return ((BooleanItem) item).value;
        }

        return true;
    }

    @Override
    protected void drawItemCenteredImpl(final double x, final double y, final DrawParameters parameters, final double diameter) throws IOException {
        final BufferedImage sprite;
        if (this.value) {
            sprite = ImageIO.read(Objects.requireNonNull(this.getClass().getResource("/sprites/wires/boolean_true.png")));
        } else {
            sprite = ImageIO.read(Objects.requireNonNull(this.getClass().getResource("/sprites/wires/boolean_false.png")));
        }
        drawCachedCentered(sprite, parameters, x, y, diameter);
    }

    private void drawCachedCentered(final BufferedImage sprite, final DrawParameters parameters, final double x, final double y, final double size) {
        this.drawCached(sprite, parameters, x, y, size);
    }

    private void drawCached(final BufferedImage sprite, final DrawParameters parameters, final double x, final double y, final double size) {
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
