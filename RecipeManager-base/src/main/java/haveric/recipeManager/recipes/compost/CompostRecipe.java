package haveric.recipeManager.recipes.compost;

import com.google.common.collect.ImmutableList;
import haveric.recipeManager.common.RMCChatColor;
import haveric.recipeManager.common.recipes.RMCRecipeType;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.MultiChoiceResultRecipe;
import haveric.recipeManager.tools.ToolsRecipeChoice;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.util.List;

public class CompostRecipe extends MultiChoiceResultRecipe {
    private double levelSuccessChance = 100;
    private double levels = 1;
    public static final ItemResult VANILLA_ITEM_RESULT = new ItemResult(new ItemStack(Material.BONE_MEAL));

    public CompostRecipe() {
        init();
    }

    /**
     * Constructor for vanilla recipes
     *
     * @param ingredient material to set as the ingredient
     * @param levelSuccessChance the per ingredient success chance to add levels
     *
     */
    public CompostRecipe(Material ingredient, double levelSuccessChance) {
        init();
        setMaxIngredients(1);
        addValidChars(ImmutableList.of('a'));

        setIngredientChoice(new RecipeChoice.MaterialChoice(ingredient));
        setResult(VANILLA_ITEM_RESULT.clone());
        this.levelSuccessChance = levelSuccessChance;

        updateHash();
    }

    public CompostRecipe(BaseRecipe recipe) {
        super(recipe);
        init();

        if (recipe instanceof CompostRecipe r) {
            levelSuccessChance = r.levelSuccessChance;
            levels = r.levels;
        }

        updateHash();
    }

    public CompostRecipe(Flags flags) {
        super(flags);
        init();
    }

    private void init() {
        setMaxIngredients(1);
        addValidChars(ImmutableList.of('a'));
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

    public RecipeChoice getIngredientChoice() {
        return getIngredient('a');
    }

    public void setIngredientChoice(RecipeChoice choice) {
        setIngredient('a', choice);
    }

    public boolean hasIngredientChoice() {
        return hasIngredient('a');
    }

    public void addIngredientChoiceItems(List<ItemStack> items) {
        if (hasIngredientChoice()) {
            setIngredientChoice(ToolsRecipeChoice.mergeRecipeChoiceWithItems(getIngredientChoice(), items));
        } else {
            RecipeChoice.ExactChoice exactChoice = new RecipeChoice.ExactChoice(items);
            setIngredientChoice(exactChoice);
        }
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

        s.append(ToolsRecipeChoice.getRecipeChoiceName(getIngredientChoice()));

        s.append(" to ");

        s.append(getResultsString());

        if (removed) {
            s.append(" [removed recipe]");
        }

        name = s.toString();
        customName = false;
    }

    @Override
    public boolean isValid() {
        return hasIngredientChoice(); // TODO: Does this need hasResults()?
    }

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

        for (RecipeChoice choice : getIngredients().values()) {
            s.append('\n').append(ToolsRecipeChoice.printRecipeChoice(choice, RMCChatColor.BLACK, RMCChatColor.BLACK));
        }

        s.append("\n\n");
        s.append(Messages.getInstance().parse("recipebook.header.compostlevel")).append(RMCChatColor.BLACK);
        s.append('\n').append(Messages.getInstance().parse("recipebook.compost.level", "{levelsuccess}", levelSuccessChance, "{levels}", levels));

        return s.toString();
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

            return ToolsRecipeChoice.getIngredientMatchQuality(ingredient, getIngredientChoice(), checkExact);
        }

        return 0;
    }

    @Override
    public boolean requiresRecipeManagerModification() {
        return true;
    }
}
