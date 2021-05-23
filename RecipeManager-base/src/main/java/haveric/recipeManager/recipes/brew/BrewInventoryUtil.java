package haveric.recipeManager.recipes.brew;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class BrewInventoryUtil {
    private static final List<Material> ingredients = new ArrayList<>();
    private static final List<Material> potions = new ArrayList<>();
    private static final List<Material> results = new ArrayList<>();

    private static boolean hasCustomItems = false;

    public static void init() {
    }

    public static void clean() {
        ingredients.clear();
        potions.clear();
        results.clear();
        hasCustomItems = false;
    }

    public static void addIngredients(List<Material> materials) {
        for (Material material : materials) {
            addIngredient(material);
        }
    }

    public static void addIngredient(Material material) {
        if (!ingredients.contains(material)) {
            ingredients.add(material);
            hasCustomItems = true;
        }
    }

    public static void addPotions(List<Material> materials) {
        for (Material material : materials) {
            addPotion(material);
        }
    }

    public static void addPotion(Material material) {
        if (!potions.contains(material)) {
            potions.add(material);
            hasCustomItems = true;
        }
    }

    public static void addResults(List<Material> materials) {
        for (Material material : materials) {
            addResult(material);
        }
    }

    public static void addResult(Material material) {
        if (!results.contains(material)) {
            results.add(material);
            hasCustomItems = true;
        }
    }

    public static boolean isIngredient(Material material) {
        return ingredients.contains(material);
    }

    public static boolean isPotion(Material material) {
        return potions.contains(material);
    }

    public static boolean isResult(Material material) {
        return results.contains(material);
    }

    public static boolean isPotionOrResult(Material material) {
        return isPotion(material) || isResult(material);
    }

    public static boolean hasCustomItems() {
        return hasCustomItems;
    }
}
