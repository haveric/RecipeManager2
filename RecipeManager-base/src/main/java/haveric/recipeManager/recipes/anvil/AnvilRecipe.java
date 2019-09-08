package haveric.recipeManager.recipes.anvil;

import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.flag.flags.any.FlagItemName;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.PreparableResultRecipe;
import haveric.recipeManager.tools.ToolsItem;
import haveric.recipeManagerCommon.RMCChatColor;
import haveric.recipeManagerCommon.recipes.RMCRecipeType;
import haveric.recipeManagerCommon.util.RMCUtil;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AnvilRecipe extends PreparableResultRecipe {
    private List<Material> primaryIngredient = new ArrayList<>();
    private List<Material> secondaryIngredient = new ArrayList<>();
    private int repairCost = 0;
    private boolean renamingAllowed = false;

    public AnvilRecipe() {

    }

    public AnvilRecipe(BaseRecipe recipe) {
        super(recipe);

        if (recipe instanceof AnvilRecipe) {
            AnvilRecipe r = (AnvilRecipe) recipe;

            setPrimaryIngredient(r.primaryIngredient);
            setSecondaryIngredient(r.secondaryIngredient);

            repairCost = r.repairCost;

            renamingAllowed = r.renamingAllowed;

            updateHash();
        }
    }

    public AnvilRecipe(Flags flags) {
        super(flags);
    }

    public List<Material> getPrimaryIngredient() {
        return primaryIngredient;
    }

    public void setPrimaryIngredient(List<Material> ingredientList) {
        primaryIngredient.clear();
        primaryIngredient.addAll(ingredientList);

        updateHash();
    }

    public List<Material> getSecondaryIngredient() {
        return secondaryIngredient;
    }

    public void setSecondaryIngredient(List<Material> ingredientList) {
        secondaryIngredient.clear();
        secondaryIngredient.addAll(ingredientList);

        updateHash();
    }

    public boolean hasIngredients() {
        return primaryIngredient != null && !primaryIngredient.isEmpty() && secondaryIngredient != null && !secondaryIngredient.isEmpty();
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

    private void updateHash() {
        StringBuilder str = new StringBuilder("anvil");

        List<Material> sortedPrimary = new ArrayList<>(primaryIngredient);
        Collections.sort(sortedPrimary);

        for (Material material : sortedPrimary) {
            str.append(material.toString()).append(";");
        }

        str.append(" + ");

        List<Material> sortedSecondary = new ArrayList<>(secondaryIngredient);
        Collections.sort(sortedSecondary);

        for (Material material : sortedSecondary) {
            str.append(material.toString()).append(";");
        }

        hash = str.toString().hashCode();
    }

    @Override
    public void resetName() {
        StringBuilder str = new StringBuilder();

        str.append("anvil (");

        List<Material> sortedPrimary = new ArrayList<>(primaryIngredient);
        Collections.sort(sortedPrimary);

        for (Material material : sortedPrimary) {
            str.append(material.toString()).append(";");
        }

        str.append(" + ");

        List<Material> sortedSecondary = new ArrayList<>(secondaryIngredient);
        Collections.sort(sortedSecondary);

        for (Material material : sortedSecondary) {
            str.append(material.toString()).append(";");
        }

        str.append(")");

        if (repairCost > 0) {
            str.append("{repairCost: ").append(repairCost).append(" }");
        }
        str.append(" ").append(getResultsString());

        name = str.toString();
        customName = false;
    }

    public List<String> getIndexString() {
        List<String> indexString = new ArrayList<>();

        for (Material primary : primaryIngredient) {
            for (Material secondary : secondaryIngredient) {
                indexString.add(primary.toString() + "-" + secondary.toString());
            }
        }

        return indexString;
    }

    @Override
    public boolean isValid() {
        return hasIngredients();
    }

    @Override
    public RMCRecipeType getType() {
        return RMCRecipeType.ANVIL;
    }

    @Override
    public String printBookResult(ItemResult result) {
        StringBuilder s = new StringBuilder(256);

        s.append(Messages.getInstance().parse("recipebook.header.anvil"));

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

        s.append('\n').append(ToolsItem.printChoice(primaryIngredient, RMCChatColor.BLACK, RMCChatColor.BLACK));
        s.append('\n').append(ToolsItem.printChoice(secondaryIngredient, RMCChatColor.BLACK, RMCChatColor.BLACK));

        s.append("\n\n");
        s.append(Messages.getInstance().parse("recipebook.anvil.repaircost", "{repaircost}", repairCost));

        return s.toString();
    }
}
