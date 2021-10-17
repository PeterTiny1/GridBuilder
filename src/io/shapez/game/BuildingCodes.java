package io.shapez.game;

import io.shapez.core.Vector;
import io.shapez.game.buildings.MetaBeltBuilding;
import io.shapez.game.buildings.MetaHubBuilding;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class BuildingCodes {
    public static final String defaultBuildingVariant = "default";
    public static final HashMap<Integer, BuildingVariantIdentifier> gBuildingVariants = new HashMap<>() {{
        put(1, new BuildingVariantIdentifier(new MetaBeltBuilding(), BuildingCodes.defaultBuildingVariant, 0, new Vector(1, 1)));
        put(2, new BuildingVariantIdentifier(new MetaBeltBuilding(), BuildingCodes.defaultBuildingVariant, 1, new Vector(1, 1)));
        put(3, new BuildingVariantIdentifier(new MetaBeltBuilding(), BuildingCodes.defaultBuildingVariant, 2, new Vector(1, 1)));
        put(26, new BuildingVariantIdentifier(new MetaHubBuilding(), BuildingCodes.defaultBuildingVariant, 0, new Vector(4, 4)));
    }};
    private static final HashMap<String, Integer> variantsCache = new HashMap<>();

    public static int getCodeFromBuildingData(final MetaBuilding metaBuilding, final String variant, final int rotationVariant) {
        final String hash = metaBuilding.getId() + "/" + variant + "/" + rotationVariant;
        return BuildingCodes.variantsCache.get(hash);
    }

    public static void buildBuildingCodeCache() {
        for (final Map.Entry<Integer, BuildingVariantIdentifier> entry : BuildingCodes.gBuildingVariants.entrySet()) {
            final BuildingVariantIdentifier data = entry.getValue();
            final String hash = data.metaBuilding.getId() + "/" + data.variant + "/" + data.rotationVariant;
            BuildingCodes.variantsCache.put(hash, entry.getKey());
        }
    }

    public static class BuildingVariantIdentifier {
        private final int rotationVariant;
        private final String variant;
        private final MetaBuilding metaBuilding;
        public final Vector tileSize;
        public BufferedImage image;

        public BuildingVariantIdentifier(final MetaBuilding metaBuilding, final String variant, final int rotationVariant, final Vector tileSize) {
            this.metaBuilding = metaBuilding;
            this.variant = variant;
            this.rotationVariant = rotationVariant;
            this.tileSize = tileSize;
        }
    }
}
