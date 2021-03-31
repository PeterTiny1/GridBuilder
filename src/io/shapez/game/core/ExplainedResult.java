package io.shapez.game.core;

public class ExplainedResult {
    private final boolean result;

    public ExplainedResult(boolean result, String reason) {
        this.result = result;
    }

    public ExplainedResult(boolean result) {
        this.result = result;
    }

    public static ExplainedResult bad(String reason) {
        return new ExplainedResult(false, reason);
    }


    public static ExplainedResult good() {
        return new ExplainedResult(true);
    }

    public boolean isGood() {
        return this.result;
    }
}
