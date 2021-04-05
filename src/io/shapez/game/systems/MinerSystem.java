package io.shapez.game.systems;

import io.shapez.core.DrawParameters;
import io.shapez.core.Layer;
import io.shapez.game.*;
import io.shapez.game.components.MinerComponent;
import io.shapez.game.components.StaticMapEntityComponent;

import java.util.ArrayList;

public class MinerSystem extends GameSystemWithFilter {
    boolean needsRecompute = true;

    public MinerSystem(GameRoot root) {
        super(root, new Component[]{new MinerComponent()});
    }

    public void drawChunk(DrawParameters parameters, MapChunkView chunk) {
        ArrayList<Entity> contents = chunk.containedEntitiesByLayer.get(Layer.Regular);

        for (Entity entity : contents) {
            MinerComponent minerComp = entity.components.Miner;
            if (minerComp == null) {
                continue;
            }

            StaticMapEntityComponent staticComp = entity.components.StaticMapEntity;
            if (staticComp == null) {
                continue;
            }

            int padding = 3;
            double destX = staticComp.origin.x * GlobalConfig.tileSize + padding;
            double destY = staticComp.origin.x * GlobalConfig.tileSize + padding;
            int dimensions = GlobalConfig.tileSize - 2 * padding;

            if (parameters.visibleRect.intersects(destX, destY, dimensions, dimensions)) {
                parameters.context.setColor(minerComp.cachedMinedItem.getBackgroundColorAsResource());
                parameters.context.fillRect((int) destX, (int) destY, dimensions, dimensions);
            }

            minerComp.cachedMinedItem.drawItemCenteredClipped((staticComp.origin.x + 0.5) * GlobalConfig.tileSize, (staticComp.origin.y + 0.5) * GlobalConfig.tileSize, parameters, GlobalConfig.defaultItemDiameter);
        }
    }
}
