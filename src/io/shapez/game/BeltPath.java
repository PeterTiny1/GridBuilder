package io.shapez.game;

import io.shapez.core.Direction;
import io.shapez.core.DrawParameters;
import io.shapez.core.Layer;
import io.shapez.core.Vector;
import io.shapez.game.components.BeltComponent;
import io.shapez.game.components.ItemAcceptorComponent;
import io.shapez.game.components.StaticMapEntityComponent;
import io.shapez.game.savegame.BaseDataType;
import io.shapez.game.savegame.BasicSerializableObject;

import java.awt.*;
import java.io.IOException;
import java.util.*;

public class BeltPath extends BasicSerializableObject {
    static String getId = "BeltPath";
    private final ArrayList<AbstractMap.SimpleEntry<Double, BaseItem>> items = new ArrayList<>();
    private GameRoot root;
    public LinkedList<Entity> entityPath = new LinkedList<>();
    AcceptingEntityAndSlot acceptorTarget;
    private int numCompressedItemsAfterFirstItem;
    private double totalLength;
    public double spacingToFirstItem;
    private Rectangle worldBounds;

    public BeltPath(final GameRoot root, final LinkedList<Entity> entityPath) {
        this.root = root;
        this.entityPath = entityPath;
        this.init(true);
    }

    public BeltPath() {

    }

    static BeltPath fromSerialized(final Object data) {
        final BeltPath fakeObject = new BeltPath();
// String errorCodeDesiralize = fakeObject.deserialize(data);
// if (errorCodeDesiralize != null) {
//     return errorCodeDesiralize;
// }
        fakeObject.init(false);
        return fakeObject;
    }

    private void init(final boolean computeSpacing) {
        this.onPathChanged();
        this.totalLength = this.computeTotalLength();

        if (computeSpacing) {
            this.spacingToFirstItem = this.totalLength;
        }

        this.worldBounds = computeBounds();

        for (final Entity entity : this.entityPath) {
            entity.components.Belt.assignedPath = this;
        }
    }

    private Rectangle computeBounds() {
        Rectangle bounds = this.entityPath.get(0).components.StaticMapEntity.getTileSpaceBounds();
        for (int i = 1; i < this.entityPath.size(); i++) {
            final StaticMapEntityComponent staticComp = this.entityPath.get(i).components.StaticMapEntity;
            final Rectangle otherBounds = staticComp.getTileSpaceBounds();
            bounds = bounds.union(otherBounds);
        }
        return bounds;
    }

    public void onPathChanged() {
        this.acceptorTarget = this.computeAcceptingEntityAndSlot();
        this.numCompressedItemsAfterFirstItem = 0;
    }

    private AcceptingEntityAndSlot computeAcceptingEntityAndSlot() {
        final Entity lastEntity = this.entityPath.get(this.entityPath.size() - 1);
        final StaticMapEntityComponent lastStatic = lastEntity.components.StaticMapEntity;
        final BeltComponent lastBeltComp = lastEntity.components.Belt;

        final Vector ejectSlotWsTile = lastStatic.localTileToWorld(new Vector(0, 0));
        final Direction ejectSlotWsDirection = lastStatic.localDirectionToWorld(lastBeltComp.direction);
        final Vector ejectSlotWsDirectionVector = Vector.directionToVector(ejectSlotWsDirection);
        final Vector ejectSlotWsTargetWsTile = ejectSlotWsTile.add(ejectSlotWsDirectionVector);

        final Entity targetEntity = root.map.getLayerContentXY(ejectSlotWsTargetWsTile.x, ejectSlotWsTargetWsTile.y, Layer.Regular);

        if (targetEntity != null) {
            final StaticMapEntityComponent targetStaticComp = targetEntity.components.StaticMapEntity;
            final BeltComponent targetBeltComp = targetEntity.components.Belt;

            if (targetBeltComp != null) {
                final Direction beltAcceptingDirection = targetStaticComp.localDirectionToWorld(Direction.Top);
                if (ejectSlotWsDirection == beltAcceptingDirection) {
                    return new AcceptingEntityAndSlot(targetEntity, null, 0);
                }
            }
            final ItemAcceptorComponent targetAcceptorComp = targetEntity.components.ItemAcceptor;
            if (targetAcceptorComp == null) {
                return null;
            }

            final Direction ejectingDirection = targetStaticComp.worldDirectionToLocal(ejectSlotWsDirection);
            final ItemAcceptorComponent.ItemAcceptorLocatedSlot matchingSlot = targetAcceptorComp.findMatchingSlot(targetStaticComp.worldToLocalTile(ejectSlotWsTile), ejectingDirection);
            if (matchingSlot == null) {
                return null;
            }
            return new AcceptingEntityAndSlot(targetEntity, Vector.invertDirection(ejectingDirection), matchingSlot.index);
        }
        return null;
    }

