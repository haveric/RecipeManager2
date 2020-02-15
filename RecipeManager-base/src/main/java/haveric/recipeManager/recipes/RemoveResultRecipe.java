package haveric.recipeManager.recipes;

import haveric.recipeManager.RecipeManager;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo.RecipeOwner;
import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class RemoveResultRecipe extends BaseRecipe {
    private Map<BaseRecipe, RMCRecipeInfo> removed;
    private ItemStack result;

    public RemoveResultRecipe() {

    }

    public RemoveResultRecipe(ItemStack newResult) {
        Validate.notNull(newResult, "Result must not be null!");

        result = newResult;
    }

    /**
     * @return copy of the result item
     */
    public ItemStack getResult() {
        return result.clone();
    }

    // TODO trigger this somewhere..
    public void apply() {
        if (removed != null) {
            throw new IllegalAccessError("Recipe has already been applied!");
        }

        removed = new HashMap<>();

        for (Entry<BaseRecipe, RMCRecipeInfo> e : RecipeManager.getRecipes().getRecipeList().entrySet()) {
            BaseRecipe recipe = e.getKey();
            RMCRecipeInfo info = e.getValue();

            if (info.getOwner() != RecipeOwner.RECIPEMANAGER) {
                ItemStack resultItem = null;

                if (recipe instanceof MultiResultRecipe) {
                    MultiResultRecipe r = (MultiResultRecipe) recipe;
                    resultItem = r.getFirstResult();
                }

                // TODO if smeltrecipe gets back to single result
                /*
                 * else if(recipe instanceof SmeltRecipe) { SmeltRecipe r = (SmeltRecipe)recipe;
                 *
                 * result = r.getResult(); }
                 */

                if (resultItem != null && resultItem.equals(result)) {
                    removeRecipe(recipe, info);
                }
            }
        }
    }

    private void removeRecipe(BaseRecipe recipe, RMCRecipeInfo info) {
        if (removed != null) {
            removed.put(recipe, info);
            recipe.remove();
        }
    }

    @Override
    public Recipe remove() {
        if (removed == null) {
            throw new IllegalAccessError("Recipe has not been applied, therefore it can't be undone.");
        }

        for (Entry<BaseRecipe, RMCRecipeInfo> e : removed.entrySet()) {
            RecipeManager.getRecipes().registerRecipe(e.getKey(), e.getValue());
        }

        return super.remove();
    }
}
