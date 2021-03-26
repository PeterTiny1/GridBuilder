package io.shapez.game.systems;

import io.shapez.game.Component;
import io.shapez.game.GameSystemWithFilter;
import io.shapez.game.components.BeltComponent;

public class BeltSystem extends GameSystemWithFilter {

    public BeltSystem() {
        super(new Component[]{new BeltComponent(null)});
    }
}
