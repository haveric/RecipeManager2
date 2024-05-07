package haveric.recipeManager.tools;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Recipe;

import java.util.Iterator;

public class RecipeIterator {
    public Iterator<Recipe> getIterator() {
        return getBukkitRecipeIterator();
    }

    protected Iterator<Recipe> getBukkitRecipeIterator() {
        return Bukkit.recipeIterator();
    }
}
