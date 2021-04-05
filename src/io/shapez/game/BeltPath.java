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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class BeltPath extends BasicSerializableObject {
    static String getId = "BeltPath";
    private final ArrayList<BaseItem> items = new ArrayList<>();
    private GameRoot root;
    public LinkedList<Entity> entityPath = new LinkedList<>();
    AcceptingEntityAndSlot acceptorTarget;
    private int numCompressedItemsAfterFirstItem;
    private double totalLength;
    private double spacingToFirstItem;
    private Rectangle worldBounds;

    public BeltPath(GameRoot root, LinkedList<Entity> entityPath) {
        this.root = root;
        this.entityPath = entityPath;
        this.init(true);
    }

    public BeltPath() {

    }

    static BeltPath fromSerialized(Object data) {
        BeltPath fakeObject = new BeltPath();
// String errorCodeDesiralize = fakeObject.deserialize(data);
// if (errorCodeDesiralize != null) {
//     return errorCodeDesiralize;
// }
        fakeObject.init(false);
        return fakeObject;
    }

    private void init(boolean computeSpacing) {
        this.onPathChanged();
        this.totalLength = this.computeTotalLength();

        if (computeSpacing) {
            this.spacingToFirstItem = this.totalLength;
        }

        this.worldBounds = computeBounds();

        for (Entity entity : this.entityPath) {
            entity.components.Belt.assignedPath = this;
        }
    }

    private Rectangle computeBounds() {
        Rectangle bounds = this.entityPath.get(0).components.StaticMapEntity.getTileSpaceBounds();
        for (int i = 1; i < this.entityPath.size(); i++) {
            StaticMapEntityComponent staticComp = this.entityPath.get(i).components.StaticMapEntity;
            Rectangle otherBounds = staticComp.getTileSpaceBounds();
            bounds = bounds.union(otherBounds);
        }
        return bounds;
    }

    public void onPathChanged() {
        this.acceptorTarget = this.computeAcceptingEntityAndSlot();
        this.numCompressedItemsAfterFirstItem = 0;
    }

    private AcceptingEntityAndSlot computeAcceptingEntityAndSlot() {
        Entity lastEntity = this.entityPath.get(this.entityPath.size() - 1);
        StaticMapEntityComponent lastStatic = lastEntity.components.StaticMapEntity;
        BeltComponent lastBeltComp = lastEntity.components.Belt;

        Vector ejectSlotWsTile = lastStatic.localTileToWorld(new Vector(0, 0));
        Direction ejectSlotWsDirection = lastStatic.localDirectionToWorld(lastBeltComp.direction);
        Vector ejectSlotWsDirectionVector = Vector.directionToVector(ejectSlotWsDirection);
        Vector ejectSlotWsTargetWsTile = ejectSlotWsTile.add(ejectSlotWsDirectionVector);

        Entity targetEntity = root.map.getLayerContentXY(ejectSlotWsTargetWsTile.x, ejectSlotWsTargetWsTile.y, Layer.Regular);

        if (targetEntity != null) {
            StaticMapEntityComponent targetStaticComp = targetEntity.components.StaticMapEntity;
            BeltComponent targetBeltComp = targetEntity.components.Belt;

            if (targetBeltComp != null) {
                Direction beltAcceptingDirection = targetStaticComp.localDirectionToWorld(Direction.Top);
                if (ejectSlotWsDirection == beltAcceptingDirection) {
                    return new AcceptingEntityAndSlot(targetEntity, null, 0);
                }
            }
            ItemAcceptorComponent targetAcceptorComp = targetEntity.components.ItemAcceptor;
            if (targetAcceptorComp == null) {
                return null;
            }

            Direction ejectingDirection = targetStaticComp.worldDirectionToLocal(ejectSlotWsDirection);
            ItemAcceptorComponent.ItemAcceptorLocatedSlot matchingSlot = targetAcceptorComp.findMatchingSlot(targetStaticComp.worldToLocalTile(ejectSlotWsTile), ejectingDirection);
            if (matchingSlot == null) {
                return null;
            }
            return new AcceptingEntityAndSlot(targetEntity, Vector.invertDirection(ejectingDirection), matchingSlot.index);
        }
        return null;
    }

    private String deserialize(Object data) {
        return deserializeSchema(this, data, null);
    }

    private String deserializeSchema(BeltPath beltPath, Object data, String baseClassErrorResult) {
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

    private HashMap<String, HashMap<Object, Object>> serializeSchema(BeltPath obj, HashMap<String, BaseDataType> schema) {
        HashMap<String, HashMap<Object, Object>> mergeWith = new HashMap<>();
        for (Map.Entry<String, BaseDataType> entry : schema.entrySet()) {
//            if (!obj.hasOwnProperty(entry.getKey()))
            mergeWith.put(entry.getKey(), entry.getValue().serialize(/*obj.get(entry.getKey()*/""));
        }
        return mergeWith;
    }

    @Override
    protected String getId() {
        return null;
    }

    public boolean isStartEntity(Entity entity) {
        return this.entityPath.get(0) == entity;
    }

    public void deleteEntityOnStart(Entity entity) {
        assert entity == this.entityPath.get(0);

        BeltComponent beltComp = entity.components.Belt;
        double beltLength = beltComp.getEffectiveLengthTiles();
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
        for (Entity entity : this.entityPath) {
            length += entity.components.Belt.getEffectiveLengthTiles();
        }
        return length;
    }

    public boolean isEndEntity(Entity entity) {
        return this.entityPath.get(this.entityPath.size() - 1) == entity;
    }

    public void deleteEntityOnEnd(Entity entity) {
        assert this.entityPath.get(this.entityPath.size() - 1) == entity;

        BeltComponent beltComp = entity.components.Belt;
        double beltLength = beltComp.getEffectiveLengthTiles();

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
                double lastDistance = this.totalLength - lastItemOffset;
                assert lastDistance >= 0.0;
//                this.items.get(this.items.size() - 1) = lastDistance;
            } else {
                this.spacingToFirstItem = this.totalLength;
            }
        }
        this.worldBounds = this.computeBounds();
    }

    public BeltPath deleteEntityOnPathsSplitIntoTwo(Entity entity) {
        BeltComponent beltComp = entity.components.Belt;
        beltComp.assignedPath = null;
        double entityLength = beltComp.getEffectiveLengthTiles();

        int firstPathEntityCount = 0;
        double firstPathLength = 0;
//        Entity firstPathEndEntity = null;

        for (Entity otherEntity : this.entityPath) {
            if (otherEntity == entity) {
                break;
            }
            ++firstPathEntityCount;
//            firstPathEndEntity = otherEntity;
            firstPathLength += otherEntity.components.Belt.getEffectiveLengthTiles();
        }

//        double secondPathLength = this.totalLength - firstPathLength - entityLength;
        double secondPathStart = firstPathLength + entityLength;
        java.util.List<Entity> secondEntities = this.entityPath.subList(firstPathEntityCount + 1, entityPath.size());
        this.entityPath.remove(this.entityPath.size() - 1);

        BeltPath secondPath = new BeltPath(root, (LinkedList<Entity>) secondEntities);
        double itemPos = spacingToFirstItem;
        for (int i = 0; i < this.items.size(); i++) {
            BaseItem item = this.items.get(i);
            int distanceToNext = i;
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
                double clampedDistanceToNext = Math.min(itemPos + distanceToNext, firstPathLength) - itemPos;
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

    public void extendOnEnd(Entity entity) {
        BeltComponent beltComponent = entity.components.Belt;

        this.entityPath.add(entity);
        this.onPathChanged();

        double additionalLength = beltComponent.getEffectiveLengthTiles();
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

    public void extendByPath(BeltPath otherPath) {
        assert otherPath != this;

        LinkedList<Entity> entities = otherPath.entityPath;

        double oldLength = this.totalLength;

        for (Entity entity : entities) {
            BeltComponent beltComp = entity.components.Belt;

            this.entityPath.add(entity);
            beltComp.assignedPath = this;

            double additionalLength = beltComp.getEffectiveLengthTiles();
            this.totalLength += additionalLength;
        }

        if (this.items.size() != 0) {
            BaseItem lastItem = this.items.get(this.items.size() - 1);
//            lastItem[nextDistance] += otherPath.spacingToFirstItem;
        } else {
            this.spacingToFirstItem = oldLength + otherPath.spacingToFirstItem;
        }

        for (int i = 0; i < otherPath.items.size(); i++) {
            BaseItem item = items.get(i);
            this.items.add(item);
        }

        this.worldBounds = this.computeBounds();

        this.onPathChanged();
    }

    public void extendOnBeginning(Entity entity) {
        BeltComponent beltComp = entity.components.Belt;

        double length = beltComp.getEffectiveLengthTiles();

        this.totalLength += length;

        this.spacingToFirstItem += length;

        beltComp.assignedPath = this;
        this.entityPath.addFirst(entity);
        this.onPathChanged();

        this.worldBounds = this.computeBounds();
    }

    public void draw(DrawParameters parameters) {
        if (!parameters.visibleRect.contains(this.worldBounds)) {
            return;
        }

        if (this.items.size() == 0) {
            return;
        }

        if (this.checkIsPotatoMode()) {
            BaseItem firstItem = this.items.get(0);
            if (this.entityPath.size() > 1 && firstItem != null) {
                int medianBeltIndex = Math.max(0, Math.min(this.entityPath.size() - 1, this.entityPath.size() / 2 - 1));
                Entity medianBelt = this.entityPath.get(medianBeltIndex);
                BeltComponent beltComp = medianBelt.components.Belt;
                StaticMapEntityComponent staticComp = medianBelt.components.StaticMapEntity;
                Vector centerPosLocal = beltComp.transformBeltToLocalSpace(this.entityPath.size() % 2 == 0 ? beltComp.getEffectiveLengthTiles() : 0.5);
                Vector centerPos = staticComp.localTileToWorld(centerPosLocal).toWorldSpaceCenterOfTile();
//                firstItem.drawItemCenteredClipped(centerPos.x, centerPos.y, g2d);
            }
            return;
        }
        double currentItemPos = this.spacingToFirstItem;
        int currentItemIndex = 0;
        double trackPos = 0.0;

        for (Entity entity : this.entityPath) {
            BeltComponent beltComp = entity.components.Belt;
            double beltLength = beltComp.getEffectiveLengthTiles();
            while (trackPos + beltLength >= currentItemPos - 1e-5) { // this warning is here because of some problem I created
                StaticMapEntityComponent staticComp = entity.components.StaticMapEntity;
                Vector localPos = beltComp.transformBeltToLocalSpace(currentItemPos - trackPos);
                Vector worldPos = staticComp.localTileToWorld(localPos).toWorldSpaceCenterOfTile();
                items.get(currentItemIndex).drawItemCenteredClipped(worldPos.x, worldPos.y, parameters);
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
}

class AcceptingEntityAndSlot {
    private final Entity entity;
    private final Direction direction;
    private final int slot;

    public AcceptingEntityAndSlot(Entity entity, Direction direction, int slot) {
        this.entity = entity;
        this.direction = direction;
        this.slot = slot;
    }
}

