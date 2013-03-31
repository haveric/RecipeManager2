package ro.thehunters.digi.recipeManager.flags;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.Tools;
import ro.thehunters.digi.recipeManager.recipes.BaseRecipe;
import ro.thehunters.digi.recipeManager.recipes.BaseRecipe.RecipeType;
import ro.thehunters.digi.recipeManager.recipes.ItemResult;

/**
 * Easily modifiable arguments for the flag classes without needing to re-edit all of them
 * 
 * @author Digi
 */
public class Args
{
    private Player player;
    private String playerName;
    private Location location;
    private BaseRecipe recipe;
    private RecipeType recipeType;
    private Inventory inventory;
    private ItemResult result;
    
    private List<String> reasons;
    private List<String> effects;
    
    protected Args()
    {
    }
    
    protected void setPlayer(Player player)
    {
        this.player = player;
    }
    
    protected void setPlayerName(String playerName)
    {
        this.playerName = playerName;
    }
    
    protected void setLocation(Location location)
    {
        this.location = location;
    }
    
    protected void setRecipe(BaseRecipe recipe)
    {
        this.recipe = recipe;
    }
    
    protected void setRecipeType(RecipeType recipeType)
    {
        this.recipeType = recipeType;
    }
    
    protected void setInventory(Inventory inventory)
    {
        this.inventory = inventory;
    }
    
    protected void setResult(ItemResult result)
    {
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
    
    public BaseRecipe recipe()
    {
        return recipe;
    }
    
    public boolean hasRecipe()
    {
        return recipe != null;
    }
    
    public Inventory inventory()
    {
        return inventory;
    }
    
    public boolean hasInventory()
    {
        return inventory != null;
    }
    
    public ItemResult result()
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
    
    public void addCustomReason(String message)
    {
        if(reasons == null)
        {
            reasons = new ArrayList<String>();
        }
        
        reasons.add(message);
    }
    
    public void addReason(Messages globalMessage, String customMessage, Object... variables)
    {
        addCustomReason(globalMessage.getCustom(customMessage, variables));
    }
    
    public void clearReasons()
    {
        if(reasons != null)
        {
            reasons.clear();
        }
    }
    
    public void sendReasons(CommandSender sender, Messages prefix)
    {
        sendList(sender, prefix, reasons);
    }
    
    public List<String> effects()
    {
        return effects;
    }
    
    public boolean hasEffects()
    {
        return (effects != null && !effects.isEmpty());
    }
    
    public void addCustomEffect(String message)
    {
        if(effects == null)
        {
            effects = new ArrayList<String>();
        }
        
        effects.add(message);
    }
    
    public void addEffect(Messages globalMessage, String customMessage, Object... variables)
    {
        addCustomEffect(globalMessage.getCustom(customMessage, variables));
    }
    
    public void clearEffects()
    {
        if(effects != null)
        {
            effects.clear();
        }
    }
    
    public void sendEffects(CommandSender sender, Messages prefix)
    {
        sendList(sender, prefix, effects);
    }
    
    public void clear()
    {
        clearReasons();
        clearEffects();
    }
    
    private void sendList(CommandSender sender, Messages prefix, List<String> list)
    {
        if(sender == null || list == null)
        {
            return;
        }
        
        for(String s : list)
        {
            if(s != null)
            {
                Messages.send(sender, prefix.get() + s);
            }
        }
    }
    
    public String parseVariables(String string)
    {
        String name = (hasPlayerName() ? playerName() : "(nobody)");
        
        string = string.replace("{player}", name);
        string = string.replace("{playerdisplay}", (player != null ? player.getDisplayName() : name));
        string = string.replace("{result}", Tools.printItem(result()));
        string = string.replace("{recipename}", (hasRecipe() ? recipe().getName() : "(no recipe)"));
        string = string.replace("{recipetype}", (hasRecipeType() ? recipeType().toString().toLowerCase() : "(no recipe)"));
        string = string.replace("{inventorytype}", (hasInventory() ? inventory().getType().toString().toLowerCase() : "(no inventory)"));
        string = string.replace("{world}", (hasLocation() ? location().getWorld().getName() : "(no location)"));
        string = string.replace("{x}", (hasLocation() ? "" + location().getBlockX() : "0"));
        string = string.replace("{y}", (hasLocation() ? "" + location().getBlockY() : "0"));
        string = string.replace("{z}", (hasLocation() ? "" + location().getBlockZ() : "0"));
        
        return string;
    }
    
    /**
     * Start building an argument class for flag events
     * 
     * @return linkable methods
     */
    public static ArgBuilder create()
    {
        return new ArgBuilder();
    }
}