package io.shapez.game;

import java.util.ArrayList;

public class GameSystemWithFilter extends GameSystem {
    private final Component[] requiredComponents;
    final ArrayList<String> requiredComponentIds = new ArrayList<>();
    ArrayList<Entity> allEntities = new ArrayList<>();

    public GameSystemWithFilter(GameRoot root, Component[] requiredComponents) {
        super(root);
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