    private String deserialize(final Object data) {
        return deserializeSchema(this, data, null);
    }

    private String deserializeSchema(final BeltPath beltPath, final Object data, final String baseClassErrorResult) {
        if (baseClassErrorResult != null) {
            return baseClassErrorResult;
        }

        if (data == null) {
            return "Got null data";
        }
        return null;
    }

    public Object serialize() {
        return serializeSchema(this, this.getCachedSchema());
    }

    private HashMap<String, HashMap<Object, Object>> serializeSchema(final BeltPath obj, final HashMap<String, BaseDataType> schema) {
        final HashMap<String, HashMap<Object, Object>> mergeWith = new HashMap<>();
        for (final Map.Entry<String, BaseDataType> entry : schema.entrySet()) {
//            if (!obj.hasOwnProperty(entry.getKey()))
            mergeWith.put(entry.getKey(), entry.getValue().serialize(/*obj.get(entry.getKey()*/""));
        }
        return mergeWith;
    }

    @Override
    protected String getId() {
        return null;
    }

    public boolean isStartEntity(final Entity entity) {
        return this.entityPath.get(0) == entity;
    }

    public void deleteEntityOnStart(final Entity entity) {
        assert entity == this.entityPath.get(0);

        final BeltComponent beltComp = entity.components.Belt;
        final double beltLength = beltComp.getEffectiveLengthTiles();
        this.totalLength -= beltLength;
        this.entityPath.remove(0);
        this.onPathChanged();

        beltComp.assignedPath = null;

        if (this.items.size() == 0) {
            this.spacingToFirstItem = this.totalLength;
        } else {
            if (this.spacingToFirstItem >= beltLength) {
                this.spacingToFirstItem -= beltLength;
            } else {
                double itemOffset = this.spacingToFirstItem;
                for (int i = 0; i < this.items.size(); i++) {
//                    BaseItem item = this.items.get(i);
                    if (itemOffset <= beltLength) {
                        this.items.remove(i);
                        i -= 1;
                        itemOffset += i;//idk what item was supposed to do here
                    } else {
                        break;
                    }
                }

                if (this.items.size() > 0) {
                    this.spacingToFirstItem = itemOffset - beltLength;
                    assert this.spacingToFirstItem >= 0.0;
                } else {
                    this.spacingToFirstItem = this.totalLength;
                }
            }
        }
        this.worldBounds = this.computeBounds();
    }

    private double computeTotalLength() {
        double length = 0;
        for (final Entity entity : this.entityPath) {
            length += entity.components.Belt.getEffectiveLengthTiles();
        }
        return length;
    }

    public boolean isEndEntity(final Entity entity) {
        return this.entityPath.get(this.entityPath.size() - 1) == entity;
    }

    public void deleteEntityOnEnd(final Entity entity) {
        assert this.entityPath.get(this.entityPath.size() - 1) == entity;

        final BeltComponent beltComp = entity.components.Belt;
        final double beltLength = beltComp.getEffectiveLengthTiles();

        this.totalLength -= beltLength;
        this.entityPath.remove(entityPath.size() - 1);
        this.onPathChanged();

        if (this.items.size() == 0) {
            this.spacingToFirstItem = this.totalLength;
        } else {
            double itemOffset = this.spacingToFirstItem;
            double lastItemOffset = itemOffset;

            for (int i = 0; i < this.items.size(); i++) {
//                BaseItem item = this.items.get(i);
                if (itemOffset >= this.totalLength) {
                    this.items.remove(i);
                    i -= 1;
                    continue;
                }

                lastItemOffset = itemOffset;
                itemOffset += i; // idk what is supposed to be here either
            }

            if (this.items.size() > 0) {
                final double lastDistance = this.totalLength - lastItemOffset;
                assert lastDistance >= 0.0;
//                this.items.get(this.items.size() - 1) = lastDistance;
            } else {
                this.spacingToFirstItem = this.totalLength;
            }
        }
        this.worldBounds = this.computeBounds();
    }

