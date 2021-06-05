package io.shapez.game;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class GameMode {
    public abstract HashMap<String, UpgradeTiers> getUpgrades();

    public class UpgradeTiers {
        final ArrayList<TierRequirement> required;

        public UpgradeTiers(final ArrayList<TierRequirement> required) {
            this.required = required;
        }
    }

    public static class TierRequirement {
        private final boolean excludePrevious;
        private final UpgradeRequirement[] required;

        public TierRequirement(final UpgradeRequirement[] required) {
            this.required = required;
            excludePrevious = false;
        }

        public TierRequirement(final UpgradeRequirement[] required, final boolean excludePrevious) {
            this.required = required;
            this.excludePrevious = excludePrevious;
        }
    }

    public static class UpgradeRequirement {
        final String shape;
        final int amount;

        public UpgradeRequirement(final String shape, final int amount) {
            this.shape = shape;
            this.amount = amount;
        }
    }
}
