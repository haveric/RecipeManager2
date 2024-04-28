package haveric.recipeManager.recipes.smithing;

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

import java.util.ArrayList;
import java.util.List;

public class RMSmithing1_19_4TransformRecipe extends RMSmithingRecipe {
    private RecipeChoice templateIngredient;

    public RMSmithing1_19_4TransformRecipe() {

    }

    public RMSmithing1_19_4TransformRecipe(SmithingTransformRecipe recipe) {
        setTemplateIngredient(recipe.getTemplate());
        setPrimaryIngredient(recipe.getBase());
        setSecondaryIngredient(recipe.getAddition());

        setResult(recipe.getResult());
    }

    public RMSmithing1_19_4TransformRecipe(BaseRecipe recipe) {
        super(recipe);

        if (recipe instanceof RMSmithing1_19_4TransformRecipe) {
            RMSmithing1_19_4TransformRecipe r = (RMSmithing1_19_4TransformRecipe) recipe;

            if (r.templateIngredient != null) {
                templateIngredient = r.templateIngredient.clone();
            }

            updateHash();
        }
    }

    public RMSmithing1_19_4TransformRecipe(Flags flags) {
        super(flags);
    }

    @Override
    public boolean hasIngredient(char character) {
        if (character == 'a') {
            return getPrimaryIngredient() != null;
        } else if (character == 'b') {
            return getSecondaryIngredient() != null;
        } else if (character == 't') {
            return templateIngredient != null;
        }

        return false;
    }

    @Override
    public RecipeChoice getIngredient(char character) {
        if (character == 'a') {
            return getPrimaryIngredient();
        } else if (character == 'b') {
            return getSecondaryIngredient();
        } else if (character == 't') {
            return templateIngredient;
        }

        return null;
    }

    @Override
    public void setIngredient(char character, RecipeChoice choice) {
        if (character == 'a') {
            setPrimaryIngredient(choice);
        } else if (character == 'b') {
            setSecondaryIngredient(choice);
        } else if (character == 't') {
            setTemplateIngredient(choice);
        }
    }

    public RecipeChoice getTemplateIngredient() {
        return templateIngredient;
    }

    public void setTemplateIngredient(RecipeChoice choice) {
        templateIngredient = choice.clone();

        updateHash();
    }

    public boolean hasTemplateIngredient() {
        return templateIngredient != null;
    }

    @Override
    public SmithingTransformRecipe toBukkitRecipe(boolean vanilla) {
        if (!hasIngredients() || !hasResults()) {
            return null;
        }

        SmithingTransformRecipe bukkitRecipe;
        if (vanilla) {
            bukkitRecipe = new SmithingTransformRecipe(getNamespacedKey(), getFirstResult(), templateIngredient, getPrimaryIngredient(), getSecondaryIngredient());
        } else {
            ItemResult firstResult = getFirstResult();

            Args a = ArgBuilder.create().result(firstResult).build();
            getFlags().sendPrepare(a, true);
            firstResult.getFlags().sendPrepare(a, true);

            bukkitRecipe = new SmithingTransformRecipe(getNamespacedKey(), a.result(), templateIngredient, getPrimaryIngredient(), getSecondaryIngredient());
        }

        return bukkitRecipe;
    }

    private void updateHash() {
        StringBuilder str = new StringBuilder("smithing");

        if (hasTemplateIngredient()) {
            str.append(" t:");
            str.append(ToolsRecipeChoice.getRecipeChoiceHash(templateIngredient));
        }

        str.append(" a:");
        str.append(ToolsRecipeChoice.getRecipeChoiceHash(getPrimaryIngredient()));

        str.append(" b:");
        str.append(ToolsRecipeChoice.getRecipeChoiceHash(getSecondaryIngredient()));

        hash = str.toString().hashCode();
    }

    @Override
    public void resetName() {
        StringBuilder str = new StringBuilder();

        str.append("smithing (");

        if (hasTemplateIngredient()) {
            str.append(" t:");
            str.append(ToolsRecipeChoice.getRecipeChoiceName(templateIngredient));
        }

        str.append(" a:");
        str.append(ToolsRecipeChoice.getRecipeChoiceName(getPrimaryIngredient()));

        str.append(" b:");
        str.append(ToolsRecipeChoice.getRecipeChoiceName(getSecondaryIngredient()));

        str.append(")");

        str.append(" to ").append(getResultsString());

        name = str.toString();
        customName = false;
    }

    @Override
    public List<String> getIndexes() {
        List<String> indexString = new ArrayList<>();

        if (templateIngredient == null) {
            templateIngredient = new RecipeChoice.MaterialChoice(Material.AIR);
        }
        List<String> templateIndexes = getIndexForChoice(templateIngredient);
        List<String> primaryIndexes = getIndexForChoice(getPrimaryIngredient());
        List<String> secondaryIndexes = getIndexForChoice(getSecondaryIngredient());

        for (String templateIndex : templateIndexes) {
            for (String primaryIndex : primaryIndexes) {
                for (String secondaryIndex : secondaryIndexes) {
                    indexString.add(templateIndex + "-" + primaryIndex + "-" + secondaryIndex);
                }
            }
        }

        return indexString;
    }

    @Override
    public String getInvalidErrorMessage() {
        return super.getInvalidErrorMessage() + " Needs a result, and two ingredients! Must also have a base ingredient that is not air.";
    }

    @Override
    public boolean isValid() {
        return hasIngredients() && hasResults() && !ToolsRecipeChoice.isMaterialChoiceAir(getPrimaryIngredient());
    }

    @Override
    public String printBookResult(ItemResult result) {
        StringBuilder s = getHeaderResult("smithing", result);

        s.append(Messages.getInstance().parse("recipebook.header.ingredients"));

        if (hasTemplateIngredient()) {
            s.append('\n').append(ToolsRecipeChoice.printRecipeChoice(templateIngredient, RMCChatColor.BLACK, RMCChatColor.BLACK));
        }
        s.append('\n').append(ToolsRecipeChoice.printRecipeChoice(getPrimaryIngredient(), RMCChatColor.BLACK, RMCChatColor.BLACK));
        s.append('\n').append(ToolsRecipeChoice.printRecipeChoice(getSecondaryIngredient(), RMCChatColor.BLACK, RMCChatColor.BLACK));

        return s.toString();
    }


    @Override
    public int findItemInIngredients(Material type, Short data) {
        int found = 0;

        if (hasTemplateIngredient()) {
            found += ToolsRecipeChoice.getNumMaterialsInRecipeChoice(type, templateIngredient);
        }
        found += ToolsRecipeChoice.getNumMaterialsInRecipeChoice(type, getPrimaryIngredient());
        found += ToolsRecipeChoice.getNumMaterialsInRecipeChoice(type, getSecondaryIngredient());

        return found;
    }

    @Override
    public List<String> getRecipeIndexesForInput(List<ItemStack> ingredients, ItemStack result) {
        List<String> recipeIndexes = new ArrayList<>();
        if (ingredients.size() == 3) {
            recipeIndexes.add(ingredients.get(0).getType() + "-" + ingredients.get(1).getType() + "-" + ingredients.get(2).getType());
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
