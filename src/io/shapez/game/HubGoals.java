package io.shapez.game;

import java.util.HashMap;

public class HubGoals {
    private final GameRoot root;
    int level = 1;
    HashMap<String, Integer> gainedRewards = new HashMap<>();
    HashMap<String, Integer> storedShapes = new HashMap<>();
    HashMap<String, Integer> upgradeLevels = new HashMap<>();
    HashMap<String, Integer> upgradeImprovements = new HashMap<>();

    public HubGoals(GameRoot root) {
        this.root = root;
        var upgrades = this.root.gameMode.getUpgrades();
    }
}
