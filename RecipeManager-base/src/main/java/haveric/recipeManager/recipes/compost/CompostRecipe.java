package haveric.recipeManager.recipes.compost;

import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.MultiResultRecipe;
import haveric.recipeManager.tools.ToolsItem;
import haveric.recipeManager.common.RMCChatColor;
import haveric.recipeManager.common.recipes.RMCRecipeType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CompostRecipe extends MultiResultRecipe {
    private List<Material> ingredients = new ArrayList<>();
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
        ingredients.add(ingredient);
        setResult(VANILLA_ITEM_RESULT.clone());
        this.levelSuccessChance = levelSuccessChance;

        updateHash();
    }

    public CompostRecipe(BaseRecipe recipe) {
        super(recipe);

        if (recipe instanceof CompostRecipe) {
            CompostRecipe r = (CompostRecipe) recipe;

            if (r.ingredients == null) {
                ingredients = null;
            } else {
                ingredients.addAll(r.ingredients);
            }

            levelSuccessChance = r.levelSuccessChance;
            levels = r.levels;
        }

        updateHash();
    }

    public CompostRecipe(Flags flags) {
        super(flags);
    }

    public List<Material> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Material> newIngredients) {
        ingredients.clear();
        ingredients.addAll(newIngredients);

        updateHash();
    }

    private void updateHash() {
        String newHash = "compost";

        int size = ingredients.size();
        for (int i = 0; i < size; i++) {
            newHash += ingredients.get(i).toString();

            if (i + 1 < size) {
                newHash += ", ";
            }
        }

        hash = newHash.hashCode();
    }

    public boolean hasIngredients() {
        return ingredients != null && ingredients.size() > 0;
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

        int size = ingredients.size();
        for (int i = 0; i < size; i++) {
            s.append(ingredients.get(i).toString().toLowerCase());

            if (i + 1 < size) {
                s.append(", ");
            }
        }

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

        for (Material material : ingredients) {
            indexString.add(material.toString());
        }

        return indexString;
    }

    @Override
    public boolean isValid() {
        return hasIngredients();
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

        s.append('\n').append(ToolsItem.printChoice(ingredients, RMCChatColor.BLACK, RMCChatColor.BLACK));

        s.append("\n\n");
        s.append(Messages.getInstance().parse("recipebook.header.compostlevel")).append(RMCChatColor.BLACK);
        s.append('\n').append(Messages.getInstance().parse("recipebook.compost.level", "{levelsuccess}", levelSuccessChance, "{levels}", levels));

        return s.toString();
    }

    @Override
    public int findItemInIngredients(Material type, Short data) {
        int found = 0;

        for (Material material : ingredients) {
            if (type == material) {
                found++;
                break;
            }
        }

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
}
