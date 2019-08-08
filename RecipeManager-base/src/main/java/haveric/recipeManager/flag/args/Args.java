package haveric.recipeManager.flag.args;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.tools.ToolsItem;
import haveric.recipeManagerCommon.RMCChatColor;
import haveric.recipeManagerCommon.recipes.RMCRecipeType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Easily modifiable arguments for the flag classes without needing to re-edit all of them
 */
public class Args {
    private UUID playerUUID;
    private Player player;
    private Location location;
    private BaseRecipe recipe;
    private RMCRecipeType recipeType;
    private InventoryView inventoryView;
    private Inventory inventory;
    private ItemResult result;
    private Object extra;
    private boolean firstRun = true;

    private List<String> reasons;
    private List<String> effects;

    protected Args() { }

    public static void init() { }

    public void setPlayerUUID(UUID newPlayerUUID) {
        playerUUID = newPlayerUUID;
    }

    public void setPlayer(Player newPlayer) {
        player = newPlayer;
    }

    public void setLocation(Location newLocation) {
        location = newLocation;
    }

    public void setRecipe(BaseRecipe newRecipe) {
        recipe = newRecipe;
    }

    public void setRecipeType(RMCRecipeType newRecipeType) {
        recipeType = newRecipeType;
    }

    public void setInventoryView(InventoryView newInventoryView) {
        inventoryView = newInventoryView;
        setInventory(inventoryView.getTopInventory());
    }

    public void setInventory(Inventory newInventory) {
        inventory = newInventory;
    }

    public void setResult(ItemResult newResult) {
        result = newResult;
    }

    public void setExtra(Object newExtra) {
        extra = newExtra;
    }

    public void setFirstRun(boolean newFirstRun) {
        firstRun = newFirstRun;
    }

    public UUID playerUUID() {
        return playerUUID;
    }

    public boolean hasPlayerUUID() {
        return playerUUID != null;
    }
    /**
     * Gets the Player object from either player() or playerName()
     *
     * @return player object or null if player just doesn't exist
     */
    public Player player() {
        return player;
    }

    public boolean hasPlayer() {
        return player != null;
    }

    /**
     * Gets a location from either location, player or playerName arguments.
     *
     * @return null in case no location could be generated
     */
    public Location location() {
        return location;
    }

    public boolean hasLocation() {
        return location != null;
    }

    public RMCRecipeType recipeType() {
        return recipeType;
    }

    public boolean hasRecipeType() {
        return recipeType != null;
    }

    public BaseRecipe recipe() {
        return recipe;
    }

    public boolean hasRecipe() {
        return recipe != null;
    }

    public InventoryView inventoryView() {
        return inventoryView;
    }

    public boolean hasInventoryView() {
        return inventoryView != null;
    }

    public Inventory inventory() {
        return inventory;
    }

    public boolean hasInventory() {
        return inventory != null;
    }

    public ItemResult result() {
        return result;
    }

    public boolean hasResult() {
        return result != null;
    }

    public Object extra() {
        return extra;
    }

    public boolean hasExtra() {
        return extra != null;
    }

    public boolean isFirstRun() {
        return firstRun;
    }

    public List<String> reasons() {
        return reasons;
    }

    public boolean hasReasons() {
        return (reasons != null && !reasons.isEmpty());
    }

    public void addCustomReason(String message) {
        if (reasons == null) {
            reasons = new ArrayList<>();
        }

        reasons.add(message);
    }

    public void clearReasons() {
        if (reasons != null) {
            reasons.clear();
        }
    }
    public void addReason(String globalMessagePath, String customMessage, Object... variables) {
        addCustomReason(Messages.getInstance().parseCustom(globalMessagePath, customMessage, variables));
    }

    public void sendReasons(CommandSender sender, String prefix) {
        sendList(sender, prefix, reasons());
    }

    public List<String> effects() {
        return effects;
    }

    public boolean hasEffects() {
        return (effects != null && !effects.isEmpty());
    }

    public void addCustomEffect(String message) {
        if (effects == null) {
            effects = new ArrayList<>();
        }

        effects.add(message);
    }

    public void clearEffects() {
        if (effects != null) {
            effects.clear();
        }
    }

    public void addEffect(String globalMessagePath, String customMessage, Object... variables) {
        addCustomEffect(Messages.getInstance().parseCustom(globalMessagePath, customMessage, variables));
    }

    public void sendEffects(CommandSender sender, String prefix) {
        sendList(sender, prefix, effects());
    }

