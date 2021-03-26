package io.shapez.game;

import java.util.ArrayList;

public class GameSystemWithFilter {
    private final Component[] requiredComponents;
    ArrayList<String> requiredComponentIds = new ArrayList<>();
    ArrayList<Entity> allEntities = new ArrayList<>();

    public GameSystemWithFilter(Component[] requiredComponents) {
        this.requiredComponents = requiredComponents;
        for (Component requiredComponent : requiredComponents) {
            requiredComponentIds.add(requiredComponent.getId());
        }
//        this.root.entityAdded.add();
    }

    private void internalPushEntityIfMatching(Entity entity) {
        for (String requiredComponentId : this.requiredComponentIds) {
            if (!entity.components.get(requiredComponentId)) {
                return;
            }
        }
    }
}
