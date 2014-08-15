package haveric.recipeManager.flags;

import haveric.recipeManager.Messages;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.BaseRecipe.RecipeType;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.tools.ToolsItem;
import haveric.recipeManager.uuidFetcher.UUIDFetcher;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;


/**
 * Easily modifiable arguments for the flag classes without needing to re-edit all of them
 *
 * @author Digi
 */
public class Args {
    private Player player;
    private String playerName;
    private Location location;
    private BaseRecipe recipe;
    private RecipeType recipeType;
    private Inventory inventory;
    private ItemResult result;
    private Object extra;

    private List<String> reasons;
    private List<String> effects;

    protected Args() {
    }

    public static void init() {
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setRecipe(BaseRecipe recipe) {
        this.recipe = recipe;
    }

    public void setRecipeType(RecipeType recipeType) {
        this.recipeType = recipeType;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public void setResult(ItemResult result) {
        this.result = result;
    }

    public void setExtra(Object extra) {
        this.extra = extra;
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

    public String playerName() {
        return playerName;
    }

    public boolean hasPlayerName() {
        return playerName != null;
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

    public RecipeType recipeType() {
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

    public List<String> reasons() {
        return reasons;
    }

    public boolean hasReasons() {
        return (reasons != null && !reasons.isEmpty());
    }

    public void addCustomReason(String message) {
        if (reasons == null) {
            reasons = new ArrayList<String>();
        }

        debug("reason", message);

        reasons.add(message);
    }

    public void addReason(Messages globalMessage, String customMessage, Object... variables) {
        addCustomReason(globalMessage.getCustom(customMessage, variables));
    }

    public void clearReasons() {
        if (reasons != null) {
            reasons.clear();
        }
    }

    public void sendReasons(CommandSender sender, String prefix) {
        sendList(sender, prefix, reasons);
    }

    public List<String> effects() {
        return effects;
    }

    public boolean hasEffects() {
        return (effects != null && !effects.isEmpty());
    }

    public void addCustomEffect(String message) {
        if (effects == null) {
            effects = new ArrayList<String>();
        }

        debug("effect", message);

        effects.add(message);
    }

    public void addEffect(Messages globalMessage, String customMessage, Object... variables) {
        addCustomEffect(globalMessage.getCustom(customMessage, variables));
    }

    public void clearEffects() {
        if (effects != null) {
            effects.clear();
        }
    }

    public void sendEffects(CommandSender sender, String prefix) {
        sendList(sender, prefix, effects);
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
                Messages.send(sender, prefix + s);
            }
        }
    }

    public String parseVariables(String string) {
        String name;
        if (hasPlayerName()) {
            name = playerName();
        } else {
            name = "(nobody)";
        }

        string = string.replace("{player}", name);
        string = string.replace("{playerdisplay}", (player != null ? player.getDisplayName() : name));
        string = string.replace("{result}", ToolsItem.print(result()));
        string = string.replace("{recipename}", (hasRecipe() ? recipe().getName() : "(unknown)"));
        string = string.replace("{recipetype}", (hasRecipeType() ? recipeType().toString().toLowerCase() : "(unknown)"));
        string = string.replace("{inventorytype}", (hasInventory() ? inventory().getType().toString().toLowerCase() : "(unknown)"));
        string = string.replace("{world}", (hasLocation() ? location().getWorld().getName() : "(unknown)"));
        string = string.replace("{x}", (hasLocation() ? "" + location().getBlockX() : "(?)"));
        string = string.replace("{y}", (hasLocation() ? "" + location().getBlockY() : "(?)"));
        string = string.replace("{z}", (hasLocation() ? "" + location().getBlockZ() : "(?)"));

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
     * For example, if you only set player name, the player() will still be null, but by triggering this it will try to assign player() to an Player object.
     *
     * @return same instance
     */
    public Args processArgs() {
        Player player = player();
        String playerName = playerName();

        if (player == null && playerName != null) {
            try {
                UUID uuid = UUIDFetcher.getUUIDOf(playerName);
                setPlayer(Bukkit.getPlayer(uuid));
            } catch (Exception e) {}
        }

        if (playerName == null && player != null) {
            setPlayerName(player().getName());
        }

        if (location() == null && player != null) {
            setLocation(player().getLocation());
        }

        if (recipeType() == null && recipe() != null) {
            setRecipeType(recipe().getType());
        }

        return this;
    }

    private void debug(String type, String message) {
        // Messages.debug(type + " | " + message);
    }
}
