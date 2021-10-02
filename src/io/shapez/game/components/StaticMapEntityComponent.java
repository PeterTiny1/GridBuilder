package io.shapez.game.components;

import io.shapez.core.Direction;
import io.shapez.core.DrawParameters;
import io.shapez.core.Vector;
import io.shapez.game.BuildingCodes;
import io.shapez.game.Component;
import io.shapez.game.GlobalConfig;

import java.awt.*;
import java.awt.image.BufferedImage;

public class StaticMapEntityComponent extends Component {
    public final Vector origin;
    public short rotation;
    public int code;
    public final short originalRotation;

    public StaticMapEntityComponent(final Vector origin, final Vector tileSize, final short rotation, final short originalRotation, final int code) {
        super();
        this.origin = origin;
        this.rotation = rotation;
        this.code = code;
        this.originalRotation = originalRotation;
    }

    public StaticMapEntityComponent(final Vector origin, final int rotation, final int originalRotation, final Vector vector, final int code) {
        super();
        this.origin = origin;
        this.rotation = (short) rotation;
        this.originalRotation = (short) originalRotation;
        this.code = code;
    }

    public Vector localTileToWorld(final Vector localTile) {
        final Vector result = localTile.rotateFastMultipleOf90(this.rotation);
        result.x += this.origin.x;
        result.y += this.origin.y;
        return result;
    }

    public Direction localDirectionToWorld(final Direction direction) {
        return Vector.transformDirectionFromMultipleOf90(direction, this.rotation);
    }

    public Direction worldDirectionToLocal(final Direction direction) {
        return Vector.transformDirectionFromMultipleOf90(direction, 360 - this.rotation);
    }

    public Vector worldToLocalTile(final Vector worldTile) {
        final Vector localUnrotated = worldTile.sub(this.origin);
        return this.unapplyRotationToVector(localUnrotated);
    }

    private Vector unapplyRotationToVector(final Vector vector) {
        return vector.rotateFastMultipleOf90(360 - this.rotation);
    }


    @Override
    public String getId() {
        return "StaticMapEntity";
    }

    public Rectangle getTileSpaceBounds() {
        final Vector size = this.getTileSize();
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

    private BuildingCodes.BuildingVariantIdentifier getBuildingDataFromCode(final int code) {
        return BuildingCodes.gBuildingVariants.get(code);
    }

    public void drawSpriteOnBoundsClipped(final DrawParameters parameters, final BufferedImage sprite, final int extrudePixels) {
        if (!this.shouldBeDrawn(parameters)) {
            return;
        }
        final Vector size = this.getTileSize();
        final double worldX = this.origin.x * GlobalConfig.tileSize;
        final double worldY = this.origin.y * GlobalConfig.tileSize;

        if (this.rotation == 0) {
            drawCached(sprite, parameters, worldX - extrudePixels * size.x, worldY - extrudePixels * size.y, GlobalConfig.tileSize * size.x + 2 * extrudePixels * size.x, GlobalConfig.tileSize * size.y + 2 * extrudePixels * size.y);
        } else {
            final double rotationCenterX = worldX - GlobalConfig.halfTileSize;
            final double rotationCenterY = worldY - GlobalConfig.halfTileSize;
            parameters.context.translate(rotationCenterX, rotationCenterY);
            parameters.context.rotate(Math.toRadians(this.rotation));
            drawCached(sprite, parameters, -GlobalConfig.halfTileSize - extrudePixels * size.x, -GlobalConfig.halfTileSize - extrudePixels * size.x, GlobalConfig.tileSize * size.x + 2 * extrudePixels * size.x, GlobalConfig.tileSize * size.y + 2 * extrudePixels * size.y);
            parameters.context.translate(-rotationCenterX, -rotationCenterY);
            parameters.context.rotate(-Math.toRadians(this.rotation));
        }
    }

    private void drawCached(final BufferedImage image, final DrawParameters parameters, final double x, final double y, final double w, final double h) {
        final Rectangle visibleRect = parameters.visibleRect;

        final String scale = parameters.desiredAtlasScale;
        parameters.context.drawImage(image, (int) x, (int) y, (int) w, (int) h, null);
    }

    public boolean shouldBeDrawn(final DrawParameters parameters) {
        double x = 0;
        double y = 0;
        double w = 0;
        double h = 0;

        final Vector size = this.getTileSize();

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

    public BufferedImage getImage() {
        return getBuildingDataFromCode(this.code).image;
    }

}
