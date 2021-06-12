package haveric.recipeManager.recipes.item;

import haveric.recipeManager.common.recipes.RMCRecipeType;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.SingleResultRecipe;

import java.util.HashMap;
import java.util.Map;

public class ItemRecipe extends SingleResultRecipe {
    public static Map<String, ItemRecipe> itemRecipes = new HashMap<>();

    public ItemRecipe() {

    }

    public ItemRecipe(BaseRecipe recipe) {
        super(recipe);
    }


    @Override
    public RMCRecipeType getType() {
        return RMCRecipeType.ITEM;
    }


    @Override
    public void resetName() {
        name = "item " + getResultString();
        customName = false;
    }

    public void addRecipe(String name) {
        itemRecipes.put(name, this);
    }

    public static void clearRecipes() {
        itemRecipes.clear();
    }

    public static ItemRecipe getRecipe(String name) {
        if (itemRecipes.containsKey(name)) {
            return itemRecipes.get(name);
        }

        return null;
    }
}
