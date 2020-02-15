package haveric.recipeManager.recipes.stonecutting;

import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.SingleResultRecipe;
import haveric.recipeManager.tools.ToolsItem;
import haveric.recipeManagerCommon.RMCChatColor;
import haveric.recipeManagerCommon.recipes.RMCRecipeType;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.StonecuttingRecipe;

import java.util.ArrayList;
import java.util.List;

public class RMStonecuttingRecipe extends SingleResultRecipe {
    private List<Material> ingredientChoice = new ArrayList<>();

    public RMStonecuttingRecipe() {

    }

    public RMStonecuttingRecipe(BaseRecipe recipe) {
        super(recipe);

        if (recipe instanceof RMStonecuttingRecipe) {
            RMStonecuttingRecipe r = (RMStonecuttingRecipe) recipe;

            if (r.ingredientChoice == null) {
                ingredientChoice = null;
            } else {
                ingredientChoice.addAll(r.ingredientChoice);
            }

            hash = r.hash;
        }
    }

    public RMStonecuttingRecipe(Flags flags) {
        super(flags);
    }

    public RMStonecuttingRecipe(StonecuttingRecipe recipe) {
        RecipeChoice choice = recipe.getInputChoice();
        if (choice instanceof RecipeChoice.MaterialChoice) {
            RecipeChoice.MaterialChoice materialChoice = (RecipeChoice.MaterialChoice) choice;

            setIngredientChoice(materialChoice.getChoices());
        }

        setResult(recipe.getResult());
    }

    public List<Material> getIngredientChoice() {
        return ingredientChoice;
    }

    public void setIngredientChoice(List<Material> materials) {
        RecipeChoice.MaterialChoice materialChoice = new RecipeChoice.MaterialChoice(materials);
        setIngredientChoice(materialChoice);
    }

    private void setIngredientChoice(RecipeChoice choice) {
        if (choice instanceof RecipeChoice.MaterialChoice) {
            ingredientChoice.clear();
            RecipeChoice.MaterialChoice materialChoice = (RecipeChoice.MaterialChoice) choice;
            ingredientChoice.addAll(materialChoice.getChoices());

            StringBuilder newHash = new StringBuilder("campfire");

            int size = ingredientChoice.size();
            for (int i = 0; i < size; i++) {
                newHash.append(ingredientChoice.get(i).toString());

                if (i + 1 < size) {
                    newHash.append(", ");
                }
            }

            hash = newHash.toString().hashCode();
        }

        updateHash();
    }

    @Override
    public void setResult(ItemStack newResult) {
        Validate.notNull(newResult);

        if (newResult instanceof ItemResult) {
            result = ((ItemResult) newResult).setRecipe(this);
        } else {
            result = new ItemResult(newResult).setRecipe(this);
        }

        updateHash();
    }

    private void updateHash() {
        if (ingredientChoice != null && result != null) {
            String newHash = "stonecutting";

            int size = ingredientChoice.size();
            for (int i = 0; i < size; i++) {
                newHash += ingredientChoice.get(i).toString();

                if (i + 1 < size) {
                    newHash += ", ";
                }
            }

            newHash += " - " + result.getType().toString();

            hash = newHash.hashCode();
        }
    }

    @Override
    public void resetName() {
        StringBuilder s = new StringBuilder();
        boolean removed = hasFlag(FlagType.REMOVE);

        s.append("stonecutting ");

        int size = ingredientChoice.size();
        for (int i = 0; i < size; i++) {
            s.append(ingredientChoice.get(i).toString().toLowerCase());

            if (i + 1 < size) {
                s.append(", ");
            }
        }

        s.append(" to ");
        s.append(getResultString());

        if (removed) {
            s.append(" [removed recipe]");
        }

        name = s.toString();
        customName = false;
    }

    public List<String> getIndexString() {
        List<String> indexString = new ArrayList<>();

        for (Material material : ingredientChoice) {
            indexString.add(material.toString() + " - " + result.getType().toString());
        }

        return indexString;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof StonecuttingRecipe && hash == obj.hashCode();
    }

    @Override
    public StonecuttingRecipe toBukkitRecipe(boolean vanilla) {
        if (!hasIngredientChoice() || !hasResult()) {
            return null;
        }

        return new StonecuttingRecipe(getNamespacedKey(), getResult(), new RecipeChoice.MaterialChoice(getIngredientChoice()));
    }

    public boolean hasIngredientChoice() {
        return ingredientChoice != null;
    }

    @Override
    public boolean isValid() {
        return hasIngredientChoice() && (hasFlag(FlagType.REMOVE) || hasFlag(FlagType.RESTRICT) || hasResult());
    }

    @Override
    public String getInvalidErrorMessage() {
        return super.getInvalidErrorMessage() + " Needs a result and ingredient!";
    }

    @Override
    public RMCRecipeType getType() {
        return RMCRecipeType.STONECUTTING;
    }

    @Override
    public String printBookResult(ItemResult result) {
        StringBuilder s = getHeaderResult("stonecutting");

        String print = getConditionResultName(result);

        if (print.equals("")) {
            print = ToolsItem.printChoice(getIngredientChoice(), RMCChatColor.RESET, RMCChatColor.BLACK);
        }

        s.append('\n').append(print);

        return s.toString();
    }

    @Override
    public int findItemInIngredients(Material type, Short data) {
        int found = 0;

        List<Material> choice = getIngredientChoice();
        for (Material material : choice) {
            if (type == material) {
                found++;
                break;
            }
        }

        return found;
    }
}
