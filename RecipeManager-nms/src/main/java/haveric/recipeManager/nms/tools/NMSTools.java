package haveric.recipeManager.nms.tools;

import haveric.recipeManagerCommon.RMCVanilla;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class NMSTools {
    public static boolean compareShapedRecipeToMatrix(ShapedRecipe recipe, ItemStack[] matrix, ItemStack[] matrixMirror) {
        ItemStack[] ingredients = convertShapedRecipeToItemMatrix(recipe);

        boolean result = compareItemMatrix(ingredients, matrix);

        if (!result) {
            result = compareItemMatrix(ingredients, matrixMirror);
        }

        return result;
    }

    public static ItemStack[] convertShapedRecipeToItemMatrix(ShapedRecipe bukkitRecipe) {
        Map<Character, ItemStack> items = bukkitRecipe.getIngredientMap();
        ItemStack[] matrix = new ItemStack[9];
        String[] shape = bukkitRecipe.getShape();
        int slot = 0;

        int shapeLength = shape.length;
        for (int r = 0; r < shapeLength; r++) {
            for (char col : shape[r].toCharArray()) {
                matrix[slot] = items.get(col);
                slot++;
            }

            slot = ((r + 1) * 3);
        }

        trimItemMatrix(matrix);

        return matrix;
    }

    public static boolean compareItemMatrix(ItemStack[] ingredients, ItemStack[] matrix) {
        for (int i = 0; i < 9; i++) {
            if (matrix[i] == null && ingredients[i] == null) {
                continue;
            }

            if (matrix[i] == null || ingredients[i] == null || ingredients[i].getType() != matrix[i].getType() || (ingredients[i].getDurability() != RMCVanilla.DATA_WILDCARD && ingredients[i].getDurability() != matrix[i].getDurability())) {
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

    public static boolean compareIngredientList(List<ItemStack> sortedIngr, List<ItemStack> ingredients) {
        int size = ingredients.size();

        if (size != sortedIngr.size()) {
            return false;
        }

        sortIngredientList(ingredients);

        for (int i = 0; i < size; i++) {
            if (!sortedIngr.get(i).equals(ingredients.get(i))) {
                return false;
            }
        }

        return true;
    }

    public static void sortIngredientList(List<ItemStack> ingredients) {
        ingredients.sort(new Comparator<ItemStack>() {
            public int compare(ItemStack item1, ItemStack item2) {
                String id1 = item1.getType().toString();
                String id2 = item2.getType().toString();

                int compare;
                if (id1.equals(id2)) {
                    if (item1.getDurability() > item2.getDurability()) {
                        compare = -1;
                    } else {
                        compare = 1;
                    }
                } else {
                    compare = id1.compareTo(id2);
                }

                return compare;
            }
        });
    }
}
