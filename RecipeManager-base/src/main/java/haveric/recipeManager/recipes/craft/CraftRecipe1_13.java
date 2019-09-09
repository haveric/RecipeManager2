package haveric.recipeManager.recipes.craft;

import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.PreparableResultRecipe;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.ToolsItem;
import haveric.recipeManager.tools.Version;
import haveric.recipeManagerCommon.RMCChatColor;
import haveric.recipeManagerCommon.recipes.RMCRecipeType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.util.*;
import java.util.Map.Entry;

public class CraftRecipe1_13 extends PreparableResultRecipe {
    private Map<Character, RecipeChoice> ingredientsChoiceMap = new HashMap<>();
    private String[] choiceShape;

    private int width;
    private int height;

    public CraftRecipe1_13() {
    }

    public CraftRecipe1_13(ShapedRecipe recipe) {
        setBukkitRecipe(recipe);
        setChoiceShape(recipe.getShape());
        setIngredientsChoiceMap(recipe);
        setResult(recipe.getResult());
    }

    public CraftRecipe1_13(BaseRecipe recipe) {
        super(recipe);

        if (recipe instanceof CraftRecipe1_13) {
            CraftRecipe1_13 r = (CraftRecipe1_13) recipe;

            if (r.ingredientsChoiceMap.size() > 0) {
                ingredientsChoiceMap.putAll(r.ingredientsChoiceMap);
            }

            choiceShape = r.choiceShape;

            width = r.width;
            height = r.height;
        }
    }

    public CraftRecipe1_13(Flags flags) {
        super(flags);
    }

    private void setIngredientsChoiceMap(ShapedRecipe recipe) {
        ingredientsChoiceMap.clear();
        ingredientsChoiceMap.putAll(recipe.getChoiceMap());

        updateChoiceHash();
    }

    public void setIngredientsChoiceMap(Map<Character, List<Material>> newIngredientsChoiceMap) {
        ingredientsChoiceMap.clear();

        for (Map.Entry<Character, List<Material>> entry : newIngredientsChoiceMap.entrySet()) {
            List<Material> materials = entry.getValue();

            if (materials.size() == 1 && materials.get(0) == Material.AIR) {
                ingredientsChoiceMap.put(entry.getKey(), null);
            } else {
                RecipeChoice.MaterialChoice newMaterialList = new RecipeChoice.MaterialChoice(entry.getValue());
                ingredientsChoiceMap.put(entry.getKey(), newMaterialList);
            }
        }

        updateChoiceHash();
    }

    public Map<Character, RecipeChoice> getIngredientsChoiceMap() {
        return ingredientsChoiceMap;
    }

    public void setChoiceShape(String[] shape) {
        choiceShape = shape;

        width = shape[0].length();
        height = shape.length;
    }

    public String[] getChoiceShape() {
        return choiceShape;
    }

    private void updateChoiceHash() {
        StringBuilder str = new StringBuilder("craft ");
        int shapeSize = choiceShape.length;
        for (int i = 0; i < shapeSize; i++) {
            str.append(choiceShape[i]);

            if (i + 1 < shapeSize) {
                str.append(",");
            }
        }

        for (Entry<Character, RecipeChoice> entry : ingredientsChoiceMap.entrySet()) {
            str.append(" ").append(entry.getKey()).append(":");

            RecipeChoice choice = entry.getValue();
            if (choice instanceof RecipeChoice.MaterialChoice) {
                str.append("material:");
                RecipeChoice.MaterialChoice materialChoice = (RecipeChoice.MaterialChoice) choice;

                List<Material> sorted = new ArrayList<>(materialChoice.getChoices());
                Collections.sort(sorted);

                int materialsSize = sorted.size();
                for (int i = 0; i < materialsSize; i++) {
                    str.append(sorted.get(i).toString());

                    if (i + 1 < materialsSize) {
                        str.append(",");
                    }
                }
            } else if (choice instanceof RecipeChoice.ExactChoice) {
                str.append("exact:");
                RecipeChoice.ExactChoice exactChoice = (RecipeChoice.ExactChoice) choice;

                List<ItemStack> sorted = new ArrayList<>(exactChoice.getChoices());
                sorted.sort(Comparator.comparing(ItemStack::getType));

                int itemsSize = sorted.size();
                for (int i = 0; i < itemsSize; i++) {
                    str.append(sorted.get(i).hashCode());

                    if (i + 1 < itemsSize) {
                        str.append(",");
                    }
                }
            } else {
                str.append("air");
            }
        }

        hash = str.toString().hashCode();
    }

