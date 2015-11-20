package haveric.recipeManager.flags;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.event.inventory.InventoryType;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Messages;
import haveric.recipeManagerCommon.util.RMCUtil;

public class FlagInventory extends Flag {

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <inventory type> , ... | [fail message]", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Checks if crafting in the specific type of inventory",
            "",
            "The <inventory type> argument is required",
            "  Values: " + RMCUtil.collectionToString(Arrays.asList(InventoryType.values())).toLowerCase(),
            "",
            "Can declare multiple inventory types seperated by commas",
            "",
            "Optionally you can overwrite the fail message or you can use 'false' to hide it.",
            "In the message the following variables can be used:",
            "  {inventory} = name of inventory type(s)", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} crafting // Player crafting menu",
            "{flag} workbench // Must use a crafting table", };
    }


    private List<InventoryType> inventories = new ArrayList<InventoryType>();
    private String failMessage;

    public FlagInventory() {
    }

    public FlagInventory(FlagInventory flag) {
        inventories = flag.inventories;
    }

    @Override
    public FlagInventory clone() {
        super.clone();
        return new FlagInventory(this);
    }

    public List<InventoryType> getInventories() {
        return inventories;
    }

    public void setInventories(List<InventoryType> listInventories) {
        inventories = listInventories;
    }

    public void addInventory(InventoryType inventory) {
        inventories.add(inventory);
    }

    public String getFailMessage() {
        return failMessage;
    }

    public void setFailMessage(String newFailMessage) {
        failMessage = newFailMessage;
    }

    @Override
    protected boolean onParse(String value) {
        String[] split = value.split("\\|");

        if (split.length > 1) {
            setFailMessage(split[1].trim());
        }

        split = split[0].toLowerCase().split(",");

        for (String arg : split) {
            try {
                addInventory(InventoryType.valueOf(arg.trim().toUpperCase()));
            } catch (IllegalArgumentException e) {
                return ErrorReporter.error("Flag " + getType() + "  has unknown inventory type(s): " + value);
            }
        }

        return true;
    }

    @Override
    protected void onCheck(Args a) {
        boolean success = false;

        if (a.hasInventory()) {
            InventoryType craftedType = a.inventory().getType();

            for (InventoryType type : getInventories()) {
                if (craftedType.equals(type)) {
                    success = true;
                    break;
                }
            }
        }

        if (!success) {
            a.addReason(Messages.FLAG_INVENTORY, failMessage, "{inventory}", getInventories().toString());
        }
    }
}
