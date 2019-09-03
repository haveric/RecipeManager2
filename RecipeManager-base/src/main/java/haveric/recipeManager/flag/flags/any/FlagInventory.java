package haveric.recipeManager.flag.flags.any;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManagerCommon.util.RMCUtil;
import org.bukkit.event.inventory.InventoryType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FlagInventory extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.INVENTORY;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <inventory type> , ... | [arguments]", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Checks if crafting in the specific type of inventory",
            "",
            "The <inventory type> argument is required",
            "  Values: " + RMCUtil.collectionToString(Arrays.asList(InventoryType.values())).toLowerCase(),
            "",
            "Can declare multiple inventory types separated by commas",
            "",
            "",
            "Optional arguments:",
            "  title <text>      - Add an inventory title restriction",
            "    You can add more titles seperated by a , character to set the allowed titles.",
            "    Also you can disallow titles by prefixing them with a ! character.",
            "    Checks if any allowed titles are matched and all disallowed titles are not matched.",
            "",
            "",
            "  failmsg <message> - Overwrite the fail message or you can use 'false' to hide it.",
            "    In the message the following variables can be used:",
            "      {inventory} = name of inventory type(s)",
            "      {title}     = title of inventory", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} crafting // Player crafting menu",
            "{flag} workbench // Must use a crafting table",
            "{flag} workbench | title Custom // Must use a crafting table named 'Custom'", };
    }


    private List<InventoryType> inventories = new ArrayList<>();
    private List<String> allowedTitles = new ArrayList<>();
    private List<String> unallowedTitles = new ArrayList<>();

    private String failMessage;

    public FlagInventory() {
    }

    public FlagInventory(FlagInventory flag) {
        inventories = flag.inventories;
        allowedTitles.addAll(flag.allowedTitles);
        unallowedTitles.addAll(flag.unallowedTitles);
        failMessage = flag.failMessage;
    }

    @Override
    public FlagInventory clone() {
        return new FlagInventory((FlagInventory) super.clone());
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

    public boolean hasTitles() {
        return !allowedTitles.isEmpty() || !unallowedTitles.isEmpty();
    }
    public List<String> getAllowedTitles() {
        return allowedTitles;
    }

    public void addAllowedTitle(String title) {
        allowedTitles.add(RMCUtil.parseColors(title, false));
    }

    public List<String> getUnallowedTitles() {
        return unallowedTitles;
    }

    public void addUnallowedTitle(String title) {
        unallowedTitles.add(RMCUtil.parseColors(title, false));
    }

    public String getFailMessage() {
        return failMessage;
    }

    public void setFailMessage(String newFailMessage) {
        failMessage = newFailMessage;
    }

    @Override
    public boolean onParse(String value) {
        String[] split = value.split("\\|");

        if (split.length > 1) {
            for (int i = 1; i < split.length; i++) {
                value = split[i].trim();

                if (value.toLowerCase().startsWith("title")) {
                    String[] titles = value.substring("title".length()).split(",");

                    for (String title : titles) {
                        title = title.trim();

                        boolean not = title.charAt(0) == '!';
                        if (not) {
                            addUnallowedTitle(RMCUtil.trimExactQuotes(title.substring(1)));
                        } else {
                            addAllowedTitle(RMCUtil.trimExactQuotes(title));
                        }
                    }
                } else if (value.toLowerCase().startsWith("failmsg")) {
                    setFailMessage(RMCUtil.trimExactQuotes(value.substring("failmsg".length())));
                }
            }
        }

        split = split[0].toLowerCase().split(",");

        for (String arg : split) {
            try {
                addInventory(InventoryType.valueOf(arg.trim().toUpperCase()));
            } catch (IllegalArgumentException e) {
                return ErrorReporter.getInstance().error("Flag " + getFlagType() + "  has unknown inventory type(s): " + value);
            }
        }

        return true;
    }

    @Override
    public void onCheck(Args a) {
        boolean success = false;

        if (a.hasInventoryView()) {
            InventoryType craftedType = a.inventoryView().getType();

            for (InventoryType type : getInventories()) {
                if (craftedType.equals(type)) {
                    success = true;
                    break;
                }
            }

            if (hasTitles()) {
                String inventoryTitle = a.inventoryView().getTitle();

                boolean allowed = false;

                if (allowedTitles.isEmpty()) {
                    allowed = true;
                } else {
                    for (String title : allowedTitles) {
                        if (inventoryTitle.equals(title)) {
                            allowed = true;
                            break;
                        }
                    }
                }

                boolean notAllowed = true;
                for (String title : unallowedTitles) {
                    if (inventoryTitle.equals(title)) {
                        notAllowed = false;
                        break;
                    }
                }

                success = allowed && notAllowed;
            }
        }

        if (!success) {
            a.addReason("flag.inventory", failMessage, "{inventory}", getInventories().toString(), "{title}", a.inventoryView().getTitle());
        }
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        toHash += "inventories: ";
        for (InventoryType type : inventories) {
            toHash += type.toString();
        }

        toHash += "allowedTitles: ";
        for (String title : allowedTitles) {
            toHash += title;
        }

        toHash += "unallowedTitles: ";
        for (String title : unallowedTitles) {
            toHash += title;
        }

        toHash += "failMessage: " + failMessage;

        return toHash.hashCode();
    }
}
