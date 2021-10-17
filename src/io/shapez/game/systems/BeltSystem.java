package io.shapez.game.systems;

import io.shapez.core.Direction;
import io.shapez.core.DrawParameters;
import io.shapez.core.Layer;
import io.shapez.core.Vector;
import io.shapez.game.Component;
import io.shapez.game.*;
import io.shapez.game.buildings.MetaBeltBuilding;
import io.shapez.game.components.BeltComponent;
import io.shapez.game.components.StaticMapEntityComponent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

import static io.shapez.game.buildings.MetaBeltBuilding.arrayBeltVariantToRotation;

public class BeltSystem extends GameSystemWithFilter {
    private final HashMap<Direction, ArrayList<BufferedImage>> beltAnimations = new HashMap<>() {{
        put(Direction.Top, new ArrayList<>());
        put(Direction.Left, new ArrayList<>());
        put(Direction.Right, new ArrayList<>());
    }};
    private final HashMap<Direction, BufferedImage> beltSprites = new HashMap<>() {{
        put(Direction.Top, ImageIO.read(Objects.requireNonNull(BeltSystem.class.getResource("/sprites/belt/forward_0.png"))));
        put(Direction.Left, ImageIO.read(Objects.requireNonNull(BeltSystem.class.getResource("/sprites/belt/left_0.png"))));
        put(Direction.Right, ImageIO.read(Objects.requireNonNull(BeltSystem.class.getResource("/sprites/belt/right_0.png"))));
    }};
    public static final byte BELT_ANIM_COUNT = 14;
    final ArrayList<BeltPath> beltPaths = new ArrayList<>();

    public BeltSystem(final GameRoot root) throws IOException {
        super(root, new Component[]{new BeltComponent(null)});
        for (int i = 0; i < BeltSystem.BELT_ANIM_COUNT; i++) {
            this.beltAnimations.get(Direction.Top).add(ImageIO.read(Objects.requireNonNull(BeltSystem.class.getResource("/sprites/belt/forward_" + i + ".png"))));
            this.beltAnimations.get(Direction.Left).add(ImageIO.read(Objects.requireNonNull(BeltSystem.class.getResource("/sprites/belt/left_" + i + ".png"))));
            this.beltAnimations.get(Direction.Right).add(ImageIO.read(Objects.requireNonNull(BeltSystem.class.getResource("/sprites/belt/right_" + i + ".png"))));
        }
    }

//    public Object[] serializePaths() {
//        final ArrayList<Object> data = new ArrayList<>();
//        for (final BeltPath beltPath : this.beltPaths) {
//            data.add(beltPath.serialize());
//        }
//        return data.toArray();
//    }

    public void onEntityDestroyed(final Entity entity) {
        if (entity.components.Belt == null) {
            return;
        }

        final BeltPath assignedPath = entity.components.Belt.assignedPath;
        assert assignedPath != null;

        this.deleteEntityFromPath(assignedPath, entity);
    }

    private void deleteEntityFromPath(final BeltPath path, final Entity entity) {
        if (path.entityPath.size() == 1) {
            this.beltPaths.remove(path);
            return;
        }

        if (path.isStartEntity(entity)) {
            path.deleteEntityOnStart(entity);
        } else if (path.isEndEntity(entity)) {
            path.deleteEntityOnEnd(entity);
        } else {
            final BeltPath newPath = path.deleteEntityOnPathsSplitIntoTwo(entity); // great name :D
            this.beltPaths.add(newPath);
        }
    }

