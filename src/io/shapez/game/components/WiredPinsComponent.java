package io.shapez.game.components;

import io.shapez.game.Component;
import io.shapez.game.systems.WireSystem;

import java.util.ArrayList;

public class WiredPinsComponent extends Component {
    public ArrayList<WirePinSlot> slots = new ArrayList<>();

    @Override
    public String getId() {
        return "WiredPins";
    }

    public class WirePinSlot {
        public WireSystem.WireNetwork linkedNetwork;
    }
}
