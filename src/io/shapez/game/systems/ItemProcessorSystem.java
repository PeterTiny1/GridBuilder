package io.shapez.game.systems;

import io.shapez.game.*;
import io.shapez.game.components.ItemProcessorComponent;
import io.shapez.game.items.ShapeItem;

import java.util.ArrayList;
import java.util.HashMap;

public class ItemProcessorSystem extends GameSystemWithFilter {
    public ItemProcessorSystem(GameRoot root) {
        super(root, new Component[] {new ItemProcessorComponent()});
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
