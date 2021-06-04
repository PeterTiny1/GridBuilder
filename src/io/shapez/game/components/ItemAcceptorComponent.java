package io.shapez.game.components;

import io.shapez.core.Direction;
import io.shapez.core.Vector;
import io.shapez.game.BaseItem;
import io.shapez.game.Component;

import java.util.ArrayList;

public class ItemAcceptorComponent extends Component {
    public ArrayList<ItemAcceptorSlot> slots;
    public ArrayList<ItemConsumptionAnimation> itemConsumptionAnimations = new ArrayList<>();

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

    public static class ItemAcceptorSlot {

        public Vector pos;
        public Direction[] directions;
    }

    public static class ItemAcceptorLocatedSlot {
        private final ItemAcceptorSlot slot;
        public final int index;
        private final Direction acceptedDirection;

        public ItemAcceptorLocatedSlot(final ItemAcceptorSlot slot, final int index, final Direction acceptedDirection) {
            this.slot = slot;
            this.index = index;
            this.acceptedDirection = acceptedDirection;
        }
    }

    public class ItemConsumptionAnimation {
        public int slotIndex;
        public Direction direction;
        public double animProgress;
        public BaseItem item;
    }
}

