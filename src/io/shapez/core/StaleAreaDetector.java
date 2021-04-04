package io.shapez.core;

import io.shapez.game.Component;
import io.shapez.game.Entity;

import java.awt.*;
import java.util.Arrays;
import java.util.function.Consumer;

public class StaleAreaDetector {
    private final String name;
    private final Consumer<Rectangle> recomputeMethod;
    Rectangle staleArea = null;
    Object[] componentIds;
    int tilesAround;
    public Consumer<Entity> checker = (entity) -> {
        for (Object componentId : componentIds) {
            if (entity.components.get((String) componentId)) {
                Rectangle area = new Rectangle(entity.components.StaticMapEntity.getTileSpaceBounds().x - tilesAround, entity.components.StaticMapEntity.getTileSpaceBounds().y - tilesAround, entity.components.StaticMapEntity.getTileSpaceBounds().width + 2 * tilesAround, entity.components.StaticMapEntity.getTileSpaceBounds().height + 2 * tilesAround);
                this.invalidate(area);
                return;
            }
        }
    };

    public StaleAreaDetector(String name, Consumer<Rectangle> recomputeMethod) {
        this.name = name;
        this.recomputeMethod = recomputeMethod;

    }

    public void recomputeOnComponentsChanged(Component[] components, int tilesAround) {
        this.componentIds = Arrays.stream(components).map(Component::getId).toArray();
        this.tilesAround = tilesAround;
    }

    private void invalidate(Rectangle area) {
        if (this.staleArea != null) {
            this.staleArea = this.staleArea.union(area);
        } else {
            this.staleArea = area;
        }
    }
}
