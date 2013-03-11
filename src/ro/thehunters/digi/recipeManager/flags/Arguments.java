package ro.thehunters.digi.recipeManager.flags;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.Tools;
import ro.thehunters.digi.recipeManager.recipes.BaseRecipe.RecipeType;

/**
 * Easily modifiable arguments for the flag classes without needing to re-edit all of them
 * 
 * @author Digi
 */
public class Arguments
{
    private Player       player;
    private String       playerName;
    private Location     location;
    private RecipeType   recipeType;
    private ItemStack    result;
    private List<String> reasons;
    private List<String> effects;
    
    /**
     * 
     * @param player
     *            player instance if you have it, enter playerName if you only have that
     * @param playerName
     *            automatically filled if player is not null, otherwise enter it manually if you have it!
     * @param location
     *            the location if available, enter null otherwise
     * @param recipeType
     *            the recipe type if appliable, null otherwise
     * @param result
     *            the recipe's result if appliable, null otherwise
     */
    public Arguments(Player player, String playerName, Location location, RecipeType recipeType, ItemStack result)
    {
        this.player = (player != null ? player : (playerName != null ? Bukkit.getPlayerExact(playerName) : null));
        this.playerName = (this.player != null ? this.player.getName() : playerName);
        this.location = (location != null ? location : (player != null ? player.getLocation() : null));
        this.recipeType = recipeType;
        this.result = result;
    }
    
    /**
     * Gets the Player object from either player() or playerName()
     * 
     * @return player object or null if player just doesn't exist
     */
    public Player player()
    {
        return player;
    }
    
    public boolean hasPlayer()
    {
        return player != null;
    }
    
    public String playerName()
    {
        return playerName;
    }
    
    public boolean hasPlayerName()
    {
        return playerName != null;
    }
    
    /**
     * Gets a location from either location, player or playername arguments.
     * 
     * @return null in case no location could be generated
     */
    public Location location()
    {
        return location;
    }
    
    public boolean hasLocation()
    {
        return location != null;
    }
    
    public RecipeType recipeType()
    {
        return recipeType;
    }
    
    public boolean hasRecipeType()
    {
        return recipeType != null;
    }
    
    public ItemStack result()
    {
        return result;
    }
    
    public boolean hasResult()
    {
        return result != null;
    }
    
    public List<String> reasons()
    {
        return reasons;
    }
    
    public boolean hasReasons()
    {
        return (reasons != null && !reasons.isEmpty());
    }
    
    // TODO remove:
    public void addReason(String message)
    {
        if(reasons == null)
            reasons = new ArrayList<String>();
        
        reasons.add(message);
    }
    
    public void addReason(Messages globalMessage, String customMessage, String... variables)
    {
        addReason(globalMessage.getCustom(customMessage, variables));
    }
    
    public void clearReasons()
    {
        if(reasons != null)
            reasons.clear();
    }
    
    public void sendReasons(CommandSender sender)
    {
        if(sender == null || reasons == null)
            return;
        
        for(String s : reasons)
        {
            Messages.send(sender, s);
        }
    }
    
    public List<String> effects()
    {
        return effects;
    }
    
    public boolean hasEffects()
    {
        return (effects != null && !effects.isEmpty());
    }
    
    public void addEffect(String message)
    {
        if(effects == null)
            effects = new ArrayList<String>();
        
        effects.add(message);
    }
    
    public void addEffect(Messages globalMessage, String customMessage, String... variables)
    {
        addEffect(globalMessage.getCustom(customMessage, variables));
    }
    
    public void clearEffects()
    {
        if(effects != null)
            effects.clear();
    }
    
    public void sendEffects(CommandSender sender)
    {
        if(sender == null || effects == null)
            return;
        
        for(String s : effects)
        {
            Messages.send(sender, s);
        }
    }
    
    public String parseVariables(String string)
    {
        String name = (hasPlayerName() ? playerName() : "(nobody)");
        
        string = string.replace("{player}", name);
        string = string.replace("{playerdisplay}", (player != null ? player.getDisplayName() : name));
        string = string.replace("{result}", Tools.printItemStack(result()));
        string = string.replace("{recipetype}", (hasRecipeType() ? recipeType().toString() : "(unknown)"));
        string = string.replace("{world}", (hasLocation() ? location().getWorld().getName() : "(unknown)"));
        string = string.replace("{x}", (hasLocation() ? "" + location().getBlockX() : "0"));
        string = string.replace("{y}", (hasLocation() ? "" + location().getBlockY() : "0"));
        string = string.replace("{z}", (hasLocation() ? "" + location().getBlockZ() : "0"));
        
        return string;
    }
}