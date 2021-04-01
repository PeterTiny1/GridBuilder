package io.shapez.game;

import io.shapez.game.systems.BeltSystem;
import io.shapez.game.systems.ItemEjectorSystem;
import io.shapez.game.systems.MapResourcesSystem;
import io.shapez.game.systems.MinerSystem;

import java.io.IOException;

public class GameSystemManager {
    BeltSystem belt = new BeltSystem();
    ItemEjectorSystem itemEjector = new ItemEjectorSystem();
    MapResourcesSystem mapResources = new MapResourcesSystem();
    MinerSystem miner = new MinerSystem();

    public GameSystemManager(GameRoot root) throws IOException {
    }
}
