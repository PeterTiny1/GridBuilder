package io.shapez.game;

import io.shapez.core.DrawParameters;
import io.shapez.core.Layer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MapChunkView extends MapChunk {
    public String renderKey;
    public final HashMap<Layer, ArrayList<Entity>> containedEntitiesByLayer = new HashMap<>() {{
        put(Layer.Regular, new ArrayList<>());
        put(Layer.Wires, new ArrayList<>());
    }};
    int renderIteration = 0;

    public MapChunkView(final GameRoot root, final int x, final int y) {
        super(root, x, y);
        this.markDirty();
    }

    private void markDirty() {
        ++this.renderIteration;
        this.renderKey = this.x + "/" + this.y + "@" + this.renderIteration;
    }

    void drawBackgroundLayer(final DrawParameters parameters) throws IOException {
        final GameSystemManager systemManager = this.root.systemMgr;
        systemManager.mapResources.drawChunk(parameters, this);
        systemManager.beltUnderlays.drawChunk(parameters, this);
        systemManager.belt.drawChunk(parameters, this);
    }

    public void drawForegroundDynamicLayer(final DrawParameters parameters) throws IOException {
        final GameSystemManager systemMgr = root.systemMgr;
        systemMgr.itemEjector.drawChunk(parameters, this);
        systemMgr.itemAcceptor.drawChunk(parameters, this);
        systemMgr.miner.drawChunk(parameters, this);
    }

    public void drawForegroundStaticLayer(final DrawParameters parameters) throws IOException {
        final GameSystemManager systemManager = this.root.systemMgr;
        systemManager.staticMapEntities.drawChunk(parameters, this);
        systemManager.lever.drawChunk(parameters, this);
        systemManager.display.drawChunk(parameters, this);
    }

    public enum Methods {
        drawForegroundDynamicLayer, drawForegroundStaticLayer, drawBackgroundLayer
    }
}
