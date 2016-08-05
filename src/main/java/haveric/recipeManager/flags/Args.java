package haveric.recipeManager.flags;

import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.tools.ToolsItem;
import haveric.recipeManagerCommon.recipes.RMCRecipeType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Easily modifiable arguments for the flag classes without needing to re-edit all of them
 */
public class Args {
    private UUID playerUUID;
    private Player player;
    private Location location;
    private BaseRecipe recipe;
    private RMCRecipeType recipeType;
    private Inventory inventory;
    private ItemResult result;
    private Object extra;

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

    public void setInventory(Inventory newInventory) {
        inventory = newInventory;
    }

    public void setResult(ItemResult newResult) {
        result = newResult;
    }

    public void setExtra(Object newExtra) {
        extra = newExtra;
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

    public String parseVariables(String string) {
        String name;
        if (hasPlayerUUID()) {
            name = Bukkit.getOfflinePlayer(playerUUID).getName();
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