    public void updateSurroundingBeltPlacement(final Entity entity) {
        final StaticMapEntityComponent staticComp = entity.components.StaticMapEntity;
        if (staticComp == null) return;
        final SingletonFactory<MetaBuilding> gMetaBuildingRegistry = new SingletonFactory<>();
        final MetaBeltBuilding metaBelt = (MetaBeltBuilding) gMetaBuildingRegistry.findByClass(new MetaBeltBuilding());
        final Rectangle originalRect = staticComp.getTileSpaceBounds();
        final Rectangle affectedArea = new Rectangle(originalRect.x - 1, originalRect.y - 1, originalRect.width + 1, originalRect.height + 1);
        final HashSet<BeltPath> changedPaths = new HashSet<>();

        for (int x = affectedArea.x; x < affectedArea.width + affectedArea.x; x++) {
            for (int y = affectedArea.y; y < affectedArea.y + affectedArea.width; y++) {
                if (originalRect.contains(new Point(x, y))) {
                    continue;
                }

                final Entity[] targetEntities = root.map.getLayerContentsMultipleXY(x, y);
                for (final Entity targetEntity : targetEntities) {
                    final BeltComponent targetBeltComp = targetEntity.components.Belt;
                    final StaticMapEntityComponent targetStaticComp = targetEntity.components.StaticMapEntity;
                    if (targetBeltComp == null) {
                        continue;
                    }
                    final String defaultBuildingVariant = "default";
                    final short[] rotations = Objects.requireNonNull(metaBelt).computeOptimalDirectionAndRotationVariantAtTile(new Vector(x, y), targetStaticComp.originalRotation, defaultBuildingVariant, targetEntity.layer);
                    final Direction newDirection = arrayBeltVariantToRotation[rotations[1]];
                    if (targetStaticComp.rotation != rotations[0] || newDirection != targetBeltComp.direction) {
                        final BeltPath originalPath = targetBeltComp.assignedPath;

                        this.deleteEntityFromPath(targetBeltComp.assignedPath, targetEntity);

                        targetStaticComp.rotation = rotations[0];
                        metaBelt.updateVariants(targetEntity, rotations[1], defaultBuildingVariant);

                        targetStaticComp.code = getCodeFromBuildingData(metaBelt, defaultBuildingVariant, rotations[1]);

                        originalPath.onPathChanged();
                        this.addEntityToPaths(targetEntity);

                        // root.signals.entityChanged.dispatch(targetEntity);
                    }

                    if (targetBeltComp.assignedPath != null) {
                        changedPaths.add(targetBeltComp.assignedPath);
                    }
                }
            }
        }
        changedPaths.forEach(BeltPath::onSurroundingsChanged);
    }

    private void addEntityToPaths(final Entity entity) {
        final Entity fromEntity = this.findSupplyingEntity(entity);
        final Entity toEntity = this.findFollowUpEntity(entity);

        if (fromEntity != null) {
            final BeltPath fromPath = fromEntity.components.Belt.assignedPath;
            fromPath.extendOnEnd(entity);

            if (toEntity != null) {
                final BeltPath toPath = toEntity.components.Belt.assignedPath;

                if (fromPath != toPath) {
                    fromPath.extendByPath(toPath);
                    this.beltPaths.remove(toPath);
                }
            }
        } else {
            if (toEntity != null) {
                final BeltPath toPath = toEntity.components.Belt.assignedPath;
                toPath.extendOnBeginning(entity);
            } else {
                final BeltPath path = new BeltPath(root, new LinkedList<>() {{
                    add(entity);
                }});
                this.beltPaths.add(path);
            }
        }
    }

    private Entity findFollowUpEntity(final Entity entity) {
        final StaticMapEntityComponent staticComp = entity.components.StaticMapEntity;
        final BeltComponent beltComp = entity.components.Belt;

        final Direction followUpDirection = staticComp.localDirectionToWorld(beltComp.direction);
        final Vector followUpVector = Vector.directionToVector(followUpDirection);

        final Vector followUpTile = staticComp.origin.add(followUpVector);
        final Entity followUpEntity = root.map.getLayerContentXY(followUpTile.x, followUpTile.y, entity.layer);

        if (followUpEntity != null) {
            final BeltComponent followUpBeltComp = followUpEntity.components.Belt;
            if (followUpBeltComp != null) {
                final StaticMapEntityComponent followUpStatic = followUpEntity.components.StaticMapEntity;

                final Direction acceptedDirection = followUpStatic.localDirectionToWorld(Direction.Top);
                if (acceptedDirection == followUpDirection) {
                    return followUpEntity;
                }
            }
        }

        return null;
    }