    @Override
    public void resetName() {
        StringBuilder s = new StringBuilder();
        boolean removed = hasFlag(FlagType.REMOVE);

        s.append("shaped ").append(getWidth()).append('x').append(getHeight());

        s.append(" (");

        if (choiceShape != null) {
            int shapeSize = choiceShape.length;
            for (int i = 0; i < shapeSize; i++) {
                s.append(choiceShape[i]);

                if (i + 1 < shapeSize) {
                    s.append(",");
                }
            }
        }

        for (Entry<Character, RecipeChoice> entry : ingredientsChoiceMap.entrySet()) {
            s.append(" ").append(entry.getKey()).append(":");

            RecipeChoice choice = entry.getValue();
            if (choice instanceof RecipeChoice.MaterialChoice) {
                s.append("material:");
                RecipeChoice.MaterialChoice materialChoice = (RecipeChoice.MaterialChoice) choice;
                List<Material> materials = materialChoice.getChoices();
                int materialsSize = materials.size();
                for (int i = 0; i < materialsSize; i++) {
                    s.append(materials.get(i).toString());

                    if (i + 1 < materialsSize) {
                        s.append(",");
                    }
                }
            } else if (choice instanceof RecipeChoice.ExactChoice) {
                s.append("exact:");
                RecipeChoice.ExactChoice exactChoice = (RecipeChoice.ExactChoice) choice;
                List<ItemStack> items = exactChoice.getChoices();

                int itemsSize = items.size();
                for (int i = 0; i < itemsSize; i++) {
                    s.append(items.get(i).getType().toString()).append("-").append(items.get(i).hashCode());

                    if (i + 1 < itemsSize) {
                        s.append(",");
                    }
                }
            } else {
                s.append("air");
            }
        }

        s.append(") to ");
        s.append(getResultsString());

        if (removed) {
            s.append(" [removed recipe]");
        }

        name = s.toString();
        customName = false;
    }

    /**
     * @return Shape width, 1 to 3
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return Shape height, 1 to 3
     */
    public int getHeight() {
        return height;
    }

    @Override
    public ShapedRecipe toBukkitRecipe(boolean vanilla) {
        if (!hasIngredientChoices() || !hasResults()) {
            return null;
        }

        ShapedRecipe bukkitRecipe;
        if (Version.has1_12Support()) {
            if (vanilla) {
                bukkitRecipe = new ShapedRecipe(getNamespacedKey(), getFirstResult());
            } else {
                bukkitRecipe = new ShapedRecipe(getNamespacedKey(), Tools.createItemRecipeId(getFirstResult(), getIndex()));
            }
        } else {
        if (vanilla) {
            bukkitRecipe = new ShapedRecipe(getFirstResult());
        } else {
            bukkitRecipe = new ShapedRecipe(Tools.createItemRecipeId(getFirstResult(), getIndex()));
        }
    }

        bukkitRecipe.shape(choiceShape);

        for (Entry<Character, RecipeChoice> entry : ingredientsChoiceMap.entrySet()) {
            bukkitRecipe.setIngredient(entry.getKey(), entry.getValue());
        }

        return bukkitRecipe;
    }

    public boolean hasIngredientChoices() {
        return !ingredientsChoiceMap.isEmpty();
    }

