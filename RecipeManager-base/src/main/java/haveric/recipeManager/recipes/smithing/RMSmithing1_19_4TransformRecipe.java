package haveric.recipeManager.recipes.smithing;

import com.google.common.collect.ImmutableList;
import haveric.recipeManager.common.RMCChatColor;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.tools.ToolsRecipeChoice;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingTransformRecipe;

import java.util.List;
import java.util.Map;

public class RMSmithing1_19_4TransformRecipe extends RMSmithingRecipe {
    public RMSmithing1_19_4TransformRecipe() {
        init();
    }

    public RMSmithing1_19_4TransformRecipe(SmithingTransformRecipe recipe) {
        init();
        setTemplateIngredientChoice(recipe.getTemplate());
        setPrimaryIngredientChoice(recipe.getBase());
        setSecondaryIngredientChoice(recipe.getAddition());

        setResult(recipe.getResult());
    }

    public RMSmithing1_19_4TransformRecipe(BaseRecipe recipe) {
        super(recipe);
        init();
    }

    public RMSmithing1_19_4TransformRecipe(Flags flags) {
        super(flags);
        init();
    }

    private void init() {
        setMaxIngredients(3);
        addValidChars(ImmutableList.of('a', 'b', 't'));
    }

    @Override
    public SmithingTransformRecipe toBukkitRecipe(boolean vanilla) {
        if (!hasIngredients() || !hasResults()) {
            return null;
        }

        SmithingTransformRecipe bukkitRecipe;
        if (vanilla) {
            bukkitRecipe = new SmithingTransformRecipe(getNamespacedKey(), getFirstResult(), getTemplateIngredientChoice(), getPrimaryIngredientChoice(), getSecondaryIngredientChoice());
        } else {
            ItemResult firstResult = getFirstResult();

            Args a = ArgBuilder.create().result(firstResult).build();
            getFlags().sendPrepare(a, true);
            firstResult.getFlags().sendPrepare(a, true);

            bukkitRecipe = new SmithingTransformRecipe(getNamespacedKey(), a.result(), getTemplateIngredientChoice(), getPrimaryIngredientChoice(), getSecondaryIngredientChoice());
        }

        return bukkitRecipe;
    }

    @Override
    public String getInvalidErrorMessage() {
        return super.getInvalidErrorMessage() + " Needs a result, and two ingredients! Must also have a base ingredient that is not air.";
    }

    @Override
    public boolean isValid() {
        return hasIngredients() && hasResults() && !ToolsRecipeChoice.isMaterialChoiceAir(getIngredient('a'));
    }

    public RecipeChoice getTemplateIngredientChoice() {
        return getIngredient('t');
    }

    public void setTemplateIngredientChoice(RecipeChoice choice) {
        setIngredient('t', choice);
    }

    public boolean hasTemplateIngredientChoice() {
        return hasIngredient('t');
    }

    @Override
    public String printBookResult(ItemResult result) {
        StringBuilder s = getHeaderResult("smithing", result);

        s.append(Messages.getInstance().parse("recipebook.header.ingredients"));

        // Display template first
        if (hasTemplateIngredientChoice()) {
            s.append('\n').append(ToolsRecipeChoice.printRecipeChoice(getTemplateIngredientChoice(), RMCChatColor.BLACK, RMCChatColor.BLACK));
        }

        for (Map.Entry<Character, RecipeChoice> entry : getIngredients().entrySet()) {
            if (entry.getKey() != 't') {
                s.append('\n').append(ToolsRecipeChoice.printRecipeChoice(entry.getValue(), RMCChatColor.BLACK, RMCChatColor.BLACK));
            }
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
                String pattern = "tab";
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
}
