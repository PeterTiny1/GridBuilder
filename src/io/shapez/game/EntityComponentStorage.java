package io.shapez.game;

import io.shapez.game.components.*;

public class EntityComponentStorage {
    public StaticMapEntityComponent StaticMapEntity;
    public BeltComponent Belt;
    public ItemEjectorComponent ItemEjector;
    public ItemAcceptorComponent ItemAcceptor;
    public MinerComponent Miner;
    public ItemProcessorComponent ItemProcessor;
    public UndergroundBeltComponent UndergroundBelt;
    //        public HubComponent Hub;
    public StorageComponent Storage;
    public WiredPinsComponent WiredPins;
    public BeltUnderlaysComponent BeltUnderlays;
    //        public WireComponent Wire;
//        public ConstantSignalComponent ConstantSignal;
//        public LogicGateComponent LogicGate;
    public LeverComponent Lever;
    //        public WireTunnelComponent WireTunnel;
    public DisplayComponent Display;
    //        public BeltReaderComponent BeltReader;
    public FilterComponent Filter;
//        public ItemProducerComponent ItemProducer;

    public boolean get(final String id) {
        return true;
    }
}
