package haveric.recipeManager.recipes.stonecutting;

import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.flag.conditions.ConditionsIngredient;
import haveric.recipeManager.flag.flags.any.FlagIngredientCondition;
import haveric.recipeManager.flag.flags.result.FlagItemName;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.SingleResultRecipe;
import haveric.recipeManager.tools.ToolsItem;
import haveric.recipeManagerCommon.RMCChatColor;
import haveric.recipeManagerCommon.recipes.RMCRecipeType;
import haveric.recipeManagerCommon.util.RMCUtil;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.StonecuttingRecipe;

import java.util.ArrayList;
import java.util.List;

public class RMStonecuttingRecipe extends SingleResultRecipe {
    private List<Material> ingredientChoice = new ArrayList<>();

    private int hash;

    public RMStonecuttingRecipe() {

    }

    public RMStonecuttingRecipe(BaseRecipe recipe) {
        super(recipe);

        if (recipe instanceof RMStonecuttingRecipe) {
            RMStonecuttingRecipe r = (RMStonecuttingRecipe) recipe;

            if (r.ingredientChoice == null) {
                ingredientChoice = null;
            } else {
                ingredientChoice.addAll(r.ingredientChoice);
            }

            hash = r.hash;
        }
    }

    public RMStonecuttingRecipe(Flags flags) {
        super(flags);
    }

    public RMStonecuttingRecipe(StonecuttingRecipe recipe) {
        RecipeChoice choice = recipe.getInputChoice();
        if (choice instanceof RecipeChoice.MaterialChoice) {
            RecipeChoice.MaterialChoice materialChoice = (RecipeChoice.MaterialChoice) choice;

            setIngredientChoice(materialChoice.getChoices());
        }

        setResult(recipe.getResult());
    }

    public List<Material> getIngredientChoice() {
        return ingredientChoice;
    }

    public void setIngredientChoice(List<Material> materials) {
        RecipeChoice.MaterialChoice materialChoice = new RecipeChoice.MaterialChoice(materials);
        setIngredientChoice(materialChoice);
    }

    private void setIngredientChoice(RecipeChoice choice) {
        if (choice instanceof RecipeChoice.MaterialChoice) {
            ingredientChoice.clear();
            RecipeChoice.MaterialChoice materialChoice = (RecipeChoice.MaterialChoice) choice;
            ingredientChoice.addAll(materialChoice.getChoices());

            String newHash = "campfire";

            int size = ingredientChoice.size();
            for (int i = 0; i < size; i++) {
                newHash += ingredientChoice.get(i).toString();

                if (i + 1 < size) {
                    newHash += ", ";
                }
            }

            hash = newHash.hashCode();
        }

        updateHash();
    }

    @Override
    public void setResult(ItemStack newResult) {
        Validate.notNull(newResult);

        if (newResult instanceof ItemResult) {
            result = ((ItemResult) newResult).setRecipe(this);
        } else {
            result = new ItemResult(newResult).setRecipe(this);
        }

        updateHash();
    }

    private void updateHash() {
        if (ingredientChoice != null && result != null) {
            String newHash = "stonecutting";

            int size = ingredientChoice.size();
            for (int i = 0; i < size; i++) {
                newHash += ingredientChoice.get(i).toString();

                if (i + 1 < size) {
                    newHash += ", ";
                }
            }

            newHash += " - " + result.getType().toString();

            hash = newHash.hashCode();
        }
    }

    @Override
    public void resetName() {
        StringBuilder s = new StringBuilder();
        boolean removed = hasFlag(FlagType.REMOVE);

        s.append("stonecutting ");

        int size = ingredientChoice.size();
        for (int i = 0; i < size; i++) {
            s.append(ingredientChoice.get(i).toString().toLowerCase());

            if (i + 1 < size) {
                s.append(", ");
            }
        }

        s.append(" to ");

        if (removed) {
            s.append("removed recipe");
        } else {
            s.append(getResultString());
        }

        name = s.toString();
        customName = false;
    }

    public List<String> getIndexString() {
        List<String> indexString = new ArrayList<>();

        for (Material material : ingredientChoice) {
            indexString.add(material.toString() + " - " + result.getType().toString());
        }

        return indexString;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof StonecuttingRecipe && hash == obj.hashCode();
    }

    @Override
    public StonecuttingRecipe toBukkitRecipe(boolean vanilla) {
        if (!hasIngredientChoice() || !hasResult()) {
            return null;
        }

        return new StonecuttingRecipe(getNamespacedKey(), getResult(), new RecipeChoice.MaterialChoice(getIngredientChoice()));
    }

