package haveric.recipeManager.recipes.brew;

import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.tools.ToolsRecipeChoice;
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
                setIngredientChoice(r.ingredientChoice.clone());
            }
            if (r.potionChoice != null) {
                setPotionChoice(r.potionChoice.clone());
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

        s.append(ToolsRecipeChoice.getRecipeChoiceName(ingredientChoice));
        s.append(" + ");
        s.append(ToolsRecipeChoice.getRecipeChoiceName(potionChoice));

        s.append(" to ").append(getResultsString());

        name = s.toString();
        customName = false;
    }

    @Override
    public boolean isValid() {
        return hasIngredientChoice() && hasPotionChoice() && hasResults();
    }

    @Override
    public void onRegister() {
        if (hasIngredientChoice()) {
            List<Material> ingredientMaterials = ToolsRecipeChoice.getMaterialsInRecipeChoice(ingredientChoice);
            BrewInventoryUtil.addIngredients(ingredientMaterials);
        }

        if (hasPotionChoice()) {
            List<Material> potionMaterials = ToolsRecipeChoice.getMaterialsInRecipeChoice(potionChoice);
            BrewInventoryUtil.addPotions(potionMaterials);
        }

        if (hasResults()) {
            List<ItemResult> results = getResults();
            for (ItemResult itemResult : results) {
                BrewInventoryUtil.addResult(itemResult.getType());
            }
        }
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

    public RecipeChoice getPotionChoice() {
        return potionChoice;
    }

    public void addPotionChoice(List<Material> materials) {
        if (potionChoice == null) {
            setPotionChoice(materials);
        } else {
            potionChoice = ToolsRecipeChoice.mergeRecipeChoiceWithMaterials(potionChoice, materials);
            updateHash();
        }
    }

    public void addPotionChoiceItems(List<ItemStack> items) {
        if (potionChoice == null) {
            setPotionChoiceItems(items);
        } else {
            potionChoice = ToolsRecipeChoice.mergeRecipeChoiceWithItems(potionChoice, items);
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
            newHash += ToolsRecipeChoice.getRecipeChoiceHash(ingredientChoice);
        }

        newHash += " + ";

        if (hasPotionChoice()) {
            newHash += ToolsRecipeChoice.getRecipeChoiceHash(potionChoice);
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

        StringBuilder index = new StringBuilder();
        if (ingredientChoice instanceof RecipeChoice.MaterialChoice) {
            for (Material material : ((RecipeChoice.MaterialChoice) ingredientChoice).getChoices()) {
                index.append(material);
            }
        } else if (ingredientChoice instanceof RecipeChoice.ExactChoice) {
            for (ItemStack item : ((RecipeChoice.ExactChoice) ingredientChoice).getChoices()) {
                index.append(item.getType());
            }
        }

        index.append(" - ");

        if (potionChoice instanceof RecipeChoice.MaterialChoice) {
            for (Material material : ((RecipeChoice.MaterialChoice) potionChoice).getChoices()) {
                index.append(material);
            }
        } else if (potionChoice instanceof RecipeChoice.ExactChoice) {
            for (ItemStack item : ((RecipeChoice.ExactChoice) potionChoice).getChoices()) {
                index.append(item.getType());
            }
        }

        indexString.add(index.toString());

        return indexString;
    }

    @Override
    public int findItemInIngredients(Material type, Short data) {
        int found = 0;

        found += ToolsRecipeChoice.getNumMaterialsInRecipeChoice(type, ingredientChoice);
        found += ToolsRecipeChoice.getNumMaterialsInRecipeChoice(type, potionChoice);

        return found;
    }

    @Override
    public List<String> getRecipeIndexesForInput(List<ItemStack> ingredients, ItemStack result) {
        List<String> recipeIndexes = new ArrayList<>();
        if (ingredients.size() > 1) {
            ItemStack ingredient = ingredients.get(0);

            for (int i = 1; i < ingredients.size(); i++) {
                recipeIndexes.add(ingredient.getType() + " - " + ingredients.get(i).getType());
            }
        }

        return recipeIndexes;
    }

    @Override
    public int getIngredientMatchQuality(List<ItemStack> ingredients) {
        boolean checkExact = true;
        if (hasFlag(FlagType.INGREDIENT_CONDITION)) {
            checkExact = false;
        } else {
            for (ItemResult result : getResults()) {
                if (result.hasFlag(FlagType.INGREDIENT_CONDITION)) {
                    checkExact = false;
                    break;
                }
            }
        }

        int totalQuality = 0;
        int ingredientQuality = 0;
        int potionQuality = 0;

        ItemStack ingredient = ingredients.get(0);
        if (ingredient.getType() != Material.AIR) {
            RecipeChoice ingredientChoice = getIngredient('a');

            int newQuality = ToolsRecipeChoice.getIngredientMatchQuality(ingredient, ingredientChoice, checkExact);
            if (newQuality > ingredientQuality) {
                ingredientQuality = newQuality;
            }
        }

        if (ingredientQuality > 0) {
            for (int i = 1; i < ingredients.size(); i++) {
                ItemStack potion = ingredients.get(i);

                if (potion.getType() != Material.AIR) {
                    RecipeChoice potionChoice = getIngredient('b');

                    int newQuality = ToolsRecipeChoice.getIngredientMatchQuality(potion, potionChoice, checkExact);
                    if (newQuality > potionQuality) {
                        potionQuality = newQuality;
                    }
                }
            }

            if (potionQuality > 0) {
                totalQuality = ingredientQuality + potionQuality;
            }
        }

        return totalQuality;
    }
}
