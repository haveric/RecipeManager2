package haveric.recipeManager.recipes.craft;

import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.PreparableResultRecipe;

public class BaseCraftRecipe extends PreparableResultRecipe {
    public BaseCraftRecipe() {
        super();
    }

    public BaseCraftRecipe(BaseRecipe recipe) {
        super(recipe);
    }

    public BaseCraftRecipe(Flags flags) {
        super(flags);
    }
}
