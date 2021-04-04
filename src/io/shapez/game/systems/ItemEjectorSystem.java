package io.shapez.game.systems;

import io.shapez.core.Direction;
import io.shapez.core.Layer;
import io.shapez.core.StaleAreaDetector;
import io.shapez.core.Vector;
import io.shapez.game.*;
import io.shapez.game.Component;
import io.shapez.game.components.BeltComponent;
import io.shapez.game.components.ItemAcceptorComponent;
import io.shapez.game.components.ItemEjectorComponent;
import io.shapez.game.components.StaticMapEntityComponent;

import java.awt.*;
import java.util.HashSet;

public class ItemEjectorSystem extends GameSystemWithFilter {
    private final StaleAreaDetector staleAreaDetector;

    public ItemEjectorSystem(GameRoot root) {
        super(root, new Component[]{new ItemEjectorComponent()});

        this.staleAreaDetector = new StaleAreaDetector("item-ejector", this::recomputeArea);

        this.staleAreaDetector.recomputeOnComponentsChanged(new Component[]{new ItemEjectorComponent(), new ItemAcceptorComponent(), new BeltComponent()}, 1);
    }

    private void recomputeArea(Rectangle area) {
        HashSet<Integer> seenUids = new HashSet<>();
        for (int x = 0; x < area.width; ++x) {
            for (int y = 0; y < area.height; ++y) {
                int tileX = area.x + x;
                int tileY = area.y + y;

                Entity contents = root.map.getLayerContentXY(tileX, tileY, Layer.Regular);
                if (contents != null && contents.components.ItemEjector != null) {
                    if (!seenUids.contains(contents.uid)) {
                        seenUids.add(contents.uid);
                        this.recomputeSingleEntityCache(contents);
                    }
                }
            }
        }
    }

    private void recomputeSingleEntityCache(Entity entity) {
        ItemEjectorComponent ejectorComp = entity.components.ItemEjector;
        StaticMapEntityComponent staticComp = entity.components.StaticMapEntity;

        for (int slotIndex = 0; slotIndex < ejectorComp.slots.size(); ++slotIndex) {
            ItemEjectorComponent.ItemEjectorSlot ejectorSlot = ejectorComp.slots.get(slotIndex);

            ejectorSlot.cachedDestSlot = null;
            ejectorSlot.cachedTargetEntity = null;
            ejectorSlot.cachedBeltPath = null;

            Vector ejectSlotWsTile = staticComp.localTileToWorld(ejectorSlot.pos);
            Direction ejectSlotWsDirection = staticComp.localDirectionToWorld(ejectorSlot.direction);
            Vector ejectSlotWsDirectionVector = Vector.directionToVector(ejectSlotWsDirection);
            Vector ejectSlotTargetWsTile = ejectSlotWsTile.add(ejectSlotWsDirectionVector);

            Entity[] targetEntities = root.map.getLayerContentsMultipleXY(ejectSlotTargetWsTile.x, ejectSlotTargetWsTile.y);

            for (Entity targetEntity : targetEntities) {
                StaticMapEntityComponent targetStaticComp = targetEntity.components.StaticMapEntity;
                BeltComponent targetBeltComp = targetEntity.components.Belt;

                if (targetBeltComp != null) {
                    Direction beltAcceptingDirection = targetStaticComp.localDirectionToWorld(Direction.Top);
                    if (ejectSlotWsDirection == beltAcceptingDirection) {
                        ejectorSlot.cachedTargetEntity = targetEntity;
                        ejectorSlot.cachedBeltPath = targetBeltComp.assignedPath;
                        break;
                    }
                }

                ItemAcceptorComponent targetAcceptorComp = targetEntity.components.ItemAcceptor;
                if (targetAcceptorComp == null) {
                    continue;
                }

                ItemAcceptorComponent.ItemAcceptorLocatedSlot matchingSlot = targetAcceptorComp.findMatchingSlot(targetStaticComp.worldToLocalTile(ejectSlotWsTile), targetStaticComp.worldDirectionToLocal(ejectSlotWsDirection));
                if (matchingSlot == null) {
                    continue;
                }

                ejectorSlot.cachedTargetEntity = targetEntity;
                ejectorSlot.cachedDestSlot = matchingSlot;
                break;
            }
        }
    }
}
