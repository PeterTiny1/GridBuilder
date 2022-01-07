package io.shapez.game.components;

import io.shapez.game.BaseItem;
import io.shapez.game.Component;

import java.util.AbstractMap;
import java.util.ArrayList;

public class ItemProcessorComponent extends Component {
    final ItemProcessorTypes type = ItemProcessorTypes.balancer;
    final ArrayList<AbstractMap.SimpleEntry<BaseItem, Integer>> inputSlots = new ArrayList<>();
    public ItemProcessorRequirements processingRequirement;

    @Override
    public String getId() {
        return "ItemProcessor";
    }

    public boolean tryTakeItem(final BaseItem item, final int sourceSlot) {
        if (this.type == ItemProcessorTypes.hub || this.type == ItemProcessorTypes.trash) {
            this.inputSlots.add(new AbstractMap.SimpleEntry<>(item, sourceSlot));
            return true;
        }

        for (final AbstractMap.SimpleEntry<BaseItem, Integer> slot : this.inputSlots) {
            if (slot.getValue() == sourceSlot) {
                return false;
            }
        }
        this.inputSlots.add(new AbstractMap.SimpleEntry<>(item, sourceSlot));
        return true;
    }

    public enum ItemProcessorRequirements {
        painterQuad
    }

    enum ItemProcessorTypes {
        balancer,
        cutter,
        cutterQuad,
        rotater,
        rotaterCCW,
        rotater180,
        stacker,
        trash,
        mixer,
        painter,
        painterDouble,
        painterQuad,
        hub,
        filter,
        reader,
    }
}
