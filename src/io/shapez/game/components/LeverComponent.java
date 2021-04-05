package io.shapez.game.components;

import io.shapez.game.Component;

public class LeverComponent extends Component {
    public boolean toggled;

    @Override
    public String getId() {
        return "Lever";
    }
}
