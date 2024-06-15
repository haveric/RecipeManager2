package haveric.recipeManager.flag.conditions;

public class ConditionPotion {
    private Boolean extended;
    private int level = -1;

    public ConditionPotion() {

    }

    public Boolean getExtended() {
        return extended;
    }

    public void setExtended(Boolean extended) {
        this.extended = extended;
    }

    public boolean hasExtended() {
        return extended != null;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean hasLevel() {
        return level > -1;
    }

    @Override
    public int hashCode() {
        String toHash = "ConditionPotion:";

        if (extended != null) {
            toHash += "extended: " + extended;
        }
        toHash += "level: " + level;

        return toHash.hashCode();
    }
}
