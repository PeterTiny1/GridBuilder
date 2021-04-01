package io.shapez.game.systems;

import io.shapez.game.Component;
import io.shapez.game.GameSystemWithFilter;
import io.shapez.game.components.MinerComponent;

public class MinerSystem extends GameSystemWithFilter {
    boolean needsRecompute = true;

    public MinerSystem() {
        super(new Component[]{new MinerComponent()});
    }
}
