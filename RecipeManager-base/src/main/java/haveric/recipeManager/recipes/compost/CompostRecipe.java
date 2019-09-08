package haveric.recipeManager.recipes.compost;

import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.flag.flags.any.FlagItemName;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.MultiResultRecipe;
import haveric.recipeManager.tools.ToolsItem;
import haveric.recipeManagerCommon.RMCChatColor;
import haveric.recipeManagerCommon.recipes.RMCRecipeType;
import haveric.recipeManagerCommon.util.RMCUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CompostRecipe extends MultiResultRecipe {
    private List<Material> ingredients = new ArrayList<>();
    private double levelSuccessChance = 100;
    private double levels = 1;
    public static final ItemResult VANILLA_ITEM_RESULT = new ItemResult(new ItemStack(Material.BONE_MEAL));

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
            s.append(" / removed recipe");
        }

        name = s.toString();
        customName = false;
    }

    public List<String> getIndexString() {
        List<String> indexString = new ArrayList<>();

        for (Material material : ingredients) {
            indexString.add(material.toString());
        }

        return indexString;
    }

    @Override
    public boolean isValid() {
        return hasIngredients();
    }

    @Override
    public RMCRecipeType getType() {
        return RMCRecipeType.COMPOST;
    }

    @Override
    public List<String> printBookIndices() {
        List<String> print = new ArrayList<>();

        if (hasFlag(FlagType.INDIVIDUAL_RESULTS)) {
            for (ItemResult result : getResults()) {
                print.add(getResultPrintName(result));
            }
        } else {
            print.add(getResultPrintName(getFirstResult()));
        }

        return print;
    }

    private String getResultPrintName(ItemResult result) {
        String print;

        if (result.hasFlag(FlagType.ITEM_NAME)) {
            FlagItemName flag = (FlagItemName)result.getFlag(FlagType.ITEM_NAME);
            print = RMCUtil.parseColors(flag.getPrintName(), false);
        } else {
            print = ToolsItem.getName(getFirstResult());
        }

        return print;
    }

    @Override
    public List<String> printBookRecipes() {
        List<String> recipes = new ArrayList<>();

        if (hasFlag(FlagType.INDIVIDUAL_RESULTS)) {
            for (ItemResult result : getResults()) {
                recipes.add(printBookResult(result));
            }
        } else {
            recipes.add(printBookResult(getFirstResult()));
        }

        return recipes;
    }

    private String printBookResult(ItemResult result) {
        StringBuilder s = new StringBuilder(256);

        s.append(Messages.getInstance().parse("recipebook.header.compost"));

        if (hasCustomName()) {
            s.append('\n').append(RMCChatColor.BLACK).append(RMCChatColor.ITALIC).append(getName());
        }

        s.append('\n').append(RMCChatColor.GRAY).append('=');

        if (result.hasFlag(FlagType.ITEM_NAME)) {
            FlagItemName flag = (FlagItemName)result.getFlag(FlagType.ITEM_NAME);
            s.append(RMCChatColor.BLACK).append(RMCUtil.parseColors(flag.getPrintName(), false));
        } else {
            s.append(ToolsItem.print(getFirstResult(), RMCChatColor.DARK_GREEN, null));
        }

        if (isMultiResult() && !hasFlag(FlagType.INDIVIDUAL_RESULTS)) {
            s.append('\n').append(Messages.getInstance().parse("recipebook.moreresults", "{amount}", (getResults().size() - 1)));
        }

        s.append("\n\n");
        s.append(Messages.getInstance().parse("recipebook.header.ingredients"));

        s.append('\n').append(ToolsItem.printChoice(ingredients, RMCChatColor.BLACK, RMCChatColor.BLACK));

        s.append("\n\n");
        s.append(Messages.getInstance().parse("recipebook.header.compostlevel")).append(RMCChatColor.BLACK);
        s.append('\n').append(Messages.getInstance().parse("recipebook.compost.level", "{levelsuccess}", levelSuccessChance, "{levels}", levels));

        return s.toString();
    }
}
