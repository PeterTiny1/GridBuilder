package io.shapez.game.savegame;

import java.util.HashMap;
import java.util.Map;

public abstract class BasicSerializableObject {
    public HashMap<String, BaseDataType> getCachedSchema() {
        String id = this.getId();
        HashMap<String, BasicSerializableObject> classnamesCache = new HashMap<>();
        classnamesCache.put(id, this);
        HashMap<String, BaseDataType> globalSchemaCache = new HashMap<>();
        BaseDataType entry = globalSchemaCache.get(id);
        if (entry != null) {
            return new HashMap<>() {{
                put(id, entry);
            }};
        }
        for (Map.Entry<String, BaseDataType> schema : this.getSchema().entrySet()) {
            globalSchemaCache.put(schema.getKey(), schema.getValue());
        }
        return this.getSchema();
    }

    private HashMap<String, BaseDataType> getSchema() {
        return new HashMap<>();
    }

    protected abstract String getId();
}
