package io.shapez.game;

public class LevelDefinition {
    String shape;
    int required;
    HubGoalReward reward;
    boolean throughputOnly;

    public LevelDefinition(String shape, int required, HubGoalReward reward, boolean throughputOnly) {
        this.shape = shape;
        this.required = required;
        this.reward = reward;
        this.throughputOnly = throughputOnly;
    }
}
