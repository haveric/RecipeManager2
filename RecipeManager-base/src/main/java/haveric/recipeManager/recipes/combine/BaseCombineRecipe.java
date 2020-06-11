package haveric.recipeManager.recipes.combine;

import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.PreparableResultRecipe;
import haveric.recipeManager.tools.Tools;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BaseCombineRecipe extends PreparableResultRecipe {
    public BaseCombineRecipe() {
        super();
    }

    public BaseCombineRecipe(BaseRecipe recipe) {
        super(recipe);
    }

    public BaseCombineRecipe(Flags flags) {
        super(flags);
    }

    @Override
    public List<String> getRecipeIndexesForInput(List<ItemStack> ingredients, ItemStack result) {
        List<String> recipeIndexes = new ArrayList<>();
        if (result != null) {
            recipeIndexes.add(Tools.getRecipeIdFromItem(result));
        }

        return recipeIndexes;
    }
}
