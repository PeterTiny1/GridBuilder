package io.shapez.game.components;

import io.shapez.game.BaseItem;
import io.shapez.game.Component;

import java.util.ArrayList;

public class FilterComponent extends Component {
    public final ArrayList<PendingFilterItem> pendingItemsToLeaveThrough = new ArrayList<>();
    public final ArrayList<PendingFilterItem> pendingItemsToReject = new ArrayList<>();

    public FilterComponent() {
        super();
    }

    public String getId() {
        return "Filter";
    }

    public static class PendingFilterItem {
        final BaseItem item;
        final double progress;

        public PendingFilterItem(final BaseItem item, final double progress) {
            this.item = item;
            this.progress = progress;
        }
    }
}


