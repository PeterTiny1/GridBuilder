package io.shapez.game;

import io.shapez.core.Direction;
import io.shapez.core.Layer;
import io.shapez.core.Vector;
import io.shapez.game.components.BeltComponent;
import io.shapez.game.components.ItemAcceptorComponent;
import io.shapez.game.components.StaticMapEntityComponent;
import io.shapez.game.savegame.BaseDataType;
import io.shapez.game.savegame.BasicSerializableObject;

import java.util.HashMap;
import java.util.Map;

public class BeltPath extends BasicSerializableObject {
    static String getId = "BeltPath";
    private final HashMap<Integer, BaseItem> items = new HashMap<>();
    private Entity[] entityPath = {};
    AcceptingEntityAndSlot acceptorTarget;
    private int numCompressedItemsAfterFirstItem;

    BeltPath(Entity[] entityPath) {
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
    }

    private void onPathChanged() {
        this.acceptorTarget = this.computeAcceptingEntityAndSlot();
        this.numCompressedItemsAfterFirstItem = 0;
    }

    private AcceptingEntityAndSlot computeAcceptingEntityAndSlot() {
        Entity lastEntity = this.entityPath[this.entityPath.length - 1];
        StaticMapEntityComponent lastStatic = lastEntity.components.StaticMapEntity;
        BeltComponent lastBeltComp = lastEntity.components.Belt;

        Vector ejectSlotWsTile = lastStatic.localTileToWorld(new Vector(0, 0));
        Direction ejectSlotWsDirection = lastStatic.localDirectionToWorld(lastBeltComp.direction);
        Vector ejectSlotWsDirectionVector = Vector.directionToVector(ejectSlotWsDirection);
        Vector ejectSlotWsTargetWsTile = ejectSlotWsTile.add(ejectSlotWsDirectionVector);

        Entity targetEntity = GlobalConfig.map.getLayerContentXY(ejectSlotWsTargetWsTile.x, ejectSlotWsTargetWsTile.y, Layer.Regular);

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