    public BeltPath deleteEntityOnPathsSplitIntoTwo(final Entity entity) {
        final BeltComponent beltComp = entity.components.Belt;
        beltComp.assignedPath = null;
        final double entityLength = beltComp.getEffectiveLengthTiles();

        int firstPathEntityCount = 0;
        double firstPathLength = 0;
//        Entity firstPathEndEntity = null;

        for (final Entity otherEntity : this.entityPath) {
            if (otherEntity == entity) {
                break;
            }
            ++firstPathEntityCount;
//            firstPathEndEntity = otherEntity;
            firstPathLength += otherEntity.components.Belt.getEffectiveLengthTiles();
        }

//        double secondPathLength = this.totalLength - firstPathLength - entityLength;
        final double secondPathStart = firstPathLength + entityLength;
        final java.util.List<Entity> secondEntities = this.entityPath.subList(firstPathEntityCount + 1, entityPath.size());
        this.entityPath.remove(this.entityPath.size() - 1);

        final BeltPath secondPath = new BeltPath(root, (LinkedList<Entity>) secondEntities);
        double itemPos = spacingToFirstItem;
        for (int i = 0; i < this.items.size(); i++) {
            final AbstractMap.SimpleEntry<Double, BaseItem> item = this.items.get(i);
            final double distanceToNext = item.getKey();
            if (itemPos >= firstPathLength) {
                items.remove(i);
                i -= 1;
                if (itemPos >= secondPathStart) {
                    secondPath.items.add(item);
                    if (secondPath.items.size() == 1) {
                        secondPath.spacingToFirstItem = itemPos - secondPathStart;
                    }
                } else {
                    System.out.println("Item removed forever!!!");
                }
            } else {
                final double clampedDistanceToNext = Math.min(itemPos + distanceToNext, firstPathLength) - itemPos;
//                if (clampedDistanceToNext < distanceToNext) {
//                    //???
//                }
            }
            itemPos += distanceToNext;
        }
        this.totalLength = firstPathLength;
        if (this.items.size() == 0) {
            this.spacingToFirstItem = this.totalLength;
        }

        this.onPathChanged();
        secondPath.onPathChanged();
        this.worldBounds = this.computeBounds();
        return secondPath;
    }

    public void onSurroundingsChanged() {
        this.onPathChanged();
    }

    public void extendOnEnd(final Entity entity) {
        final BeltComponent beltComponent = entity.components.Belt;

        this.entityPath.add(entity);
        this.onPathChanged();

        final double additionalLength = beltComponent.getEffectiveLengthTiles();
        this.totalLength += additionalLength;

        if (this.items.size() == 0) {
            this.spacingToFirstItem = this.totalLength;
        } /*else {
//            BaseItem lastItem = this.items.get(this.items.size() - 1);
//            lastItem[0] += additionalLength
        }*/

        beltComponent.assignedPath = this;

        this.worldBounds = this.computeBounds();
    }

    public void extendByPath(final BeltPath otherPath) {
        assert otherPath != this;

        final LinkedList<Entity> entities = otherPath.entityPath;

        final double oldLength = this.totalLength;

        for (final Entity entity : entities) {
            final BeltComponent beltComp = entity.components.Belt;

            this.entityPath.add(entity);
            beltComp.assignedPath = this;

            final double additionalLength = beltComp.getEffectiveLengthTiles();
            this.totalLength += additionalLength;
        }

        if (this.items.size() != 0) {
            final AbstractMap.SimpleEntry<Double, BaseItem> lastItem = this.items.get(this.items.size() - 1);
            this.items.set(this.items.size() - 1, new AbstractMap.SimpleEntry<>(lastItem.getKey() + otherPath.spacingToFirstItem, lastItem.getValue()));
        } else {
            this.spacingToFirstItem = oldLength + otherPath.spacingToFirstItem;
        }

        for (int i = 0; i < otherPath.items.size(); i++) {
            final AbstractMap.SimpleEntry<Double, BaseItem> item = items.get(i);
            this.items.add(item);
        }

        this.worldBounds = this.computeBounds();

        this.onPathChanged();
    }

    public void extendOnBeginning(final Entity entity) {
        final BeltComponent beltComp = entity.components.Belt;

        final double length = beltComp.getEffectiveLengthTiles();

        this.totalLength += length;

        this.spacingToFirstItem += length;

        beltComp.assignedPath = this;
        this.entityPath.addFirst(entity);
        this.onPathChanged();

        this.worldBounds = this.computeBounds();
    }

