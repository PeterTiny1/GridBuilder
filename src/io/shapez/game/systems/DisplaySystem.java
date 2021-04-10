package io.shapez.game.systems;

import io.shapez.core.DrawParameters;
import io.shapez.core.ItemType;
import io.shapez.core.Layer;
import io.shapez.core.Vector;
import io.shapez.game.*;
import io.shapez.game.components.DisplayComponent;
import io.shapez.game.components.WiredPinsComponent;
import io.shapez.game.items.BooleanItem;
import io.shapez.game.items.ColorItem;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class DisplaySystem extends GameSystemWithFilter {
    HashMap<Colors, BufferedImage> displaySprites = new HashMap<>() {{
        try {
            put(Colors.white, ImageIO.read(this.getClass().getResource("/sprites/wires/display/white.png")));
            put(Colors.blue, ImageIO.read(this.getClass().getResource("/sprites/wires/display/blue.png")));
            put(Colors.cyan, ImageIO.read(this.getClass().getResource("/sprites/wires/display/cyan.png")));
            put(Colors.green, ImageIO.read(this.getClass().getResource("/sprites/wires/display/green.png")));
            put(Colors.purple, ImageIO.read(this.getClass().getResource("/sprites/wires/display/purple.png")));
            put(Colors.red, ImageIO.read(this.getClass().getResource("/sprites/wires/display/red.png")));
            put(Colors.yellow, ImageIO.read(this.getClass().getResource("/sprites/wires/display/yellow.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }};

    public DisplaySystem(GameRoot root) {
        super(root, new Component[]{new DisplayComponent()});


    }

    public void drawChunk(DrawParameters parameters, MapChunkView chunk) throws IOException {
        ArrayList<Entity> contents = chunk.containedEntitiesByLayer.get(Layer.Regular);
        for (Entity entity : contents) {
            if (entity != null && entity.components.Display != null) {
                WiredPinsComponent pinsComp = entity.components.WiredPins;
                WireSystem.WireNetwork network = pinsComp.slots.get(0).linkedNetwork;

                if (network == null || !network.hasValue()) {
                    continue;
                }

                BaseItem value = this.getDisplayItem(network.currentValue);

                if (value == null) continue;

                Vector origin = entity.components.StaticMapEntity.origin;
                if (value.getItemType() == ItemType.color) {
                    drawCachedCentered(this.displaySprites.get(((ColorItem) value).color), parameters, (origin.x + 0.5) * GlobalConfig.tileSize, (origin.y + 0.5) * GlobalConfig.tileSize, GlobalConfig.tileSize);
                } else if (value.getItemType() == ItemType.shape) {
                    value.drawItemCenteredClipped((origin.x + 0.5) * GlobalConfig.tileSize, (origin.y + 0.5) * GlobalConfig.tileSize, parameters, 30);
                }
            }
        }
    }

    private void drawCachedCentered(BufferedImage sprite, DrawParameters parameters, double x, double y, byte size) {
        this.drawCached(sprite, parameters, x - (double) size / 2, y - (double) size / 2, size, size);
    }

    private void drawCached(BufferedImage sprite, DrawParameters parameters, double x, double y, byte w, byte h) {
        parameters.context.drawImage(sprite, (int) x, (int) y, w, h, null);
    }

    private BaseItem getDisplayItem(BaseItem value) {
        if (value == null) {
            return null;
        }

        switch (value.getItemType()) {
            case bool:
                return isTrueItem(value) ? new ColorItem(Colors.white) : null;
            case color:
                ColorItem item = (ColorItem) value;
                return item.color == Colors.uncolored ? new ColorItem(Colors.white) : item;
            case shape:
                return value;
            default:
                return null;
        }
    }

    private boolean isTrueItem(BaseItem item) {
        return item != null && item.getItemType() == ItemType.bool && ((BooleanItem) (item)).value;
    }
}
