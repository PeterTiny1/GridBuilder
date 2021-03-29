package io.shapez.game.buildings;

import io.shapez.core.Direction;
import io.shapez.game.Entity;
import io.shapez.game.MetaBuilding;

public class MetaBeltBuilding extends MetaBuilding {
    public static final Direction[] arrayBeltVariantToRotation = new Direction[]{Direction.Top, Direction.Left, Direction.Right};

    public MetaBeltBuilding() {
        super("belt");
    }

    @Override
    public void updateVariants(Entity entity, short rotationVariant, String variant) {
        entity.components.Belt.direction = arrayBeltVariantToRotation[rotationVariant];
    }
}
