package io.shapez.game.systems;

import io.shapez.core.DrawParameters;
import io.shapez.core.Layer;
import io.shapez.core.Vector;
import io.shapez.game.*;
import io.shapez.game.components.ItemAcceptorComponent;
import io.shapez.game.components.StaticMapEntityComponent;

import java.io.IOException;
import java.util.ArrayList;

public class ItemAcceptorSystem extends GameSystemWithFilter {
    private int accumulatedTicksWhileInMapOverview;

    public ItemAcceptorSystem(final GameRoot root) {
        super(root, new Component[]{new ItemAcceptorComponent()});
    }

    public void drawChunk(final DrawParameters parameters, final MapChunkView chunk) throws IOException {
        if (this.root.app.settings.getAllSettings().simplifiedBelts) {
            return;
        }

        final ArrayList<Entity> contents = chunk.containedEntitiesByLayer.get(Layer.Regular);
        for (final Entity entity : contents) {
            final ItemAcceptorComponent acceptorComp = entity.components.ItemAcceptor;
            if (acceptorComp == null) {
                continue;
            }

            final StaticMapEntityComponent staticComp = entity.components.StaticMapEntity;
            for (int animIndex = 0; animIndex < acceptorComp.itemConsumptionAnimations.size(); animIndex++) {
                final ItemAcceptorComponent.ItemConsumptionAnimation anim = acceptorComp.itemConsumptionAnimations.get(animIndex);

                final ItemAcceptorComponent.ItemAcceptorSlot slotData = acceptorComp.slots.get(anim.slotIndex);
                final Vector realSlotPos = staticComp.localTileToWorld(slotData.pos);

                if (!chunk.tileSpaceRectangle.contains(realSlotPos.x, realSlotPos.y)) {
                    continue;
                }

                final Vector fadeOutDirection = Vector.directionToVector(staticComp.localDirectionToWorld(anim.direction));
                final Vector finalTile = realSlotPos.subScalars(fadeOutDirection.x * (anim.animProgress / 2 - 0.5), fadeOutDirection.y * (anim.animProgress / 2 - 0.5));

                anim.item.drawItemCenteredClipped((finalTile.x + 0.5) * GlobalConfig.tileSize, (finalTile.y + 0.5) * GlobalConfig.tileSize, parameters, GlobalConfig.defaultItemDiameter);
            }
        }
    }

    public void update() {
        if (this.root.app.settings.getAllSettings().simplifiedBelts) {
            return;
        }

        if (this.root.camera.getIsMapOverlayActive()) {
            ++this.accumulatedTicksWhileInMapOverview;
            return;
        }

        final int numTicks = 1 + this.accumulatedTicksWhileInMapOverview;
        final double progress = this.root.dynamicTickrate.deltaSeconds * 2 * this.root.hubGoals.getBeltBaseSpeed() * GlobalConfig.itemSpacingOnBelts * numTicks;

        this.accumulatedTicksWhileInMapOverview = 0;

        for (final Entity entity : this.allEntities) {
            final ItemAcceptorComponent acceptorComp = entity.components.ItemAcceptor;
            final ArrayList<ItemAcceptorComponent.ItemConsumptionAnimation> animations = acceptorComp.itemConsumptionAnimations;

            for (int animIndex = 0; animIndex < animations.size(); ++animIndex) {
                final ItemAcceptorComponent.ItemConsumptionAnimation anim = animations.get(animIndex);
                anim.animProgress += progress;
                if (anim.animProgress > 1) {
                    animations.remove(animIndex);
                    animIndex -= 1;
                }
            }
        }
    }
}
