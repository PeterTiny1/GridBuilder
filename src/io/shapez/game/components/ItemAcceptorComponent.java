package io.shapez.game.components;

import io.shapez.core.Direction;
import io.shapez.core.ItemType;
import io.shapez.core.Vector;
import io.shapez.game.BaseItem;
import io.shapez.game.Component;

import java.util.ArrayList;

public class ItemAcceptorComponent extends Component {
    public ArrayList<ItemAcceptorSlot> slots;
    public final ArrayList<ItemConsumptionAnimation> itemConsumptionAnimations = new ArrayList<>();

    public ItemAcceptorComponent() {

    }

    public ItemAcceptorLocatedSlot findMatchingSlot(final Vector targetLocalTile, final Direction fromLocalDirection) {
        final Direction desiredDirection = Vector.invertDirection(fromLocalDirection);
        for (int slotIndex = 0; slotIndex < this.slots.size(); ++slotIndex) {
            final ItemAcceptorSlot slot = this.slots.get(slotIndex);
            if (!slot.pos.equals(targetLocalTile)) {
                continue;
            }

            for (int i = 0; i < slot.directions.length; ++i) {
                if (desiredDirection == slot.directions[i]) {
                    return new ItemAcceptorLocatedSlot(slot, slotIndex, desiredDirection);
                }
            }
        }
        return null;
    }

    @Override
    public String getId() {
        return "ItemAcceptor";
    }

    public boolean canAcceptItem(final int slotIndex, final BaseItem item) {
        final ItemAcceptorSlot slot = this.slots.get(slotIndex);
        return slot.filter == null || slot.filter == item.getItemType();
    }

    public void onItemAccepted(int slotIndex, Direction direction, BaseItem item, Integer remainingProgress) {
        this.itemConsumptionAnimations.add(new ItemConsumptionAnimation(item, slotIndex, direction, Math.min(1, remainingProgress * 2)));
    }

    public static class ItemAcceptorSlot {

        public Vector pos;
        public Direction[] directions;
        public ItemType filter;
    }

    public static class ItemAcceptorLocatedSlot {
        public final ItemAcceptorSlot slot;
        public final int index;
        private final Direction acceptedDirection;

        public ItemAcceptorLocatedSlot(final ItemAcceptorSlot slot, final int index, final Direction acceptedDirection) {
            this.slot = slot;
            this.index = index;
            this.acceptedDirection = acceptedDirection;
        }
    }

    public static class ItemConsumptionAnimation {
        public final int slotIndex;
        public final Direction direction;
        public double animProgress;
        public final BaseItem item;

        public ItemConsumptionAnimation(BaseItem item, int slotIndex, Direction direction, int min) {
            this.slotIndex = slotIndex;
            this.item = item;
            this.direction = direction;
            this.animProgress = min;
        }
    }
}

