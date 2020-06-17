package haveric.recipeManager.tools;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.meta.ItemMeta;

public class ToolsRecipeChoice {

    public static boolean isValidMetaType(RecipeChoice choice, Class<?> metaClass) {
        if (choice instanceof RecipeChoice.MaterialChoice) {
            RecipeChoice.MaterialChoice materialChoice = (RecipeChoice.MaterialChoice) choice;

            int numMatches = 0;
            int total = materialChoice.getChoices().size();
            for (Material material : materialChoice.getChoices()) {
                ItemStack item = new ItemStack(material);
                ItemMeta meta = item.getItemMeta();
                if (meta != null && metaClass.isAssignableFrom(meta.getClass())) {
                    numMatches ++;
                }
            }

            return numMatches == total;
        } else if (choice instanceof RecipeChoice.ExactChoice) {
            RecipeChoice.ExactChoice exactChoice = (RecipeChoice.ExactChoice) choice;

            int numMatches = 0;
            int total = exactChoice.getChoices().size();
            for (ItemStack item : exactChoice.getChoices()) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null && metaClass.isAssignableFrom(meta.getClass())) {
                    numMatches ++;
                }
            }

            return numMatches == total;
        }

        return false;
    }
}
