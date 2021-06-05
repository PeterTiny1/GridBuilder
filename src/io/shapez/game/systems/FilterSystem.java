package io.shapez.game.systems;

import io.shapez.game.*;
import io.shapez.game.components.FilterComponent;
import io.shapez.game.items.BooleanItem;

import java.util.ArrayList;

public class FilterSystem extends GameSystemWithFilter {
    private static final BaseItem BOOL_TRUE_SINGLETON = new BooleanItem(true);
    private static final int MAX_ITEMS_IN_QUEUE = 2;

    public FilterSystem(final GameRoot root, final Component[] requiredComponents) {
        super(root, new Component[]{new FilterComponent()});
    }

    public boolean tryAcceptItem(final Entity entity, final int slot, final BaseItem item) {
        final WireSystem.WireNetwork network = entity.components.WiredPins.slots.get(0).linkedNetwork;
        if (network == null || !network.hasValue()) {
            return false;
        }

        final BaseItem value = network.currentValue;
        final FilterComponent filterComp = entity.components.Filter;
        assert filterComp != null;
        final ArrayList<FilterComponent.PendingFilterItem> listToCheck;
        if (value.equals(FilterSystem.BOOL_TRUE_SINGLETON) || value.equals(item)) {
            listToCheck = filterComp.pendingItemsToLeaveThrough;
        } else {
            listToCheck = filterComp.pendingItemsToReject;
        }

        if (listToCheck.size() >= FilterSystem.MAX_ITEMS_IN_QUEUE) {
            return false;
        }

        listToCheck.add(new FilterComponent.PendingFilterItem(item, 0.0));
        return true;
    }
}
