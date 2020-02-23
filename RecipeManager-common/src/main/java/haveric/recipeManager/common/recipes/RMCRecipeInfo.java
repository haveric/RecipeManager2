package haveric.recipeManager.common.recipes;

public class RMCRecipeInfo {
    public enum RecipeStatus {
        OVERRIDDEN, REMOVED
    }

    public enum RecipeOwner {
        MINECRAFT("Minecraft"),
        RECIPEMANAGER("RecipeManager"),
        UNKNOWN("Unknown plugin");

        private String name;

        RecipeOwner(String newName) {
            name = newName;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private RecipeOwner owner;
    private String adder;
    private RecipeStatus status;
    private int index;

    public RMCRecipeInfo(RecipeOwner newOwner, String newAdder) {
        owner = newOwner;
        adder = newAdder;
    }

    public RMCRecipeInfo(RecipeOwner newOwner, String newAdder, RecipeStatus newStatus) {
        owner = newOwner;
        status = newStatus;
        adder = newAdder;
    }

    public RecipeOwner getOwner() {
        return owner;
    }

    public void setOwner(RecipeOwner newOwner) {
        owner = newOwner;
    }

    public RecipeStatus getStatus() {
        return status;
    }

    public void setStatus(RecipeStatus newStatus) {
        status = newStatus;
    }

    public String getAdder() {
        return adder;
    }

    public void setAdder(String newAdder) {
        adder = newAdder;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int newIndex) {
        index = newIndex;
    }
}