    public boolean hasIngredientChoice() {
        return ingredientChoice != null;
    }

    @Override
    public boolean isValid() {
        return hasIngredientChoice() && (hasFlag(FlagType.REMOVE) || hasFlag(FlagType.RESTRICT) || hasResult());
    }

    @Override
    public RMCRecipeType getType() {
        return RMCRecipeType.STONECUTTING;
    }

    @Override
    public List<String> printBookIndices() {
        List<String> print = new ArrayList<>();

        if (hasCustomName()) {
            print.add(RMCChatColor.ITALIC + getName());
        } else {
            print.add(getResultPrintName(getResult()));
        }

        return print;
    }

    private String getResultPrintName(ItemResult result) {
        String print;

        if (result.hasFlag(FlagType.ITEM_NAME)) {
            FlagItemName flag = (FlagItemName)result.getFlag(FlagType.ITEM_NAME);
            print = RMCUtil.parseColors(flag.getPrintName(), false);
        } else {
            print = ToolsItem.getName(getResult());
        }

        return print;
    }

    @Override
    public List<String> printBookRecipes() {
        List<String> recipes = new ArrayList<>();

        recipes.add(printBookResult(getResult()));

        return recipes;
    }

    public String printBookResult(ItemResult result) {
        StringBuilder s = new StringBuilder(256);

        s.append(Messages.getInstance().parse("recipebook.header.stonecutting"));

        if (hasCustomName()) {
            s.append('\n').append(RMCChatColor.BLACK).append(RMCChatColor.ITALIC).append(getName());
        }

        s.append('\n').append(RMCChatColor.GRAY).append('=');

        if (result.hasFlag(FlagType.ITEM_NAME)) {
            FlagItemName flag = (FlagItemName)result.getFlag(FlagType.ITEM_NAME);
            s.append(RMCChatColor.BLACK).append(RMCUtil.parseColors(flag.getPrintName(), false));
        } else {
            s.append(ToolsItem.print(getResult(), RMCChatColor.DARK_GREEN, null));
        }

        /*
         * if(isMultiResult()) { s.append('\n').append(MessagesOld.RECIPEBOOK_MORERESULTS.get("{amount}", (getResults().size() - 1))); }
         */

        s.append("\n\n");
        s.append(Messages.getInstance().parse("recipebook.header.ingredient")).append(RMCChatColor.BLACK);

        String print = "";
        if (result.hasFlag(FlagType.INGREDIENT_CONDITION)) {
            FlagIngredientCondition flag = (FlagIngredientCondition) result.getFlag(FlagType.INGREDIENT_CONDITION);
            List<ConditionsIngredient> conditions = flag.getIngredientConditions(result);

            if (conditions.size() > 0) {
                ConditionsIngredient condition = conditions.get(0);

                if (condition.hasName()) {
                    print = RMCChatColor.BLACK + condition.getName();
                } else if (condition.hasLore()) {
                    print = RMCChatColor.BLACK + "" + RMCChatColor.ITALIC + condition.getLores().get(0);
                }
            }
        }

        if (print.equals("")) {
            print = ToolsItem.printChoice(getIngredientChoice(), RMCChatColor.RESET, RMCChatColor.BLACK);
        }

        s.append('\n').append(print);

        return s.toString();
    }
    /*
    public void subtractIngredient(FurnaceInventory inv, ItemResult result, boolean onlyExtra) {
        FlagIngredientCondition flagIC;
        if (hasFlag(FlagType.INGREDIENT_CONDITION)) {
            flagIC = (FlagIngredientCondition) getFlag(FlagType.INGREDIENT_CONDITION);
        } else {
            flagIC = null;
        }

        if (flagIC == null && result != null && result.hasFlag(FlagType.INGREDIENT_CONDITION)) {
            flagIC = (FlagIngredientCondition) result.getFlag(FlagType.INGREDIENT_CONDITION);
        }

        ItemStack item = inv.getSmelting();
        if (item != null) {
            int amt = item.getAmount();
            int newAmt = amt;

            if (flagIC != null) {
                List<ConditionsIngredient> condList = flagIC.getIngredientConditions(item);

                for (ConditionsIngredient cond : condList) {
                    if (cond != null && cond.checkIngredient(item, ArgBuilder.create().build())) {
                        if (cond.getAmount() > 1) {
                            newAmt -= (cond.getAmount() - 1);
                        }
                    }
                }
            }

            if (!onlyExtra) {
                newAmt -= 1;
            }

            if (amt != newAmt) {
                if (newAmt > 0) {
                    item.setAmount(newAmt);
                } else {
                    inv.setSmelting(null);
                }
            }
        }
    }
    */
}