    @Override
    public boolean isValid() {
        return hasIngredientChoices() && (hasFlag(FlagType.REMOVE) || hasFlag(FlagType.RESTRICT) || hasResults());
    }

    @Override
    public RMCRecipeType getType() {
        return RMCRecipeType.CRAFT;
    }

    @Override
    public String printBookResult(ItemResult result) {
        StringBuilder s = getHeaderResult("shaped", result);

        s.append(Messages.getInstance().parse("recipebook.header.shape")).append('\n');
        s.append(RMCChatColor.GRAY);

        for (String shape : choiceShape) {
            for (char letter : shape.toCharArray()) {
                s.append('[');

                RecipeChoice choice = ingredientsChoiceMap.get(letter);
                if (choice instanceof RecipeChoice.MaterialChoice) {
                    RecipeChoice.MaterialChoice materialChoice = (RecipeChoice.MaterialChoice) choice;
                    List<Material> materials = materialChoice.getChoices();

                    if (materials.size() == 1 && materials.contains(Material.AIR)) {
                        s.append(RMCChatColor.WHITE).append('_');
                    } else {
                        s.append(RMCChatColor.DARK_PURPLE).append(letter);
                    }
                } else if (choice instanceof RecipeChoice.ExactChoice) {
                    RecipeChoice.ExactChoice exactChoice = (RecipeChoice.ExactChoice) choice;
                    List<ItemStack> items = exactChoice.getChoices();

                    if (items.size() == 1 && items.get(0).getType() == Material.AIR) {
                        s.append(RMCChatColor.WHITE).append('_');
                    } else {
                        s.append(RMCChatColor.DARK_PURPLE).append(letter);
                    }
                }

                s.append(RMCChatColor.GRAY).append(']');
            }

            s.append('\n');
        }

        s.append(Messages.getInstance().parse("recipebook.header.ingredients"));

        for (Map.Entry<Character, RecipeChoice> entry : ingredientsChoiceMap.entrySet()) {
            s.append('\n').append(RMCChatColor.DARK_PURPLE).append(entry.getKey()).append(RMCChatColor.GRAY).append(": ");

            // TODO: Check IngredientConditions to get Names

            RecipeChoice choice = entry.getValue();
            if (choice instanceof RecipeChoice.MaterialChoice) {
                RecipeChoice.MaterialChoice materialChoice = (RecipeChoice.MaterialChoice) choice;
                List<Material> materials = materialChoice.getChoices();

                s.append(ToolsItem.printChoice(materials, RMCChatColor.BLACK, RMCChatColor.BLACK));
            } else if (choice instanceof RecipeChoice.ExactChoice) {
                RecipeChoice.ExactChoice exactChoice = (RecipeChoice.ExactChoice) choice;
                List<ItemStack> items = exactChoice.getChoices();

                s.append(ToolsItem.printExactChoice(items, RMCChatColor.BLACK, RMCChatColor.BLACK));
            }
        }

        return s.toString();
    }

    @Override
    public int findItemInIngredients(Material type, Short data) {
        int found = 0;
        Map<Character, RecipeChoice> ingredientChoiceMap = getIngredientsChoiceMap();

        for (Map.Entry<Character, RecipeChoice> entry : ingredientChoiceMap.entrySet()) {
            RecipeChoice choice = entry.getValue();

            if (choice instanceof RecipeChoice.MaterialChoice) {
                RecipeChoice.MaterialChoice materialChoice = (RecipeChoice.MaterialChoice) choice;

                List<Material> materials = materialChoice.getChoices();

                if (materials.contains(type)) {
                    found++;
                    break;
                }
            } else if (choice instanceof RecipeChoice.ExactChoice) {
                RecipeChoice.ExactChoice exactChoice = (RecipeChoice.ExactChoice) choice;
                List<ItemStack> items = exactChoice.getChoices();

                for (ItemStack item : items) {
                    if (item.getType() == type) {
                        found++;
                        break;
                    }
                }
            }
        }

        return found;
    }
}