    public void clear() {
        clearReasons();
        clearEffects();
    }

    private void sendList(CommandSender sender, String prefix, List<String> list) {
        if (sender == null || list == null) {
            return;
        }

        for (String s : list) {
            if (s != null) {
                MessageSender.getInstance().send(sender, prefix + s);
            }
        }
    }

    private String parsePosition(String string, String coord) {
        // Check for the start of the variable only as it may contain an offset
        if (!string.contains("{" + coord)) {
            return string;
        }

        Pattern regex = Pattern.compile("\\{[" + coord + "] *([+-])? *(\\d*)}");
        Matcher regexMatcher = regex.matcher(string);

        while (regexMatcher.find()) {
            String group1 = regexMatcher.group(1);
            String group2 = regexMatcher.group(2);

            String group = "";
            if (group1 != null) {
                group += group1;
            }
            if (group2 != null) {
                group += group2;
            }
            int offset = 0;

            if (!group.isEmpty()) {
                offset = Integer.parseInt(group);
            }

            String replaceString = "(?)";
            if (hasLocation()) {
                int blockCoord = offset;

                if (coord.equals("x")) {
                    blockCoord += location.getBlockX();
                } else if (coord.equals("y")) {
                    blockCoord += location.getBlockY();
                } else if (coord.equals("z")) {
                    blockCoord += location.getBlockZ();
                }

                replaceString = "" + blockCoord;
            }
            string = regexMatcher.replaceFirst(replaceString);
            regexMatcher = regex.matcher(string);
        }

        return string;
    }

    private String parseRandom(String string, boolean displayOnly) {
        if (!string.contains("{rand")) {
            return string;
        }

        Pattern regex = Pattern.compile("\\{(?:rand)(?:om)* (-?\\d*\\.?\\d*)(?: *- *(-?\\d*\\.?\\d))? *(?:, *(\\d*))*}");
        Matcher regexMatcher = regex.matcher(string);

        List<String> savedRandoms = new ArrayList<>();
        while (regexMatcher.find()) {
            String group1 = regexMatcher.group(1);
            String group2 = regexMatcher.group(2);
            String group3 = regexMatcher.group(3);

            if (group1 == null || group2 == null) {
                int indexOffset = 1;
                if (group1 != null) {
                    try {
                        indexOffset = Integer.parseInt(group1);
                    } catch (NumberFormatException e) {
                        ErrorReporter.getInstance().warning("A single parameter {rand} requires an integer: " + string + ". Example: {rand 1} or {rand 2}.");
                    }
                }

                String replaceString;
                if (displayOnly) {
                    replaceString = "{" + indexOffset + "}";
                } else {
                    if (savedRandoms.isEmpty()) {
                        ErrorReporter.getInstance().warning("Non-first {rand} needs at least two numbers to parse: " + string + ". Example: {rand 1-2} or {rand 1.0-2.0, 2}.");
                        return string;
                    } else if (savedRandoms.size() < indexOffset) {
                        ErrorReporter.getInstance().warning("Non-first {rand} trying to reference non-existing {rand} index: " + indexOffset + " .From: " + string);
                        return string;
                    } else if (indexOffset < 1) {
                        ErrorReporter.getInstance().warning("Non-first {rand} index: " + indexOffset + " must be greater than zero. From: " + string);
                        return string;
                    } else {
                        replaceString = savedRandoms.get(indexOffset - 1);
                    }
                }

                string = regexMatcher.replaceFirst(replaceString);
                regexMatcher = regex.matcher(string);
                continue;
            }

            int decimals = 0;
            if (group3 != null) {
                try {
                    decimals = Integer.parseInt(group3);
                } catch (NumberFormatException e) {
                    ErrorReporter.getInstance().warning("{rand #1-#2, #3} of " + string + " has invalid decimal(#3) value:" + group3 + ". Defaulting to 0.");
                }
            }

            double min;
            double max;
            try {
                min = Double.parseDouble(group1);
            } catch (NumberFormatException e) {
                ErrorReporter.getInstance().warning("{rand #1-#2, #3} of " + string + " has invalid number(#1) value:" + group1);
                return string;
            }

            try {
                max = Double.parseDouble(group2);
            } catch (NumberFormatException e) {
                ErrorReporter.getInstance().warning("{rand #1-#2, #3} of " + string + " has invalid number(#1) value:" + group1);
                return string;
            }

            if (max < min) {
                ErrorReporter.getInstance().warning("{rand #1-#2, #3} of " + string + " has max(#2): " + max + " less than min(#1): " + min);
                return string;
            }

            String replaceString;
            if (displayOnly) {
                String minString = String.format("%." + decimals + "f", min);
                String maxString = String.format("%." + decimals + "f", max);

                replaceString = "{" + minString + "-" + maxString + "}";
            } else {
                double generated = RecipeManager.random.nextDouble();
                double random = min + (generated * (max - min));
                String formattedRandom = String.format("%." + decimals + "f", random);
                savedRandoms.add(formattedRandom);
                replaceString = formattedRandom;
            }

            string = regexMatcher.replaceFirst(replaceString);
            regexMatcher = regex.matcher(string);
        }

        return string;
    }

