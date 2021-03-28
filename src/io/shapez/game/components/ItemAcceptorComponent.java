package io.shapez.game.components;

import io.shapez.core.Direction;
import io.shapez.core.Vector;
import io.shapez.game.Component;

import java.util.ArrayList;

public class ItemAcceptorComponent extends Component {
    private ArrayList<ItemAcceptorSlot> slots;

    public ItemAcceptorLocatedSlot findMatchingSlot(Vector targetLocalTile, Direction fromLocalDirection) {
        Direction desiredDirection = Vector.invertDirection(fromLocalDirection);
        for (int slotIndex = 0; slotIndex < this.slots.size(); ++slotIndex) {
            ItemAcceptorSlot slot = this.slots.get(slotIndex);
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

    private static class ItemAcceptorSlot {

        public Vector pos;
        public Direction[] directions;
    }

    public static class ItemAcceptorLocatedSlot {
        private final ItemAcceptorSlot slot;
        public final int index;
        private final Direction acceptedDirection;

        public ItemAcceptorLocatedSlot(ItemAcceptorSlot slot, int index, Direction acceptedDirection) {
            this.slot = slot;
            this.index = index;
            this.acceptedDirection = acceptedDirection;
        }
    }
}

