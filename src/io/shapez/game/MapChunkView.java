package io.shapez.game;

import io.shapez.core.DrawParameters;
import io.shapez.core.Layer;

import java.util.ArrayList;
import java.util.HashMap;

public class MapChunkView extends MapChunk {
    public String renderKey;
    public HashMap<Layer, ArrayList<Entity>> containedEntitiesByLayer = new HashMap<>() {{
        put(Layer.Regular, new ArrayList<>());
        put(Layer.Wires, new ArrayList<>());
    }};

    public MapChunkView(GameRoot root, int x, int y) {
        super(root, x, y);
    }

    void drawBackgroundLayer(DrawParameters parameters) {
        GameSystemManager systemManager = this.root.systemMgr;
        systemManager.mapResources.drawChunk(parameters, this);
        systemManager.beltUnderlays.drawChunk(parameters, this);
        systemManager.belt.drawChunk(parameters, this);
    }

    public void drawForegroundDynamicLayer(DrawParameters parameters) {
        GameSystemManager systemMgr = root.systemMgr;
        systemMgr.itemEjector.drawChunk(parameters, this);
        systemMgr.itemAcceptor.drawChunk(parameters, this);
    }

    public enum Methods {
        drawForegroundDynamicLayer, drawBackgroundLayer
    }
}
