package io.shapez.game.systems;

import io.shapez.core.*;
import io.shapez.game.Component;
import io.shapez.game.*;
import io.shapez.game.components.*;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class ItemEjectorSystem extends GameSystemWithFilter {
    private final StaleAreaDetector staleAreaDetector;

    public ItemEjectorSystem(final GameRoot root) {
        super(root, new Component[]{new ItemEjectorComponent()});

        this.staleAreaDetector = new StaleAreaDetector("item-ejector", this::recomputeArea);

        this.staleAreaDetector.recomputeOnComponentsChanged(new Component[]{new ItemEjectorComponent(), new ItemAcceptorComponent(), new BeltComponent()}, 1);
    }

    private void recomputeArea(final Rectangle area) {
        final HashSet<Integer> seenUids = new HashSet<>();
        for (int x = 0; x < area.width; ++x) {
            for (int y = 0; y < area.height; ++y) {
                final int tileX = area.x + x;
                final int tileY = area.y + y;

                final Entity contents = root.map.getLayerContentXY(tileX, tileY, Layer.Regular);
                if (contents != null && contents.components.ItemEjector != null) {
                    if (!seenUids.contains(contents.uid)) {
                        seenUids.add(contents.uid);
                        this.recomputeSingleEntityCache(contents);
                    }
                }
            }
        }
    }

    private void recomputeSingleEntityCache(final Entity entity) {
        final ItemEjectorComponent ejectorComp = entity.components.ItemEjector;
        final StaticMapEntityComponent staticComp = entity.components.StaticMapEntity;

        for (int slotIndex = 0; slotIndex < ejectorComp.slots.size(); ++slotIndex) {
            final ItemEjectorComponent.ItemEjectorSlot ejectorSlot = ejectorComp.slots.get(slotIndex);

            ejectorSlot.cachedDestSlot = null;
            ejectorSlot.cachedTargetEntity = null;
            ejectorSlot.cachedBeltPath = null;

            final Vector ejectSlotWsTile = staticComp.localTileToWorld(ejectorSlot.pos);
            final Direction ejectSlotWsDirection = staticComp.localDirectionToWorld(ejectorSlot.direction);
            final Vector ejectSlotWsDirectionVector = Vector.directionToVector(ejectSlotWsDirection);
            final Vector ejectSlotTargetWsTile = ejectSlotWsTile.add(ejectSlotWsDirectionVector);

            final Entity[] targetEntities = root.map.getLayerContentsMultipleXY(ejectSlotTargetWsTile.x, ejectSlotTargetWsTile.y);

            for (final Entity targetEntity : targetEntities) {
                final StaticMapEntityComponent targetStaticComp = targetEntity.components.StaticMapEntity;
                final BeltComponent targetBeltComp = targetEntity.components.Belt;

                if (targetBeltComp != null) {
                    final Direction beltAcceptingDirection = targetStaticComp.localDirectionToWorld(Direction.Top);
                    if (ejectSlotWsDirection == beltAcceptingDirection) {
                        ejectorSlot.cachedTargetEntity = targetEntity;
                        ejectorSlot.cachedBeltPath = targetBeltComp.assignedPath;
                        break;
                    }
                }

                final ItemAcceptorComponent targetAcceptorComp = targetEntity.components.ItemAcceptor;
                if (targetAcceptorComp == null) {
                    continue;
                }

                final ItemAcceptorComponent.ItemAcceptorLocatedSlot matchingSlot = targetAcceptorComp.findMatchingSlot(targetStaticComp.worldToLocalTile(ejectSlotWsTile), targetStaticComp.worldDirectionToLocal(ejectSlotWsDirection));
                if (matchingSlot == null) {
                    continue;
                }

                ejectorSlot.cachedTargetEntity = targetEntity;
                ejectorSlot.cachedDestSlot = matchingSlot;
                break;
            }
        }
    }

    public void drawChunk(final DrawParameters parameters, final MapChunkView chunk) throws IOException {
        if (this.root.app.settings.getAllSettings().simplifiedBelts) {
            return;
        }

        final ArrayList<Entity> contents = chunk.containedEntitiesByLayer.get(Layer.Regular);

        for (final Entity entity : contents) {
            final ItemEjectorComponent ejectorComp = entity.components.ItemEjector;
            if (ejectorComp == null) {
                continue;
            }

            final StaticMapEntityComponent staticComp = entity.components.StaticMapEntity;

            for (int i = 0; i < ejectorComp.slots.size(); i++) {
                final ItemEjectorComponent.ItemEjectorSlot slot = ejectorComp.slots.get(i);
                final BaseItem ejectedItem = slot.item;
                if (ejectedItem == null) {
                    continue;
                }
                if (!ejectedItem.renderFloatingItems && slot.cachedTargetEntity == null) {
                    continue;
                }

                double progress = slot.progress;
                final BeltPath nextBeltPath = slot.cachedBeltPath;
                if (nextBeltPath != null) {
                    final double maxProgress = (0.5 + nextBeltPath.spacingToFirstItem - GlobalConfig.itemSpacingOnBelts) * 2;
                    progress = Math.min(maxProgress, progress);
                }

                if (progress < 0.05) {
                    continue;
                }

                final Vector realPosition = staticComp.localTileToWorld(slot.pos);
                if (!chunk.tileSpaceRectangle.contains(realPosition.x, realPosition.y)) {
                    continue;
                }

                final Direction realDirection = staticComp.localDirectionToWorld(slot.direction);
                final Vector realDirectionVector = Vector.directionToVector(realDirection);

                final double tileX = realPosition.x + 0.5 + realDirectionVector.x * 0.5 * progress;
                final double tileY = realPosition.y + 0.5 + realDirectionVector.x * 0.5 * progress;

                final double worldX = tileX * GlobalConfig.tileSize;
                final double worldY = tileY * GlobalConfig.tileSize;

                ejectedItem.drawItemCenteredClipped(worldX, worldY, parameters, GlobalConfig.defaultItemDiameter);
            }
        }
    }

    public boolean tryPassOverItem(final BaseItem item, final Entity receiver, final int slotIndex) {
        final BeltComponent beltComp = receiver.components.Belt;
        if (beltComp != null) {
            final BeltPath path = beltComp.assignedPath;
            assert path != null;
            return path.tryAcceptItem(item);
        }

        final ItemProcessorComponent itemProcessorComp = receiver.components.ItemProcessor;
        if (itemProcessorComp != null) {
            if (!this.root.systemMgr.itemProcessor.checkRequirements(receiver, item, slotIndex)) {
                return false;
            }

            return itemProcessorComp.tryTakeItem(item, slotIndex);
        }

        final UndergroundBeltComponent undergroundBeltComp = receiver.components.UndergroundBelt;

        if (undergroundBeltComp != null) {
            return undergroundBeltComp.tryAcceptExternalItem(item, this.root.hubGoals.undergroundBeltBaseSpeed());
        }
        final StorageComponent storageComp = receiver.components.Storage;
        if (storageComp != null) {
            if (storageComp.canAcceptItem(item)) {
                storageComp.takeItem(item);
                return true;
            }
            return false;
        }

        final FilterComponent filterComp = receiver.components.Filter;
        if (filterComp != null) {
            return this.root.systemMgr.filter.tryAcceptItem(receiver, slotIndex, item);
        }
        return false;
    }
}
