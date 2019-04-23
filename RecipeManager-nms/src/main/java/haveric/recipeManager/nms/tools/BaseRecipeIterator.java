package haveric.recipeManager.nms.tools;

import haveric.recipeManagerCommon.recipes.AbstractBaseRecipe;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.Iterator;

public abstract class BaseRecipeIterator {
    public void replace(AbstractBaseRecipe recipe, ItemStack overrideItem) {

    }

    public void finish() {

    }

    public Iterator<Recipe> getIterator() {
        return getBukkitRecipeIterator();
    }

    public Iterator<Recipe> getBukkitRecipeIterator() {
        return Bukkit.recipeIterator();
    }
}
