package io.shapez.game.items;

import io.shapez.core.DrawParameters;
import io.shapez.core.ItemType;
import io.shapez.game.BaseItem;
import io.shapez.game.Colors;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import static io.shapez.game.Colors.colorToCode;
import static io.shapez.util.DrawUtil.drawCachedCentered;

public class ColorItem extends BaseItem {
    public final Colors color;
    private BufferedImage cachedSprite;

    public ColorItem(Colors color) {
        super();
        this.color = color;
    }

// --Commented out by Inspection START (17/10/2021, 15:00):
//    static String getId() {
//        return "color";
//    }
// --Commented out by Inspection STOP (17/10/2021, 15:00)

    @Override
    protected void drawItemCenteredImpl(double x, double y, DrawParameters parameters, double diameter) throws IOException {
        final double realDiameter = diameter * 0.6;
        if (this.cachedSprite == null) {
            cachedSprite = ImageIO.read(Objects.requireNonNull(this.getClass().getResource("/sprites/colors/" + this.color + ".png")));
        }
        drawCachedCentered(cachedSprite, parameters, x, y, realDiameter);
    }

    @Override
    public Color getBackgroundColorAsResource() {
        return colorToCode.get(this.color);
    }

    @Override
    public ItemType getItemType() {
        return ItemType.color;
    }
}
