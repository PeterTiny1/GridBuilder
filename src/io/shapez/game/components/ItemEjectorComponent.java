package io.shapez.game.components;

import io.shapez.core.Direction;
import io.shapez.core.Vector;
import io.shapez.game.BaseItem;
import io.shapez.game.BeltPath;
import io.shapez.game.Component;
import io.shapez.game.Entity;

import java.util.ArrayList;
import java.util.HashMap;

public class ItemEjectorComponent extends Component {
    private boolean renderFloatingItems;
    public ArrayList<ItemEjectorSlot> slots;

    public ItemEjectorComponent() {

    }

    @Override
    public String getId() {
        return "ItemEjector";
    }

    public ItemEjectorComponent(HashMap<Vector, Direction> slots, boolean renderFloatingItems) {
        super();

        this.setSlots(slots);

        this.renderFloatingItems = renderFloatingItems;
    }

    private void setSlots(HashMap<Vector, Direction> slots) {
        this.slots = new ArrayList<>();
        for (var slot : slots.entrySet()) {
            this.slots.add(new ItemEjectorSlot(slot.getKey(), slot.getValue(), null, 0, null, null));
        }
    }

    public static class ItemEjectorSlot {
        public int progress;
        public ItemAcceptorComponent.ItemAcceptorLocatedSlot cachedDestSlot;
        public Entity cachedTargetEntity;
        public BeltPath cachedBeltPath;
        public Vector pos;
        public Direction direction;
        public BaseItem item;

        public ItemEjectorSlot(Vector pos, Direction direction, BaseItem item, int progress, ItemAcceptorComponent.ItemAcceptorLocatedSlot cachedDestSlot, Entity cachedTargetEntity) {
            this.pos = pos;
            this.direction = direction;
            this.item = item;
            this.progress = progress;
            this.cachedDestSlot = cachedDestSlot;
            this.cachedTargetEntity = cachedTargetEntity;
        }
    }
}
