package io.shapez.game.systems;

import io.shapez.core.Direction;
import io.shapez.core.DrawParameters;
import io.shapez.core.Layer;
import io.shapez.core.Vector;
import io.shapez.game.*;
import io.shapez.game.Component;
import io.shapez.game.components.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class BeltUnderlaysSystem extends GameSystemWithFilter {
    private static final Rectangle FULL_CLIP_RECT = new Rectangle(0, 0, 1, 1);
    private final ArrayList<BufferedImage> underlayBeltSprites = new ArrayList<>();

    public BeltUnderlaysSystem(GameRoot root) throws IOException {
        super(root, new Component[]{new BeltUnderlaysComponent()});
        for (int i = 0; i < BeltSystem.BELT_ANIM_COUNT; i++) {
            this.underlayBeltSprites.add(ImageIO.read(Objects.requireNonNull(this.getClass().getResource("/sprites/belt/forward_" + i + ".png"))));
        }
    }

    public void drawChunk(DrawParameters parameters, MapChunkView chunk) {
        int speedMultiplier = Math.min(this.root.hubGoals.getBeltBaseSpeed(), 10);
        ArrayList<Entity> contents = chunk.containedEntitiesByLayer.get(Layer.Regular);
        for (Entity entity : contents) {
            BeltUnderlaysComponent underlayComp = entity.components.BeltUnderlays;
            if (underlayComp == null) {
                continue;
            }

            StaticMapEntityComponent staticComp = entity.components.StaticMapEntity;
            BeltUnderlaysComponent.BeltUnderlayTile[] underlays = underlayComp.underlays;
            for (BeltUnderlaysComponent.BeltUnderlayTile underlay : underlays) {
                Vector transformedPos = staticComp.localTileToWorld(underlay.pos);
                double destX = transformedPos.x * GlobalConfig.tileSize;
                double destY = transformedPos.y * GlobalConfig.tileSize;

                if (!chunk.tileSpaceRectangle.contains(transformedPos.x, transformedPos.y)) {
                    continue;
                }

                if (!parameters.visibleRect.intersects(destX, destY, GlobalConfig.tileSize, GlobalConfig.tileSize)) {
                    continue;
                }

                Direction worldDirection = staticComp.localDirectionToWorld(underlay.direction);
                int angle = Vector.directionToAngle(worldDirection);

                BeltUnderlaysComponent.ClippedBeltUnderlayType underlayType = this.computeBeltUnderlayType(entity, underlay);
                Rectangle clipRect = underlayTypeToClipRect(underlayType);
                if (clipRect == null) {
                    continue;
                }

                double x = destX + GlobalConfig.halfTileSize;
                double y = destY + GlobalConfig.halfTileSize;
                double angleRadians = Math.toRadians(angle);

                int animationIndex = (int) ((this.root.time.realtimeNow() * speedMultiplier * BeltSystem.BELT_ANIM_COUNT * 126) / 42 * GlobalConfig.itemSpacingOnBelts);
                parameters.context.translate(x, y);
                parameters.context.rotate(angleRadians);
                drawCachedWithClipRect(this.underlayBeltSprites.get(animationIndex % this.underlayBeltSprites.size()), parameters, -GlobalConfig.halfTileSize, -GlobalConfig.halfTileSize, GlobalConfig.tileSize, GlobalConfig.tileSize, clipRect);
                parameters.context.rotate(-angleRadians);
                parameters.context.translate(-x, -y);
            }
        }
    }

    private void drawCachedWithClipRect(BufferedImage image, DrawParameters parameters, int x, int y, byte w, byte h, Rectangle clipRect) {
        String scale = parameters.desiredAtlasScale;
        parameters.context.drawImage(image, x, y, w, h, null);
    }

    private Rectangle underlayTypeToClipRect(BeltUnderlaysComponent.ClippedBeltUnderlayType underlayType) {
        return switch (underlayType) {
            case none -> null;
            case full -> FULL_CLIP_RECT;
            case topOnly -> new Rectangle(0, 0, 1, 1);
            case bottomOnly -> new Rectangle(0, 1, 1, 1);
        };
    }

    private BeltUnderlaysComponent.ClippedBeltUnderlayType computeBeltUnderlayType(Entity entity, BeltUnderlaysComponent.BeltUnderlayTile underlayTile) {
        if (underlayTile.cachedType != null) {
            return underlayTile.cachedType;
        }
        StaticMapEntityComponent staticComp = entity.components.StaticMapEntity;

        Vector transformedPos = staticComp.localTileToWorld(underlayTile.pos);
        double destX = transformedPos.x * GlobalConfig.tileSize;
        double destY = transformedPos.y * GlobalConfig.tileSize;

        Direction worldDirection = staticComp.localDirectionToWorld(underlayTile.direction);
        Vector worldDirectionVector = Vector.directionToVector(worldDirection);

        boolean connectedTop = this.checkIsAcceptorConnected(transformedPos.add(worldDirectionVector), Vector.invertDirection(worldDirection));

        boolean connectedBottom = this.checkIsEjectorConnected(transformedPos.sub(worldDirectionVector), worldDirection);

        BeltUnderlaysComponent.ClippedBeltUnderlayType flag = BeltUnderlaysComponent.ClippedBeltUnderlayType.none;

        if (connectedTop && connectedBottom) {
            flag = BeltUnderlaysComponent.ClippedBeltUnderlayType.full;
        } else if (connectedTop) {
            flag = BeltUnderlaysComponent.ClippedBeltUnderlayType.topOnly;
        } else if (connectedBottom) {
            flag = BeltUnderlaysComponent.ClippedBeltUnderlayType.bottomOnly;
        }
        return flag;
    }

    private boolean checkIsEjectorConnected(Vector tile, Direction toDirection) {
        Entity contents = this.root.map.getLayerContentXY(tile.x, tile.y, Layer.Regular);
        if (contents == null) {
            return false;
        }
        StaticMapEntityComponent staticComp = contents.components.StaticMapEntity;
        BeltComponent beltComponent = contents.components.Belt;
        if (beltComponent != null) {
            return staticComp.localDirectionToWorld(beltComponent.direction) == toDirection;
        }

        ItemEjectorComponent ejectorComp = contents.components.ItemEjector;
        if (ejectorComp != null) {
            for (int i = 0; i < ejectorComp.slots.size(); i++) {
                ItemEjectorComponent.ItemEjectorSlot slot = ejectorComp.slots.get(i);
                Vector slotTile = staticComp.localTileToWorld(slot.pos);

                if (!slotTile.equals(tile)) {
                    continue;
                }

                Direction slotDirection = staticComp.localDirectionToWorld(slot.direction);
                if (slotDirection == toDirection) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkIsAcceptorConnected(Vector tile, Direction fromDirection) {
        Entity contents = this.root.map.getLayerContentXY(tile.x, tile.y, Layer.Regular);
        if (contents == null) {
            return false;
        }
        StaticMapEntityComponent staticComp = contents.components.StaticMapEntity;
        BeltComponent beltComponent = contents.components.Belt;
        if (beltComponent != null) {
            return staticComp.localDirectionToWorld(Direction.Bottom) == fromDirection;
        }
        ItemAcceptorComponent acceptorComp = contents.components.ItemAcceptor;
        if (acceptorComp != null) {
            for (int i = 0; i < acceptorComp.slots.size(); i++) {
                ItemAcceptorComponent.ItemAcceptorSlot slot = acceptorComp.slots.get(i);
                Vector slotTile = staticComp.localTileToWorld(slot.pos);
                if (!slotTile.equals(tile)) {
                    continue;
                }

                for (int j = 0; j < slot.directions.length; j++) {
                    Direction slotDirection = staticComp.localDirectionToWorld(slot.directions[j]);
                    if (slotDirection == fromDirection) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
