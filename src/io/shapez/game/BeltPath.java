package io.shapez.game;

import io.shapez.core.Direction;
import io.shapez.core.Vector;
import io.shapez.game.components.BeltComponent;
import io.shapez.game.components.StaticMapEntityComponent;

import java.util.HashMap;

public class BeltPath {
    static String getId = "BeltPath";
    private final HashMap<Integer, BaseItem> items = new HashMap<Integer, BaseItem>();;
    private Entity[] entityPath = {};
    AcceptingEntityAndSlot acceptorTarget;

    BeltPath(Entity[] entityPath) {
        this.entityPath = entityPath;
        this.init(true);
    }

    public BeltPath() {

    }

    static BeltPath fromSerialized(Object data) {
        BeltPath fakeObject = new BeltPath();
//        String errorCodeDesiralize = fakeObject.deserialize(data);
//        if (errorCodeDesiralize != null) {
//            return errorCodeDesiralize;
//        }
        fakeObject.init(false);
        return fakeObject;
    }

    private void init(boolean computeSpacing) {
        this.onPathChanged();
    }

    private void onPathChanged() {
//        this.acceptorTarget = this.computeAcceptingEntityAndSlot();
    }

//    private AcceptingEntityAndSlot computeAcceptingEntityAndSlot() {
//        Entity lastEntity = this.entityPath[this.entityPath.length - 1];
//        StaticMapEntityComponent lastStatic = lastEntity.components.StaticMapEntity;
//        BeltComponent lastBeltComp = lastEntity.components.Belt;
//
//        Vector ejectSlotWsTile = lastStatic.localTileToWorld(new Vector(0, 0));
//        Direction ejectSlotWsDirection = lastStatic.localDirectionToWorld(lastBeltComp.direction);
//        Vector ejectSlotWsDirectionVector = directionToVector(ejectSlotWsDirection);
//        Vector ejectSlotWsTargetWsTile = ejectSlotWsTile.add(ejectSlotWsDirectionVector);
//
//        Entity targetEntity =
//    }

    static Vector directionToVector(Direction ejectSlotWsDirection) {
        return switch (ejectSlotWsDirection) {
            case Top -> new Vector(0, -1);
            case Right -> new Vector(1, 0);
            case Bottom -> new Vector(0, 1);
            case Left -> new Vector(-1, 0);
        };
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
}

class AcceptingEntityAndSlot {

}

class BaseItem {

}