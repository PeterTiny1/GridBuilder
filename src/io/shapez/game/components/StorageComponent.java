package io.shapez.game.components;

import io.shapez.core.ItemType;
import io.shapez.game.BaseItem;
import io.shapez.game.Component;
import io.shapez.game.items.ColorItem;
import io.shapez.game.items.ShapeItem;


public class StorageComponent extends Component {
    private final int maximumStorage;
    BaseItem storedItem = null;
    int storedCount = 0;
    int overlayOpacity = 0;

    public String getId() {
        return "Storage";
    }

    StorageComponent(final int maximumStorage) {
        super();
        this.maximumStorage = maximumStorage;
    }

    public boolean canAcceptItem(final BaseItem item) {
        if (this.storedCount >= maximumStorage) {
            return false;
        }
        if (this.storedItem == null || this.storedCount == 0) {
            return true;
        }

        final ItemType itemType = item.getItemType();

        if (itemType != this.storedItem.getItemType()) {
            return false;
        }
        if (itemType == ItemType.color) {
            return ((ColorItem) this.storedItem).color == ((ColorItem) item).color;
        }

        if (itemType == ItemType.shape) {
            return ((ShapeItem) this.storedItem).definition.getHash().equals(((ShapeItem) item).definition.getHash());
        }

        return false;
    }

    public void takeItem(final BaseItem item) {
        this.storedItem = item;
        this.storedCount++;
    }
}