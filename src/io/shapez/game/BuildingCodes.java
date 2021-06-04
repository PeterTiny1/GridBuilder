package io.shapez.game;

import io.shapez.core.Vector;
import io.shapez.game.buildings.MetaBeltBuilding;
import io.shapez.game.buildings.MetaHubBuilding;

import java.awt.image.BufferedImage;
import java.util.HashMap;

public class BuildingCodes {
    final static String defaultBuildingVariant = "default";
    public static final HashMap<Integer, BuildingVariantIdentifier> gBuildingVariants = new HashMap<>() {{
        put(1, new BuildingVariantIdentifier(new MetaBeltBuilding(), BuildingCodes.defaultBuildingVariant, 0, new Vector(1, 1)));
        put(2, new BuildingVariantIdentifier(new MetaBeltBuilding(), BuildingCodes.defaultBuildingVariant, 1, new Vector(1, 1)));
        put(3, new BuildingVariantIdentifier(new MetaBeltBuilding(), BuildingCodes.defaultBuildingVariant, 2, new Vector(1, 1)));
        put(26, new BuildingVariantIdentifier(new MetaHubBuilding(), BuildingCodes.defaultBuildingVariant, 0, new Vector(4, 4)));
    }};

    public static class BuildingVariantIdentifier {
        private final int rotationVariant;
        private final String variant;
        private final MetaBuilding meta;
        public final Vector tileSize;
        public BufferedImage image;

        public BuildingVariantIdentifier(final MetaBuilding meta, final String variant, final int rotationVariant, final Vector tileSize) {
            this.meta = meta;
            this.variant = variant;
            this.rotationVariant = rotationVariant;
            this.tileSize = tileSize;
        }
    }
}
