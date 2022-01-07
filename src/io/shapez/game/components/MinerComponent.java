package io.shapez.game.components;

import io.shapez.game.BaseItem;
import io.shapez.game.Component;
import io.shapez.game.Entity;

import java.util.ArrayList;

public class MinerComponent extends Component {
    public final BaseItem cachedMinedItem = null;
    int lastMiningTime = 0;
    boolean chainable;
    ArrayList<BaseItem> itemChainBuffer = new ArrayList<>();
    Entity cachedChainedMiner = null;

    public MinerComponent() {
        super();

    }

    @Override
    public String getId() {
        return "Miner";
    }
}
