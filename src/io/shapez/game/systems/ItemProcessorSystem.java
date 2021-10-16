package io.shapez.game.systems;

import io.shapez.game.*;
import io.shapez.game.components.ItemProcessorComponent;
import io.shapez.game.components.WiredPinsComponent;
import io.shapez.game.items.BooleanItem;
import io.shapez.game.items.ShapeItem;

import java.util.ArrayList;
import java.util.HashMap;

public class ItemProcessorSystem extends GameSystemWithFilter {
    public ItemProcessorSystem(GameRoot root) {
        super(root, new Component[]{new ItemProcessorComponent()});
    }

    void process_HUB(ProcessorImplementationPayload payload) {
        HubComponent hubComponent = payload.entity.components.Hub;
        assert hubComponent != null;

        for (int i = 0; i < payload.inputCount; i++) {
            ShapeItem item = (ShapeItem) payload.items.get(i);
            if (item == null) {
                continue;
            }
            this.root.hubGoals.handleDefinitionDelivered(item.definition);
        }
    }

    public boolean failsRequirements(final Entity entity, final BaseItem item, final int slotIndex) {
        final ItemProcessorComponent itemProcessorComp = entity.components.ItemProcessor;
        final WiredPinsComponent pinsComp = entity.components.WiredPins;

        if (itemProcessorComp.processingRequirement == ItemProcessorComponent.ItemProcessorRequirements.painterQuad) {
            if (slotIndex == 0) {
                return false;
            }

            final WireSystem.WireNetwork network = pinsComp.slots.get(slotIndex - 1).linkedNetwork;
            return network == null || !network.hasValue() || !BooleanItem.isTruthyItem(network.currentValue);
        }
        return false;
    }

    private static class ProcessorImplementationPayload {
        Entity entity;
        HashMap<Integer, BaseItem> items;
        int inputCount;
        ArrayList<ProducedItem> outItems;
    }

    private static class ProducedItem {
        BaseItem item;
        int preferredSlot;
        int requiredSlot;
        boolean doNotTrack;
    }
}
