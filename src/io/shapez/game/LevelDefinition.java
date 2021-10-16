package io.shapez.game;

public class LevelDefinition {
    final String shape;
    final int required;
    final HubGoalReward reward;
    final boolean throughputOnly;

    public LevelDefinition(String shape, int required, HubGoalReward reward, boolean throughputOnly) {
        this.shape = shape;
        this.required = required;
        this.reward = reward;
        this.throughputOnly = throughputOnly;
    }
}
