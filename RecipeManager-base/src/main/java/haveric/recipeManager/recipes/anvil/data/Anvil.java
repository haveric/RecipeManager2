package haveric.recipeManager.recipes.anvil.data;

import haveric.recipeManager.data.BaseRecipeData;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.anvil.AnvilRecipe1_13;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Anvil extends BaseRecipeData {
    private String renameText;

    public Anvil(AnvilRecipe1_13 recipe, List<ItemStack> ingredients, ItemResult result, String renameText) {
        super(recipe, ingredients, result);
        this.renameText = renameText;
    }

    public String getRenameText() {
        return renameText;
    }
}
