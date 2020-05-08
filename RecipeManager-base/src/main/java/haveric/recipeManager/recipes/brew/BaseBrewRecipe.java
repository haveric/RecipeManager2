package haveric.recipeManager.recipes.brew;

import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.MultiResultRecipe;

public class BaseBrewRecipe extends MultiResultRecipe {
    public BaseBrewRecipe() {

    }

    public BaseBrewRecipe(BaseRecipe recipe) {
        super(recipe);
    }

    public BaseBrewRecipe(Flags flags) {
        super(flags);
    }
}
