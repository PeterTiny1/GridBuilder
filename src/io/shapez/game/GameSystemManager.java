package io.shapez.game;

import io.shapez.game.systems.*;

import java.io.IOException;

public class GameSystemManager {
    private final GameRoot root;
    public BeltSystem belt;
    public BeltUnderlaysSystem beltUnderlays;
    ItemEjectorSystem itemEjector;
    MapResourcesSystem mapResources;
    MinerSystem miner;

    public GameSystemManager(GameRoot root) throws IOException {
        this.root = root;
        mapResources = new MapResourcesSystem(root);
        belt = new BeltSystem(root);
        itemEjector = new ItemEjectorSystem(root);
        miner = new MinerSystem(root);
        beltUnderlays = new BeltUnderlaysSystem(root);
    }
}
