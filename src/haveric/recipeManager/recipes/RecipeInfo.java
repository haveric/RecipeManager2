package haveric.recipeManager.recipes;

public class RecipeInfo {
    public enum RecipeStatus {
        OVERRIDDEN, REMOVED;
    }

    public enum RecipeOwner {
        MINECRAFT("Minecraft"), RECIPEMANAGER("RecipeManager"), UNKNOWN("Unknown plugin");

        private String name;

        private RecipeOwner(String name) {
            this.name = name;
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

    public RecipeInfo(RecipeOwner owner, String adder) {
        this.owner = owner;
        this.adder = adder;
    }

    public RecipeInfo(RecipeOwner owner, String adder, RecipeStatus status) {
        this.owner = owner;
        this.status = status;
        this.adder = adder;
    }

    public RecipeOwner getOwner() {
        return owner;
    }

    public void setOwner(RecipeOwner owner) {
        this.owner = owner;
    }

    public RecipeStatus getStatus() {
        return status;
    }

    public void setStatus(RecipeStatus status) {
        this.status = status;
    }

    public String getAdder() {
        return adder;
    }

    public void setAdder(String adder) {
        this.adder = adder;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
