package haveric.recipeManager.recipes.smithing;

import haveric.recipeManager.common.RMCChatColor;
import haveric.recipeManager.common.recipes.RMCRecipeType;
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
import org.bukkit.inventory.SmithingRecipe;
import org.bukkit.inventory.SmithingTransformRecipe;

import java.util.ArrayList;
import java.util.List;

public class RMSmithingTransformRecipe extends RMSmithingRecipe {
    private RecipeChoice templateIngredient;

    public RMSmithingTransformRecipe() {

    }

    public RMSmithingTransformRecipe(SmithingTransformRecipe recipe) {
        super(recipe);

        setTemplateIngredient(recipe.getTemplate());
    }

    public RMSmithingTransformRecipe(BaseRecipe recipe) {
        super(recipe);

        if (recipe instanceof RMSmithingTransformRecipe) {
            RMSmithingTransformRecipe r = (RMSmithingTransformRecipe) recipe;

            if (r.templateIngredient != null) {
                templateIngredient = r.templateIngredient.clone();
            }

            updateHash();
        }
    }

    public RMSmithingTransformRecipe(Flags flags) {
        super(flags);
    }

    public boolean isValidBlockMaterial(Material material) {
        return material == Material.SMITHING_TABLE;
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
    public SmithingRecipe toBukkitRecipe(boolean vanilla) {
        if (!hasIngredients() || !hasResults()) {
            return null;
        }

        SmithingRecipe bukkitRecipe;

        if (hasTemplateIngredient()) {
            if (vanilla) {
                bukkitRecipe = new SmithingTransformRecipe(getNamespacedKey(), getFirstResult(), templateIngredient, getPrimaryIngredient(), getSecondaryIngredient());
            } else {
                ItemResult firstResult = getFirstResult();

                Args a = ArgBuilder.create().result(firstResult).build();
                getFlags().sendPrepare(a, true);
                firstResult.getFlags().sendPrepare(a, true);

                bukkitRecipe = new SmithingTransformRecipe(getNamespacedKey(), a.result(), templateIngredient, getPrimaryIngredient(), getSecondaryIngredient());
            }
        } else {
            if (vanilla) {
                bukkitRecipe = new SmithingRecipe(getNamespacedKey(), getFirstResult(), getPrimaryIngredient(), getSecondaryIngredient());
            } else {
                ItemResult firstResult = getFirstResult();

                Args a = ArgBuilder.create().result(firstResult).build();
                getFlags().sendPrepare(a, true);
                firstResult.getFlags().sendPrepare(a, true);

                bukkitRecipe = new SmithingRecipe(getNamespacedKey(), a.result(), getPrimaryIngredient(), getSecondaryIngredient());
            }
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

        List<String> templateIndexes = getIndexForChoice(templateIngredient);
        List<String> primaryIndexes = getIndexForChoice(getPrimaryIngredient());
        List<String> secondaryIndexes = getIndexForChoice(getSecondaryIngredient());

        if (hasTemplateIngredient()) {
            for (String templateIndex : templateIndexes) {
                for (String primaryIndex : primaryIndexes) {
                    for (String secondaryIndex : secondaryIndexes) {
                        indexString.add(templateIndex + "-" + primaryIndex + "-" + secondaryIndex);
                    }
                }
            }
        } else {
            for (String primaryIndex : primaryIndexes) {
                for (String secondaryIndex : secondaryIndexes) {
                    indexString.add(Material.AIR + "-" + primaryIndex + "-" + secondaryIndex);
                }
            }
        }

        return indexString;
    }

    @Override
    public boolean isValid() {
        return hasIngredients() && hasResults();
    }

    @Override
    public RMCRecipeType getType() {
        return RMCRecipeType.SMITHING;
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
        if (ingredients.size() == 2) {
            recipeIndexes.add(ingredients.get(0).getType() + "-" + ingredients.get(1).getType());
        } else if (ingredients.size() == 3) {
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
