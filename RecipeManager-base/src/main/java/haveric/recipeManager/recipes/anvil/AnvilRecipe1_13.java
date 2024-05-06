package haveric.recipeManager.recipes.anvil;

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

import java.util.ArrayList;
import java.util.List;

public class AnvilRecipe1_13 extends PreparableResultRecipe {
    private int repairCost = 0;
    private boolean renamingAllowed = false;
    private double anvilDamageChance = 12;
    private RecipeChoice primaryIngredient;
    private RecipeChoice secondaryIngredient;

    public AnvilRecipe1_13() {

    }

    public AnvilRecipe1_13(BaseRecipe recipe) {
        super(recipe);

        if (recipe instanceof AnvilRecipe1_13) {
            AnvilRecipe1_13 r = (AnvilRecipe1_13) recipe;

            repairCost = r.repairCost;

            renamingAllowed = r.renamingAllowed;
            anvilDamageChance = r.anvilDamageChance;

            if (r.primaryIngredient != null) {
                primaryIngredient = r.primaryIngredient.clone();
            }
            if (r.secondaryIngredient != null) {
                secondaryIngredient = r.secondaryIngredient.clone();
            }

            updateHash();
        }
    }

    public AnvilRecipe1_13(Flags flags) {
        super(flags);
    }

    @Override
    public RMCRecipeType getType() {
        return RMCRecipeType.ANVIL;
    }

    public int getRepairCost() {
        return repairCost;
    }

    public void setRepairCost(int newCost) {
        repairCost = newCost;
    }

    public boolean isRenamingAllowed() {
        return renamingAllowed;
    }

    public void setRenamingAllowed(boolean allowRenaming) {
        this.renamingAllowed = allowRenaming;
    }

    public double getAnvilDamageChance() {
        return anvilDamageChance;
    }

    public void setAnvilDamageChance(double anvilDamageChance) {
        this.anvilDamageChance = anvilDamageChance;
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

    public boolean hasIngredients() {
        return primaryIngredient != null && secondaryIngredient != null;
    }

    private void updateHash() {
        StringBuilder str = new StringBuilder("anvil");

        str.append(" a:");
        str.append(ToolsRecipeChoice.getRecipeChoiceHash(primaryIngredient));

        str.append(" b:");
        str.append(ToolsRecipeChoice.getRecipeChoiceHash(secondaryIngredient));

        hash = str.toString().hashCode();
    }

    @Override
    public void resetName() {
        StringBuilder str = new StringBuilder();

        str.append("anvil (");

        str.append(" a:");
        str.append(ToolsRecipeChoice.getRecipeChoiceName(primaryIngredient));

        str.append(" b:");
        str.append(ToolsRecipeChoice.getRecipeChoiceName(secondaryIngredient));

        str.append(")");

        if (repairCost > 0) {
            str.append("{repairCost: ").append(repairCost).append("}");
        }
        str.append(" to ").append(getResultsString());

        name = str.toString();
        customName = false;
    }

    @Override
    public List<String> getIndexes() {
        List<String> indexString = new ArrayList<>();

        if (primaryIngredient instanceof RecipeChoice.MaterialChoice) {
            for (Material material : ((RecipeChoice.MaterialChoice) primaryIngredient).getChoices()) {
                if (secondaryIngredient instanceof RecipeChoice.MaterialChoice) {
                    for (Material material2 : ((RecipeChoice.MaterialChoice) secondaryIngredient).getChoices()) {
                        indexString.add(material.toString() + "-" + material2.toString());
                    }
                } else if (secondaryIngredient instanceof RecipeChoice.ExactChoice) {
                    for (ItemStack item : ((RecipeChoice.ExactChoice) secondaryIngredient).getChoices()) {
                        indexString.add(material.toString() + "-" + item.getType());
                    }
                }
            }
        } else if (primaryIngredient instanceof RecipeChoice.ExactChoice) {
            for (ItemStack item : ((RecipeChoice.ExactChoice) primaryIngredient).getChoices()) {
                if (secondaryIngredient instanceof RecipeChoice.MaterialChoice) {
                    for (Material material : ((RecipeChoice.MaterialChoice) secondaryIngredient).getChoices()) {
                        indexString.add(item.getType() + "-" + material.toString());
                    }
                } else if (secondaryIngredient instanceof RecipeChoice.ExactChoice) {
                    for (ItemStack item2 : ((RecipeChoice.ExactChoice) secondaryIngredient).getChoices()) {
                        indexString.add(item.getType() + "-" + item2.getType());
                    }
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
    public String printBookResult(ItemResult result) {
        StringBuilder s = getHeaderResult("anvil", result);

        s.append(Messages.getInstance().parse("recipebook.header.ingredients"));

        s.append('\n').append(ToolsRecipeChoice.printRecipeChoice(primaryIngredient, RMCChatColor.BLACK, RMCChatColor.BLACK));
        s.append('\n').append(ToolsRecipeChoice.printRecipeChoice(secondaryIngredient, RMCChatColor.BLACK, RMCChatColor.BLACK));

        s.append("\n\n");
        s.append(Messages.getInstance().parse("recipebook.anvil.repaircost", "{repaircost}", repairCost));

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

    public boolean isValidBlockMaterial(Material material) {
        boolean valid = material == Material.ANVIL;

        if (!valid) {
            valid = material == Material.CHIPPED_ANVIL;

            if (!valid) {
                valid = material == Material.DAMAGED_ANVIL;
            }
        }

        return valid;
    }

    @Override
    public String getInvalidErrorMessage() {
        return super.getInvalidErrorMessage() + " Needs a result and two ingredients!";
    }

    @Override
    public boolean requiresRecipeManagerModification() {
        return true;
    }
}