    public String parseVariables(String string) {
        return parseVariables(string, false);
    }

    public String parseVariables(String string, boolean displayOnly) {
        String name = "";

        boolean containsPlayer = string.contains("{player}");
        boolean containsPlayerDisplay = string.contains("{playerdisplay}");

        if (containsPlayer || containsPlayerDisplay) {
            if (hasPlayerUUID()) {
                name = Bukkit.getOfflinePlayer(playerUUID).getName();
            } else {
                name = "(nobody)";
            }
        }

        if (containsPlayer) {
            string = string.replace("{player}", name);
        }
        if (containsPlayerDisplay) {
            string = string.replace("{playerdisplay}", (player != null ? player.getDisplayName() : name));
        }
        if (string.contains("{recipename}")) {
            string = string.replace("{recipename}", (hasRecipe() ? recipe().getName() : "(unknown)"));
        }
        if (string.contains("{recipetype}")) {
            string = string.replace("{recipetype}", (hasRecipeType() ? recipeType().toString().toLowerCase() : "(unknown)"));
        }
        if (string.contains("{inventorytype}")) {
            string = string.replace("{inventorytype}", (hasInventoryView() ? inventoryView().getType().toString().toLowerCase() : "(unknown)"));
        }
        if (string.contains("{world}")) {
            string = string.replace("{world}", (hasLocation() ? location().getWorld().getName() : "(unknown)"));
        }

        string = parsePosition(string, "x");
        string = parsePosition(string, "y");
        string = parsePosition(string, "z");

        string = parseRandom(string, displayOnly);

        if (hasResult()) {
            if (string.contains("{result}")) {
                string = string.replace("{result}", ToolsItem.print(result()));
            }

            ItemMeta meta = result.getItemMeta();

            if (meta != null) {
                if (meta.hasLore() && string.contains("{lore}")) {
                    string = string.replace("{lore}", "\"" + StringUtils.join(meta.getLore(), "\",\"") + "\"");
                }
                if (meta instanceof BookMeta) {
                    BookMeta book = (BookMeta) meta;
                    if (book.hasTitle() && string.contains("{booktitle}")) {
                        string = string.replace("{booktitle}", book.getTitle());
                    }
                    if (book.hasAuthor() && string.contains("{bookauthor}")) {
                        string = string.replace("{bookauthor}", book.getAuthor());
                    }
                    if (book.hasPages() && string.contains("{bookpages}")) {
                        String pages = "";
                        int numPages = book.getPageCount();
                        for (int i = 1; i <= numPages; i++) {
                            String page = book.getPage(i);
                            pages += "\"{text:\\\"" + page + "\\\"}\"";

                            if (i < numPages) {
                                pages += ",";
                            }
                        }

                        pages = RMCChatColor.stripColor(pages);
                        pages = pages.replace("\n", "\\\\n");
                        string = string.replace("{bookpages}", pages);
                    }
                }
            }
        }

        return string;
    }

    /**
     * Start building an argument class for flag events
     *
     * @return linkable methods
     */
    public static ArgBuilder create() {
        return new ArgBuilder();
    }

    /**
     * Re-processes the arguments to assign them in as many places as possible.<br>
     * For example, if you only set player UUID, the player() will still be null, but by triggering this it will try to assign player() to a Player object.
     *
     * @return same instance
     */
    public Args processArgs() {
        Player player = player();
        UUID playerUUID = playerUUID();

        if (player == null && playerUUID != null) {
            setPlayer(Bukkit.getPlayer(playerUUID));
        }

        if (playerUUID == null && player != null) {
            setPlayerUUID(player().getUniqueId());
        }

        if (location() == null && player != null) {
            setLocation(player().getLocation());
        }

        if (recipeType() == null && recipe() != null) {
            setRecipeType(recipe().getType());
        }

        return this;
    }
}
