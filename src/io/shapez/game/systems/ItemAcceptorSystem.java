package io.shapez.game.systems;

import io.shapez.core.Direction;
import io.shapez.core.DrawParameters;
import io.shapez.core.Layer;
import io.shapez.core.Vector;
import io.shapez.game.*;
import io.shapez.game.components.ItemAcceptorComponent;
import io.shapez.game.components.StaticMapEntityComponent;

import java.io.IOException;
import java.util.ArrayList;

public class ItemAcceptorSystem extends GameSystemWithFilter {
    public ItemAcceptorSystem(GameRoot root) {
        super(root, new Component[]{new ItemAcceptorComponent()});
    }

    public void drawChunk(DrawParameters parameters, MapChunkView chunk) throws IOException {
        if (this.root.app.settings.getAllSettings().simplifiedBelts) {
            return;
        }

        ArrayList<Entity> contents = chunk.containedEntitiesByLayer.get(Layer.Regular);
        for (Entity entity : contents) {
            ItemAcceptorComponent acceptorComp = entity.components.ItemAcceptor;
            if (acceptorComp == null) {
                continue;
            }

            StaticMapEntityComponent staticComp = entity.components.StaticMapEntity;
            for (int animIndex = 0; animIndex < acceptorComp.itemConsumptionAnimations.size(); animIndex++) {
                ItemAcceptorComponent.ItemConsumptionAnimation anim = acceptorComp.itemConsumptionAnimations.get(animIndex);

                ItemAcceptorComponent.ItemAcceptorSlot slotData = acceptorComp.slots.get(anim.slotIndex);
                Vector realSlotPos = staticComp.localTileToWorld(slotData.pos);

                if (!chunk.tileSpaceRectangle.contains(realSlotPos.x, realSlotPos.y)) {
                    continue;
                }

                Vector fadeOutDirection = Vector.directionToVector(staticComp.localDirectionToWorld(anim.direction));
                Vector finalTile = realSlotPos.subScalars(fadeOutDirection.x * (anim.progress / 2 - 0.5),fadeOutDirection.y * (anim.progress / 2 - 0.5));

                anim.item.drawItemCenteredClipped((finalTile.x + 0.5) * GlobalConfig.tileSize, (finalTile.y + 0.5) * GlobalConfig.tileSize, parameters, GlobalConfig.defaultItemDiameter);
            }
        }
    }
}
