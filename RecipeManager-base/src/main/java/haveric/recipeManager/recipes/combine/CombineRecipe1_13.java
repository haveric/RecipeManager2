package haveric.recipeManager.recipes.combine;

import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.ToolsItem;
import haveric.recipeManager.common.RMCChatColor;
import haveric.recipeManager.common.recipes.RMCRecipeType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CombineRecipe1_13 extends CombineRecipe {
    private List<List<Material>> ingredientChoiceList = new ArrayList<>();

    public CombineRecipe1_13() {
    }

    public CombineRecipe1_13(ShapelessRecipe recipe) {
        setIngredientChoice(recipe.getChoiceList());

        setResult(recipe.getResult());
    }

    public CombineRecipe1_13(BaseRecipe recipe) {
        super(recipe);

        if (recipe instanceof CombineRecipe1_13) {
            CombineRecipe1_13 r = (CombineRecipe1_13) recipe;

            if (!r.ingredientChoiceList.isEmpty()) {
                for (List<Material> ingredientChoice : r.ingredientChoiceList) {
                    ArrayList<Material> cloneList = new ArrayList<>(ingredientChoice);
                    ingredientChoiceList.add(cloneList);
                }
            }
        }
    }

    public CombineRecipe1_13(Flags flags) {
        super(flags);
    }

    /**
     * @return clone of ingredients list
     */
    public List<ItemStack> getIngredients() {
        return null; // TODO: 1.13 doesn't use this, can we remove?
    }

    public void addIngredient(int amount, Material type, short data) {
        // TODO: 1.13 doesn't use this, can we remove?
    }

    public void setIngredients(List<ItemStack> newIngredients) {
        // TODO: 1.13 doesn't use this, can we remove?
    }

    public List<List<Material>> getIngredientChoiceList() {
        return ingredientChoiceList;
    }

    public void addIngredientChoice(RecipeChoice choice) {
        if (choice instanceof RecipeChoice.MaterialChoice) {
            RecipeChoice.MaterialChoice materialChoice = (RecipeChoice.MaterialChoice) choice;

            List<Material> choices = materialChoice.getChoices();
            ingredientChoiceList.add(choices);
        }

        updateHash();
    }

    public void setIngredientChoice(List<RecipeChoice> recipeChoices) {
        ingredientChoiceList.clear();

        for (RecipeChoice choice : recipeChoices) {
            addIngredientChoice(choice);
        }

        updateHash();
    }

    public void setIngredientChoiceList(List<List<Material>> recipeChoices) {
        ingredientChoiceList.clear();

        ingredientChoiceList.addAll(recipeChoices);

        updateHash();
    }

    private void updateHash() {
        StringBuilder str = new StringBuilder("combine");

        int size = ingredientChoiceList.size();
        for (int i = 0; i < size; i++) {
            List<Material> ingredientChoice = ingredientChoiceList.get(i);

            List<Material> sorted = new ArrayList<>(ingredientChoice);
            Collections.sort(sorted);

            for (Material material : sorted) {
                str.append(material.toString()).append(';');
            }

            if (i + 1 < size) {
                str.append(",");
            }
        }

        hash = str.toString().hashCode();
    }

    @Override
    public void resetName() {
        StringBuilder s = new StringBuilder();
        boolean removed = hasFlag(FlagType.REMOVE);

        s.append("shapeless");
        s.append(" (");

        int ingredientChoiceListSize = ingredientChoiceList.size();
        for (int i = 0; i < ingredientChoiceListSize; i++) {
            List<Material> ingredientChoice = ingredientChoiceList.get(i);

            int ingredientChoiceSize = ingredientChoice.size();
            for (int j = 0; j < ingredientChoiceSize; j++) {
                s.append(ingredientChoice.get(j).toString().toLowerCase());

                if (j + 1 < ingredientChoiceSize) {
                    s.append(",");
                }
            }

            if (i + 1 < ingredientChoiceListSize) {
                s.append(" ");
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

    @Override
    public ShapelessRecipe toBukkitRecipe(boolean vanilla) {
        if (!hasIngredientChoices() || !hasResults()) {
            return null;
        }

        ShapelessRecipe bukkitRecipe;
        if (vanilla) {
            bukkitRecipe = new ShapelessRecipe(getNamespacedKey(), getFirstResult());
        } else {
            bukkitRecipe = new ShapelessRecipe(getNamespacedKey(), Tools.createItemRecipeId(getFirstResult(), hashCode()));
        }

        for (List<Material> materialChoice : ingredientChoiceList) {
            bukkitRecipe.addIngredient(new RecipeChoice.MaterialChoice(materialChoice));
        }

        return bukkitRecipe;
    }

    public boolean hasIngredientChoices() {
        return ingredientChoiceList != null && !ingredientChoiceList.isEmpty();
    }

    @Override
    public boolean isValid() {
        return hasIngredientChoices() && (hasFlag(FlagType.REMOVE) || hasFlag(FlagType.RESTRICT) || hasResults());
    }

    @Override
    public RMCRecipeType getType() {
        return RMCRecipeType.COMBINE;
    }

    @Override
    public String printBookResult(ItemResult result) {
        StringBuilder s = getHeaderResult("shapeless", result);

        s.append(Messages.getInstance().parse("recipebook.header.ingredients"));

        for (List<Material> materials : ingredientChoiceList) {
            // TODO: Check IngredientConditions to get Names

            s.append('\n').append(ToolsItem.printChoice(materials, RMCChatColor.BLACK, RMCChatColor.BLACK));
        }

        return s.toString();
    }

    @Override
    public int findItemInIngredients(Material type, Short data) {
        int found = 0;

        for (List<Material> materials : ingredientChoiceList) {
            if (materials.contains(type)) {
                found++;
                break;
            }
        }

        return found;
    }
}
