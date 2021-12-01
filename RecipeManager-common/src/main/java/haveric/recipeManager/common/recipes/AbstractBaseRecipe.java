package haveric.recipeManager.common.recipes;

import haveric.recipeManager.common.RMCChatColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractBaseRecipe {
    protected String name;
    protected boolean customName;
    protected int hash;

    public AbstractBaseRecipe() { }

    public AbstractBaseRecipe(AbstractBaseRecipe newRecipe) {
        name = newRecipe.name;
        customName = newRecipe.customName;
        hash = newRecipe.hash;
    }

    public abstract RMCRecipeInfo getInfo();

    public RMCRecipeType getType() {
        return null;
    }

    /**
     * Returns the auto-generated name or the custom name (if set) of the recipe.
     *
     * @return recipe name, never null.
     */
    public String getName() {
        if (name == null) {
            resetName();
        }

        return name;
    }

    public abstract void setName(String newName);

    /**
     * @return true if recipe has custom name or false if it's auto-generated.
     */
    public boolean hasCustomName() {
        return customName;
    }

    /**
     * Reset name to the auto-generated one.
     */
    public void resetName() {
        name = "unknown recipe";
        customName = false;
    }

    public boolean isValid() {
        return false; // empty recipe, invalid!
    }

    public String getInvalidErrorMessage() {
        String directive = getType().getDirective();
        return directive.substring(0, 1).toUpperCase() + directive.substring(1) + " Recipe " + getName() + " is invalid!";
    }

    public List<String> getIndexes() {
        return Collections.singletonList(String.valueOf(hash));
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof AbstractBaseRecipe)) {
            return false;
        }

        return obj.hashCode() == hashCode();
    }


    /**
     * @return Recipe short string for book contents index
     */
    public List<String> printBookIndices() {
        List<String> indices = new ArrayList<>();
        indices.add(RMCChatColor.RED + "(undefined)");

        return indices;
    }

    /**
     * @return Recipe detail string that can fit inside a book.
     */
    public List<String> printBookRecipes() {
        List<String> recipes = new ArrayList<>();
        recipes.add(RMCChatColor.RED + "(undefined)");
        return recipes;
    }

    /**
     * @return Recipe detail string that can fit in the chat.
     */
    public List<String> printChat() { // TODO: Verify works
        List<String> recipes = printBookRecipes();

        for (int i = 0; i < recipes.size(); i++) {
            String recipe = recipes.get(i);
            recipe = recipe.replace(RMCChatColor.WHITE.toString(), RMCChatColor.MAGIC.toString());
            recipe = recipe.replace(RMCChatColor.BLACK.toString(), RMCChatColor.WHITE.toString());
            recipe = recipe.replace(RMCChatColor.MAGIC.toString(), RMCChatColor.BLACK.toString());
            recipes.set(i, recipe);
        }

        return recipes;
    }
}
