package haveric.recipeManager.tools;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.util.List;
import java.util.Map;

public class RMBukkitTools {
    public static boolean compareShapedRecipeToChoice(ShapedRecipe recipe, String[] shape, Map<Character, RecipeChoice> choiceMap) {
        String[] recipeShape = recipe.getShape();
        if (recipeShape.length != shape.length) {
            return false;
        }

        for (int i = 0; i < shape.length; i++) {
            if (!recipeShape[i].equals(shape[i])) {
                return false;
            }
        }

        Map<Character, RecipeChoice> recipeChoiceMap = recipe.getChoiceMap();

        if (recipeChoiceMap.size() != choiceMap.size()) {
            return false;
        }

        for (Map.Entry<Character, RecipeChoice> entry : recipeChoiceMap.entrySet()) {
            Character character = entry.getKey();

            if (!choiceMap.containsKey(character)) {
                return false;
            }

            if (!compareMaterialChoice(entry.getValue(), choiceMap.get(character), true)) {
                return false;
            }
        }

        return true;
    }

    public static void trimItemMatrix(ItemStack[] matrix) {
        while (matrix[0] == null && matrix[1] == null && matrix[2] == null) {
            matrix[0] = matrix[3];
            matrix[1] = matrix[4];
            matrix[2] = matrix[5];

            matrix[3] = matrix[6];
            matrix[4] = matrix[7];
            matrix[5] = matrix[8];

            matrix[6] = null;
            matrix[7] = null;
            matrix[8] = null;
        }

        while (matrix[0] == null && matrix[3] == null && matrix[6] == null) {
            matrix[0] = matrix[1];
            matrix[3] = matrix[4];
            matrix[6] = matrix[7];

            matrix[1] = matrix[2];
            matrix[4] = matrix[5];
            matrix[7] = matrix[8];

            matrix[2] = null;
            matrix[5] = null;
            matrix[8] = null;
        }
    }

    public static boolean compareShapelessChoiceList(List<RecipeChoice> materialChoices, List<List<Material>> materialsList) {
        int listSize = materialsList.size();
        int choicesListSize = materialChoices.size();

        if (listSize != choicesListSize) {
            return false;
        }

        for (int i = 0; i < listSize; i++) {
            if (!compareShapelessChoice(materialChoices.get(i), materialsList.get(i), false)) {
                return false;
            }
        }

        return true;
    }

    private static boolean compareShapelessChoice(RecipeChoice choice, List<Material> materials, boolean airAllowed) {
        if (choice instanceof RecipeChoice.MaterialChoice materialChoice) {
            List<Material> choiceMaterials = materialChoice.getChoices();

            int size = materials.size();
            int choiceSize = choiceMaterials.size();

            if (size != choiceSize) {
                return false;
            }

            for (int j = 0; j < size; j++) {
                if (!materials.contains(choiceMaterials.get(j))) {
                    return false;
                }
            }
        } else if (choice instanceof RecipeChoice.ExactChoice exactChoice) {
            List<ItemStack> choiceMaterials = exactChoice.getChoices();

            int size = materials.size();
            int choiceSize = choiceMaterials.size();

            if (size != choiceSize) {
                return false;
            }

            for (int j = 0; j < size; j++) {
                if (!materials.contains(choiceMaterials.get(j).getType())) {
                    return false;
                }
            }
        } else {
            if (!airAllowed || materials.size() != 1 || materials.get(0) != Material.AIR) {
                return false;
            }
        }

        return true;
    }

    private static boolean compareMaterialChoice(RecipeChoice choice, RecipeChoice choice2, boolean airAllowed) {
        if (choice instanceof RecipeChoice.MaterialChoice materialChoice) {
            List<Material> choiceMaterials = materialChoice.getChoices();

            if (!(choice2 instanceof RecipeChoice.MaterialChoice materialChoice2)) {
                return false;
            }

            List<Material> choice2Materials = materialChoice2.getChoices();
            int size = choice2Materials.size();
            int choiceSize = choiceMaterials.size();

            if (size != choiceSize) {
                return false;
            }

            for (int j = 0; j < size; j++) {
                if (!choice2Materials.contains(choiceMaterials.get(j))) {
                    return false;
                }
            }
        } else if (choice instanceof RecipeChoice.ExactChoice exactChoice) {
            List<ItemStack> choiceMaterials = exactChoice.getChoices();

            if (!(choice2 instanceof RecipeChoice.ExactChoice exactChoice2)) {
                return false;
            }

            List<ItemStack> choice2Materials = exactChoice2.getChoices();

            int size = choice2Materials.size();
            int choiceSize = choiceMaterials.size();

            if (size != choiceSize) {
                return false;
            }

            for (ItemStack item1 : choiceMaterials) {
                boolean foundMatch = false;
                for (ItemStack item2 : choice2Materials) {
                    if (item1.hashCode() == item2.hashCode()) {
                        foundMatch = true;
                        break;
                    }
                }

                if (!foundMatch) {
                    return false;
                }
            }
        } else {
            if (!airAllowed) {
                return false;
            }

            if (choice == null) {
                if (choice2 == null) {
                    return true;
                } else if (choice2 instanceof RecipeChoice.MaterialChoice materialChoice) {

                    List<Material> materials = materialChoice.getChoices();

                    return materials.size() == 1 && materials.contains(Material.AIR);
                }
            }

            return false;
        }

        return true;
    }

    public static boolean isSameItemPlusDur(ItemStack one, ItemStack two) {
        boolean same = false;

        if (one != null && two != null) {
            boolean sameType = one.getType() == two.getType();
            boolean sameDur = one.getDurability() == two.getDurability();
            boolean negativeDur = (one.getDurability() == Short.MAX_VALUE) || (two.getDurability() == Short.MAX_VALUE);

            if (sameType && (sameDur || negativeDur)) {
                same = true;
            }
        }
        return same;
    }

    public static boolean isSameItemFromChoice(RecipeChoice choice, ItemStack item) {
        if (item != null) {
            Material type = item.getType();

            if (choice instanceof RecipeChoice.MaterialChoice) {
                return ((RecipeChoice.MaterialChoice) choice).getChoices().contains(type);
            }
        }

        return false;
    }
}
