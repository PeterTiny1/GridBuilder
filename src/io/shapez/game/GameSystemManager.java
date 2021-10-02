package io.shapez.game;

import io.shapez.game.systems.*;

import java.io.IOException;

public class GameSystemManager {
    private final GameRoot root;
    public BeltSystem belt;
    public io.shapez.game.systems.ItemProcessorSystem itemProcessorSystem;
    public BeltUnderlaysSystem beltUnderlays;
    public ItemAcceptorSystem itemAcceptor;
    public StaticMapEntitySystem staticMapEntities;
    public LeverSystem lever;
    public DisplaySystem display;
    public ItemProcessorSystem itemProcessor;
    public FilterSystem filter;
    public HubSystem hub;
    ItemEjectorSystem itemEjector;
    final MapResourcesSystem mapResources;
    MinerSystem miner;

    public GameSystemManager(final GameRoot root) throws IOException {
        this.root = root;
        mapResources = new MapResourcesSystem(root);
        belt = new BeltSystem(root);
        itemProcessor = new ItemProcessorSystem(root);
        itemEjector = new ItemEjectorSystem(root);
        miner = new MinerSystem(root);
        beltUnderlays = new BeltUnderlaysSystem(root);
        itemAcceptor = new ItemAcceptorSystem(root);
        staticMapEntities = new StaticMapEntitySystem(root);
        lever = new LeverSystem(root);
        display = new DisplaySystem(root);
        itemProcessor = new ItemProcessorSystem(root);
        hub = new HubSystem(root);
    }

    public void update() {
        itemAcceptor.update();
        belt.update();
    }
}
