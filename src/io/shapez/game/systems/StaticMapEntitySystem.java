package io.shapez.game.systems;

import io.shapez.core.DrawParameters;
import io.shapez.core.Layer;
import io.shapez.game.Entity;
import io.shapez.game.GameRoot;
import io.shapez.game.GameSystem;
import io.shapez.game.MapChunkView;
import io.shapez.game.components.StaticMapEntityComponent;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;

public class StaticMapEntitySystem extends GameSystem {
    HashSet<Integer> drawnUids = new HashSet<>();

    public StaticMapEntitySystem(GameRoot root) {
        super(root);

    }

    public void drawChunk(DrawParameters parameters, MapChunkView chunk) {
        ArrayList<Entity> contents = chunk.containedEntitiesByLayer.get(Layer.Regular);
        for (Entity entity : contents) {
            StaticMapEntityComponent staticComp = entity.components.StaticMapEntity;

            BufferedImage sprite = staticComp.getImage();

            if (sprite != null) {
                if (this.drawnUids.contains(entity.uid)) {
                    continue;
                }
                staticComp.drawSpriteOnBoundsClipped(parameters, sprite, 2);
            }
        }
    }
}
