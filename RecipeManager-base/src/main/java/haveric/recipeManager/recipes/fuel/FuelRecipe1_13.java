package haveric.recipeManager.recipes.fuel;

import haveric.recipeManager.common.RMCChatColor;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.tools.ToolsRecipeChoice;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FuelRecipe1_13 extends BaseFuelRecipe {
    private RecipeChoice ingredientChoice;

    public FuelRecipe1_13() {

    }

    public FuelRecipe1_13(Material type, float burnTime) {
        setIngredientChoice(Collections.singletonList(type));
        minTime = burnTime;
    }

    public FuelRecipe1_13(BaseRecipe recipe) {
        super(recipe);

        if (recipe instanceof FuelRecipe1_13) {
            FuelRecipe1_13 r = (FuelRecipe1_13) recipe;

            if (r.ingredientChoice != null) {
                ingredientChoice = r.ingredientChoice.clone();
            }
        }
    }

    public FuelRecipe1_13(Flags flags) {
        super(flags);
    }

    public RecipeChoice getIngredientChoice() {
        return ingredientChoice;
    }

    public void addIngredientChoice(List<Material> materials) {
        if (ingredientChoice == null) {
            setIngredientChoice(materials);
        } else {
            ingredientChoice = ToolsRecipeChoice.mergeRecipeChoiceWithMaterials(ingredientChoice, materials);
            updateHash();
        }
    }

    public void addIngredientChoiceItems(List<ItemStack> items) {
        if (ingredientChoice == null) {
            setIngredientChoiceItems(items);
        } else {
            ingredientChoice = ToolsRecipeChoice.mergeRecipeChoiceWithItems(ingredientChoice, items);
            updateHash();
        }
    }

    public void setIngredientChoice(List<Material> materials) {
        RecipeChoice.MaterialChoice materialChoice = new RecipeChoice.MaterialChoice(materials);
        setIngredientChoice(materialChoice);
    }

    public void setIngredientChoiceItems(List<ItemStack> items) {
        RecipeChoice.ExactChoice exactChoice = new RecipeChoice.ExactChoice(items);
        setIngredientChoice(exactChoice);
    }

    protected void setIngredientChoice(RecipeChoice choice) {
        ingredientChoice = choice.clone();

        updateHash();
    }

    private void updateHash() {
        String newHash = "fuel";

        if (hasIngredientChoice()) {
            newHash += ToolsRecipeChoice.getRecipeChoiceHash(ingredientChoice);
        }

        hash = newHash.hashCode();
    }

    public boolean hasIngredientChoice() {
        return ingredientChoice != null;
    }

    @Override
    public List<String> getIndexes() {
        List<String> indexString = new ArrayList<>();

        if (ingredientChoice instanceof RecipeChoice.MaterialChoice) {
            for (Material material : ((RecipeChoice.MaterialChoice) ingredientChoice).getChoices()) {
                indexString.add(material.toString());
            }
        } else if (ingredientChoice instanceof RecipeChoice.ExactChoice) {
            for (ItemStack item : ((RecipeChoice.ExactChoice) ingredientChoice).getChoices()) {
                indexString.add(item.getType().toString());
            }
        }

        return indexString;
    }

    @Override
    public void resetName() {
        StringBuilder s = new StringBuilder();
        boolean removed = hasFlag(FlagType.REMOVE);

        s.append("fuel ");

        s.append(ToolsRecipeChoice.getRecipeChoiceName(ingredientChoice));

        if (removed) {
            s.append(" [removed recipe]");
        }

        name = s.toString();
        customName = false;
    }

    @Override
    public boolean isValid() {
        return hasIngredientChoice();
    }

    @Override
    public List<String> printBookIndices() {
        List<String> print = new ArrayList<>();

        if (hasCustomName()) {
            print.add(RMCChatColor.ITALIC + getName());
        } else {
            print.add(ToolsRecipeChoice.getRecipeChoiceName(ingredientChoice) + " Fuel");
        }

        return print;
    }


    @Override
    public String printBookResult(ItemResult result) {
        return getPrintForIngredient(ToolsRecipeChoice.printRecipeChoice(ingredientChoice, RMCChatColor.BLACK, RMCChatColor.BLACK));
    }

    @Override
    public int getIngredientMatchQuality(List<ItemStack> ingredients) {
        if (ingredients.size() == 1) {
            ItemStack ingredient = ingredients.get(0);

            boolean checkExact = true;
            if (hasFlag(FlagType.INGREDIENT_CONDITION)) {
                checkExact = false;
            }
            return ToolsRecipeChoice.getIngredientMatchQuality(ingredient, ingredientChoice, checkExact);
        }

        return 0;
    }
}
