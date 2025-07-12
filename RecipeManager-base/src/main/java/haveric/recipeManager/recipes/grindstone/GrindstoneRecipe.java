package haveric.recipeManager.recipes.grindstone;

import com.google.common.collect.ImmutableList;
import haveric.recipeManager.common.RMCChatColor;
import haveric.recipeManager.common.recipes.RMCRecipeType;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.PreparableResultRecipe;
import haveric.recipeManager.tools.ToolsRecipeChoice;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.util.List;
import java.util.Map;

public class GrindstoneRecipe extends PreparableResultRecipe {
    public GrindstoneRecipe() {
        init();
    }

    public GrindstoneRecipe(BaseRecipe recipe) {
        super(recipe);
        init();

        if (recipe instanceof GrindstoneRecipe r) {
            updateHash();
        }
    }

    public GrindstoneRecipe(Flags flags) {
        super(flags);
        init();
    }

    private void init() {
        setMaxIngredients(2);
        addValidChars(ImmutableList.of('a', 'b'));
    }

    public boolean isValidBlockMaterial(Material material) {
        return material == Material.GRINDSTONE;
    }

    public RecipeChoice getPrimaryIngredientChoice() {
        return getIngredient('a');
    }

    public RecipeChoice getSecondaryIngredientChoice() {
        return getIngredient('b');
    }

    public void setPrimaryIngredientChoice(RecipeChoice choice) {
        setIngredient('a', choice);
    }

    public void setSecondaryIngredientChoice(RecipeChoice choice) {
        setIngredient('b', choice);
    }

    public boolean hasPrimaryIngredientChoice() {
        return hasIngredient('a');
    }

    public boolean hasSecondaryIngredientChoice() {
        return hasIngredient('b');
    }

    @Override
    public boolean hasIngredients() {
        return hasPrimaryIngredientChoice() && hasSecondaryIngredientChoice();
    }

    @Override
    public void resetName() {
        StringBuilder str = new StringBuilder();

        str.append("grindstone (");

        for (Map.Entry<Character, RecipeChoice> ingredient : getIngredients().entrySet()) {
            str.append(" ").append(ingredient.getKey()).append(":");
            str.append(ToolsRecipeChoice.getRecipeChoiceHash(ingredient.getValue()));
        }

        str.append(")");

        str.append(" to ").append(getResultsString());

        name = str.toString();
        customName = false;
    }

    @Override
    public boolean isValid() {
        return hasIngredients() && hasResults();
    }

    @Override
    public String getInvalidErrorMessage() {
        return super.getInvalidErrorMessage() + " Needs a result and two ingredients!";
    }

    @Override
    public RMCRecipeType getType() {
        return RMCRecipeType.GRINDSTONE;
    }


    @Override
    public String printBookResult(ItemResult result) {
        StringBuilder s = getHeaderResult("grindstone", result);

        s.append(Messages.getInstance().parse("recipebook.header.ingredients"));

        for (RecipeChoice choice : getIngredients().values()) {
            s.append('\n').append(ToolsRecipeChoice.printRecipeChoice(choice, RMCChatColor.BLACK, RMCChatColor.BLACK));
        }

        return s.toString();
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
        for (ItemStack ingredient : ingredients) {
            if (ingredient.getType() != Material.AIR) {
                int quality = 0;
                String pattern = "ab";
                for (Character c : pattern.toCharArray()) {
                    RecipeChoice ingredientChoice = getIngredient(c);
                    int newQuality = ToolsRecipeChoice.getIngredientMatchQuality(ingredient, ingredientChoice, checkExact);
                    if (newQuality > quality) {
                        quality = newQuality;
                        totalQuality += quality;
                    }
                }

                if (quality == 0) {
                    totalQuality = 0;
                    break;
                }
            }
        }

        return totalQuality;
    }

    @Override
    public boolean requiresRecipeManagerModification() {
        return true;
    }
}
