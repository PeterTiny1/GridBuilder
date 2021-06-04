package io.shapez.game;

import java.util.ArrayList;

public class GameSystemWithFilter extends GameSystem {
    private final Component[] requiredComponents;
    final ArrayList<String> requiredComponentIds = new ArrayList<>();
    protected ArrayList<Entity> allEntities = new ArrayList<>();

    public GameSystemWithFilter(final GameRoot root, final Component[] requiredComponents) {
        super(root);
        this.requiredComponents = requiredComponents;
        for (final Component requiredComponent : requiredComponents) {
            requiredComponentIds.add(requiredComponent.getId());
        }
//        this.root.entityAdded.add();
    }

    private void internalPushEntityIfMatching(final Entity entity) {
        for (final String requiredComponentId : this.requiredComponentIds) {
            if (!entity.components.get(requiredComponentId)) {
                return;
            }
        }
    }
}
