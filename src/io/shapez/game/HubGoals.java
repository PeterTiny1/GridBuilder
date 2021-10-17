package io.shapez.game;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;

import static io.shapez.util.RandomUtil.hash;

public class HubGoals {
    private final GameRoot root;
    final int level = 1;
    HashMap<String, Integer> gainedRewards = new HashMap<>();
    final HashMap<String, Integer> storedShapes = new HashMap<>();
    final HashMap<String, Integer> upgradeLevels = new HashMap<>();
    final HashMap<String, Integer> upgradeImprovements = new HashMap<>();
    Goal currentGoal;

    public HubGoals(final GameRoot root) throws Exception {
        this.root = root;
        final HashMap<String, GameMode.UpgradeTiers> upgrades = this.root.gameMode.getUpgrades();
        for (final Map.Entry<String, GameMode.UpgradeTiers> entry : upgrades.entrySet()) {
            upgradeLevels.put(entry.getKey(), 0);
            upgradeImprovements.put(entry.getKey(), 1);
        }
        this.computeNextGoal();
    }

    private void computeNextGoal() throws Exception {
        int storyIndex = this.level - 1;
        ArrayList<LevelDefinition> levels = this.root.gameMode.getLevelDefinitions();
        if (storyIndex < levels.size()) {
            LevelDefinition current = levels.get(storyIndex);
            this.currentGoal = new Goal(this.root.shapeDefinitionMgr.getShapeFromShortKey(current.shape), current.required, current.reward, current.throughputOnly);
            return;
        }
        double required = Math.min(200, Math.floor(4 + (this.level - 27) * 0.25));
        this.currentGoal = new Goal(this.computeFreeplayShape(this.level), (int) required, HubGoalReward.NoRewardFreeplay, true);
    }

    private ShapeDefinition computeFreeplayShape(int level) throws Exception {
        int layerCount = Math.max(2, Math.min(level / 25, 4));
        ArrayList<ShapeDefinition.ShapeLayer> layers = new ArrayList<>();
        Random rng = new Random(hash(this.root.map.seed + "/" + level));
        Colors[] colors = this.generateRandomColorSet(rng, level > 35);
        int[][] pickedSymmetry;
        ArrayList<ShapeDefinition.SubShape> availableShapes = new ArrayList<>() {{
            add(ShapeDefinition.SubShape.star);
            add(ShapeDefinition.SubShape.rect);
            add(ShapeDefinition.SubShape.circle);
        }};
        if (rng.nextDouble() > 0.5) {
            pickedSymmetry = new int[][]{
                    {0, 2},
                    {1, 3}
            };
            availableShapes.add(ShapeDefinition.SubShape.windmill);
        } else {
            int[][][] symmetries = {
                    {
                            {0, 3},
                            {1, 2}
                    },
                    {
                            {0, 1},
                            {2, 3}
                    },
                    {
                            {0, 2},
                            {1},
                            {3}
                    },
                    {
                            {1, 3},
                            {0},
                            {2}
                    }
            };
            pickedSymmetry = symmetries[rng.nextInt(symmetries.length)];
        }
        Callable<Colors> randomColor = () -> colors[rng.nextInt(colors.length)];
        Callable<ShapeDefinition.SubShape> randomShape = () -> ShapeDefinition.SubShape.values()[rng.nextInt(ShapeDefinition.SubShape.values().length)];
        boolean anyIsMissingTwo = false;

        for (int i = 0; i < layerCount; i++) {
            ShapeDefinition.ShapeLayer layer = new ShapeDefinition.ShapeLayer(new ShapeDefinition.ShapeLayerItem[]{null, null, null, null});
            for (int[] group : pickedSymmetry) {
                Colors color = randomColor.call();
                ShapeDefinition.SubShape shape = randomShape.call();
                for (int quad : group) {
                    layer.layerItems[quad] = new ShapeDefinition.ShapeLayerItem(shape, color);
                }
            }
            if (level > 75 && rng.nextDouble() > 0.95 && !anyIsMissingTwo) {
                layer.layerItems[rng.nextInt(4)] = null;
                anyIsMissingTwo = true;
            }

            layers.add(layer);
        }
        ShapeDefinition definition = new ShapeDefinition(layers);
        return this.root.shapeDefinitionMgr.registerOrReturnHandle(definition);
    }

    private Colors[] generateRandomColorSet(Random rng, boolean allowUncolored) {
        Colors[] colorWheel = {
                Colors.red,
                Colors.yellow,
                Colors.green,
                Colors.cyan,
                Colors.blue,
                Colors.purple,
                Colors.red,
                Colors.yellow
        };
        ArrayList<Colors> universalColors = new ArrayList<>() {{
            add(Colors.white);
            if (allowUncolored) {
                add(Colors.uncolored);
            }
        }};
        int index = rng.nextInt(colorWheel.length - 1);
        return new Colors[]{
                colorWheel[index],
                colorWheel[index + 1],
                colorWheel[index + 2],
                universalColors.get(rng.nextInt(universalColors.size()))
        };
    }

    public int getBeltBaseSpeed() {
        return GlobalConfig.beltSpeedItemsPerSecond * this.upgradeImprovements.get("belt");
    }

    public int undergroundBeltBaseSpeed() {
        return GlobalConfig.beltSpeedItemsPerSecond * this.upgradeImprovements.get("miner");
    }

    public int getCurrentGoalDelivered() {
        if (this.currentGoal.throughputOnly) {
            return (
                    this.root.productionAnalytics.getCurrentShapeRateRaw(
                            ProductionAnalytics.AnalyticsDataSource.delivered,
                            this.currentGoal.definition
                    )
            );
        }

        return this.getShapesStored(this.currentGoal.definition);
    }

    private int getShapesStored(ShapeDefinition definition) {
        return this.storedShapes.get(definition.getHash());
    }

    public void handleDefinitionDelivered(ShapeDefinition definition) {
        String hash = definition.getHash();
        this.storedShapes.put(hash, (this.storedShapes.get(hash)) + 1);
        this.root.productionAnalytics.onShapeDelivered(definition);
    }

    static class Goal {
        private final ShapeDefinition definition;
        private final int required;
        private final HubGoalReward reward;
        private final boolean throughputOnly;

        public Goal(ShapeDefinition definition, int required, HubGoalReward reward, boolean throughputOnly) {
            this.definition = definition;
            this.required = required;
            this.reward = reward;
            this.throughputOnly = throughputOnly;
        }
    }
}