    public void draw(final DrawParameters parameters) throws IOException {
        if (!parameters.visibleRect.contains(this.worldBounds)) {
            return;
        }

        if (this.items.size() == 0) {
            return;
        }

        if (this.checkIsPotatoMode()) {
            final AbstractMap.SimpleEntry<Double, BaseItem> firstItem = this.items.get(0);
            if (this.entityPath.size() > 1 && firstItem != null) {
                final int medianBeltIndex = Math.max(0, Math.min(this.entityPath.size() - 1, this.entityPath.size() / 2 - 1));
                final Entity medianBelt = this.entityPath.get(medianBeltIndex);
                final BeltComponent beltComp = medianBelt.components.Belt;
                final StaticMapEntityComponent staticComp = medianBelt.components.StaticMapEntity;
                final Vector centerPosLocal = beltComp.transformBeltToLocalSpace(this.entityPath.size() % 2 == 0 ? beltComp.getEffectiveLengthTiles() : 0.5);
                final Vector centerPos = staticComp.localTileToWorld(centerPosLocal).toWorldSpaceCenterOfTile();
//                firstItem.drawItemCenteredClipped(centerPos.x, centerPos.y, g2d);
            }
            return;
        }
        final double currentItemPos = this.spacingToFirstItem;
        int currentItemIndex = 0;
        double trackPos = 0.0;

        for (final Entity entity : this.entityPath) {
            final BeltComponent beltComp = entity.components.Belt;
            final double beltLength = beltComp.getEffectiveLengthTiles();
            while (trackPos + beltLength >= currentItemPos - 1e-5) { // this warning is here because of some problem I created
                final StaticMapEntityComponent staticComp = entity.components.StaticMapEntity;
                final Vector localPos = beltComp.transformBeltToLocalSpace(currentItemPos - trackPos);
                final Vector worldPos = staticComp.localTileToWorld(localPos).toWorldSpaceCenterOfTile();
                items.get(currentItemIndex).getValue().drawItemCenteredClipped(worldPos.x, worldPos.y, parameters);
//                currentItemPos += distance;
                currentItemIndex++;
                if (currentItemIndex >= this.items.size()) {
                    return;
                }
            }
            trackPos += beltLength;
        }
    }

    private boolean checkIsPotatoMode() {
//        if (this.) TODO: add setting
        return false;
    }

    public void update() {
        final double beltSpeed = this.root.hubGoals.getBeltBaseSpeed() * this.root.dynamicTickrate.deltaSeconds * GlobalConfig.itemSpacingOnBelts;
        final boolean isFirstItemProcessed = true;
        double remainingVelocity = beltSpeed;
        int lastItemProcessed;

        for (lastItemProcessed = this.items.size() - 1; lastItemProcessed >= 0; --lastItemProcessed) {
            AbstractMap.SimpleEntry<Double, BaseItem> nextDistanceAndItem = this.items.get(lastItemProcessed);

            final double minimumSpacing = lastItemProcessed == this.items.size() - 1 ? 0 : GlobalConfig.itemSpacingOnBelts;

            final double clampedProgress = Math.max(0, Math.min(remainingVelocity, nextDistanceAndItem.getKey()));

            remainingVelocity -= clampedProgress;

            nextDistanceAndItem = new AbstractMap.SimpleEntry<>(nextDistanceAndItem.getKey() - clampedProgress, nextDistanceAndItem.getValue());

            this.spacingToFirstItem += clampedProgress;

            if (isFirstItemProcessed && nextDistanceAndItem.getKey() < 1e-7) {
                final double excessVelocity = beltSpeed - clampedProgress;

                if (this.tryHandOverItem(nextDistanceAndItem.getValue(), excessVelocity)) {

                }
            }
        }
    }

    private boolean tryHandOverItem(final BaseItem item, final double remainingProgress) {
        assert !(this.acceptorTarget == null);
        final ItemAcceptorComponent targetAcceptorComp = this.acceptorTarget.entity.components.ItemAcceptor;
        if (targetAcceptorComp != null && !targetAcceptorComp.canAcceptItem(this.acceptorTarget.slot, item)) {
            return false;
        }

        if (this.root.systemMgr.itemEjector.tryPassOverItem(item, this.acceptorTarget.entity, this.acceptorTarget.slot)) {

        }
        return false;
    }

    public boolean tryAcceptItem(final BaseItem item) {
        if (this.spacingToFirstItem >= GlobalConfig.itemSpacingOnBelts) {
            final double beltProgressPerTick = this.root.hubGoals.getBeltBaseSpeed() * this.root.dynamicTickrate.deltaSeconds * GlobalConfig.itemSpacingOnBelts;
            final double maxProgress = Math.max(0, this.spacingToFirstItem - GlobalConfig.itemSpacingOnBelts);
            final double initialProgress = Math.min(maxProgress, beltProgressPerTick);
            this.items.add(0, new AbstractMap.SimpleEntry<>(this.spacingToFirstItem - initialProgress, item));
            this.spacingToFirstItem = initialProgress;
            return true;
        }
        return false;
    }
}

class AcceptingEntityAndSlot {
    public final Entity entity;
    private final Direction direction;
    public final int slot;

    public AcceptingEntityAndSlot(final Entity entity, final Direction direction, final int slot) {
        this.entity = entity;
        this.direction = direction;
        this.slot = slot;
    }
}

