package haveric.recipeManager.common.recipes;

public enum RMCRecipeType {
    ANVIL("anvil"),
    BLASTING("blasting"),
    BREW("brew"),
    CAMPFIRE("campfire"),
    CARTOGRAPHY("cartography"),
    COMBINE("combine"),
    COMPOST("compost"),
    CRAFT("craft"),
    FUEL("fuel"),
    GRINDSTONE("grindstone"),
    ITEM("item"),
    SMELT("smelt"),
    SMITHING("smithing"),
    SMOKING("smoking"),
    STONECUTTING("stonecutting"),

    SPECIAL("special");

    private final String directive;

    RMCRecipeType(String newDirective) {
        directive = newDirective;
    }

    public String getDirective() {
        return directive;
    }
}
