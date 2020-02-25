package haveric.recipeManager.recipes.compost.data;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Recipes;
import haveric.recipeManager.common.recipes.RMCRecipeType;
import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.compost.CompostRecipe;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@SerializableAs("RM_ComposterData")
public class ComposterData implements ConfigurationSerializable {
    static {
        ConfigurationSerialization.registerClass(ComposterData.class, "RM_ComposterData");
    }

    private UUID playerUUID = null;
    private List<ItemStack> ingredients = null;
    private double level = 0;

    private static final String ID_PLAYER_UUID = "playerUUID";
    private static final String ID_NUM_INGREDIENTS = "numingredients";
    private static final String ID_INGREDIENT = "ingredient";
    private static final String ID_LEVEL = "level";

    public static void init() {

    }

    public ComposterData() {

    }
    @SuppressWarnings("unchecked")
    public ComposterData(Map<String, Object> map) {
        try {
            Object obj;

            obj = map.get(ID_PLAYER_UUID);
            if (obj instanceof String) {
                playerUUID = UUID.fromString((String) obj);
            }

            int numIngredients = 0;
            obj = map.get(ID_NUM_INGREDIENTS);
            if (obj instanceof Integer) {
                numIngredients = (Integer)obj;
            }

            ingredients = new ArrayList<>();
            for (int i = 0; i < numIngredients; i++) {
                obj = map.get(ID_INGREDIENT + i);
                if (obj instanceof Map) {
                    ingredients.add(ItemStack.deserialize((Map<String, Object>) obj));
                }
            }

            obj = map.get(ID_LEVEL);
            if (obj instanceof String) {
                try {
                    level = Double.parseDouble((String) obj);
                } catch (NumberFormatException e) {
                    ErrorReporter.getInstance().error("Invalid composter level data: " + obj.toString());
                }
            }
        } catch (Throwable e) {
            MessageSender.getInstance().error(null, e, null);
        }
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        if (playerUUID != null) {
            map.put(ID_PLAYER_UUID, playerUUID.toString());
        }

        if (ingredients != null) {
            for (int i = 0; i < ingredients.size(); i++) {
                map.put(ID_INGREDIENT + i, ingredients.get(i).serialize());
            }

            map.put(ID_NUM_INGREDIENTS, ingredients.size());
        }

        if (level != 0) {
            map.put(ID_LEVEL, Double.toString(level));
        }

        return map;
    }
    public static ComposterData deserialize(Map<String, Object> map) {
        return new ComposterData(map);
    }

    public static ComposterData valueOf(Map<String, Object> map) {
        return new ComposterData(map);
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public void setPlayerUUID(UUID newplayerUUID) {
        playerUUID = newplayerUUID;
    }

    public List<ItemStack> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<ItemStack> items) {
        ingredients = items;
    }

    public void clearIngredients() {
        if (ingredients != null) {
            ingredients.clear();
        }

        level = 0;
    }

    public void addIngredient(ItemStack item) {
        if (ingredients == null) {
            ingredients = new ArrayList<>();
        }

        boolean found = false;
        for (ItemStack ingredient : ingredients) {
            ItemStack ingredientClone = ingredient.clone();
            ingredientClone.setAmount(1);
            if (ingredientClone.hashCode() == item.hashCode()) {
                if (ingredient.getAmount() < 64) {
                    found = true;
                    ingredient.setAmount(ingredient.getAmount() + 1);
                }
                break;
            }
        }

        if (!found) {
            ingredients.add(item);
        }
    }

    public void setLevel(int newLevel) {
        level = newLevel;
    }

    public double getLevel() {
        return level;
    }

    public void addToLevel(double toAdd) {
        level += toAdd;
    }

    public CompostRecipe getRecipe() {
        CompostRecipe recipe = null;

        if (ingredients != null && ingredients.size() > 0) {
            BaseRecipe baseRecipe = Recipes.getInstance().getRecipe(RMCRecipeType.COMPOST, ingredients.get(0));
            if (baseRecipe instanceof CompostRecipe) {
                recipe = (CompostRecipe) baseRecipe;
            }
        }

        return recipe;
    }
}
