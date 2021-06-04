package io.shapez.game.systems;

import io.shapez.game.BaseItem;

public class WireSystem {
    public class WireNetwork {

        public BaseItem currentValue = null;
        private final boolean valueConflict = false;

        public boolean hasValue() {
            return this.currentValue != null && !this.valueConflict;
        }
    }
}
