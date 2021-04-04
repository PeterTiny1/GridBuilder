package io.shapez.game.systems;

import io.shapez.game.Component;
import io.shapez.game.GameRoot;
import io.shapez.game.GameSystemWithFilter;
import io.shapez.game.components.MinerComponent;

public class MinerSystem extends GameSystemWithFilter {
    boolean needsRecompute = true;

    public MinerSystem(GameRoot root) {
        super(root, new Component[]{new MinerComponent()});
    }
}
