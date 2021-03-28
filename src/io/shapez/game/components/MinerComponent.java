package io.shapez.game.components;

import io.shapez.game.BaseItem;
import io.shapez.game.Component;
import io.shapez.game.Entity;

import java.util.ArrayList;

public class MinerComponent extends Component {
    int lastMiningTime = 0;
    boolean chainable;
    ArrayList<BaseItem> itemChainBuffer = new ArrayList<>();
    BaseItem cachedMinedItem = null;
    Entity cachedChainedMiner = null;

    @Override
    public String getId() {
        return "Miner";
    }

    public MinerComponent() {
        super();

    }
}
