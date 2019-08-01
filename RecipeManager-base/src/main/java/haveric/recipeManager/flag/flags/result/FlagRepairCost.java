package haveric.recipeManager.flag.flags.result;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.recipes.ItemResult;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

import java.util.ArrayList;
import java.util.List;

public class FlagRepairCost extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.REPAIR_COST;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <text or false>", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Changes result's repair cost.", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} 1 // Sets the default repair cost to 1",
            "{flag} 25 // Sets the default repair cost to 25", };
    }


    private int cost;

    public FlagRepairCost() {
    }

    public FlagRepairCost(FlagRepairCost flag) {
        cost = flag.cost;
    }

    @Override
    public FlagRepairCost clone() {
        return new FlagRepairCost((FlagRepairCost) super.clone());
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int newCost) {
        cost = newCost;
    }

    @Override
    public boolean onValidate() {
        ItemResult result = getResult();

        if (result == null || !(result.getItemMeta() instanceof Repairable)) {
            ErrorReporter.getInstance().error("Flag " + getFlagType() + " needs a repairable result!");
            return false;
        }

        return true;
    }

    @Override
    public boolean onParse(String value) {
        try {
            setCost(Integer.parseInt(value.trim()));
        } catch (NumberFormatException e) {
            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid number: " + value);
            return false;
        }

        return true;
    }

    @Override
    public void onPrepare(Args a) {
        if (!a.hasResult()) {
            a.addCustomReason("Needs result!");
            return;
        }

        ItemMeta meta = a.result().getItemMeta();
        if (!(meta instanceof Repairable)) {
            return;
        }

        List<String> lores = meta.getLore();
        if (lores == null) {
            lores = new ArrayList<>();
        }
        lores.add("Repair cost: " + getCost());

        meta.setLore(lores);

        a.result().setItemMeta(meta);
    }

    @Override
    public void onCrafted(Args a) {
        if (!a.hasResult()) {
            a.addCustomReason("Needs result!");
            return;
        }

        ItemMeta meta = a.result().getItemMeta();
        if (!(meta instanceof Repairable)) {
            return;
        }

        Repairable repairable = (Repairable) meta;

        repairable.setRepairCost(getCost());

        a.result().setItemMeta(meta);
    }
}
