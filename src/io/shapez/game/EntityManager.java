package io.shapez.game;

import io.shapez.game.savegame.BasicSerializableObject;

import java.util.ArrayList;

public class EntityManager extends BasicSerializableObject {
    private final GameRoot root;
    final ArrayList<Entity> entities = new ArrayList<>();
    ArrayList<Entity> destroyList = new ArrayList<>();
    // --Commented out by Inspection (17/10/2021, 15:02):HashMap<String, ArrayList<Entity>> componentToEntity = new HashMap<>();
    int nextUid = 10000;

    public EntityManager(final GameRoot root) {
        this.root = root;
    }

    @Override
    protected String getId() {
        return null;
    }

    public void registerEntity(final Entity entity) {
        this.entities.add(entity);

        entity.uid = this.generateUid();
    }

    private int generateUid() {
        return this.nextUid++;
    }

    public void update() {
        this.processDestroyList();
    }

    private void processDestroyList() {
        for (final Entity entity : this.destroyList) {
            this.entities.remove(entity);
//            this.unregisterEntityComponents(entity);
        }
        this.destroyList = new ArrayList<>();
    }

//    private void unregisterEntityComponents(Entity entity) {
////        for (componentId : entity.components) {
////            if (entity.components.get())
////        }
//    }
}
