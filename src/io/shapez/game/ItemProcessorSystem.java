package io.shapez.game;

import io.shapez.game.components.ItemProcessorComponent;
import io.shapez.game.components.WiredPinsComponent;
import io.shapez.game.items.BooleanItem;
import io.shapez.game.systems.WireSystem;

public class ItemProcessorSystem extends GameSystemWithFilter {
    public ItemProcessorSystem(final GameRoot root) {
        super(root, new Component[]{new ItemProcessorComponent()});
    }

    public boolean checkRequirements(final Entity entity, final BaseItem item, final int slotIndex) {
        final ItemProcessorComponent itemProcessorComp = entity.components.ItemProcessor;
        final WiredPinsComponent pinsComp = entity.components.WiredPins;

        if (itemProcessorComp.processingRequirement == ItemProcessorComponent.ItemProcessorRequirements.painterQuad) {
            if (slotIndex == 0) {
                return true;
            }

            final WireSystem.WireNetwork network = pinsComp.slots.get(slotIndex - 1).linkedNetwork;
            return network != null && network.hasValue() && BooleanItem.isTruthyItem(network.currentValue);
        }
        return true;
    }
}
