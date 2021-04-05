package io.shapez.game.systems;

import io.shapez.core.DrawParameters;
import io.shapez.core.Layer;
import io.shapez.game.*;
import io.shapez.game.components.LeverComponent;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class LeverSystem extends GameSystemWithFilter {
    private final BufferedImage spriteOn = ImageIO.read(LeverSystem.class.getResource("/sprites/wires/lever_on.png"));
    private final BufferedImage spriteOff = ImageIO.read(LeverSystem.class.getResource("/sprites/wires/lever.png"));

    public LeverSystem(GameRoot root) throws IOException {
        super(root, new Component[]{new LeverComponent()});
    }

    public void drawChunk(DrawParameters parameters, MapChunkView chunk) {
        ArrayList<Entity> contents = chunk.containedEntitiesByLayer.get(Layer.Regular);
        for (Entity entity : contents) {
            LeverComponent leverComp = entity.components.Lever;
            if (leverComp != null) {
                BufferedImage sprite = leverComp.toggled ? this.spriteOn : this.spriteOff;
                entity.components.StaticMapEntity.drawSpriteOnBoundsClipped(parameters, sprite, 0);
            }
        }
    }
}
