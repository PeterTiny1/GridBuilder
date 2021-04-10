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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import static io.shapez.game.buildings.MetaBeltBuilding.arrayBeltVariantToRotation;

public class BeltSystem extends GameSystemWithFilter {
    private final HashMap<Direction, ArrayList<BufferedImage>> beltAnimations;
    private final HashMap<Direction, BufferedImage> beltSprites;
    public static final byte BELT_ANIM_COUNT = 14;
    ArrayList<BeltPath> beltPaths = new ArrayList<>();

    public BeltSystem(GameRoot root) throws IOException {
        super(root, new Component[]{new BeltComponent(null)});
        beltSprites = new HashMap<>() {{
            put(Direction.Top, ImageIO.read(BeltSystem.class.getResource("/sprites/belt/forward_0.png")));
            put(Direction.Left, ImageIO.read(BeltSystem.class.getResource("/sprites/belt/left_0.png")));
            put(Direction.Right, ImageIO.read(BeltSystem.class.getResource("/sprites/belt/right_0.png")));
        }};
        beltAnimations = new HashMap<>() {{
            put(Direction.Top, new ArrayList<>());
            put(Direction.Left, new ArrayList<>());
            put(Direction.Right, new ArrayList<>());
        }};
        for (int i = 0; i < BELT_ANIM_COUNT; i++) {
            this.beltAnimations.get(Direction.Top).add(ImageIO.read(BeltSystem.class.getResource("/sprites/belt/forward_" + i + ".png")));
            this.beltAnimations.get(Direction.Left).add(ImageIO.read(BeltSystem.class.getResource("/sprites/belt/left_" + i + ".png")));
            this.beltAnimations.get(Direction.Right).add(ImageIO.read(BeltSystem.class.getResource("/sprites/belt/right_" + i + ".png")));
        }
    }

    public Object[] serializePaths() {
        ArrayList<Object> data = new ArrayList<>();
        for (BeltPath beltPath : this.beltPaths) {
            data.add(beltPath.serialize());
        }
        return data.toArray();
    }

    public void onEntityDestroyed(Entity entity) {
        if (entity.components.Belt == null) {
            return;
        }

        BeltPath assignedPath = entity.components.Belt.assignedPath;
        assert assignedPath != null;

        this.deleteEntityFromPath(assignedPath, entity);
    }

    private void deleteEntityFromPath(BeltPath path, Entity entity) {
        if (path.entityPath.size() == 1) {
            this.beltPaths.remove(path);
            return;
        }

        if (path.isStartEntity(entity)) {
            path.deleteEntityOnStart(entity);
        } else if (path.isEndEntity(entity)) {
            path.deleteEntityOnEnd(entity);
        } else {
            BeltPath newPath = path.deleteEntityOnPathsSplitIntoTwo(entity); // great name :D
            this.beltPaths.add(newPath);
        }
    }

