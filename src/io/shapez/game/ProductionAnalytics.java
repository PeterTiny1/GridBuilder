package io.shapez.game;


import java.util.ArrayList;
import java.util.HashMap;

public class ProductionAnalytics {
    final HashMap<AnalyticsDataSource, ArrayList<Integer>> history = new HashMap<>() {{
        put(AnalyticsDataSource.produced, new ArrayList<>());
        put(AnalyticsDataSource.stored, new ArrayList<>());
        put(AnalyticsDataSource.delivered, new ArrayList<>());
    }};
    private final GameRoot root;

    public ProductionAnalytics(GameRoot root) {
        this.root = root;
        for (int i = 0; i < GlobalConfig.statisticsGraphSlices; i++) {
            this.startNewSlice();
        }
    }

    private void startNewSlice() {
        for (ArrayList<Integer> integers : this.history.values()) {
//            if (key == AnalyticsDataSource.stored) { FIXME: I don't understand how the JavaScript for this works
//                this.history.get(key).add(this.root.hubGoals.storedShapes);
//            } else {
//                this.history.get(key).add({});
//            }
            while (integers.size() > GlobalConfig.statisticsGraphSlices) {
                integers.remove(0);
            }
        }
    }

    public int getCurrentShapeRateRaw(AnalyticsDataSource dataSource, ShapeDefinition definition) {
        ArrayList<Integer> slices = this.history.get(dataSource);
        return slices.get(slices.size() - 2);
    }

    public void onShapeDelivered(ShapeDefinition definition) {
        String key = definition.getHash();
        ArrayList<Integer> entry = this.history.get(AnalyticsDataSource.delivered);
        entry.set(entry.size() - 1, (entry.get(entry.size() - 1)) + 1);
    }

    enum AnalyticsDataSource {
        produced,
        stored,
        delivered
    }
}
