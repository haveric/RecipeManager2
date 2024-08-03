package haveric.recipeManager.recipes.compost;

import haveric.recipeManager.common.RMCChatColor;
import haveric.recipeManager.common.recipes.RMCRecipeType;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.MultiResultRecipe;
import haveric.recipeManager.tools.ToolsRecipeChoice;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.util.ArrayList;
import java.util.List;

public class CompostRecipe extends MultiResultRecipe {
    private RecipeChoice ingredientChoice;
    private double levelSuccessChance = 100;
    private double levels = 1;
    public static final ItemResult VANILLA_ITEM_RESULT = new ItemResult(new ItemStack(Material.BONE_MEAL));

    public CompostRecipe() {
    }

    /**
     * Constructor for vanilla recipes
     *
     * @param ingredient material to set as the ingredient
     * @param levelSuccessChance the per ingredient success chance to add levels
     *
     */
    public CompostRecipe(Material ingredient, double levelSuccessChance) {
        ingredientChoice = new RecipeChoice.MaterialChoice(ingredient);
        setResult(VANILLA_ITEM_RESULT.getItemStack().clone());
        this.levelSuccessChance = levelSuccessChance;

        updateHash();
    }

    public CompostRecipe(BaseRecipe recipe) {
        super(recipe);

        if (recipe instanceof CompostRecipe) {
            CompostRecipe r = (CompostRecipe) recipe;

            if (r.ingredientChoice != null) {
                ingredientChoice = r.ingredientChoice.clone();
            }

            levelSuccessChance = r.levelSuccessChance;
            levels = r.levels;
        }

        updateHash();
    }

    public CompostRecipe(Flags flags) {
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
        String newHash = "compost";

        if (hasIngredientChoice()) {
            newHash += ToolsRecipeChoice.getRecipeChoiceHash(ingredientChoice);
        }

        hash = newHash.hashCode();
    }

    public boolean hasIngredientChoice() {
        return ingredientChoice != null;
    }

    public double getLevelSuccessChance() {
        return levelSuccessChance;
    }

    public void setLevelSuccessChance(double newSuccessChance) {
        levelSuccessChance = newSuccessChance;
    }

    public double getLevels() {
        return levels;
    }

    public void setLevels(double newLevels) {
        levels = newLevels;
    }

    @Override
    public void resetName() {
        StringBuilder s = new StringBuilder();
        boolean removed = hasFlag(FlagType.REMOVE);

        s.append("compost ");

        if (levelSuccessChance < 100) {
            s.append(levelSuccessChance).append("% ");
        }

        if (levels > 1) {
            s.append("levels x").append(levels).append(" ");
        }

        s.append(ToolsRecipeChoice.getRecipeChoiceName(ingredientChoice));

        s.append(" to ");

        s.append(getResultsString());

        if (removed) {
            s.append(" [removed recipe]");
        }

        name = s.toString();
        customName = false;
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
    public boolean isValid() {
        return hasIngredientChoice();
    } // TODO: Does this need hasResults()?

    @Override
    public String getInvalidErrorMessage() {
        return super.getInvalidErrorMessage() + " Needs a result and ingredients!";
    }

    @Override
    public RMCRecipeType getType() {
        return RMCRecipeType.COMPOST;
    }

    @Override
    public String printBookResult(ItemResult result) {
        StringBuilder s = getHeaderResult("compost", result);

        s.append(Messages.getInstance().parse("recipebook.header.ingredients"));

        s.append('\n').append(ToolsRecipeChoice.printRecipeChoice(ingredientChoice, RMCChatColor.BLACK, RMCChatColor.BLACK));

        s.append("\n\n");
        s.append(Messages.getInstance().parse("recipebook.header.compostlevel")).append(RMCChatColor.BLACK);
        s.append('\n').append(Messages.getInstance().parse("recipebook.compost.level", "{levelsuccess}", levelSuccessChance, "{levels}", levels));

        return s.toString();
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
    public int getIngredientMatchQuality(List<ItemStack> ingredients) {
        if (ingredients.size() == 1) {
            ItemStack ingredient = ingredients.get(0);

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

            return ToolsRecipeChoice.getIngredientMatchQuality(ingredient, ingredientChoice, checkExact);
        }

        return 0;
    }

    @Override
    public boolean requiresRecipeManagerModification() {
        return true;
    }
}