    public void updateSurroundingBeltPlacement(Entity entity) {
        StaticMapEntityComponent staticComp = entity.components.StaticMapEntity;
        if (staticComp == null) return;
        SingletonFactory<MetaBuilding> gMetaBuildingRegistry = new SingletonFactory<>();
        MetaBeltBuilding metaBelt = (MetaBeltBuilding) gMetaBuildingRegistry.findByClass(new MetaBeltBuilding());
        Rectangle originalRect = staticComp.getTileSpaceBounds();
        Rectangle affectedArea = staticComp.getTileSpaceBounds()/*originalRect.expandedInAllDirections(1)*/;
        // TODO: Increase size of it in all directions
        HashSet<BeltPath> changedPaths = new HashSet<>();

        for (int x = affectedArea.x; x < affectedArea.width + affectedArea.x; x++) {
            for (int y = affectedArea.y; y < affectedArea.y + affectedArea.width; y++) {
                if (originalRect.contains(new Point(x, y))) {
                    continue;
                }

                Entity[] targetEntities = root.map.getLayerContentsMultipleXY(x, y);
                for (Entity targetEntity : targetEntities) {
                    BeltComponent targetBeltComp = targetEntity.components.Belt;
                    StaticMapEntityComponent targetStaticComp = targetEntity.components.StaticMapEntity;
                    if (targetBeltComp == null) {
                        continue;
                    }
                    String defaultBuildingVariant = "default";
                    short[] rotations = metaBelt.computeOptimalDirectionAndRotationVariantAtTile(new Vector(x, y), targetStaticComp.originalRotation, defaultBuildingVariant, targetEntity.layer);
                    Direction newDirection = arrayBeltVariantToRotation[rotations[1]];
                    if (targetStaticComp.rotation != rotations[0] || newDirection != targetBeltComp.direction) {
                        BeltPath originalPath = targetBeltComp.assignedPath;

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

    private void addEntityToPaths(Entity entity) {
        Entity fromEntity = this.findSupplyingEntity(entity);
        Entity toEntity = this.findFollowUpEntity(entity);

        if (fromEntity != null) {
            BeltPath fromPath = fromEntity.components.Belt.assignedPath;
            fromPath.extendOnEnd(entity);

            if (toEntity != null) {
                BeltPath toPath = toEntity.components.Belt.assignedPath;

                if (fromPath != toPath) {
                    fromPath.extendByPath(toPath);
                    this.beltPaths.remove(toPath);
                }
            }
        } else {
            if (toEntity != null) {
                BeltPath toPath = toEntity.components.Belt.assignedPath;
                toPath.extendOnBeginning(entity);
            } else {
                BeltPath path = new BeltPath(root, new LinkedList<>() {{
                    add(entity);
                }});
                this.beltPaths.add(path);
            }
        }
    }

    private Entity findFollowUpEntity(Entity entity) {
        StaticMapEntityComponent staticComp = entity.components.StaticMapEntity;
        BeltComponent beltComp = entity.components.Belt;

        Direction followUpDirection = staticComp.localDirectionToWorld(beltComp.direction);
        Vector followUpVector = Vector.directionToVector(followUpDirection);

        Vector followUpTile = staticComp.origin.add(followUpVector);
        Entity followUpEntity = root.map.getLayerContentXY(followUpTile.x, followUpTile.y, entity.layer);

        if (followUpEntity != null) {
            BeltComponent followUpBeltComp = followUpEntity.components.Belt;
            if (followUpBeltComp != null) {
                StaticMapEntityComponent followUpStatic = followUpEntity.components.StaticMapEntity;

                Direction acceptedDirection = followUpStatic.localDirectionToWorld(Direction.Top);
                if (acceptedDirection == followUpDirection) {
                    return followUpEntity;
                }
            }
        }

        return null;
    }

    private Entity findSupplyingEntity(Entity entity) {
        StaticMapEntityComponent staticComp = entity.components.StaticMapEntity;

        Direction supplyDirection = staticComp.localDirectionToWorld(Direction.Bottom);
        Vector supplyVector = Vector.directionToVector(supplyDirection);

        Vector supplyTile = staticComp.origin.add(supplyVector);
        Entity supplyEntity = root.map.getLayerContentXY(supplyTile.x, supplyTile.y, entity.layer);

        if (supplyEntity != null) {
            BeltComponent supplyBeltComp = supplyEntity.components.Belt;
            if (supplyBeltComp != null) {
                StaticMapEntityComponent supplyStatic = supplyEntity.components.StaticMapEntity;
                Direction otherDirection = supplyStatic.localDirectionToWorld(Vector.invertDirection(supplyBeltComp.direction));

                if (otherDirection == supplyDirection) {
                    return supplyEntity;
                }
            }
        }
        return null;
    }

    private int getCodeFromBuildingData(MetaBuilding metaBuilding, String variant, short rotationVariant) {
        String hash = metaBuilding.getId() + "/" + variant + "/" + rotationVariant;
        HashMap<String, Integer> variantsCache = new HashMap<>();
        return variantsCache.get(hash);
    }

    public void onEntityAdded(Entity entity) {
        if (entity.components.Belt == null) {
            return;
        }
        this.addEntityToPaths(entity);
    }

    public void drawBeltItems(DrawParameters parameters) throws IOException {
        for (BeltPath beltPath : this.beltPaths) {
            beltPath.draw(parameters);
        }
    }

    public void drawChunk(DrawParameters parameters, MapChunkView chunk) {
        int speedMultiplier = Math.min(this.root.hubGoals.getBeltBaseSpeed(), 10);

        int animationIndex = (int) ((this.root.time.realtimeNow() * speedMultiplier * BELT_ANIM_COUNT * 126) / 42 * GlobalConfig.itemSpacingOnBelts);
        ArrayList<Entity> contents = chunk.containedEntitiesByLayer.get(Layer.Regular);
        if (this.root.app.settings.getAllSettings().simplifiedBelts) {
            BeltPath hoveredBeltPath = null;
            Vector mousePos = new Vector(this.root.app.getMousePosition());
            if (this.root.currentLayer == Layer.Regular) {
                Vector tile = this.root.camera.screenToWorld(mousePos).toTileSpace();
                Entity content = this.root.map.getLayerContentXY(tile.x, tile.y, Layer.Regular);
                if (content != null && content.components.Belt != null) {
                    hoveredBeltPath = content.components.Belt.assignedPath;
                }
            }

            for (Entity entity : contents) {
                if (entity.components.Belt != null) {
                    Direction direction = entity.components.Belt.direction;
                    BufferedImage sprite = this.beltAnimations.get(direction).get(0);
                    if (entity.components.Belt.assignedPath == hoveredBeltPath) {
                        sprite = this.beltAnimations.get(direction).get(animationIndex % BELT_ANIM_COUNT);
                    }

                    entity.components.StaticMapEntity.drawSpriteOnBoundsClipped(parameters, sprite, 0);
                }
            }
        }
    }

    private static class SingletonFactory<T> {
        private ArrayList<T> entries;

        private SingletonFactory(ArrayList<T> entries) {
            this.entries = entries;
        }

        public SingletonFactory() {

        }

        public T findByClass(Object classHandle) {
            for (T entry : this.entries) {
                if (entry.getClass() == classHandle.getClass()) {
                    return entry;
                }
            }
            assert false;
            return null;
        }
    }
}
