package haveric.recipeManager.recipes;

public enum RecipeType {
    ANY(null),
    CRAFT("craft"),
    COMBINE("combine"),
    WORKBENCH(null),
    SMELT("smelt"),
    FUEL("fuel"),
    SPECIAL("special");

    private final String directive;

    private RecipeType(String newDirective) {
        directive = newDirective;
    }

    public String getDirective() {
        return directive;
    }
}