    private Entity findSupplyingEntity(final Entity entity) {
        final StaticMapEntityComponent staticComp = entity.components.StaticMapEntity;

        final Direction supplyDirection = staticComp.localDirectionToWorld(Direction.Bottom);
        final Vector supplyVector = Vector.directionToVector(supplyDirection);

        final Vector supplyTile = staticComp.origin.add(supplyVector);
        final Entity supplyEntity = root.map.getLayerContentXY(supplyTile.x, supplyTile.y, entity.layer);

        if (supplyEntity != null) {
            final BeltComponent supplyBeltComp = supplyEntity.components.Belt;
            if (supplyBeltComp != null) {
                final StaticMapEntityComponent supplyStatic = supplyEntity.components.StaticMapEntity;
                final Direction otherDirection = supplyStatic.localDirectionToWorld(Vector.invertDirection(supplyBeltComp.direction));

                if (otherDirection == supplyDirection) {
                    return supplyEntity;
                }
            }
        }
        return null;
    }

    private int getCodeFromBuildingData(final MetaBuilding metaBuilding, final String variant, final short rotationVariant) {
        final String hash = metaBuilding.getId() + "/" + variant + "/" + rotationVariant;
        final HashMap<String, Integer> variantsCache = new HashMap<>();
        return variantsCache.get(hash);
    }

    public void onEntityAdded(final Entity entity) {
        if (entity.components.Belt == null) {
            return;
        }
        this.addEntityToPaths(entity);
    }

    public void drawBeltItems(final DrawParameters parameters) throws IOException {
        for (final BeltPath beltPath : this.beltPaths) {
            beltPath.draw(parameters);
        }
    }

    public void drawChunk(final DrawParameters parameters, final MapChunkView chunk) {
        final int speedMultiplier = Math.min(this.root.hubGoals.getBeltBaseSpeed(), 10);

        final int animationIndex = (int) ((this.root.time.realtimeNow() * speedMultiplier * BeltSystem.BELT_ANIM_COUNT * 126) / 42 * GlobalConfig.itemSpacingOnBelts);
        final ArrayList<Entity> contents = chunk.containedEntitiesByLayer.get(Layer.Regular);
        if (this.root.app.settings.getAllSettings().simplifiedBelts) {
            BeltPath hoveredBeltPath = null;
            final Vector mousePos = new Vector(this.root.app.getMousePosition());
            if (this.root.currentLayer == Layer.Regular) {
                final Vector tile = this.root.camera.screenToWorld(mousePos).toTileSpace();
                final Entity content = this.root.map.getLayerContentXY(tile.x, tile.y, Layer.Regular);
                if (content != null && content.components.Belt != null) {
                    hoveredBeltPath = content.components.Belt.assignedPath;
                }
            }

            for (final Entity entity : contents) {
                if (entity.components.Belt != null) {
                    final Direction direction = entity.components.Belt.direction;
                    BufferedImage sprite = this.beltAnimations.get(direction).get(0);
                    if (entity.components.Belt.assignedPath == hoveredBeltPath) {
                        sprite = this.beltAnimations.get(direction).get(animationIndex % BeltSystem.BELT_ANIM_COUNT);
                    }

                    entity.components.StaticMapEntity.drawSpriteOnBoundsClipped(parameters, sprite, 0);
                }
            }
        }
    }

    public void update() {
        for (final BeltPath beltPath : this.beltPaths) {
            beltPath.update();
        }
    }

    private static class SingletonFactory<T> {
        private ArrayList<T> entries;

        public SingletonFactory() {

        }

        public T findByClass(final Object classHandle) {
            for (final T entry : this.entries) {
                if (entry.getClass() == classHandle.getClass()) {
                    return entry;
                }
            }
            assert false;
            return null;
        }
    }
}
