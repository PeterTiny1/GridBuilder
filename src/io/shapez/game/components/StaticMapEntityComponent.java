package io.shapez.game.components;

import io.shapez.core.Direction;
import io.shapez.core.DrawParameters;
import io.shapez.core.Layer;
import io.shapez.core.Vector;
import io.shapez.game.Component;
import io.shapez.game.GameLogic;
import io.shapez.game.GlobalConfig;
import io.shapez.game.MetaBuilding;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class StaticMapEntityComponent extends Component {
    public Vector origin;
    public short rotation;
    public int code;
    public final short originalRotation;
    private ArrayList<BuildingVariantIdentifier> gBuildingVariants;

    public StaticMapEntityComponent(Vector origin,/* Vector tileSize,*/ short rotation, short originalRotation, int code) {
        super();
        this.origin = origin;
        this.rotation = rotation;
        this.code = code;
        this.originalRotation = originalRotation;
    }

    public Vector localTileToWorld(Vector localTile) {
        Vector result = localTile.rotateFastMultipleOf90(this.rotation);
        result.x += this.origin.x;
        result.y += this.origin.y;
        return result;
    }

    public Direction localDirectionToWorld(Direction direction) {
        return Vector.transformDirectionFromMultipleOf90(direction, this.rotation);
    }

    public Direction worldDirectionToLocal(Direction direction) {
        return Vector.transformDirectionFromMultipleOf90(direction, 360 - this.rotation);
    }

    public Vector worldToLocalTile(Vector worldTile) {
        Vector localUnrotated = worldTile.sub(this.origin);
        return this.unapplyRotationToVector(localUnrotated);
    }

    private Vector unapplyRotationToVector(Vector vector) {
        return vector.rotateFastMultipleOf90(360 - this.rotation);
    }


    @Override
    public String getId() {
        return "StaticMapEntity";
    }

    public Rectangle getTileSpaceBounds() {
        Vector size = this.getTileSize();
        return switch (this.rotation) {
            case 0 -> new Rectangle((int) this.origin.x, (int) this.origin.y, (int) size.x, (int) size.y);
            case 90 -> new Rectangle((int) (this.origin.x - size.y - 1), (int) this.origin.y, (int) size.y, (int) size.x);
            case 180 -> new Rectangle((int) (this.origin.x - size.x + 1), (int) (this.origin.y - size.y + 1), (int) size.x, (int) size.y);
            case 270 -> new Rectangle((int) this.origin.x, (int) (this.origin.y - size.x + 1), (int) size.y, (int) size.x);
            default -> new Rectangle();
        };
    }

    private Vector getTileSize() {
        return getBuildingDataFromCode(this.code).tileSize;
    }

    private BuildingVariantIdentifier getBuildingDataFromCode(int code) {
        return gBuildingVariants.get(code);
    }

    public void drawSpriteOnBoundsClipped(DrawParameters parameters, BufferedImage sprite, int extrudePixels) {
        if (!this.shouldBeDrawn(parameters)) {
            return;
        }
        Vector size = this.getTileSize();
        double worldX = this.origin.x * GlobalConfig.tileSize;
        double worldY = this.origin.y * GlobalConfig.tileSize;

        if (this.rotation == 0) {
            drawCached(sprite, parameters, worldX - extrudePixels * size.x, worldY - extrudePixels * size.y, GlobalConfig.tileSize * size.x + 2 * extrudePixels * size.x, GlobalConfig.tileSize * size.y + 2 * extrudePixels * size.y);
        }
    }

    private void drawCached(BufferedImage image, DrawParameters parameters, double x, double y, double w, double h) {
        Rectangle visibleRect = parameters.visibleRect;

        String scale = parameters.desiredAtlasScale;
        parameters.context.drawImage(image, (int) x, (int) y, (int) w, (int) h, null);
    }

    private boolean shouldBeDrawn(DrawParameters parameters) {
        double x = 0;
        double y = 0;
        double w = 0;
        double h = 0;

        Vector size = this.getTileSize();

        switch (this.rotation) {
            case 0 -> {
                x = this.origin.x;
                y = this.origin.y;
                w = size.x;
                h = size.y;
            }
            case 90 -> {
                x = this.origin.x - size.y + 1;
                y = this.origin.y;
                w = size.y;
                h = size.x;
            }
            case 180 -> {
                x = this.origin.x - size.x + 1;
                y = this.origin.y - size.y + 1;
                w = size.x;
                h = size.y;
            }
            case 270 -> {
                x = this.origin.x;
                y = this.origin.y - size.x + 1;
                w = size.y;
                h = size.x;
            }
        }
        return parameters.visibleRect.intersects(x * GlobalConfig.tileSize, y * GlobalConfig.tileSize, w * GlobalConfig.tileSize, h * GlobalConfig.tileSize);
    }

    public static class BuildingVariantIdentifier {
        private final int rotationVariant;
        private final Layer variant;
        private final MetaBuilding meta;
        public Vector tileSize;

        public BuildingVariantIdentifier(MetaBuilding meta, Layer variant, int rotationVariant, Vector tileSize) {
            this.meta = meta;
            this.variant = variant;
            this.rotationVariant = rotationVariant;
            this.tileSize = tileSize;
        }
    }
}
