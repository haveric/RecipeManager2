package haveric.recipeManager.recipes;

import com.google.common.base.Preconditions;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.tools.ToolsRecipeChoice;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.util.ArrayList;
import java.util.List;

public abstract class SingleRecipeChoiceSingleResultRecipe extends SingleResultRecipe {
    protected RecipeChoice ingredientChoice;

    public SingleRecipeChoiceSingleResultRecipe() {

    }

    public SingleRecipeChoiceSingleResultRecipe(BaseRecipe recipe) {
        super(recipe);

        if (recipe instanceof SingleRecipeChoiceSingleResultRecipe) {
            SingleRecipeChoiceSingleResultRecipe r = (SingleRecipeChoiceSingleResultRecipe) recipe;

            if (r.ingredientChoice != null) {
                ingredientChoice = r.ingredientChoice.clone();
            }
        }
    }

    public SingleRecipeChoiceSingleResultRecipe(Flags flags) {
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

    public void setIngredientChoice(RecipeChoice choice) {
        ingredientChoice = choice.clone();

        updateHash();
    }

    @Override
    public void setResult(ItemStack newResult) {
        Preconditions.checkNotNull(newResult);

        if (newResult instanceof ItemResult) {
            result = ((ItemResult) newResult).setRecipe(this);
        } else {
            result = new ItemResult(newResult).setRecipe(this);
        }

        updateHash();
    }

    private void updateHash() {
        String newHash = getType().getDirective();

        if (hasIngredientChoice()) {
            newHash += ToolsRecipeChoice.getRecipeChoiceHash(ingredientChoice);
        }

        if (hasResult()) {
            newHash += " - " + result.getType();
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
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean isValid() {
        return hasIngredientChoice() && (hasFlag(FlagType.REMOVE) || hasFlag(FlagType.RESTRICT) || hasResult());
    }

    @Override
    public void resetName() {
        StringBuilder s = new StringBuilder();
        boolean removed = hasFlag(FlagType.REMOVE);

        s.append(getType().getDirective()).append(" ");

        s.append(ToolsRecipeChoice.getRecipeChoiceName(ingredientChoice));

        s.append(" to ");
        s.append(getResultString());
        if (removed) {
            s.append(" [removed recipe]");
        }

        name = s.toString();
        customName = false;
    }

    @Override
    public int findItemInIngredients(Material type, Short data) {
        int found = 0;

        found += ToolsRecipeChoice.getNumMaterialsInRecipeChoice(type, ingredientChoice);

        return found;
    }

    @Override
    public List<String> getRecipeIndexesForInput(List<ItemStack> ingredients, ItemStack result) {
        List<String> recipeIndexes = new ArrayList<>();
        if (ingredients.size() == 1) {
            recipeIndexes.add(ingredients.get(0).getType().toString());
        }

        return recipeIndexes;
    }

    @Override
    public String getInvalidErrorMessage() {
        return super.getInvalidErrorMessage() + " Needs a result and ingredient!";
    }

    @Override
    public int getIngredientMatchQuality(List<ItemStack> ingredients) {
        if (ingredients.size() == 1) {
            ItemStack ingredient = ingredients.get(0);

            boolean checkExact = true;
            if (hasFlag(FlagType.INGREDIENT_CONDITION) || result.hasFlag(FlagType.INGREDIENT_CONDITION)) {
                checkExact = false;
            }
            return ToolsRecipeChoice.getIngredientMatchQuality(ingredient, ingredientChoice, checkExact);
        }

        return 0;
    }
}
