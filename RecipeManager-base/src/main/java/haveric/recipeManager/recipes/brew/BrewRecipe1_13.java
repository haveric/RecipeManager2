package haveric.recipeManager.recipes.brew;

import haveric.recipeManager.common.recipes.RMCRecipeType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.tools.ToolsItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;

import java.util.ArrayList;
import java.util.List;

public class BrewRecipe1_13 extends BaseBrewRecipe {
    private RecipeChoice ingredientChoice;
    private RecipeChoice potionChoice;

    public BrewRecipe1_13() {

    }

    public BrewRecipe1_13(BaseRecipe recipe) {
        super(recipe);

        if (recipe instanceof BrewRecipe1_13) {
            BrewRecipe1_13 r = (BrewRecipe1_13) recipe;

            if (r.ingredientChoice != null) {
                ingredientChoice = r.ingredientChoice.clone();
            }
            if (r.potionChoice != null) {
                potionChoice = r.potionChoice.clone();
            }
        }
    }

    public BrewRecipe1_13(Flags flags) {
        super(flags);
    }

    @Override
    public Recipe getBukkitRecipe(boolean vanilla) {
        return null;
    }

    @Override
    public void setBukkitRecipe(Recipe newRecipe) {

    }

    @Override
    public Recipe toBukkitRecipe(boolean vanilla) {
        return null;
    }

    @Override
    public void resetName() {
        StringBuilder s = new StringBuilder();

        s.append(ToolsItem.getRecipeChoiceName(ingredientChoice));
        s.append(" + ");
        s.append(ToolsItem.getRecipeChoiceName(potionChoice));

        s.append(" to ").append(getResultsString());

        name = s.toString();
        customName = false;
    }

    @Override
    public boolean isValid() {
        return hasIngredientChoice() && hasPotionChoice() && hasResults();
    }

    @Override
    public String getInvalidErrorMessage() {
        return super.getInvalidErrorMessage() + " Needs a result and ingredient!";
    }

    @Override
    public RMCRecipeType getType() {
        return RMCRecipeType.BREW;
    }


    public boolean hasIngredient(char character) {
        if (character == 'a') {
            return ingredientChoice != null;
        } else if (character == 'b') {
            return potionChoice != null;
        }

        return false;
    }

    public RecipeChoice getIngredient(char character) {
        if (character == 'a') {
            return ingredientChoice;
        } else if (character == 'b') {
            return potionChoice;
        }

        return null;
    }

    public void setIngredient(char character, RecipeChoice choice) {
        if (character == 'a') {
            setIngredientChoice(choice);
        } else if (character == 'b') {
            setPotionChoice(choice);
        }
    }

    public RecipeChoice getIngredientChoice() {
        return ingredientChoice;
    }

    public void addIngredientChoice(List<Material> materials) {
        if (ingredientChoice == null) {
            setIngredientChoice(materials);
        } else {
            ingredientChoice = ToolsItem.mergeRecipeChoiceWithMaterials(ingredientChoice, materials);
            updateHash();
        }
    }

    public void addIngredientChoiceItems(List<ItemStack> items) {
        if (ingredientChoice == null) {
            setIngredientChoiceItems(items);
        } else {
            ingredientChoice = ToolsItem.mergeRecipeChoiceWithItems(ingredientChoice, items);
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

    public RecipeChoice getPotionChoice() {
        return potionChoice;
    }

    public void addPotionChoice(List<Material> materials) {
        if (potionChoice == null) {
            setPotionChoice(materials);
        } else {
            potionChoice = ToolsItem.mergeRecipeChoiceWithMaterials(potionChoice, materials);
            updateHash();
        }
    }

    public void addPotionChoiceItems(List<ItemStack> items) {
        if (potionChoice == null) {
            setPotionChoiceItems(items);
        } else {
            potionChoice = ToolsItem.mergeRecipeChoiceWithItems(potionChoice, items);
            updateHash();
        }
    }

    public void setPotionChoice(List<Material> materials) {
        RecipeChoice.MaterialChoice materialChoice = new RecipeChoice.MaterialChoice(materials);
        setPotionChoice(materialChoice);
    }

    public void setPotionChoiceItems(List<ItemStack> items) {
        RecipeChoice.ExactChoice exactChoice = new RecipeChoice.ExactChoice(items);
        setPotionChoice(exactChoice);
    }

    protected void setPotionChoice(RecipeChoice choice) {
        potionChoice = choice.clone();

        updateHash();
    }

    private void updateHash() {
        String newHash = "brew";

        if (hasIngredientChoice()) {
            newHash += ToolsItem.getRecipeChoiceHash(ingredientChoice);
        }

        newHash += " + ";

        if (hasPotionChoice()) {
            newHash += ToolsItem.getRecipeChoiceHash(potionChoice);
        }

        hash = newHash.hashCode();
    }

    public boolean hasIngredientChoice() {
        return ingredientChoice != null;
    }

    public boolean hasPotionChoice() {
        return potionChoice != null;
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
    public int findItemInIngredients(Material type, Short data) {
        int found = 0;

        found += ToolsItem.getNumMaterialsInRecipeChoice(type, ingredientChoice);
        found += ToolsItem.getNumMaterialsInRecipeChoice(type, potionChoice);

        return found;
    }

    @Override
    public List<String> getRecipeIndexesForInput(List<ItemStack> ingredients, ItemStack result) {
        List<String> recipeIndexes = new ArrayList<>();
        if (ingredients.size() == 1) {
            ItemStack ingredient = ingredients.get(0);
            recipeIndexes.add(ingredient.getType().toString());
        }

        return recipeIndexes;
    }
}
