package io.shapez.game;

import io.shapez.core.Direction;
import io.shapez.core.Tile;
import io.shapez.game.components.BeltComponent;
import io.shapez.game.components.StaticMapEntityComponent;

import java.awt.*;

public class Entity {
    public int x, y;
    public Tile tile;
    public Image texture;
    public Direction direction;
    public EntityComponentStorage components;

    public Entity(Tile type, Image texture, Direction direction, int x, int y) {
        this.x = x;
        this.y = y;
        this.tile = type;
        this.texture = texture;
        this.direction = direction;
    }

    public static class EntityComponentStorage {
        public StaticMapEntityComponent StaticMapEntity;
        public BeltComponent Belt;
//        public ItemEjectorComponent ItemEjector;
//        public ItemAcceptorComponent ItemAcceptor;
//        public MinerComponent Miner;
//        public ItemProcessorComponent ItemProcessor;
//        public UndergroundBeltComponent UndergroundBelt;
//        public HubComponent Hub;
//        public StorageComponent Storage;
//        public WiredPinsComponent WiredPins;
//        public BeltUnderlaysComponent BeltUnderlays;
//        public WireComponent Wire;
//        public ConstantSignalComponent ConstantSignal;
//        public LogicGateComponent LogicGate;
//        public LeverComponent Lever;
//        public WireTunnelComponent WireTunnel;
//        public DisplayComponent Display;
//        public BeltReaderComponent BeltReader;
//        public FilterComponent Filter;
//        public ItemProducerComponent ItemProducer;

        public boolean get(String requiredComponentId) {
            return true;
        }
    }

}