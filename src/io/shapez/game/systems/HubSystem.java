package io.shapez.game.systems;

import io.shapez.core.DrawParameters;
import io.shapez.game.Component;
import io.shapez.game.Entity;
import io.shapez.game.GameRoot;
import io.shapez.game.GameSystemWithFilter;
import io.shapez.game.components.StaticMapEntityComponent;

public class HubSystem extends GameSystemWithFilter {
    public HubSystem(GameRoot root) {
        super(root, new Component[]{new HubComponent(null)});
    }

    public void draw(DrawParameters parameters) {
        for (Entity entity : this.allEntities) {
            this.drawEntity(parameters, entity);
        }
    }

    private void drawEntity(DrawParameters parameters, Entity entity) {
        StaticMapEntityComponent staticComp = entity.components.StaticMapEntity;
        if (staticComp.shouldNotBeDrawn(parameters)) {
            return;
        }
        int delivered = this.root.hubGoals.getCurrentGoalDelivered();
    }
}
