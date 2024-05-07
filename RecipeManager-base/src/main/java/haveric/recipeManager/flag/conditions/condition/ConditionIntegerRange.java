package haveric.recipeManager.flag.conditions.condition;

public class ConditionIntegerRange {
    private final int min;
    private final int max;

    public ConditionIntegerRange(int value) {
        this.min = value;
        this.max = value;
    }

    public ConditionIntegerRange(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public boolean isInRange(int value) {
        return value >= min && value <= max;
    }

    public String getHashString() {
        if (this.min == this.max) {
            return "" + this.min;
        }

        return this.min + "-" + this.max;
    }
}
