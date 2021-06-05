package io.shapez.game.systems;

import io.shapez.game.BaseItem;

public class WireSystem {
    public static class WireNetwork {

        public final BaseItem currentValue = null;
        private final boolean valueConflict = false;

        public boolean hasValue() {
            return this.currentValue != null && !this.valueConflict;
        }
    }
}
