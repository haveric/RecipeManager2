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
import haveric.recipeManager.recipes.PreparableResultRecipe;
import haveric.recipeManager.tools.ToolsRecipeChoice;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingRecipe;

import java.util.ArrayList;
import java.util.List;

public class RMSmithingRecipe extends PreparableResultRecipe {
    private RecipeChoice primaryIngredient;
    private RecipeChoice secondaryIngredient;

    public RMSmithingRecipe() {

    }

    public RMSmithingRecipe(SmithingRecipe recipe) {
        setPrimaryIngredient(recipe.getBase());
        setSecondaryIngredient(recipe.getAddition());

        setResult(recipe.getResult());
    }

    public RMSmithingRecipe(BaseRecipe recipe) {
        super(recipe);

        if (recipe instanceof RMSmithingRecipe r) {
            if (r.primaryIngredient != null) {
                primaryIngredient = r.primaryIngredient.clone();
            }
            if (r.secondaryIngredient != null) {
                secondaryIngredient = r.secondaryIngredient.clone();
            }

            updateHash();
        }
    }

    public RMSmithingRecipe(Flags flags) {
        super(flags);
    }

    public boolean isValidBlockMaterial(Material material) {
        return material == Material.SMITHING_TABLE;
    }

    public boolean hasIngredient(char character) {
        if (character == 'a') {
            return primaryIngredient != null;
        } else if (character == 'b') {
            return secondaryIngredient != null;
        }

        return false;
    }

    public RecipeChoice getIngredient(char character) {
        if (character == 'a') {
            return primaryIngredient;
        } else if (character == 'b') {
            return secondaryIngredient;
        }

        return null;
    }

    public void setIngredient(char character, RecipeChoice choice) {
        if (character == 'a') {
            setPrimaryIngredient(choice);
        } else if (character == 'b') {
            setSecondaryIngredient(choice);
        }
    }

    public RecipeChoice getPrimaryIngredient() {
        return primaryIngredient;
    }

    public void setPrimaryIngredient(RecipeChoice choice) {
        primaryIngredient = choice.clone();

        updateHash();
    }

    public RecipeChoice getSecondaryIngredient() {
        return secondaryIngredient;
    }

    public void setSecondaryIngredient(RecipeChoice choice) {
        secondaryIngredient = choice.clone();

        updateHash();
    }

    @Override
    public SmithingRecipe toBukkitRecipe(boolean vanilla) {
        if (!hasIngredients() || !hasResults()) {
            return null;
        }

        SmithingRecipe bukkitRecipe;
        if (vanilla) {
            bukkitRecipe = new SmithingRecipe(getNamespacedKey(), getFirstResult(), primaryIngredient, secondaryIngredient);
        } else {
            ItemResult firstResult = getFirstResult();

            Args a = ArgBuilder.create().result(firstResult).build();
            getFlags().sendPrepare(a, true);
            firstResult.getFlags().sendPrepare(a, true);

            bukkitRecipe = new SmithingRecipe(getNamespacedKey(), a.result(), primaryIngredient, secondaryIngredient);
        }

        return bukkitRecipe;
    }

    public boolean hasIngredients() {
        return primaryIngredient != null && secondaryIngredient != null;
    }

    private void updateHash() {
        StringBuilder str = new StringBuilder("smithing");

        str.append(" a:");
        str.append(ToolsRecipeChoice.getRecipeChoiceHash(primaryIngredient));

        str.append(" b:");
        str.append(ToolsRecipeChoice.getRecipeChoiceHash(secondaryIngredient));

        hash = str.toString().hashCode();
    }

    @Override
    public void resetName() {
        StringBuilder str = new StringBuilder();

        str.append("smithing (");

        str.append(" a:");
        str.append(ToolsRecipeChoice.getRecipeChoiceName(primaryIngredient));

        str.append(" b:");
        str.append(ToolsRecipeChoice.getRecipeChoiceName(secondaryIngredient));

        str.append(")");

        str.append(" to ").append(getResultsString());

        name = str.toString();
        customName = false;
    }

    @Override
    public List<String> getIndexes() {
        List<String> indexString = new ArrayList<>();

        List<String> primaryIndexes = getIndexForChoice(primaryIngredient);
        List<String> secondaryIndexes = getIndexForChoice(secondaryIngredient);

        for (String primaryIndex : primaryIndexes) {
            for (String secondaryIndex : secondaryIndexes) {
                indexString.add(primaryIndex + "-" + secondaryIndex);
            }
        }

        return indexString;
    }

    protected List<String> getIndexForChoice(RecipeChoice choice) {
        List<String> choiceString = new ArrayList<>();
        if (choice instanceof RecipeChoice.MaterialChoice) {
            for (Material material : ((RecipeChoice.MaterialChoice) choice).getChoices()) {
                choiceString.add(material.toString());
            }
        } else if (choice instanceof RecipeChoice.ExactChoice) {
            for (ItemStack item : ((RecipeChoice.ExactChoice) choice).getChoices()) {
                choiceString.add(item.getType().toString());
            }
        }

        return choiceString;
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
        return RMCRecipeType.SMITHING;
    }


    @Override
    public String printBookResult(ItemResult result) {
        StringBuilder s = getHeaderResult("smithing", result);

        s.append(Messages.getInstance().parse("recipebook.header.ingredients"));

        s.append('\n').append(ToolsRecipeChoice.printRecipeChoice(primaryIngredient, RMCChatColor.BLACK, RMCChatColor.BLACK));
        s.append('\n').append(ToolsRecipeChoice.printRecipeChoice(secondaryIngredient, RMCChatColor.BLACK, RMCChatColor.BLACK));

        return s.toString();
    }


    @Override
    public int findItemInIngredients(Material type, Short data) {
        int found = 0;

        found += ToolsRecipeChoice.getNumMaterialsInRecipeChoice(type, primaryIngredient);
        found += ToolsRecipeChoice.getNumMaterialsInRecipeChoice(type, secondaryIngredient);

        return found;
    }

    @Override
    public List<String> getRecipeIndexesForInput(List<ItemStack> ingredients, ItemStack result) {
        List<String> recipeIndexes = new ArrayList<>();
        if (ingredients.size() == 2) {
            recipeIndexes.add(ingredients.get(0).getType() + "-" + ingredients.get(1).getType());
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
}
