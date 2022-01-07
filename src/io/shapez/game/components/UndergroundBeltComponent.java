package io.shapez.game.components;

import io.shapez.game.BaseItem;
import io.shapez.game.Component;
import io.shapez.game.Entity;

import java.util.ArrayList;
import java.util.HashMap;

public class UndergroundBeltComponent extends Component {
    final HashMap<BaseItem, Integer> pendingItems = new HashMap<>();
    private final int tier;
    private final UndergroundBeltMode mode;
    ArrayList<ItemAcceptorComponent.ItemConsumptionAnimation> consumptionAnimations = new ArrayList<>();
    LinkedUndergroundBelt cachedLinkedEntity = null;

    UndergroundBeltComponent(final UndergroundBeltMode mode, final int tier) {
        super();
        this.tier = tier;
        this.mode = mode;

    }

    public String getId() {
        return "UndergroundBelt";
    }

    public boolean tryAcceptExternalItem(final BaseItem item, final int beltSpeed) {
        if (this.mode != UndergroundBeltMode.sender) {
            return false;
        }

        if (this.pendingItems.size() > 0) {
            return false;
        }

        this.pendingItems.put(item, 0);
        return true;
    }

    private enum UndergroundBeltMode {
        sender,
        receiver
    }
}

class LinkedUndergroundBelt {
    Entity entity;
    int distance;
}

