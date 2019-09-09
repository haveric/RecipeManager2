package haveric.recipeManager.recipes.anvil;

import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.PreparableResultRecipe;
import haveric.recipeManager.tools.ToolsItem;
import haveric.recipeManagerCommon.RMCChatColor;
import haveric.recipeManagerCommon.recipes.RMCRecipeType;
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

        boolean first = true;
        for (Material material : sortedPrimary) {
            if (first) {
                first = false;
            } else {
                str.append(";");
            }
            str.append(material.toString());
        }

        str.append(" + ");

        List<Material> sortedSecondary = new ArrayList<>(secondaryIngredient);
        Collections.sort(sortedSecondary);

        first = true;
        for (Material material : sortedSecondary) {
            if (first) {
                first = false;
            } else {
                str.append(";");
            }
            str.append(material.toString());
        }

        str.append(")");

        if (repairCost > 0) {
            str.append("{repairCost: ").append(repairCost).append("}");
        }
        str.append(" to ").append(getResultsString());

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
        StringBuilder s = getHeaderResult("anvil", result);

        s.append(Messages.getInstance().parse("recipebook.header.ingredients"));

        s.append('\n').append(ToolsItem.printChoice(primaryIngredient, RMCChatColor.BLACK, RMCChatColor.BLACK));
        s.append('\n').append(ToolsItem.printChoice(secondaryIngredient, RMCChatColor.BLACK, RMCChatColor.BLACK));

        s.append("\n\n");
        s.append(Messages.getInstance().parse("recipebook.anvil.repaircost", "{repaircost}", repairCost));

        return s.toString();
    }

    @Override
    public int findItemInIngredients(Material type, Short data) {
        int found = 0;

        List<Material> primary = getPrimaryIngredient();

        for (Material material : primary) {
            if (type == material) {
                found++;
                break;
            }
        }

        List<Material> secondary = getSecondaryIngredient();

        for (Material material : secondary) {
            if (type == material) {
                found++;
                break;
            }
        }

        return found;
    }
}
