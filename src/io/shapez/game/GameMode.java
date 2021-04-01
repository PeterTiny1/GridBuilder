package io.shapez.game;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class GameMode {
    public abstract HashMap<String, UpgradeTiers> getUpgrades();

    public class UpgradeTiers {
        ArrayList<TierRequirement> required;

        public UpgradeTiers(ArrayList<TierRequirement> required) {
            this.required = required;
        }
    }

    public class TierRequirement {
        private final boolean excludePrevious;
        private final UpgradeRequirement[] required;

        public TierRequirement(UpgradeRequirement[] required) {
            this.required = required;
            excludePrevious = false;
        }

        public TierRequirement(UpgradeRequirement[] required, boolean excludePrevious) {
            this.required = required;
            this.excludePrevious = excludePrevious;
        }
    }

    public class UpgradeRequirement {
        String shape;
        int amount;

        public UpgradeRequirement(String shape, int amount) {
            this.shape = shape;
            this.amount = amount;
        }
    }
}
